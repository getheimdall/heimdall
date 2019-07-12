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

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_CACHES;

/**
 * Uses a {@link } to provide methods to read and remove the cache.
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
    private RedissonClient redissonClientCacheInterceptor;


    /**
     * Deletes all caches created by Cache interceptors.
     *
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete all caches created by Cache interceptors")
    @DeleteMapping("/interceptors")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_CACHES)
    public ResponseEntity<?> deleteInterceptors() {

        redissonClientCacheInterceptor.getKeys().flushdb();

        return ResponseEntity.noContent().build();
    }

}
