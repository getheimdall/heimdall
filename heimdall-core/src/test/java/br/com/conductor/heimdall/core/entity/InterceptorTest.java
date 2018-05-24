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
