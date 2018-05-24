package br.com.conductor.heimdall.core.converter;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

import br.com.conductor.heimdall.core.dto.InterceptorDTO;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Interceptor;

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
