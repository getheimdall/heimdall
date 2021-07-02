package br.com.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import static br.com.heimdall.core.util.ConstantsOAuth.*;
import br.com.heimdall.core.dto.request.OAuthRequest;
import br.com.heimdall.core.dto.response.TokenImplicit;
import br.com.heimdall.core.entity.App;
import br.com.heimdall.core.entity.OAuthAuthorize;
import br.com.heimdall.core.entity.Provider;
import br.com.heimdall.core.entity.TokenOAuth;
import br.com.heimdall.core.exception.*;
import br.com.heimdall.core.exception.BadRequestException;
import br.com.heimdall.core.exception.ExceptionMessage;
import br.com.heimdall.core.exception.ForbiddenException;
import br.com.heimdall.core.exception.HeimdallException;
import br.com.heimdall.core.exception.ProviderException;
import br.com.heimdall.core.exception.ServerErrorException;
import br.com.heimdall.core.exception.UnauthorizedException;
import br.com.heimdall.core.repository.AppRepository;
import br.com.heimdall.core.repository.OAuthAuthorizeRepository;
import br.com.heimdall.core.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides methods to create and validate the {@link TokenOAuth}
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Slf4j
@Service
public class OAuthService {

    @Autowired
    private OAuthAuthorizeRepository oAuthAuthorizeRepository;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private AppRepository appRepository;

    /**
     * Generates {@link TokenOAuth} from Code Authorize or RefreshToken
     *
     * @param oAuthRequest     The {@link OAuthRequest}
     * @param privateKey       The privateKey used to generate Token
     * @param timeAccessToken  The time to expire the accessToken
     * @param timeRefreshToken The time to expire the RefreshToken
     * @param claimsObject     The claimsObject in {@link String}
     * @return The {@link TokenOAuth}
     * @throws HeimdallException Token expired, code not found, grant_type not found, Code already used
     */
    public TokenOAuth generateTokenOAuth(OAuthRequest oAuthRequest, String clientId, String privateKey, int timeAccessToken, int timeRefreshToken, String claimsObject) throws HeimdallException {

        if (oAuthRequest.getGrantType() == null) {
            throw new BadRequestException(ExceptionMessage.GRANT_TYPE_NOT_FOUND);
        }

        switch (oAuthRequest.getGrantType().toUpperCase()) {
            case GRANT_TYPE_PASSWORD:

                final Map<String, Object> claimsFromJSONObjectBodyRequest = JwtUtils.getClaimsFromJSONObjectBodyRequest(claimsObject);

                TokenOAuth tokenOAuth = JwtUtils.generateTokenOAuth(privateKey, timeAccessToken, timeRefreshToken, claimsFromJSONObjectBodyRequest);

                saveToken(
                        clientId,
                        tokenOAuth.getAccessToken(),
                        JwtUtils.recoverDateExpirationFromToken(tokenOAuth.getAccessToken(), privateKey),
                        GRANT_TYPE_PASSWORD,
                        timeAccessToken
                );

                saveToken(
                        clientId,
                        tokenOAuth.getRefreshToken(),
                        JwtUtils.recoverDateExpirationFromToken(tokenOAuth.getRefreshToken(), privateKey),
                        GRANT_TYPE_REFRESH_TOKEN,
                        timeRefreshToken
                );

                return tokenOAuth;
            case GRANT_TYPE_REFRESH_TOKEN:
                if (oAuthRequest.getRefreshToken() == null) {
                    throw new BadRequestException(ExceptionMessage.REFRESH_TOKEN_NOT_FOUND);
                }
                //validate token
                tokenIsValid(oAuthRequest.getRefreshToken(), privateKey);
                //get OAuthAuthorize from database
                OAuthAuthorize tokenFound = oAuthAuthorizeRepository.findByTokenAuthorize(oAuthRequest.getRefreshToken());
                // verify if grantType of the token is equal REFRESH_TOKEN
                if (!tokenFound.getGrantType().equals(GRANT_TYPE_REFRESH_TOKEN)) {
                    throw new ForbiddenException(ExceptionMessage.TOKEN_INVALID);
                }
                //generate tokenOAuthGenerated
                TokenOAuth tokenOAuthGenerated = generateTokenOAuthFromOtherToken(tokenFound.getTokenAuthorize(), privateKey, timeAccessToken, timeRefreshToken);
                if (Objects.nonNull(tokenOAuthGenerated)) {
                    //save accessToken
                    saveToken(
                            tokenFound.getClientId(),
                            tokenOAuthGenerated.getAccessToken(),
                            JwtUtils.recoverDateExpirationFromToken(tokenOAuthGenerated.getAccessToken(), privateKey),
                            GRANT_TYPE_PASSWORD,
                            timeAccessToken
                    );
                    //save refreshToken
                    saveToken(
                            tokenFound.getClientId(),
                            tokenOAuthGenerated.getRefreshToken(),
                            JwtUtils.recoverDateExpirationFromToken(tokenOAuthGenerated.getRefreshToken(), privateKey),
                            GRANT_TYPE_REFRESH_TOKEN,
                            timeRefreshToken
                    );
                    //delete token used
                    this.oAuthAuthorizeRepository.delete(tokenFound);
                    //return new tokens
                    return tokenOAuthGenerated;
                }
                throw new ServerErrorException(ExceptionMessage.TOKEN_NOT_GENERATE);
            default:
                throw new BadRequestException(ExceptionMessage.GRANT_TYPE_NOT_FOUND);
        }

    }

