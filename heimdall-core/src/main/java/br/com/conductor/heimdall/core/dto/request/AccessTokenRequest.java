
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

import br.com.conductor.heimdall.core.dto.integration.ReferenceAppDTO;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Class that represents a request for a {@link AccessToken}.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
public class AccessTokenRequest implements Serializable {

     private static final long serialVersionUID = -9130167171077204284L;

     @Size(max = 250)
     private String code;

     @NotNull
     private ReferenceAppDTO app;
     
     private LocalDateTime expiredDate;

     private Status status;

}
