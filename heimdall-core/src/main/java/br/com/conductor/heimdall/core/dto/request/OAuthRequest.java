package br.com.conductor.heimdall.core.dto.request;

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
import java.util.Set;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Class is a Data Transfer Object for the OAuthRequest
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Data
public class OAuthRequest implements Serializable {

    private static final long serialVersionUID = 6985307454557101510L;

    @NotNull(message = "client_id needs to be informed.")
    private String client_id;

    private String grant_type;

    private Set<String> operations;

    private String refresh_token;

    private String code;
}
