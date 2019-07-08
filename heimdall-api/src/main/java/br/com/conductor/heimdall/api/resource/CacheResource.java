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
package br.com.conductor.heimdall.api.resource;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_CACHES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.service.CacheService;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a {@link CacheService} to provide methods to read and remove the cache.
 *
 * @author Filipe Germano
 */
@io.swagger.annotations.Api(
        value = PATH_CACHES,
        produces = MediaType.APPLICATION_JSON_VALUE,
        tags = {ConstantsTag.TAG_CACHES})
@RestController
@RequestMapping(value = PATH_CACHES)
public class CacheResource {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private AMQPCacheService amqpCacheService;

    /**
     * Finds all caches.
     *
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Caches")
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_CACHES)
    public ResponseEntity<?> findAll() {

        return ResponseEntity.ok(cacheService.list());
    }

    /**
     * Deletes a cache by its key.
     *
     * @param cacheKey The cache key
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Cache")
    @DeleteMapping(value = "/{cacheKey}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_CACHES)
    public ResponseEntity<?> delete(@PathVariable("cacheKey") String cacheKey) {

        amqpCacheService.dispatchClean(cacheKey);

        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a cache by its key and Id.
     *
     * @param cacheKey The cache key
     * @param cacheId  The cache Id
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Cache")
    @DeleteMapping(value = "/{cacheKey}/{cacheId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_CACHES)
    public ResponseEntity<?> delete(@PathVariable("cacheKey") String cacheKey,
                                    @PathVariable("cacheId") String cacheId) {

        amqpCacheService.dispatchClean(cacheKey, cacheId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes all caches.
     *
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete All Caches")
    @DeleteMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_CACHES)
    public ResponseEntity<?> delete() {

        amqpCacheService.dispatchClean();

        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes all caches created by Cache interceptors.
     *
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete all caches created by Cache interceptors")
    @DeleteMapping("/interceptors")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_CACHES)
    public ResponseEntity<?> deleteInterceptors() {

        amqpCacheService.dispatchCleanInterceptorsCache();

        return ResponseEntity.noContent().build();
    }

}