    /**
     * Generates {@link TokenImplicit} to OAuth Implicit.
     *
     * @param oAuthRequest    The {@link OAuthRequest}
     * @param privateKey      The privateKey used to generate Token
     * @param timeAccessToken The time to expire the accessToken
     * @param claimsObject    The claimsObject in {@link String}
     * @return {@link TokenImplicit}
     */
    public TokenImplicit generateTokenImplicit(OAuthRequest oAuthRequest, String privateKey, int timeAccessToken, String claimsObject) {
        final Map<String, Object> claimsFromJSONObjectBodyRequest = JwtUtils.getClaimsFromJSONObjectBodyRequest(claimsObject);
        TokenImplicit tokenImplicit = JwtUtils.generateTokenImplicit(privateKey, timeAccessToken, claimsFromJSONObjectBodyRequest);
        saveToken(
                oAuthRequest.getClientId(),
                tokenImplicit.getAccessToken(),
                JwtUtils.recoverDateExpirationFromToken(tokenImplicit.getAccessToken(), privateKey),
                GRANT_TYPE_IMPLICIT,
                timeAccessToken
        );
        return tokenImplicit;
    }

    /**
     * Validate if token exist and not expired.
     *
     * @param token      The token
     * @param privateKey The privateKey used to generate token
     * @throws HeimdallException If token not valid.
     */
    public void tokenIsValid(String token, String privateKey) throws HeimdallException {
        if (!tokenExist(token)) {
            throw new UnauthorizedException(ExceptionMessage.TOKEN_INVALID);
        }
        try {
            JwtUtils.tokenExpired(token, privateKey);
        } catch (HeimdallException ex) {
            this.oAuthAuthorizeRepository.delete(this.oAuthAuthorizeRepository.findByTokenAuthorize(token));
            throw ex;
        }
    }


    /**
     * Validates if token exist in database.
     *
     * @param token The token
     * @return True if exist and false otherwise.
     */
    private boolean tokenExist(String token) {
        return Objects.nonNull(this.oAuthAuthorizeRepository.findByTokenAuthorize(token));
    }

    /**
     * Validates if token contain in operations the URL from request
     *
     * @param token       The token that contain the Operations
     * @param privateKey  The privateKey that is used to get the SecretKey
     * @param pathRequest The URL from request
     * @return True if token contain URL from request in Operations or false otherwise
     */
    public boolean tokenIsValidToResource(String token, String privateKey, String pathRequest) {
        Set<String> operationsFromToken = JwtUtils.getOperationsFromToken(token, privateKey);
        Optional<String> findFirst = operationsFromToken.stream().filter(o -> o.equals(pathRequest)).findFirst();
        return findFirst.isPresent();
    }

