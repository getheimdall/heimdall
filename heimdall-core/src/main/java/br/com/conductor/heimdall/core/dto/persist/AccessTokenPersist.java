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
package br.com.conductor.heimdall.core.dto.persist;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;

/**
 * Class that represents the persist for a {@link AccessToken}.
 *
 * @author Filipe Germano
 *
 */
@Data
public class AccessTokenPersist implements Serializable {

     private static final long serialVersionUID = -9130167171077204284L;

     @Size(max = 250)
     private String code;

     @NotNull
     private String app;

     @NotNull
     @Size(min = 1)
     private List<String> plans;
     
     private Status status;

}
