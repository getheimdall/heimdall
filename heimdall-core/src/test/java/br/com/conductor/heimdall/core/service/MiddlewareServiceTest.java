package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.MiddlewareDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.MiddlewarePage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.environment.Property.Middlewares;
import br.com.conductor.heimdall.core.exception.NotFoundException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.InterceptorRepository;
import br.com.conductor.heimdall.core.repository.MiddlewareRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPMiddlewareService;
import br.com.conductor.heimdall.core.util.Pageable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

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
	
	@Mock
	private Property property;

	@Value("${zuul.filter.root}")
	private String root;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	private Api api;
	private List<Middleware> middlewareList;
	private List<Interceptor> interceptors = new ArrayList<>();
	private Middleware m1, m2, m3, m4, m5, middleware;	
	private Middlewares middlewareProperty;
	private MiddlewareDTO middlewareDTO;
	private MockMultipartFile multipartFile;

	@Before
	public void setUp() throws Exception {
		api = new Api();
		api.setId(10L);

		multipartFile = new MockMultipartFile("artifact", "strongbox-validate-8.1.jar",
				"application/octet-stream", "some content".getBytes());
		
		m1 = new Middleware();
		m1.setStatus(Status.ACTIVE);
		m1.setApi(api);
		m1.setId(10L);
		m1.setCreationDate(LocalDateTime.of(2017, Month.JULY, 29, 19, 30, 40));
		m1.setFile(multipartFile.getBytes());

		m2 = new Middleware();
		m2.setStatus(Status.INACTIVE);
		m2.setApi(api);
		m2.setId(20L);
		m2.setCreationDate(LocalDateTime.of(2016, Month.JULY, 29, 19, 30, 40));
		m2.setFile(multipartFile.getBytes());

		m3 = new Middleware();
		m3.setStatus(Status.INACTIVE);
		m3.setApi(api);
		m3.setId(30L);
		m3.setCreationDate(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40));
		m3.setFile(multipartFile.getBytes());

		m4 = new Middleware();
		m4.setStatus(Status.DEPRECATED);
		m4.setApi(api);
		m4.setId(40L);
		m4.setCreationDate(LocalDateTime.of(2014, Month.JULY, 29, 19, 30, 40));
		m4.setFile(multipartFile.getBytes());

		m5 = new Middleware();
		m5.setStatus(Status.DEPRECATED);
		m5.setApi(api);
		m5.setId(50L);
		m5.setCreationDate(LocalDateTime.of(2013, Month.JULY, 29, 19, 30, 40));
		m5.setFile(multipartFile.getBytes());

		middlewareList = new ArrayList<>();
		middlewareList.add(m1);
		middlewareList.add(m2);
		middlewareList.add(m3);
		middlewareList.add(m4);
		middlewareList.add(m5);
		
		Property p = new Property();
		middlewareProperty = p.getMiddlewares();
		
		middlewareDTO = new MiddlewareDTO();
		middlewareDTO.setStatus(Status.ACTIVE);
		middlewareDTO.setVersion("0.0.1");

		

		middleware = GenericConverter.mapper(middlewareDTO, Middleware.class);
		middleware.setApi(api);
		middleware.setPath(root + "/api/" + api.getId() + "/middleware");
		middleware.setType("jar");
		middleware.setStatus(Status.ACTIVE);
		try {
			middleware.setFile(multipartFile.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		Mockito.when(middlewareRepository.findByApiId(api.getId())).thenReturn(middlewareList);
		Mockito.when(apiRepository.findOne(api.getId())).thenReturn(api);
		Mockito.when(interceptorRepository.findByTypeAndOperationResourceApiId(TypeInterceptor.MIDDLEWARE, api.getId()))
				.thenReturn(interceptors);
		
		Mockito.when(property.getMiddlewares()).thenReturn(middlewareProperty);

		Mockito.when(middlewareRepository.save(middleware)).thenReturn(middleware);
		Mockito.when(middlewareRepository.save(middlewareList)).thenReturn(middlewareList);

		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m1.getId())).thenReturn(m1);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m2.getId())).thenReturn(m2);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m3.getId())).thenReturn(m3);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m4.getId())).thenReturn(m4);
		Mockito.when(middlewareRepository.findByApiIdAndId(api.getId(), m5.getId())).thenReturn(m5);
		
	}

	@Test
	public void saveNewMiddlewareTest() {
		
		middlewareProperty.setAllowInactive(1);
		middlewareProperty.setDeleteDeprecated(true);
		
		Middleware saved = service.save(api.getId(), middlewareDTO, multipartFile);

		Map<Status, List<Middleware>> middlewareMap = middlewareList.stream()
				.collect(Collectors.groupingBy(m -> m.getStatus()));
		
		assertTrue(saved.equals(middleware));
		assertEquals(Status.ACTIVE, saved.getStatus());
		assertEquals(1, middlewareMap.get(Status.INACTIVE).size());
		assertEquals(4, middlewareMap.get(Status.DEPRECATED).size());

	}
	
	@Test
	public void propertyNotSetTest() {
		
		middlewareProperty.setAllowInactive(null);
		middlewareProperty.setDeleteDeprecated(null);

		Middleware saved = service.save(api.getId(), middlewareDTO, multipartFile);

		Map<Status, List<Middleware>> middlewareMap = middlewareList.stream()
				.collect(Collectors.groupingBy(m -> m.getStatus()));
		
		assertTrue(saved.equals(middleware));
		assertEquals(Status.ACTIVE, saved.getStatus());
		assertEquals(3, middlewareMap.get(Status.INACTIVE).size());
		assertEquals(2, middlewareMap.get(Status.DEPRECATED).size());

	}
	
	@Test
	public void saveNewMiddlewareHugeRollbackTest() {
		
		middlewareProperty.setAllowInactive(99999999);
		
		Middleware saved = service.save(api.getId(), middlewareDTO, multipartFile);

		Map<Status, List<Middleware>> middlewareMap = middlewareList.stream()
				.collect(Collectors.groupingBy(m -> m.getStatus()));
		
		assertTrue(saved.equals(middleware));
		assertEquals(Status.ACTIVE, saved.getStatus());
		assertEquals(3, middlewareMap.get(Status.INACTIVE).size());
		assertEquals(2, middlewareMap.get(Status.DEPRECATED).size());

	}
	
	@Test
	public void doNotDeleteDeprecated() {
		
		middlewareProperty.setAllowInactive(1);
		middlewareProperty.setDeleteDeprecated(false);
		
		Middleware saved = service.save(api.getId(), middlewareDTO, multipartFile);

		Map<Status, List<Middleware>> middlewareMap = middlewareList.stream()
				.collect(Collectors.groupingBy(m -> m.getStatus()));
		
		assertTrue(saved.equals(middleware));
		assertEquals(Status.ACTIVE, saved.getStatus());
		assertEquals(1, middlewareMap.get(Status.INACTIVE).size());
		assertEquals(4, middlewareMap.get(Status.DEPRECATED).size());
		assertNotNull(m2.getFile());
		assertNotNull(m3.getFile());
		assertNotNull(m4.getFile());
		assertNotNull(m5.getFile());

	}
	
	@Test
	public void deleteDeprecated() {
		
		middlewareProperty.setAllowInactive(1);
		middlewareProperty.setDeleteDeprecated(true);
		
		Middleware saved = service.save(api.getId(), middlewareDTO, multipartFile);

		Map<Status, List<Middleware>> middlewareMap = middlewareList.stream()
				.collect(Collectors.groupingBy(m -> m.getStatus()));
		
		assertTrue(saved.equals(middleware));
		assertEquals(Status.ACTIVE, saved.getStatus());
		assertEquals(1, middlewareMap.get(Status.INACTIVE).size());
		assertEquals(4, middlewareMap.get(Status.DEPRECATED).size());
		
		assertNull(m2.getFile());
		assertNull(m3.getFile());
		
		assertNotNull(m4.getFile());
		assertNotNull(m5.getFile());
		
	}
	
	@Test
	public void noMiddlewareTest() {
				
		Mockito.when(middlewareRepository.findByApiId(api.getId())).thenReturn(null);
		
		middlewareProperty.setAllowInactive(1);
		middlewareProperty.setDeleteDeprecated(true);
		
		Middleware saved = service.save(api.getId(), middlewareDTO, multipartFile);
		
		assertTrue(saved.equals(middleware));
		
	}

	@Test
	public void findTest() {
		Mockito.when(middlewareRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(m1);

		Middleware middleware = service.find(1L, 1L);

		assertEquals(m1, middleware);
	}

	@Test
	public void findNotFoundTest() {
		thrown.expect(NotFoundException.class);
		thrown.expectMessage("Resource not found");

		Mockito.when(middlewareRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);

		Middleware middleware = service.find(1L, 1L);
		assertEquals(m1, middleware);
	}

	@Test
	public void listPageableTest() {
		Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
		Mockito.when(middlewareRepository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class))).thenReturn(getPageMiddleware());

		MiddlewarePage middlewarePage = service.list(1L, new MiddlewareDTO(), new PageableDTO());

		assertEquals(middlewareList, middlewarePage.getContent());
	}

	@Test
	public void listTest() {
		Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
		Mockito.when(middlewareRepository.findAll(Mockito.any(Example.class))).thenReturn(middlewareList);

		List<Middleware> middlewareListActual = service.list(1L, new MiddlewareDTO());

		assertEquals(middlewareList, middlewareListActual);
	}

	@Test
	public void deleteTest() {
		Mockito.when(middlewareRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(middleware);
		service.delete(1L, 1L);

		Mockito.verify(middlewareRepository, Mockito.times(1)).findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong());
	}

	@Test
	public void deleteAllTest() {
		Mockito.when(middlewareRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(middleware);
		Mockito.when(middlewareRepository.findByApiId(Mockito.anyLong())).thenReturn(middlewareList);
		service.deleteAll(1L);

		Mockito.verify(middlewareRepository, Mockito.times(1)).findByApiId(Mockito.anyLong());
	}

	@Test
	public void updateTest() {
		Mockito.when(middlewareRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(middleware);
		Mockito.when(middlewareRepository.findByApiIdAndVersion(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
		middleware.setVersion("0.0.2");
		Mockito.when(middlewareRepository.save(Mockito.any(Middleware.class))).thenReturn(middleware);
		Middleware updated = service.update(1L, 1L, middlewareDTO);

		assertEquals(middleware, updated);

	}

	private Page<Middleware> getPageMiddleware() {
		return new Page<Middleware>() {
			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return middlewareList.size();
			}

			@Override
			public <S> Page<S> map(Converter<? super Middleware, ? extends S> converter) {
				return null;
			}

			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return middlewareList.size();
			}

			@Override
			public int getNumberOfElements() {
				return middlewareList.size();
			}

			@Override
			public List<Middleware> getContent() {
				return middlewareList;
			}

			@Override
			public boolean hasContent() {
				return !middlewareList.isEmpty();
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return false;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public org.springframework.data.domain.Pageable nextPageable() {
				return null;
			}

			@Override
			public org.springframework.data.domain.Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<Middleware> iterator() {
				return middlewareList.iterator();
			}
		};
	}
}