    /**
     * Finds the {@link Provider} by its Id
     *
     * @param providerId The {@link Provider} Id
     * @return The {@link Provider}
     * @throws ProviderException Provider not found
     */
    public Provider getProvider(Long providerId) throws ProviderException {
        Provider provider = providerService.findOne(providerId);
        if (provider == null) {
            throw new ProviderException(ExceptionMessage.PROVIDER_NOT_FOUND);
        }
        return provider;
    }

    /**
     * Generates the code authorize by clientId and {@link Provider} Id
     *
     * @param clientId The clientId
     * @return The code authorize
     */
    public String generateAuthorize(String clientId) {

        OAuthAuthorize found = oAuthAuthorizeRepository.findByClientIdAndExpirationDateIsNull(clientId);

        if (found == null) {
            OAuthAuthorize oAuthAuthorize = new OAuthAuthorize(clientId);
            return this.oAuthAuthorizeRepository.save(oAuthAuthorize).getTokenAuthorize();
        }

        found.generateCodeAuthorize();
        return this.oAuthAuthorizeRepository.save(found).getTokenAuthorize();
    }

    /**
     * Save a token to one clientId.
     *
     * @param clientId The clientId
     * @param token    The token
     */
    public void saveToken(String clientId, String token, LocalDateTime expirationDate, String grantType, int expirationTime) {

        App app = appRepository.findByClientId(clientId);

        HeimdallException.checkThrow(app == null, ExceptionMessage.CLIENT_ID_NOT_FOUND);

        OAuthAuthorize oAuthAuthorizeAccessToken = new OAuthAuthorize();
        oAuthAuthorizeAccessToken.setClientId(clientId);
        oAuthAuthorizeAccessToken.setTokenAuthorize(token);
        oAuthAuthorizeAccessToken.setExpirationDate(expirationDate);
        oAuthAuthorizeAccessToken.setGrantType(grantType);
        oAuthAuthorizeAccessToken.setExpirationTime(expirationTime);
        oAuthAuthorizeRepository.save(oAuthAuthorizeAccessToken);
    }

    /**
     * Delete one {@link OAuthAuthorize}
     *
     * @param oAuthAuthorize The {@link OAuthAuthorize}
     */
    public void delete(OAuthAuthorize oAuthAuthorize) {
        oAuthAuthorizeRepository.delete(oAuthAuthorize);
    }

    /**
     * Generate a new {@link TokenOAuth} from other token valid.
     *
     * @param token            The token
     * @param privateKey       The privateKey to be encoded
     * @param timeAccessToken  Time to expire accessToken
     * @param timeRefreshToken Time to expire refreshToken
     * @return The new {@link TokenOAuth}
     */
    private TokenOAuth generateTokenOAuthFromOtherToken(String token, String privateKey, int timeAccessToken, int timeRefreshToken) throws HeimdallException {
        String secretKey = JwtUtils.encodePrivateKey(privateKey);
        Claims claimsFromTheToken = JwtUtils.getClaimsFromTheToken(token, secretKey);
        Map<String, Object> claims = claimsFromTheToken.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return JwtUtils.generateTokenOAuth(privateKey, timeAccessToken, timeRefreshToken, claims);
    }

    /**
     * Generate a new {@link TokenImplicit} from other token valid.
     *
     * @param token      The token
     * @param privateKey The privateKey to be encoded
     * @return The new {@link TokenImplicit}
     */
    public TokenImplicit generateTokenImplicitFromOtherToken(String token, String privateKey, int timeAccessToken) throws HeimdallException {
        String secretKey = JwtUtils.encodePrivateKey(privateKey);
        Claims claimsFromTheToken = JwtUtils.getClaimsFromTheToken(token, secretKey);
        Map<String, Object> claims = claimsFromTheToken.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return JwtUtils.generateTokenImplicit(privateKey, timeAccessToken, claims);
    }

    /**
     * Obtain {@link OAuthAuthorize} by Token
     *
     * @param token The token
     * @return {@link OAuthAuthorize}
     */
    public OAuthAuthorize getOAuthAuthorizeFromToken(String token) {
        return this.oAuthAuthorizeRepository.findByTokenAuthorize(token);
    }
}
