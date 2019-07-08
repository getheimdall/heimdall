/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.dto.persist.AccessTokenPersist;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

/**
 * This class provides methods to create, read, update and delete the {@link AccessToken} resource.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class AccessTokenService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private AppService appService;

    @Autowired
    private AMQPCacheService amqpCacheService;

    /**
     * Looks for a {@link AccessToken} based on it's.
     *
     * @param id The id of the {@link AccessToken}
     * @return The {@link AccessToken} found
     */
    public AccessToken find(String id) {

        AccessToken accessToken = accessTokenRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(accessToken == null, GLOBAL_NOT_FOUND, "Access Token");

        return accessToken;
    }

    public List<AccessToken> findByAppId(String appId) {
        return accessTokenRepository.findByApp(appId);
    }

    public AccessToken findByCode(String code) {
        return accessTokenRepository.findByCode(code);
    }

    /**
     * Returns a paged list of all {@link AccessToken} from a request.
     *
     * @return The paged {@link AccessToken} list
     */
    @Transactional(readOnly = true)
    public Page<AccessToken> list(Pageable pageable) {

        return accessTokenRepository.findAll(pageable);
    }

    /**
     * Returns a list of all {@link AccessToken} from a request
     *
     * @return The list of {@link AccessToken}
     */
    @Transactional(readOnly = true)
    public List<AccessToken> list() {

        return accessTokenRepository.findAll();
    }

    /**
     * Saves a new {@link AccessToken} for a {@link App}. If the {@link AccessToken} does not
     * have a token it generates a new token for it.
     *
     * @param accessToken {@link AccessTokenPersist}
     * @return The {@link AccessToken} that was saved to the repository
     */
    @Transactional
    public AccessToken save(AccessToken accessToken) {

        App app = appService.find(accessToken.getApp());
        HeimdallException.checkThrow(!app.getPlans().containsAll(accessToken.getPlans()), SOME_PLAN_NOT_PRESENT_IN_APP);

        if (accessToken.getCode() != null) {

            HeimdallException.checkThrow(accessTokenRepository.findByCode(accessToken.getCode()) != null, ACCESS_TOKEN_ALREADY_EXISTS);
        } else {

            RandomString randomString = new RandomString(12);
            String token = randomString.nextString();

            while (accessTokenRepository.findByCode(token) != null) {
                token = randomString.nextString();
            }

            accessToken.setCode(token);
        }

        accessToken.setCreationDate(LocalDateTime.now());
        accessToken.setCode(accessToken.getCode().trim());

        accessToken = accessTokenRepository.save(accessToken);

        app.addAccessToken(accessToken.getId());

        appService.update(app);

        return accessToken;
    }

    /**
     * Updates a {@link AccessToken} by its ID.
     *
     * @param id                 The ID of the {@link AccessToken} to be updated
     * @param accessTokenPersist {@link AccessTokenPersist} The request for {@link AccessToken}
     * @return The {@link AccessToken} updated
     */
    @Transactional
    public AccessToken update(String id, AccessToken accessTokenPersist) {

        AccessToken accessToken = this.find(id);

        App app = appService.find(accessTokenPersist.getApp());
        HeimdallException.checkThrow(!app.getPlans().containsAll(accessTokenPersist.getPlans()), SOME_PLAN_NOT_PRESENT_IN_APP);

        accessToken = accessTokenRepository.save(accessToken);

        amqpCacheService.dispatchClean();

        return accessToken;
    }

    public AccessToken update(AccessToken accessTokenPersist) {
        return this.update(accessTokenPersist.getId(), accessTokenPersist);
    }


    /**
     * Deletes a {@link AccessToken} by its ID.
     *
     * @param id The ID of the {@link AccessToken} to be deleted
     */
    @Transactional
    public void delete(String id) {

        AccessToken accessToken = this.find(id);

        amqpCacheService.dispatchClean();

        accessTokenRepository.delete(accessToken);
    }

}
