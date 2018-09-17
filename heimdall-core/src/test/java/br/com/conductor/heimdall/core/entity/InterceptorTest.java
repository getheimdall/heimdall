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
package br.com.conductor.heimdall.core.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.converter.InterceptorMap;
import br.com.conductor.heimdall.core.dto.InterceptorDTO;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;

public class InterceptorTest {

	
	@Test
	public void mapperObjectWithIdPopulate() {
		InterceptorDTO dto = new InterceptorDTO();
		dto.setEnvironment(new ReferenceIdDTO(10L));
		
		Interceptor interceptor = GenericConverter.mapperWithMapping(dto, Interceptor.class, new InterceptorMap());
		
		assertEquals(new Long(10), interceptor.getEnvironment().getId());
	}
	
	@Test
	public void mapperObjectWithIdNull() {
		InterceptorDTO dto = new InterceptorDTO();
		dto.setEnvironment(new ReferenceIdDTO(null));
		
		Interceptor interceptor = GenericConverter.mapperWithMapping(dto, Interceptor.class, new InterceptorMap());
		
		assertEquals(null, interceptor.getEnvironment());
	}
	
	@Test
	public void mapperObjectWithReferenceIdNull() {
		InterceptorDTO dto = new InterceptorDTO();
		dto.setEnvironment(null);
		
		Interceptor interceptor = GenericConverter.mapperWithMapping(dto, Interceptor.class, new InterceptorMap());
		
		assertEquals(null, interceptor.getEnvironment());
	}
	
	@Test
	public void overrideExistentInterceptorWithEnvironment() {
		Environment env = new Environment();
		env.setId(10L);
		
		Interceptor interceptor = new Interceptor();
		interceptor.setEnvironment(env);
		
		InterceptorDTO dto = new InterceptorDTO();
		dto.setEnvironment(new ReferenceIdDTO(null));
		
		interceptor = GenericConverter.mapperWithMapping(dto, interceptor, new InterceptorMap());
		
		assertEquals(null, interceptor.getEnvironment());
	}
	
	@Test
	public void overrideExistentInterceptorWithReferenceIdNull() {
		Environment env = new Environment();
		env.setId(10L);
		
		Interceptor interceptor = new Interceptor();
		interceptor.setEnvironment(env);
		
		InterceptorDTO dto = new InterceptorDTO();
		dto.setEnvironment(null);
		
		interceptor = GenericConverter.mapperWithMapping(dto, interceptor, new InterceptorMap());
		
		assertEquals(null, interceptor.getEnvironment());
	}
}
