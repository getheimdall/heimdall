package br.com.conductor.heimdall.gateway.zuul.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.*;

import br.com.conductor.heimdall.core.repository.jdbc.ApiJDBCRepository;
import br.com.conductor.heimdall.core.repository.jdbc.OperationJDBCRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.util.Constants;

@RunWith(MockitoJUnitRunner.class)
public class CacheZuulRouteStorageTest {

     @InjectMocks
     private CacheZuulRouteStorage storage;
     
     @Mock
     private ApiRepository repository;

     @Mock
     private ResourceRepository resourceRepository;

     @Mock
     private ApiJDBCRepository apiJDBCRepository;

     @Mock
     private OperationJDBCRepository operationJDBCRepository;

     @Before
     public void setup() {
          MockitoAnnotations.initMocks(this);
     }
     
     @Test
     public void testLoadOneApiWithTwoResources() {
          List<Api> apis = new LinkedList<>();
          List<Environment> environments = new ArrayList<>();
          Environment environment = new Environment();
          environment.setInboundURL("dns.production.com.br");
          environment.setOutboundURL("dns.production.com.br");
          Api api = new Api(10L, "foo", "v1", "fooDescription", "/foo", false, LocalDateTime.now(), new HashSet<>(), Status.ACTIVE, environments, null);

          List<Resource> res = new LinkedList<>();
          
          Resource resource = new Resource();
          resource.setId(88L);
          resource.setApi(api);
          resource.setOperations(new ArrayList<>());
          
          Operation opPost = new Operation(10L, HttpMethod.POST, "/api/foo", "POST description", resource);
          Operation opGet = new Operation(11L, HttpMethod.GET, "/api/foo/{id}", "GET description", resource);
          Operation opDelete = new Operation(12L, HttpMethod.DELETE, "/api/foo/{id}", "DELETE description", resource);
          resource.getOperations().addAll(Arrays.asList(opDelete, opGet, opPost));
          
          res.add(resource);
          api.setResources(new HashSet<>(res));
          apis.add(api);
          
          Mockito.when(repository.findByStatus(Status.ACTIVE)).thenReturn(apis);
          Mockito.when(resourceRepository.findByApiId(Mockito.anyLong())).thenReturn(res);
          Mockito.when(apiJDBCRepository.findAllIds()).thenReturn(Collections.singletonList(10L));
          Mockito.when(operationJDBCRepository.findOperationsFromAllApis(Collections.singletonList(10L))).thenReturn(Arrays.asList("/api/foo", "/api/foo/{id}", "/api/foo/{id}"));
          ReflectionTestUtils.setField(this.storage, "profile", Constants.PRODUCTION);
          
          List<ZuulRoute> zuulRoutes = storage.findAll();
          
          assertNotNull(zuulRoutes);
          assertEquals(resource.getOperations().size(), zuulRoutes.size());
     }
}
