package br.com.conductor.heimdall.api.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.conductor.heimdall.api.dto.RoleDTO;
import br.com.conductor.heimdall.api.repository.PrivilegeRepository;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.exception.BadRequestException;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

     @InjectMocks
     private RoleService service;
     
     @Mock
     private RoleRepository roleRepository;
     
     @Mock
     private PrivilegeRepository privilegeRepository;
     
     @Rule
     public ExpectedException thrown = ExpectedException.none();
     
     @Test
     public void rejectNewRoleWithInvalidPrivileges() {
          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Privileges [9, 10] defined to attach in role not exist");
          
          RoleDTO roleDTO = new RoleDTO();
          roleDTO.setName("name");
          
          List<ReferenceIdDTO> privileges = Arrays.asList(new ReferenceIdDTO(10L), new ReferenceIdDTO(9L));
          roleDTO.setPrivileges(privileges);
          
          Mockito.when(privilegeRepository.findOne(Mockito.anyLong())).thenReturn(null);
          
          service.save(roleDTO);
     }
        
}
