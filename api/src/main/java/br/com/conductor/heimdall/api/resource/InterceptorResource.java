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
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.InterceptorDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.InterceptorType;
import br.com.conductor.heimdall.core.service.InterceptorService;
import br.com.conductor.heimdall.core.publisher.RedisInterceptorPublisher;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_INTERCEPTORS;

/**
 * Uses a {@link InterceptorService} to provide methods to create, read, update and delete a {@link Interceptor}.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 */
@io.swagger.annotations.Api(
        value = PATH_INTERCEPTORS,
        produces = MediaType.APPLICATION_JSON_VALUE,
        tags = {ConstantsTag.TAG_INTERCEPTORS})
@RestController
@RequestMapping(value = PATH_INTERCEPTORS)
public class InterceptorResource {

    @Autowired
    private InterceptorService interceptorService;

    @Autowired
    private RedisInterceptorPublisher redisInterceptorPublisher;

    /**
     * Finds a {@link Interceptor} by its Id.
     *
     * @param id The Interceptor Id
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find API by id", response = Interceptor.class)
    @GetMapping(value = "/{interceptorId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_INTERCEPTOR)
    public ResponseEntity<?> findById(@PathVariable("interceptorId") String id) {

        Interceptor interceptor = interceptorService.find(id);

        return ResponseEntity.ok(interceptor);
    }

    /**
     * Finds all {@link Interceptor} from a request.
     *
     * @param pageableDTO {@link PageableDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Interceptors", responseContainer = "List", response = Interceptor.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_INTERCEPTOR)
    public ResponseEntity<?> findAll(@ModelAttribute PageableDTO pageableDTO) {

        if (pageableDTO != null && !pageableDTO.isEmpty()) {
            final Pageable pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getLimit());
            Page<Interceptor> interceptorPage = interceptorService.list(pageable);

            return ResponseEntity.ok(interceptorPage);
        } else {

            List<Interceptor> interceptors = interceptorService.list();
            return ResponseEntity.ok(interceptors);
        }
    }

    /**
     * Lists all types of {@link Interceptor}.
     *
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "List all types of Interceptors", responseContainer = "List", response = Option.class)
    @GetMapping(value = "/types")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_INTERCEPTOR)
    public ResponseEntity<?> types() {

        List<Option> types = new ArrayList<>();
        for (InterceptorType type : InterceptorType.values()) {
            types.add(new Option(type.name()));
        }

        return ResponseEntity.ok(types);
    }

    /**
     * Saves a {@link Interceptor}.
     *
     * @param interceptorDTO {@link InterceptorDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Save a new Interceptor")
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_INTERCEPTOR)
    public ResponseEntity<?> save(@RequestBody @Valid InterceptorDTO interceptorDTO) {

        Interceptor interceptor = GenericConverter.mapper(interceptorDTO, Interceptor.class);

        interceptor = interceptorService.save(interceptor);

        return ResponseEntity.created(
                URI.create(String.format("/%s/%s", "interceptors", interceptor.getId()))
        ).build();
    }

    /**
     * Updates a {@link Interceptor}.
     *
     * @param id             The Interceptor Id
     * @param interceptorDTO {@link InterceptorDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Update Interceptor")
    @PutMapping(value = "/{interceptorId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_INTERCEPTOR)
    public ResponseEntity<?> update(@PathVariable("interceptorId") String id, @RequestBody InterceptorDTO interceptorDTO) {

        Interceptor interceptor = GenericConverter.mapper(interceptorDTO, Interceptor.class);

        interceptor = interceptorService.update(id, interceptor);

        return ResponseEntity.ok(interceptor);
    }

    /**
     * Delets a {@link Interceptor}
     *
     * @param id The Interceptor Id
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Interceptor")
    @DeleteMapping(value = "/{interceptorId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_INTERCEPTOR)
    public ResponseEntity<?> delete(@PathVariable("interceptorId") String id) {

        interceptorService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Refreshes all {@link Interceptor}.
     *
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Refresh Interceptor")
    @PostMapping(value = "/refresh")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_REFRESH_INTERCEPTOR)
    public ResponseEntity<?> refresh() {

        redisInterceptorPublisher.dispatchRefreshAllInterceptors();

        return ResponseEntity.noContent().build();
    }

    /*
     * Data class that holds the Option type.
     */
    @Data
    @AllArgsConstructor
    private class Option {
        private String type;
    }

}
