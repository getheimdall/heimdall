package br.com.conductor.heimdall.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import org.springframework.stereotype.Service;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ProviderDTO;
import br.com.conductor.heimdall.core.dto.page.ProviderPage;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.repository.ProviderRepository;
import br.com.conductor.heimdall.core.util.Pageable;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma
 *         Silva</a>
 *
 */
@Service
public class ProviderService {

	@Autowired
	private ProviderRepository providerRepository;
	
	public Provider save(ProviderDTO providerPersist) {
		Provider provider = GenericConverter.mapper(providerPersist, Provider.class);
		return this.providerRepository.save(provider);
	}

	public Provider edit(Long idProvider, ProviderDTO providerEdit) {
		Provider found = this.providerRepository.findOne(idProvider);
		Provider provider = GenericConverter.mapper(providerEdit, found);
		return this.providerRepository.save(provider);
	}

	public ProviderPage listWithPageableAndFilter(ProviderDTO providerDTO, PageableDTO pageableDTO) {

		Provider provider = GenericConverter.mapper(providerDTO, Provider.class);
		Example<Provider> example = Example.of(provider,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

		Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());

		Page<Provider> page = this.providerRepository.findAll(example, pageable);

		return new ProviderPage(PageDTO.build(page));
	}

	public List<Provider> listWithFilter(ProviderDTO providerDTO) {

		Provider provider = GenericConverter.mapper(providerDTO, Provider.class);
		Example<Provider> example = Example.of(provider,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

		return this.providerRepository.findAll(example);
	}
	

	public Provider findOne(Long id) {
		return this.providerRepository.findOne(id);
	}
	
	public void delete(Long id) {
		this.providerRepository.delete(id);
	}
}
