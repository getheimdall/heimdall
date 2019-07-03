/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.api.service;

import br.com.conductor.heimdall.api.dto.PrivilegeDTO;
import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.repository.PrivilegeRepository;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_NOT_FOUND;

/**
 * Provides methods to find one or mode {@link Privilege}.
 *
 * @author Marcos Filho
 *
 */
@Service
public class PrivilegeService {

     @Autowired
     private PrivilegeRepository repository;

     /**
      * Finds a {@link Privilege} by its Id.
      * 
      * @param id		The Privilege Id
      * @return			{@link Privilege}
      */
     public Privilege find(Long id) {

          Privilege privilege = repository.findOne(id);
          HeimdallException.checkThrow(privilege == null, GLOBAL_NOT_FOUND, "Privilege");

          return privilege;
     }

     /**
      * Finds all {@link Privilege} from a paged request.
      * 
      * @param privilegeDTO		{@link PrivilegeDTO}
      * @param pageableDTO		{@link PageableDTO}
      * @return
      */
     public Page<Privilege> list(PrivilegeDTO privilegeDTO, PageableDTO pageableDTO) {

          Privilege privilege = GenericConverter.mapper(privilegeDTO, Privilege.class);

          Example<Privilege> example = Example.of(privilege, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());

          return repository.findAll(example, pageable);
     }

     /**
      * Finds a {@link List} of {@link Privilege} associated with one Privilege provided.
      * 
      * @param privilegeDTO		{@link PrivilegeDTO}
      * @return					{@link List} of {@link Privilege}
      */
     public List<Privilege> list(PrivilegeDTO privilegeDTO) {

          Privilege privilege = GenericConverter.mapper(privilegeDTO, Privilege.class);

          Example<Privilege> example = Example.of(privilege, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          List<Privilege> privileges = repository.findAll(example);

          return privileges;
     }

     public Set<Privilege> list(String username) {
          return repository.findPrivilegeByUsername(username);
     }
}
