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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
     * @param privateKey       The privateKey that is used to get the SecretKey
     * @param operationsPath   Paths that the token can be used
     * @param timeToken        Time to expire the accessToken
     * @param timeRefreshToken Time to expire the refreshToken
     * @param claims           The {@link Map}<{@link String}, {@link Object}> claims
     * @return The new {@link TokenOAuth}
     */
    public TokenOAuth generateNewToken(String privateKey, Set<String> operationsPath, int timeToken, int timeRefreshToken, Map<String, Object> claims) {
        return generateToken(privateKey, timeToken, timeRefreshToken, operationsPath, claims);
    }

    /**
     * This method generate a new token with default time in accessToken and refreshToken
     *
     * @param privateKey     The privateKey that is used to get the SecretKey
     * @param operationsPath Paths that the token can be used
     * @param claims         The {@link Map}<{@link String}, {@link Object}> claims
     * @return The new {@link TokenOAuth}
     */
    public TokenOAuth generateNewTokenTimeDefault(String privateKey, Set<String> operationsPath, Map<String, Object> claims) {
        return generateToken(privateKey, 20, 3600, operationsPath, claims);
    }

    /**
     * This method validate if token is expired
     *
     * @param token      The token to be validate
     * @param privateKey The privateKey that is used to get the SecretKey
     * @return True if token is expired or false otherwise
     */
    public boolean tokenExpired(String token, String privateKey) {
        Claims claimsFromTheToken;
        try {
            claimsFromTheToken = getClaimsFromTheToken(token, getSecretKeyByClientId(privateKey));
        } catch (Exception e) {
            log.error(e.getMessage());
            return true;
        }

        return new Date().after(claimsFromTheToken.getExpiration());
    }

    /**
     * This method recover from the Token the Operations.
     *
     * @param token      The token that contain the operations
     * @param privateKey The privateKey that is used to get the SecretKey
     * @return The operations from the token
     */
    @SuppressWarnings("unchecked")
    public Set<String> getOperationsFromToken(String token, String privateKey) {
        Claims claimsFromTheToken;
        Set<String> operations = new HashSet<>();
        try {
            claimsFromTheToken = getClaimsFromTheToken(token, getSecretKeyByClientId(privateKey));
        } catch (Exception e) {
            log.error(e.getMessage());
            return operations;
        }

        List<String> list = claimsFromTheToken.get("operations", ArrayList.class);
        operations.addAll(list);

        return operations;
    }

    /**
     * Convert JsonObject to {@link Map}<{@link String}, {@link Object}>
     *
     * @param jsonObject The JsonObject
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    public Map<String, Object> getClaimsFromJSONObjectBodyRequest(String jsonObject) {

        Map<String, Object> claims = new HashMap<>();

        try {
            claims = new ObjectMapper().readValue(jsonObject, HashMap.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return claims;
    }

    /**
     * This method generate a new token.
     *
     * @param privateKey       The privateKey that is used to get the SecretKey
     * @param operationsPath   Paths that the token can be used
     * @param timeToken        Time to expire the accessToken
     * @param timeRefreshToken Time to expire the refreshToken
     * @param claims           The {@link Map}<{@link String}, {@link Object}> claims
     * @return The new {@link TokenOAuth}
     */
    private TokenOAuth generateToken(String privateKey, int timeToken, int timeRefreshToken, Set<String> operationsPath, Map<String, Object> claims) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime dateExpiredAccessToken = now.plusSeconds(timeToken);
        final LocalDateTime dateExpiredRefreshToken = now.plusSeconds(timeRefreshToken);
        final String secretKey = getSecretKeyByClientId(privateKey);

        String accessToken = Jwts.builder()
                .setExpiration(Date.from(dateExpiredAccessToken.atZone(ZoneId.systemDefault()).toInstant()))
                .claim("operations", operationsPath)
                .addClaims(claims)
                .signWith(
                        SignatureAlgorithm.HS256,
                        secretKey
                )
                .compact();
        String refreshToken = Jwts.builder()
                .setExpiration(Date.from(dateExpiredRefreshToken.atZone(ZoneId.systemDefault()).toInstant()))
                .claim("operations", operationsPath)
                .addClaims(claims)
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
     * This method generate a SecretKey from the param privateKey of the type {@link String}
     *
     * @param privateKey Information to get a SecretKey
     * @return The SecretKey of the type @{link {@link String}} encoded
     */
    private String getSecretKeyByClientId(String privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getBytes());
    }

    private static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
