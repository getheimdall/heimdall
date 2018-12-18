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

import br.com.conductor.heimdall.core.dto.response.TokenImplicit;
import br.com.conductor.heimdall.core.entity.TokenOAuth;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides methods to generate and validate token with JWT
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
@Slf4j
public class JwtUtils {

    /**
     * This method generate a new token with refreshToken.
     *
     * @param privateKey       The privateKey that is used to get the SecretKey
     * @param timeToken        Time to expire the accessToken
     * @param timeRefreshToken Time to expire the refreshToken
     * @param claims           The {@link Map}<{@link String}, {@link Object}> claims
     * @return The new {@link TokenOAuth}
     */
    public static TokenOAuth generateTokenOAuth(String privateKey, int timeToken, int timeRefreshToken, Map<String, Object> claims) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateExpiredAccessToken = now.plusSeconds(timeToken);
        LocalDateTime dateExpiredRefreshToken = now.plusSeconds(timeRefreshToken);

        String accessToken = generateToken(privateKey, dateExpiredAccessToken, claims);
        String refreshToken = generateToken(privateKey, dateExpiredRefreshToken, claims);
        TokenOAuth tokenOAuth = new TokenOAuth();
        tokenOAuth.setAccessToken(accessToken);
        tokenOAuth.setRefreshToken(refreshToken);
        tokenOAuth.setExpiration(LocalDateTime.now().until(dateExpiredAccessToken, ChronoUnit.SECONDS));
        return tokenOAuth;
    }

    /**
     * This method generate a new token with OAuthImplicit.
     *
     * @param privateKey The privateKey that is used to get the SecretKey
     * @param timeToken  Time to expire the accessToken
     * @param claims     The {@link Map}<{@link String}, {@link Object}> claims
     * @return The new {@link TokenImplicit}
     */
    public static TokenImplicit generateTokenImplicit(String privateKey, int timeToken, Map<String, Object> claims) {
        LocalDateTime dateExpiredAccessToken = LocalDateTime.now().plusSeconds(timeToken);
        String token = generateToken(privateKey, dateExpiredAccessToken, claims);
        TokenImplicit tokenImplicit = new TokenImplicit();
        tokenImplicit.setAccessToken(token);
        tokenImplicit.setExpiration(LocalDateTime.now().until(dateExpiredAccessToken, ChronoUnit.SECONDS));
        tokenImplicit.setToken_type("bearer");
        return tokenImplicit;
    }

    /**
     * This method validate if token is expired
     *
     * @param token      The token to be validate
     * @param privateKey The privateKey that is used to get the SecretKey
     * @throws HeimdallException If token expired
     */
    public static void tokenExpired(String token, String privateKey) throws HeimdallException {
        getClaimsFromTheToken(token, encodePrivateKey(privateKey));
    }

    /**
     * This method recover from the Token the Operations.
     *
     * @param token      The token that contain the operations
     * @param privateKey The privateKey that is used to get the SecretKey
     * @return The operations from the token
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getOperationsFromToken(String token, String privateKey) {
        Claims claimsFromTheToken;
        Set<String> operations = new HashSet<>();
        try {
            claimsFromTheToken = getClaimsFromTheToken(token, encodePrivateKey(privateKey));
        } catch (Exception e) {
            log.error(e.getMessage());
            return operations;
        }

        List<String> list = claimsFromTheToken.get("operations", ArrayList.class);
        if (list != null && !list.isEmpty())
            operations.addAll(list);

        return operations;
    }

    /**
     * Convert JsonObject to {@link Map}<{@link String}, {@link Object}>
     *
     * @param jsonObject The JsonObject
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getClaimsFromJSONObjectBodyRequest(String jsonObject) {

        Map<String, Object> claims = new HashMap<>();

        try {
            claims = new ObjectMapper().readValue(jsonObject, HashMap.class);
            claims = claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return claims;
    }

    /**
     * This method generate a new token
     *
     * @param privateKey      The privateKey that is used to get the SecretKey
     * @param dateExpireToken Date the expiration of type {@link LocalDateTime}
     * @return The new token as {@link String}
     */
    private static String generateToken(String privateKey, LocalDateTime dateExpireToken, Map<String, Object> claims) {
        final String secretKey = encodePrivateKey(privateKey);
        return Jwts.builder()
                .addClaims(claims)
                .setIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(dateExpireToken.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(
                        SignatureAlgorithm.HS256,
                        secretKey
                )
                .compact();
    }

    /**
     * This method return {@link Claims} from the token
     *
     * @param token     The token that contain the {@link Claims}
     * @param secretKey To validate token and recover {@link Claims}
     * @return The {@link Claims}
     * @throws HeimdallException Token expired
     */
    public static Claims getClaimsFromTheToken(String token, String secretKey) throws HeimdallException {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ExceptionMessage.TOKEN_EXPIRED);
        }
    }

    /**
     * Get expiration {@link LocalDateTime} from token.
     *
     * @param token      The token
     * @param privateKey The privateKey used to generate token
     * @return The expiration {@link LocalDateTime}
     * @throws HeimdallException If token expired
     */
    public static LocalDateTime recoverDateExpirationFromToken(String token, String privateKey) throws HeimdallException {
        Claims claims = getClaimsFromTheToken(token, encodePrivateKey(privateKey));
        return claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * This method encrypt the privateKey of the type {@link String}
     *
     * @param privateKey Information to be encrypt.
     * @return The privateKey of the type @{link {@link String}} encoded
     */
    public static String encodePrivateKey(String privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getBytes());
    }
}
