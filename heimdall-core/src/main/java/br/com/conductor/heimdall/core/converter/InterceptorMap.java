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

import br.com.conductor.heimdall.core.dto.InterceptorDTO;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Interceptor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

/**
 * Maps a full Environment to one with only the Id filled.
 *
 * @author Marcos Filho
 *
 */
public class InterceptorMap extends PropertyMap<InterceptorDTO, Interceptor> {

	@Override
	protected void configure() {

		using(envConverter).map(source.getEnvironment(), destination.getEnvironment());
	}

	Converter<ReferenceIdDTO, Environment> envConverter = new AbstractConverter<ReferenceIdDTO, Environment>() {

		protected Environment convert(ReferenceIdDTO ref) {
			if (ref == null || ref.getId() == null) {
				return null;
			}

			Environment environment = new Environment();
			environment.setId(ref.getId());
			return environment;
		}
	};

}
