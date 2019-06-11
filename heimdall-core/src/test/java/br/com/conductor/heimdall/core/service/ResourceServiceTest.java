package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ResourceDTO;
import br.com.conductor.heimdall.core.dto.page.ResourcePage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServiceTest {

     @InjectMocks
     private ResourceService resourceService;

     @Mock
     private ResourceRepository resourceRepository;

     @Mock
     private ApiRepository apiRepository;

     @Mock
     private OperationService operationService;

     @Mock
     private InterceptorService interceptorService;

     @Mock
     private CacheService cacheService;

     @Mock
     private AMQPRouteService amqpRoute;

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     private Resource resource;

     private Api api;

     private ResourceDTO resourceDTO;

     @Before
     public void initAttributes() {

          api = new Api();
          api.setId(10L);

          resource = new Resource();
          resource.setId(1L);
          resource.setName("Local Resource");
          resource.setDescription("My Resource Description");
          resource.setApi(api);

          List<Operation> operations = new ArrayList<Operation>();
          operations.add(new Operation());
          operations.add(new Operation());
          resource.setOperations(operations);

          resourceDTO = new ResourceDTO();
          resourceDTO.setName("Local Resource");
          resourceDTO.setDescription("My Resource Description");
     }

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

          resourceService.save(10L, res);
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
          Mockito.when(resourceRepository.findByApiIdAndName(Mockito.anyLong(), Mockito.anyString()))
                 .thenReturn(resData);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(newRes);

          Resource resource = resourceService.save(10L, res);

          assertEquals(res.getName(), resource.getName());
          assertNotEquals(api.getId(), resource.getApi().getId());
     }

     @Test
     public void findResource() {

          Mockito.when(this.resourceRepository.findByApiIdAndId(Mockito.any(Long.class), Mockito.anyLong()))
                 .thenReturn(resource);
          Resource resourceResp = resourceService.find(10L, 1L);
          assertEquals(resourceResp.getId(), resource.getId());
          Mockito.verify(this.resourceRepository, Mockito.times(1))
                 .findByApiIdAndId(Mockito.any(Long.class), Mockito.anyLong());
     }

     @Test
     public void listResources() {

          List<Resource> resources = new ArrayList<>();
          resources.add(resource);

          Mockito.when(this.apiRepository.findOne(1L)).thenReturn(api);
          Mockito.when(this.resourceRepository.findAll(Mockito.any(Example.class))).thenReturn(resources);

          List<Resource> environmentResp = this.resourceService.list(1L, this.resourceDTO);

          assertEquals(resources.size(), environmentResp.size());
          Mockito.verify(this.resourceRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listResourcesWithPageable() {

          PageableDTO pageableDTO = new PageableDTO();
          pageableDTO.setLimit(10);
          pageableDTO.setOffset(0);

          ArrayList<Resource> listResources = new ArrayList<>();

          this.resource.setName("Resource Name");

          listResources.add(resource);

          Page<Resource> page = new PageImpl<>(listResources);

          Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
          Mockito.when(this.resourceRepository
                                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
                 .thenReturn(page);

          ResourcePage resourcePageResp = this.resourceService.list(1L, this.resourceDTO, pageableDTO);

          assertEquals(1L, resourcePageResp.getTotalElements());
          Mockito.verify(this.resourceRepository, Mockito.times(1))
                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class));
     }

     @Test
     public void updateResourceSuccess() {

          Mockito.when(apiRepository.findOne(1L)).thenReturn(api);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.any(), Mockito.anyLong()))
                 .thenReturn(resource);

          resourceDTO.setName("Another name");
          resourceDTO.setDescription("Anoter description");

          Resource saved = resourceService.save(1L, resourceDTO);

          assertEquals(saved.getId(), resource.getId());

          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);
          Mockito.when(resourceRepository.findOne(Mockito.anyLong())).thenReturn(resource);

          Resource update = resourceService.update(1L, 1L, resourceDTO);

          assertEquals(update.getId(), resource.getId());
     }

     @Test
     public void updateResourceErrorOnePerAPI() {

          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Only one resource per api");

          Mockito.when(apiRepository.findOne(10L)).thenReturn(api);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.any(), Mockito.anyLong()))
                 .thenReturn(resource);

          Api resApi = new Api();
          resApi.setId(10L);

          Resource resData = new Resource();
          resData.setApi(resApi);

          resourceDTO.setName("Another name");
          resourceDTO.setDescription("Anoter description");

          Resource saved = resourceService.save(10L, resourceDTO);

          assertEquals(saved.getId(), resource.getId());
          Mockito.when(resourceRepository.findByApiIdAndName(10L, "Another name")).thenReturn(resData);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);
          Mockito.when(resourceRepository.findOne(Mockito.anyLong())).thenReturn(resource);

          Resource update = resourceService.update(10L, 10L, resourceDTO);

          assertEquals(update.getId(), resource.getId());
     }

     @Test
     public void deleteResourceFromAPI() {

          List<Resource> resources = new ArrayList<>();
          List<Operation> operations = new ArrayList<>();

          Operation operation = new Operation();
          operation.setId(1L);
          operation.setPath("path example");
          operation.setResource(resource);
          operation.setMethod(HttpMethod.GET);

          operations.add(operation);
          resources.add(resource);

          Mockito.doNothing().when(operationService)
                 .deleteAllfromResource(Mockito.anyLong(), Mockito.anyLong());
          Mockito.doNothing().when(interceptorService).deleteAllfromResource(Mockito.anyLong());

          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(resource);
          Mockito.when(resourceRepository.findByApiId(1L)).thenReturn(resources);

          resourceService.deleteAllFromApi(1L);
          Mockito.verify(this.resourceRepository, Mockito.times(1)).delete(Mockito.anyLong());
     }

     @Test
     public void saveResource() {

          Mockito.when(apiRepository.findOne(1L)).thenReturn(api);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);

          Resource saved = resourceService.save(1L, resource);

          assertEquals(saved.getId(), resource.getId());
     }

     @Test
     public void saveResourceOnlyOnePerAPI() {

          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Only one resource per api");

          Api resApi = new Api();
          resApi.setId(10L);

          Resource resData = new Resource();
          resData.setApi(resApi);

          Mockito.when(apiRepository.findOne(1L)).thenReturn(api);
          Mockito.when(resourceRepository.findByApiIdAndName(1L, "Local Resource")).thenReturn(resData);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);

          Resource saved = resourceService.save(1L, resource);

          assertEquals(saved.getId(), resource.getId());
     }

}
