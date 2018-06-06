package br.com.conductor.heimdall.core.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.MiddlewareDTO;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.InterceptorRepository;
import br.com.conductor.heimdall.core.repository.MiddlewareRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPMiddlewareService;

@RunWith(MockitoJUnitRunner.class)
public class MiddlewareServiceTest {

	@InjectMocks
	private MiddlewareService service;

	@Mock
	private MiddlewareRepository middlewareRepository;

	@Mock
	private ApiRepository apiRepository;

	@Mock
	private InterceptorRepository interceptorRepository;

	@Mock
	private AMQPMiddlewareService amqpMiddlewareService;
	
	@Value("${zuul.filter.root}")
    private String root; 

	private Api api;
	private List<Middleware> middlewares;
	private List<Interceptor> interceptors = new ArrayList<>();
	private Middleware m1, m2, m3, m4, m5;

	@Before
	public void setUp() {
		api = new Api();
		api.setId(10L);

		m1 = new Middleware();
		m1.setStatus(Status.ACTIVE);
		m1.setApi(api);
		m1.setId(10L);
		m1.setCreationDate(LocalDateTime.of(2017, Month.JULY, 29, 19, 30, 40));

		m2 = new Middleware();
		m2.setStatus(Status.INACTIVE);
		m2.setApi(api);
		m2.setId(20L);
		m2.setCreationDate(LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40));

		m3 = new Middleware();
		m3.setStatus(Status.INACTIVE);
		m3.setApi(api);
		m3.setId(30L);
		m3.setCreationDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40));
		
		m4 = new Middleware();
		m4.setStatus(Status.DEPRECATED);
		m4.setApi(api);
		m4.setId(30L);
		m4.setCreationDate(LocalDateTime.of(2014, Month.JULY, 29, 19, 30, 40));
		
		
		m5 = new Middleware();
		m5.setStatus(Status.DEPRECATED);
		m5.setApi(api);
		m5.setId(30L);
		m5.setCreationDate(LocalDateTime.of(2013, Month.JULY, 29, 19, 30, 40));

		middlewares = new ArrayList<>();
		middlewares.add(m1);
		middlewares.add(m2);
		middlewares.add(m3);
		middlewares.add(m4);
		middlewares.add(m5);

	}

	@Test
	public void saveNewMiddlewareTest() {

		MiddlewareDTO middlewareDTO = new MiddlewareDTO();
		middlewareDTO.setStatus(Status.ACTIVE);
		middlewareDTO.setVersion("0.0.1");
		
		MockMultipartFile multipartFile = new MockMultipartFile("artifact", "strongbox-validate-8.1.jar",
				"application/octet-stream", "some content".getBytes());
		
		Middleware middleware = GenericConverter.mapper(middlewareDTO, Middleware.class);
        middleware.setApi(api);
        middleware.setPath(root + "/api/" + api.getId() + "/middleware");
        middleware.setType("jar");
        middleware.setStatus(Status.ACTIVE);
        try {
			middleware.setFile(multipartFile.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Mockito.when(middlewareRepository.findByApiId(api.getId())).thenReturn(middlewares);
		Mockito.when(apiRepository.findOne(api.getId())).thenReturn(api);
		Mockito.when(interceptorRepository.findByTypeAndOperationResourceApiId(TypeInterceptor.MIDDLEWARE, api.getId()))
				.thenReturn(interceptors);
		
		Mockito.when(middlewareRepository.save(middleware)).thenReturn(middleware);
		Mockito.when(middlewareRepository.save(middlewares)).thenReturn(middlewares);

		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m1.getId())).thenReturn(m1);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m2.getId())).thenReturn(m2);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m3.getId())).thenReturn(m3);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m4.getId())).thenReturn(m4);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m5.getId())).thenReturn(m5);

		Middleware saved = service.save(api.getId(), middlewareDTO, multipartFile);
		
		Map<Status, List<Middleware>> middlewareMap = middlewares.stream()
      		  .collect(Collectors.groupingBy(m -> m.getStatus()));
		
		System.out.println(m1.getStatus());
		System.out.println(m2.getStatus());
		System.out.println(m3.getStatus());
		System.out.println(m4.getStatus());
		System.out.println(m5.getStatus());
		
		assertTrue(saved.equals(middleware));
		assertNull(middlewareMap.get(Status.ACTIVE));
		assertEquals(2, middlewareMap.get(Status.INACTIVE).size());
		assertEquals(3, middlewareMap.get(Status.DEPRECATED).size());
		assertEquals(Status.DEPRECATED, m3.getStatus());
		
	}
	
	@Test
	public void deprecateMiddleware() {
		
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m1.getId())).thenReturn(m1);
		
		Middleware modified = service.depreciate(api.getId(), m1.getId());
		
		assertTrue(modified.getStatus().equals(Status.DEPRECATED));
	}
}
