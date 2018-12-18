
package br.com.conductor.heimdall.api.dto;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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

import br.com.conductor.heimdall.core.enums.Status;
import org.hibernate.validator.constraints.Email;

import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import lombok.Data;

/**
 * Data transference object class that represents a Heimdall user.
 *
 * @author Marcos Filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
public class UserDTO implements Serializable {

     private static final long serialVersionUID = 1870453965792235839L;

     @NotNull
     @Size(max = 80)
     private String firstName;

     @NotNull
     @Size(max = 80)
     private String lastName;

     @NotNull
     @Size(max = 80)
     @Email
     private String email;

     @NotNull
     @Size(max = 16)
     private String password;
     
     @NotNull
     @Size(max = 30, min=5)
     private String userName;

     private Status status;

     private List<ReferenceIdDTO> roles;
}
