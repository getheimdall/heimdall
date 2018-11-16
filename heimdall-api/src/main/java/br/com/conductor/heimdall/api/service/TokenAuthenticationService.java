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

import br.com.conductor.heimdall.api.entity.Ldap;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.CredentialStateEnum;
import br.com.conductor.heimdall.api.environment.JwtProperty;
import br.com.conductor.heimdall.api.security.AccountCredentials;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Data class that holds tha JTW properties.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
@Slf4j
public class TokenAuthenticationService {

    @Autowired
    private JwtProperty jwtProperty;

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialStateService credentialStateService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private AuthenticationManagerBuilder authenticateManagerBuilder;

    @Autowired
    private LdapAuthoritiesPopulator populator;

    private static final String TOKEN_PREFIX = "Bearer ";

    private static final String HEIMDALL_AUTHORIZATION_NAME = "Authorization";

    public void login(AccountCredentials accountCredentials, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken userFound = new UsernamePasswordAuthenticationToken(
                accountCredentials.getUsername(),
                accountCredentials.getPassword(),
                Collections.emptyList());
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(userFound);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);

            Ldap ldapActive = ldapService.getLdapActive();

            if (Objects.nonNull(ldapActive)){
                try {
                    authenticateManagerBuilder.authenticationProvider(ldapProvider(ldapActive));
                    authenticate = ldapProvider(ldapActive).authenticate(userFound);
                } catch (Exception exception) {
                    log.error(exception.getMessage(), exception);
                }
            }
        }

        if (Objects.nonNull(authenticate)){
            addAuthentication(response, accountCredentials.getUsername(), null);
            response.setStatus(200);
            try {
                response.getWriter().write("{ \"Login with success!\" }");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            HeimdallException.checkThrow(true, ExceptionMessage.USERNAME_OR_PASSWORD_INCORRECT);
        }
    }

    private void addAuthentication(HttpServletResponse response, String username, String jti) {
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

    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

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
                        User userFound = userService.findByUsername(user);
                        addAuthentication(response, user, claims.getId());
                        return new UsernamePasswordAuthenticationToken(userFound.getUserName(), userFound.getPassword(), getAuthoritiesByRoles(userFound.getRoles()));
                    }
                    return null;
                }
            } catch (ExpiredJwtException ex) {
                credentialStateService.logout(token);
                ExceptionMessage.TOKEN_EXPIRED.raise();
            }
        }

        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesByRoles(Set<Role> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        roles.forEach(role -> {
            role.getPrivileges().forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege.getName())));
        });

        return authorities;
    }

    private LdapAuthenticationProvider ldapProvider(Ldap ldap) {

        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldap.getUrl());
        contextSource.setUserDn(ldap.getUserDn());
        contextSource.setPassword(ldap.getPassword());
        contextSource.setReferral("follow");
        contextSource.afterPropertiesSet();

        LdapUserSearch ldapUserSearch = new FilterBasedLdapUserSearch(ldap.getSearchBase(), ldap.getUserSearchFilter(), contextSource);

        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);
        bindAuthenticator.setUserSearch(ldapUserSearch);
        return new LdapAuthenticationProvider(bindAuthenticator, populator);
    }
}
