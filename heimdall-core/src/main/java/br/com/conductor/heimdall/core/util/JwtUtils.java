package br.com.conductor.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import org.springframework.stereotype.Component;

import br.com.conductor.heimdall.core.entity.TokenOAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Component
public class JwtUtils {

	public TokenOAuth generateNewToken(String clientId, Set<String> operationsPath, int timeToken, int timeRefreshToken) {
		return generateToken(clientId, timeToken, timeRefreshToken, operationsPath);
	}
	
	public TokenOAuth generateNewTokenTimeDefault(String clientId, Set<String> operationsPath) {
		return generateToken(clientId, 20, 3600, operationsPath);
	}

	public boolean tokenExpired(String token, String clientId) {
		Claims claimsFromTheToken;
		try {
			claimsFromTheToken = getClaimsFromTheToken(token, getSecretKeyByClientId(clientId));
		} catch (Exception e) {
			//TraceContextHolder.getInstance().getActualTrace().trace(e.getMessage(), e);
			return true;
		}
		
		return new Date().after(claimsFromTheToken.getExpiration());
	}
	
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
	
	private String getSecretKeyByClientId(String clientId) {
		return Base64.getEncoder().encodeToString(clientId.getBytes());
	}
}
