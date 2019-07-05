package br.com.conductor.heimdall.api.configuration;

import br.com.conductor.heimdall.api.entity.FirstExecution;
import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.repository.FirstExecutionRepository;
import br.com.conductor.heimdall.api.service.PrivilegeService;
import br.com.conductor.heimdall.api.service.RoleService;
import br.com.conductor.heimdall.api.service.UserService;
import br.com.conductor.heimdall.core.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserConfiguration {

    @Autowired
    private UserService userService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private FirstExecutionRepository firstExecutionRepository;

    private List<String> privilegeNames = Arrays.asList("CREATE_ACCESSTOKEN",
            "CREATE_API",
            "CREATE_APP",
            "CREATE_DEVELOPER",
            "CREATE_ENVIRONMENT",
            "CREATE_INTERCEPTOR",
            "CREATE_MIDDLEWARE",
            "CREATE_OPERATION",
            "CREATE_PLAN",
            "CREATE_PROVIDER",
            "CREATE_RESOURCE",
            "CREATE_ROLE",
            "CREATE_SCOPE",
            "CREATE_TRACES",
            "CREATE_USER",
            "DELETE_ACCESSTOKEN",
            "DELETE_API",
            "DELETE_APP",
            "DELETE_CACHES",
            "DELETE_DEVELOPER",
            "DELETE_ENVIRONMENT",
            "DELETE_INTERCEPTOR",
            "DELETE_MIDDLEWARE",
            "DELETE_OPERATION",
            "DELETE_PLAN",
            "DELETE_PROVIDER",
            "DELETE_RESOURCE",
            "DELETE_ROLE",
            "DELETE_SCOPE",
            "DELETE_TRACES",
            "DELETE_USER",
            "READ_ACCESSTOKEN",
            "READ_API",
            "READ_APP",
            "READ_CACHES",
            "READ_DEVELOPER",
            "READ_ENVIRONMENT",
            "READ_INTERCEPTOR",
            "READ_LDAP",
            "READ_METRICS",
            "READ_MIDDLEWARE",
            "READ_OPERATION",
            "READ_PLAN",
            "READ_PRIVILEGE",
            "READ_PROVIDER",
            "READ_RESOURCE",
            "READ_ROLE",
            "READ_SCOPE",
            "READ_TRACES",
            "READ_USER",
            "REFRESH_INTERCEPTOR",
            "REFRESH_OPERATION",
            "REFRESH_RESOURCE",
            "UPDATE_ACCESSTOKEN",
            "UPDATE_API",
            "UPDATE_APP",
            "UPDATE_DEVELOPER",
            "UPDATE_ENVIRONMENT",
            "UPDATE_INTERCEPTOR",
            "UPDATE_LDAP",
            "UPDATE_MIDDLEWARE",
            "UPDATE_OPERATION",
            "UPDATE_PLAN",
            "UPDATE_PROVIDER",
            "UPDATE_RESOURCE",
            "UPDATE_ROLE",
            "UPDATE_SCOPE",
            "UPDATE_TRACES",
            "UPDATE_USER");

    private static final Logger log = LoggerFactory.getLogger(UserConfiguration.class);

    @PostConstruct
    public void contextInitialized() {

        if (isFirstExecution()) {

            createPrivileges();
            log.info("Created privileges");

            createRoles();
            log.info("Created roles");

            log.info("Creating user ADMIN");
            User user = new User();
            user.setFirstName("ADMIN");
            user.setLastName("ADMIN");
            user.setEmail("admin@getheimdall.io");
            user.setStatus(Status.ACTIVE);
            user.setUserName("admin");
            user.setPassword("admin");

            final Role admin = roleService.findByName("ADMIN");
            Set<String> roles = new HashSet<>();
            roles.add(admin.getId());
            user.setRoles(roles);

            userService.save(user);

            log.info("User ADMIN created");

        }
    }

    private boolean isFirstExecution() {
        final List<FirstExecution> executions = firstExecutionRepository.findAll();
        if (executions.isEmpty()) {
            FirstExecution firstExecution = new FirstExecution();
            firstExecutionRepository.save(firstExecution);

            return true;
        }

        return false;
    }

    private void createPrivileges() {

        privilegeNames.forEach(name -> {
            Privilege privilege = new Privilege();
            privilege.setName(name);
            privilegeService.save(privilege);
        });

    }

    private void createRoles() {

        roleService.createRole("ADMIN", privilegeNames);
        roleService.createRole("DEFAULT", Arrays.asList("READ_API", "READ_OPERATION", "CREATE_OPERATION", "UPDATE_OPERATION", "DELETE_OPERATION", "REFRESH_OPERATION", "READ_PRIVILEGE"));
        roleService.createRole("PORTAL", Arrays.asList("CREATE_ACCESSTOKEN", "CREATE_APP", "CREATE_DEVELOPER", "DELETE_ACCESSTOKEN", "DELETE_APP", "DELETE_DEVELOPER", "READ_ACCESSTOKEN", "READ_API", "READ_APP", "READ_DEVELOPER", "READ_PLAN", "UPDATE_ACCESSTOKEN", "UPDATE_APP", "UPDATE_DEVELOPER"));

    }

}
