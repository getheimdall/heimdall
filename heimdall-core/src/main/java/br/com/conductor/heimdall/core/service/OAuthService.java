package br.com.conductor.heimdall.core.service;

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

	public boolean validateToken(String token, String clientId) {
		return jwtUtils.tokenExpired(token, clientId);
	}

	public Provider getProvider(Long providerId) {
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
