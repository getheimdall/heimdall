package br.com.conductor.heimdall.gateway.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

/**
 * Represents the algorithm to sort {@link ZuulRoute} correctly.
 * 
 * @author Marcos Filho
 * @author Marcelo Rodrigues
 *
 */
public class RouteSort implements Comparator<ZuulRoute> {

     @Override
     public int compare(ZuulRoute r1, ZuulRoute r2) {
          
          Path path1 = Paths.get(r1.getPath());
          Path path2 = Paths.get(r2.getPath());
          
          return path1.compareTo(path2);
          
     }

}
