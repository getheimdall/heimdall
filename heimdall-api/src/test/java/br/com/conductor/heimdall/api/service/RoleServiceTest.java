package br.com.conductor.heimdall.api.service;

import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

     @InjectMocks
     private RoleService service;

     @Mock
     private RoleRepository roleRepository;

     @Mock
     private PrivilegeService privilegeService;

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     @Test
     public void rejectNewRoleWithInvalidPrivileges() {
          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Privileges [9, 10] defined to attach in role not exist");

          Role role = new Role();
          role.setName("name");

          Set<String> privileges = new HashSet<>();
          privileges.add("9L");
          privileges.add("10L");
          role.setPrivileges(privileges);

          Mockito.when(privilegeService.find(Mockito.anyString())).thenReturn(null);

          service.save(role);
     }

}
