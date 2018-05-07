
package br.com.conductor.heimdall.core.util;

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

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Comparator;

import br.com.conductor.heimdall.core.entity.Operation;

/**
 * This class provides a comparator for the {@link Operation} resource.
 * 
 * @author Marcos Filho
 * @author Marcelo Rodrigues
 *
 */
public class OperationSort implements Comparator<Operation> {

     @Override
     public int compare(Operation r1, Operation r2) {
         
          Path path1 = Paths.get(r1.getPath());
          Path path2 = Paths.get(r2.getPath());
          
          return path1.compareTo(path2);
     }
}
