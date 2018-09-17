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
package br.com.conductor.heimdall.core.converter;

import br.com.conductor.heimdall.core.dto.AppDTO;
import br.com.conductor.heimdall.core.entity.App;
import com.github.thiagonego.alfred.object.Objeto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

import java.util.List;

/**
 * Maps a {@link AppDTO} to a {@link App} object.
 *
 * @author Filipe Germano
 *
 */
public class AppMap extends PropertyMap<AppDTO, App> {

     @Override
     protected void configure() {

          using(tagsConverter).map(source.getTags(), destination.getTag());
     }

     Converter<List<String>, String> tagsConverter = new AbstractConverter<List<String>, String>() {
          
          protected String convert(List<String> tags) {
               
               String tag = null;
               if (Objeto.notBlank(tags)) {

                    tag = String.join(";", tags);
               }
               
               return tag;
          }
     };

}
