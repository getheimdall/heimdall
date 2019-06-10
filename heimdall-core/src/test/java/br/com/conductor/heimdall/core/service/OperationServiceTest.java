package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.dto.OperationDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.OperationPage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.InterceptorRepository;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class OperationServiceTest {

     @InjectMocks
     private OperationService operationService;

     @Mock
     private OperationRepository operationRepository;

     @Mock
     private ResourceRepository resourceRepository;

     @Mock
     private ApiRepository apiRepository;

     @Mock
     private InterceptorRepository interceptorRepository;

     @Mock
     private InterceptorService interceptorService;

     @Mock
     private ApiService apiService;

     @Mock
     private CacheService cacheService;

     @Mock
     private AMQPCacheService amqpCacheService;

     @Mock
     private AMQPRouteService amqpRoute;

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     private Operation operation;

     private Resource res;

     private Api api;

     private OperationDTO operationDTO;

     @Before
     public void initAttributes() {

          Set<Resource> resources = new HashSet<>();

          api = new Api();
          api.setId(1L);
          api.setBasePath("http://127.0.0.1");
          api.setResources(resources);

          res = new Resource();
          res.setId(1L);
          res.setApi(api);
          resources.add(res);

          operation = new Operation();
          operation.setId(1L);
          operation.setMethod(HttpMethod.GET);
          operation.setResource(res);
          operation.setDescription("Operation Description");
          operation.setPath("/test");

          operationDTO = new OperationDTO();
          operationDTO.setDescription("Operation Description");
          operationDTO.setMethod(HttpMethod.GET);
          operationDTO.setPath("/test");
     }

     @Test
     public void saveOperationDTO() {

          Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(operationRepository.findByResourceApiIdAndMethodAndPath(Mockito.anyLong(),
                                                                               Mockito.any(HttpMethod.class),
                                                                               Mockito.anyString()
          )).thenReturn(null);
          Operation saved = operationService.save(api.getId(), res.getId(), operationDTO);
          assertEquals(saved.getId(), operation.getId());
     }

     @Test
     public void saveOperation() {

          Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(operationRepository.findByResourceApiIdAndMethodAndPath(Mockito.anyLong(),
                                                                               Mockito.any(HttpMethod.class),
                                                                               Mockito.anyString()
          )).thenReturn(null);
          Operation saved = operationService.save(api.getId(), res.getId(), operation);
          assertEquals(saved.getId(), operation.getId());
     }

     @Test
     public void saveWithResourceNull() {

          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Only one operation per resource");

          Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(operationRepository.findByResourceApiIdAndMethodAndPath(Mockito.anyLong(),
                                                                               Mockito.any(HttpMethod.class),
                                                                               Mockito.anyString()
          )).thenReturn(operation);

          Operation saved = operationService.save(api.getId(), res.getId(), operationDTO);
          assertEquals(saved.getId(), operation.getId());
     }

     @Test
     public void findOperation() {

          Mockito.when(this.operationRepository.findByResourceApiIdAndResourceIdAndId(Mockito.any(Long.class),
                                                                                      Mockito.anyLong(),
                                                                                      Mockito.anyLong()
          )).thenReturn(operation);
          Operation opearationResp = operationService.find(1L, 1L, 1L);
          assertEquals(opearationResp.getId(), operation.getId());
          Mockito.verify(this.operationRepository, Mockito.times(1))
                 .findByResourceApiIdAndResourceIdAndId(Mockito.any(Long.class), Mockito.anyLong(),
                                                        Mockito.anyLong()
                 );
     }

     @Test
     public void listOperations() {

          List<Operation> operations = new ArrayList<>();
          operations.add(operation);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(this.operationRepository.findAll(Mockito.any(Example.class))).thenReturn(operations);

          List<Operation> operationsResp = this.operationService.list(1L, 1L, this.operationDTO);

          assertEquals(operations.size(), operationsResp.size());
          Mockito.verify(this.operationRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listOperationsFromAPI() {
          List<Operation> operations = new ArrayList<>();
          Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(api);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(this.operationRepository.findAll(Mockito.any(Example.class))).thenReturn(operations);

          List<Operation> operationsResp = this.operationService.list(1L);

          assertEquals(operations.size(), operationsResp.size());
          Mockito.verify(this.operationRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listOperationsNotEmptyFromAPI() {
          List<Operation> operations = new ArrayList<>();
          operations.add(operation);
          Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(api);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(this.operationRepository.findAll(Mockito.any(Example.class))).thenReturn(operations);

          List<Operation> operationsResp = this.operationService.list(1L);

          assertEquals(operations.size(), operationsResp.size());
          Mockito.verify(this.operationRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listOperationsWithPageable() {

          PageableDTO pageableDTO = new PageableDTO();
          pageableDTO.setLimit(10);
          pageableDTO.setOffset(0);

          ArrayList<Operation> listOperations = new ArrayList<>();

          listOperations.add(operation);

          Page<Operation> page = new PageImpl<>(listOperations);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(this.operationRepository
                                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
                 .thenReturn(page);

          OperationPage operationPageResp = this.operationService
                    .list(1L, 1L, this.operationDTO, pageableDTO);

          assertEquals(1L, operationPageResp.getTotalElements());
          Mockito.verify(this.operationRepository, Mockito.times(1))
                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class));
     }

     @Test
     public void updateOperation() {

          Mockito.when(this.operationRepository.findByResourceApiIdAndResourceIdAndId(Mockito.any(Long.class),
                                                                                      Mockito.anyLong(),
                                                                                      Mockito.anyLong()
          )).thenReturn(operation);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(res);
          Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);
          Mockito.when(resourceRepository.findByApiIdAndId(Mockito.any(), Mockito.anyLong())).thenReturn(res);

          operationDTO.setDescription("Anoter description");

          Operation saved = operationService.save(1L, 1L, operationDTO);

          assertEquals(saved.getId(), operation.getId());

          Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);

          Operation update = operationService.update(1L, 1L, 1L, operationDTO);

          assertEquals(update.getId(), operation.getId());
     }

     @Test
     public void deleteOperation() {

          Mockito.when(operationRepository
                                 .findByResourceApiIdAndResourceIdAndId(Mockito.anyLong(), Mockito.anyLong(),
                                                                        Mockito.anyLong()
                                 )).thenReturn(operation);
          Mockito.when(operationRepository.findOne(Mockito.anyLong())).thenReturn(operation);
          List<Interceptor> interceptors = new ArrayList<>();
          Mockito.when(interceptorRepository.findByOperationId(Mockito.anyLong())).thenReturn(interceptors);
          this.operationService.delete(1L, 1L, 1L);
          Mockito.verify(this.operationRepository, Mockito.times(1)).delete(operation.getId());

     }

     @Test
     public void deleteAllFromResources() {
          Mockito.when(operationRepository
                                 .findByResourceApiIdAndResourceIdAndId(Mockito.anyLong(), Mockito.anyLong(),
                                                                        Mockito.anyLong()
                                 )).thenReturn(operation);

          this.operationService.deleteAllfromResource(1L, 1L);
          Mockito.verify(this.operationRepository, Mockito.times(0)).delete(operation.getId());

     }

}
