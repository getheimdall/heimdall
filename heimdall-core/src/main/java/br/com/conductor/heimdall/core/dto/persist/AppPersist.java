package br.com.conductor.heimdall.core.dto.persist;

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

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;

/**
 * Class is a Data Transfer Object for the {@link App}.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
public class AppPersist implements Serializable {

    private static final long serialVersionUID = 4168592185106510648L;

    @NotNull
    @Size(max = 180)
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private ReferenceIdDTO developer;

    private List<String> tags;

    private Status status;

    private List<ReferenceIdDTO> plans;

    private String clientId;
}
