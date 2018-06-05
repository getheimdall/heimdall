package br.com.conductor.heimdall.core.service;

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

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conductor.heimdall.core.dto.request.OAuthRequest;
import br.com.conductor.heimdall.core.entity.OAuthAuthorize;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.entity.TokenOAuth;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.ProviderException;
import br.com.conductor.heimdall.core.exception.UnauthorizedException;
import br.com.conductor.heimdall.core.repository.OAuthAuthorizeRepository;
import br.com.conductor.heimdall.core.util.JwtUtils;
import br.com.twsoftware.alfred.object.Objeto;

/**
 * This class provides methods to create and validate the {@link TokenOAuth}.
 * 
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Service
public class OAuthService {

	private final static String GRANT_TYPE_PASSWORD = "PASSWORD";
	private final static String GRANT_TYPE_REFRESH_TOKEN = "REFRESH_TOKEN";

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private OAuthAuthorizeRepository oAuthAuthorizeRepository;
	@Autowired
	private ProviderService providerService;
	
	
	public TokenOAuth generateToken(OAuthRequest oAuthRequest, int timeAccessToken, int timeRefreshToken) throws UnauthorizedException, BadRequestException {

		TokenOAuth tokenOAuth = new TokenOAuth();

		if (oAuthRequest.getGrant_type().toUpperCase().equals(GRANT_TYPE_PASSWORD)) {
			
			OAuthAuthorize foundCode = oAuthAuthorizeRepository.findOne(oAuthRequest.getClient_id());
			
			if (Objeto.notBlank(foundCode)) {
				if (foundCode.getTokenAuthorize().equals(oAuthRequest.getCode())) {
					tokenOAuth = jwtUtils.generateNewToken(oAuthRequest.getClient_id(), oAuthRequest.getOperations(), timeAccessToken, timeRefreshToken);
					oAuthAuthorizeRepository.delete(foundCode);	
				} else {
					throw new BadRequestException(ExceptionMessage.CODE_NOT_FOUND);
				}
				
			}else {
				throw new BadRequestException(ExceptionMessage.CODE_NOT_FOUND);
			}
			
		} else if (oAuthRequest.getGrant_type().toUpperCase().equals(GRANT_TYPE_REFRESH_TOKEN)) {
			if (Objeto.isBlank(oAuthRequest.getRefresh_token())) {
				throw new BadRequestException(ExceptionMessage.REFRESH_TOKEN_NOT_EXIST);
			}
			boolean tokenExpired = jwtUtils.tokenExpired(oAuthRequest.getRefresh_token(), oAuthRequest.getClient_id());
			if (tokenExpired) {
				throw new UnauthorizedException(ExceptionMessage.TOKEN_EXPIRED);
			} else {
				tokenOAuth = jwtUtils.generateNewTokenTimeDefault(oAuthRequest.getClient_id(), oAuthRequest.getOperations());
			}
		} else {
			throw new BadRequestException(ExceptionMessage.GRANT_TYPE_NOT_EXIST);
		}

		return tokenOAuth;
	}

	public boolean tokenExpired(String token, String clientId) {
		return jwtUtils.tokenExpired(token, clientId);
	}
	
	public boolean tokenIsValidToResource(String token, String clientId, String pathRequest) {
		Set<String> operationsFromToken = jwtUtils.getOperationsFromToken(token, clientId);
		Optional<String> findFirst = operationsFromToken.stream().filter(o -> o.equals(pathRequest)).findFirst();
		return findFirst.isPresent();
	}

	public Provider getProvider(Long providerId) throws ProviderException{
		Provider provider = providerService.findOne(providerId);
		if (Objeto.isBlank(provider)) {
			throw new ProviderException(ExceptionMessage.PROVIDER_NOT_FOUND);
		}
		return provider;
	}
	
	public String generateAuthorize(String clientId, Long providerId) {
		
		OAuthAuthorize found = oAuthAuthorizeRepository.findOne(clientId);
	
		if (Objeto.isBlank(found)) {
			OAuthAuthorize oAuthAuthorize = new OAuthAuthorize(clientId);
			return this.oAuthAuthorizeRepository.save(oAuthAuthorize).getTokenAuthorize();
		} 
		
		found.generateTokenAuthorize();
		return this.oAuthAuthorizeRepository.save(found).getTokenAuthorize();
	}
}
