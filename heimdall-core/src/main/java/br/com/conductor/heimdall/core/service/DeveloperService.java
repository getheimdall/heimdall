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

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.request.DeveloperLogin;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.DeveloperRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_NOT_FOUND;

/**
 * This class provides methods to create, read, update and delete the {@link Developer} resource.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final AppService appService;

    public DeveloperService(DeveloperRepository developerRepository,
                            @Lazy AppService appService) {
        this.developerRepository = developerRepository;
        this.appService = appService;
    }

    /**
     * Finds a {@link Developer} by its ID.
     *
     * @param id The ID of the {@link Developer}
     * @return The {@link Developer} found
     */
    public Developer find(final String id) {

        final Developer developer = developerRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(developer == null, GLOBAL_NOT_FOUND, "Developer");

        return developer;
    }

    /**
     * Finds a {@link Developer} by its email and password.
     *
     * @param developerLogin The {@link DeveloperLogin}
     * @return The Developer
     */
    public Developer login(final DeveloperLogin developerLogin) {

        return developerRepository.findByEmailAndPassword(developerLogin.getEmail(), developerLogin.getPassword());
    }

    /**
     * Generates a paged list of {@link Developer}s from a request.
     *
     * @return The paged {@link Developer} list
     */
    public Page<Developer> list(final Pageable pageable) {

        return developerRepository.findAll(pageable);
    }

    /**
     * Generates a list of {@link Developer} from a request.
     *
     * @return The list of {@link Developer}
     */
    public List<Developer> list() {

        return developerRepository.findAll();
    }

    /**
     * Saves a {@link Developer}.
     *
     * @param developer The {@link Developer}
     * @return The {@link Developer} saved
     */
    @Transactional
    public Developer save(final Developer developer) {

        developer.setCreationDate(LocalDateTime.now());

        return developerRepository.save(developer);
    }

    /**
     * Updates a {@link Developer} by its ID.
     *
     * @param id        The ID of the {@link Developer} to be updated
     * @param developer The {@link Developer}
     * @return The updated {@link Developer}
     */
    @Transactional
    public Developer update(final String id, final Developer developer) {

        final Developer developerToUpdate = this.find(id);

        final Developer updated = GenericConverter.mapper(developer, developerToUpdate);

        return developerRepository.save(updated);
    }

    /**
     * Deletes a {@link Developer} by its ID.
     *
     * @param id The ID of the {@link Developer} to be deleted
     */
    @Transactional
    public void delete(String id) {

        Developer developer = this.find(id);

        developerRepository.delete(developer);
    }

    public List<App> list(String developerId) {
        final List<App> apps = this.appService.list();

        if (apps != null && !apps.isEmpty()) {
            return apps.stream()
                    .filter(app -> developerId.equals(app.getDeveloperId()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

}
