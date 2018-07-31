package br.com.conductor.heimdall.api.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */

import br.com.conductor.heimdall.api.enums.CredentialStateEnum;
import br.com.conductor.heimdall.api.environment.JwtProperty;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * Data class that holds tha JTW properties.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class TokenAuthenticationService {

    @Autowired
    private JwtProperty jwtProperty;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CredentialStateService credentialStateService;

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String HEIMDALL_AUTHORIZATION_NAME = "Authorization";

    public void addAuthentication(HttpServletResponse response, String username, String jti) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        final LocalDateTime expirationDate = now.plusSeconds(jwtProperty.getExpirationTime());

        if (jti == null) {
            jti = UUID.randomUUID().toString();
            while (credentialStateService.findOne(jti) != null) {
                jti = UUID.randomUUID().toString();
            }

            credentialStateService.save(jti, username, CredentialStateEnum.LOGIN);
        }

        String jwt = Jwts.builder()
                .setSubject(username)
                .setId(jti)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expirationDate.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtProperty.getSecret())
                .compact();

        response.setHeader(HEIMDALL_AUTHORIZATION_NAME, jwt);
    }

    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException{

        String token = request.getHeader(HEIMDALL_AUTHORIZATION_NAME);

        if (token != null && !token.isEmpty()) {
            token = token.replace(TOKEN_PREFIX, "");
            // faz parse do token
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtProperty.getSecret())
                        .parseClaimsJws(token)
                        .getBody();
                String user = claims.getSubject();

                if (user != null) {
                    if (!credentialStateService.verifyIfTokenIsRevokeOrLogout(claims.getId())) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(user);
                        addAuthentication(response, userDetails.getUsername(), claims.getId());
                        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    }
                    return null;
                }
            } catch (ExpiredJwtException ex) {
                credentialStateService.logout(token);
                HeimdallException.checkThrow(true, ExceptionMessage.TOKEN_EXPIRED);
            }
        }

        return null;
    }

}
