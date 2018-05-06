package br.com.conductor.heimdall.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.conductor.heimdall.core.dto.ResourceDTO;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServiceTest {

     @InjectMocks
     private ResourceService service;
     
     @Mock
     private ResourceRepository resourceRepository;

     @Mock
     private ApiRepository apiRepository;
     
     @Mock
     private CacheService cacheService;

     @Mock
     private AMQPRouteService amqpRoute;
     
     @Rule
     public ExpectedException thrown = ExpectedException.none();
     
     @Test
     public void rejectNewResourceWithPathExistentInTheSameApi() {
          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Only one resource per api");
          
          ResourceDTO res = new ResourceDTO();
          res.setName("name");
          
          Api api = new Api();
          api.setId(10L);
          
          Resource resData = new Resource();
          resData.setApi(api);
          
          Mockito.when(apiRepository.findOne(10L)).thenReturn(api);
          Mockito.when(resourceRepository.findByApiIdAndName(api.getId(), res.getName())).thenReturn(resData);
          
          service.save(10L, res);
     }
     
     @Test
     public void acceptNewResourceWithPathExistentInDifferentApi() {
          ResourceDTO res = new ResourceDTO();
          res.setName("name");
          res.setDescription("description");
          
          Api api = new Api();
          api.setId(10L);
          
          Api diffApi = new Api();
          diffApi.setId(20L);
          
          Resource resData = new Resource();
          resData.setApi(diffApi);
          
          Resource newRes = new Resource();
          newRes.setId(40L);
          newRes.setName("name");
          newRes.setApi(diffApi);
          newRes.setDescription("foo description");
          
          
          Mockito.when(apiRepository.findOne(10L)).thenReturn(api);
          Mockito.when(resourceRepository.findByApiIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(resData);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(newRes);
          
          Resource resource = service.save(10L, res);
          
          assertEquals(res.getName(), resource.getName());
          assertNotEquals(api.getId(), resource.getApi().getId());
     }
}
