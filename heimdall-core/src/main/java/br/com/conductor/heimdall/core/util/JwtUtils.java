package br.com.conductor.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import br.com.conductor.heimdall.core.entity.TokenOAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * This class provides methods to generate and validate token with JWT
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * This method generate a new token.
     *
     * @param clientId         The clientId that is used to get the SecretKey
     * @param operationsPath   Paths that the token can be used
     * @param timeToken        Time to expire the accessToken
     * @param timeRefreshToken Time to expire the refreshToken
     * @return The new {@link TokenOAuth}
     */
    public TokenOAuth generateNewToken(String clientId, Set<String> operationsPath, int timeToken, int timeRefreshToken) {
        return generateToken(clientId, timeToken, timeRefreshToken, operationsPath);
    }

    /**
     * This method generate a new token with default time in accessToken and refreshToken
     *
     * @param clientId       The clientId that is used to get the SecretKey
     * @param operationsPath Paths that the token can be used
     * @return The new {@link TokenOAuth}
     */
    public TokenOAuth generateNewTokenTimeDefault(String clientId, Set<String> operationsPath) {
        return generateToken(clientId, 20, 3600, operationsPath);
    }

    /**
     * This method validate if token is expired
     *
     * @param token    The token to be validate
     * @param clientId The clientId that is used to get the SecretKey
     * @return True if token is expired or false otherwise
     */
    public boolean tokenExpired(String token, String clientId) {
        Claims claimsFromTheToken;
        try {
            claimsFromTheToken = getClaimsFromTheToken(token, getSecretKeyByClientId(clientId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return true;
        }

        return new Date().after(claimsFromTheToken.getExpiration());
    }

    /**
     * This method recover from the Token the Operations.
     *
     * @param token    The token that contain the operations
     * @param clientId The clientId that is used to get the SecretKey
     * @return The operations from the token
     */
    @SuppressWarnings("unchecked")
    public Set<String> getOperationsFromToken(String token, String clientId) {
        Claims claimsFromTheToken;
        Set<String> operations = new HashSet<>();
        try {
            claimsFromTheToken = getClaimsFromTheToken(token, getSecretKeyByClientId(clientId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return operations;
        }

        List<String> list = claimsFromTheToken.get("operations", ArrayList.class);
        list.forEach(o -> {
            operations.add(o);
        });

        return operations;
    }

    /**
     * This method generate a new token.
     *
     * @param clientId         The clientId that is used to get the SecretKey
     * @param operationsPath   Paths that the token can be used
     * @param timeToken        Time to expire the accessToken
     * @param timeRefreshToken Time to expire the refreshToken
     * @return The new {@link TokenOAuth}
     */
    private TokenOAuth generateToken(String clientId, int timeToken, int timeRefreshToken, Set<String> operationsPath) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime dateExpiredAccessToken = now.plusSeconds(timeToken);
        final LocalDateTime dateExpiredRefreshToken = now.plusSeconds(timeRefreshToken);
        final String secretKey = getSecretKeyByClientId(clientId);

        String accessToken = Jwts.builder()
                .setExpiration(Date.from(dateExpiredAccessToken.atZone(ZoneId.systemDefault()).toInstant()))
                .claim("client_id", clientId)
                .claim("operations", operationsPath)
                .signWith(
                        SignatureAlgorithm.HS256,
                        secretKey
                )
                .compact();
        String refreshToken = Jwts.builder()
                .setExpiration(Date.from(dateExpiredRefreshToken.atZone(ZoneId.systemDefault()).toInstant()))
                .claim("client_id", clientId)
                .claim("operations", operationsPath)
                .signWith(
                        SignatureAlgorithm.HS256,
                        secretKey
                )
                .compact();

        TokenOAuth tokenOAuth = new TokenOAuth();
        tokenOAuth.setAccessToken(accessToken);
        tokenOAuth.setRefreshToken(refreshToken);
        tokenOAuth.setExpiration(LocalDateTime.now().until(dateExpiredAccessToken, ChronoUnit.SECONDS));
        return tokenOAuth;
    }

    /**
     * This method return {@link Claims} from the token
     *
     * @param token     The token that contain the {@link Claims}
     * @param secretKey To validate token and recover {@link Claims}
     * @return The {@link Claims}
     * @throws Exception Token expired
     */
    private Claims getClaimsFromTheToken(String token, String secretKey) throws Exception {

        Claims claims;

        try {
            claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new Exception("JWT expired");
        }

        return claims;
    }

    /**
     * This method generate a SecretKey from the param clientId of the type {@link String}
     *
     * @param clientId Information to get a SecretKey
     * @return The SecretKey of the type @{link {@link String}}
     */
    private String getSecretKeyByClientId(String clientId) {
        return Base64.getEncoder().encodeToString(clientId.getBytes());
    }
}
