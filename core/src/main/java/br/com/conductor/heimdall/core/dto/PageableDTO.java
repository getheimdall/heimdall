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
package br.com.conductor.heimdall.core.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Class is a Data Transfer Object.
 *
 * @author Filipe Germano
 *
 */
@Data
public class PageableDTO implements Serializable {

     private static final long serialVersionUID = -3593999942005387183L;

     @Min(0)
     private Integer page;

     @Min(0)
     @Max(100)
     private Integer limit;

     public boolean isEmpty() {
          return this.page == null && this.limit == null;
     }
}
