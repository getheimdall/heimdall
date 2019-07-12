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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://github.com/cassioesp" target="_blank">Cássio Espíndola/a>
 */
@RunWith(MockitoJUnitRunner.class)
public class PlanServiceTest {

    @InjectMocks
    private PlanService planService;

    @Mock
    private ApiService apiService;

    @Mock
    private AppService appService;

    @Mock
    private InterceptorService interceptorService;

    @Mock
    private PlanRepository planRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Plan planDTO;

    private Plan plan;

    @Before
    public void initAttributes() {

        plan = new Plan();
        plan.setId("1L");
        plan.setName("My Plan");
        plan.setDescription("My Plan Description");
        plan.setDefaultPlan(true);
        plan.setStatus(Status.ACTIVE);

        planDTO = new Plan();
        planDTO.setName("My Plan");
        planDTO.setDescription("My Plan Description");
        planDTO.setDefaultPlan(true);
        planDTO.setStatus(Status.ACTIVE);
    }

    @Test
    public void saveDefaultPlanSuccess() {

        Api api = new Api();
        api.setId("1L");
        api.setBasePath("/test");
        api.setName("test");
        api.setDescription("test");
        api.setVersion("1.0.0");
        api.setStatus(Status.ACTIVE);

        plan.setApiId(api.getId());
        Mockito.when(planRepository.save(Mockito.any(Plan.class))).thenReturn(plan);
        Mockito.when(apiService.find("1L")).thenReturn(api);

        planDTO.setApiId("1L");

        Plan saved = planService.save(planDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveNotDefaultPlanSuccess() {

        Api api = new Api();
        api.setId("1L");
        api.setBasePath("/test");
        api.setName("test");
        api.setDescription("test");
        api.setVersion("1.0.0");
        api.setStatus(Status.ACTIVE);

        plan.setApiId(api.getId());

        Mockito.when(planRepository.save(Mockito.any(Plan.class))).thenReturn(plan);
        Mockito.when(apiService.find("1L")).thenReturn(api);

        planDTO.setDefaultPlan(false);
        planDTO.setApiId("1L");

        Plan saved = planService.save(planDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void findPlan() {

        Mockito.when(this.planRepository.findById(Mockito.anyString())).thenReturn(Optional.of(plan));
        Plan planResp = planService.find("1L");
        assertEquals(planResp.getId(), plan.getId());
        Mockito.verify(this.planRepository, Mockito.times(1)).findById(Mockito.anyString());
    }

    @Test
    public void listPlans() {

        this.plan.setName("Plan Name");

        List<Plan> plans = new ArrayList<>();
        plans.add(plan);

        Mockito.when(this.planRepository.findAll()).thenReturn(plans);

        List<Plan> planResp = this.planService.list();

        assertEquals(plans.size(), planResp.size());
        Mockito.verify(this.planRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void listPlansWithPageable() {

        Pageable pageable = PageRequest.of(0, 10);

        ArrayList<Plan> listPlans = new ArrayList<>();

        this.plan.setName("Plan Name");

        listPlans.add(plan);

        Page<Plan> page = new PageImpl<>(listPlans);

        Mockito.when(this.planRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Page<Plan> planPageResp = this.planService.list(pageable);

        assertEquals(1L, planPageResp.getTotalElements());
        Mockito.verify(this.planRepository, Mockito.times(1))
                .findAll(Mockito.any(Pageable.class));
    }

    @Test
    public void updateNotDefaultPlanSuccess() {

        Api api = new Api();
        api.setId("1L");
        api.setBasePath("/test");
        api.setName("test");
        api.setDescription("test");
        api.setVersion("1.0.0");
        api.setStatus(Status.ACTIVE);
        plan.setApiId(api.getId());

        Mockito.when(planRepository.save(Mockito.any(Plan.class))).thenReturn(plan);
        Mockito.when(apiService.find("1L")).thenReturn(api);

        planDTO.setDefaultPlan(false);
        planDTO.setApiId("1L");

        Plan saved = planService.save(planDTO);

        assertEquals(saved.getId(), api.getId());

        Mockito.when(planRepository.findById(Mockito.anyString())).thenReturn(Optional.of(plan));

        planDTO.setApiId("1L");

        Plan update = planService.update("1L", planDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateDefaultPlanSuccess() {

        Api api = new Api();
        api.setId("1L");
        api.setBasePath("/test");
        api.setName("test");
        api.setDescription("test");
        api.setVersion("1.0.0");
        api.setStatus(Status.ACTIVE);
        plan.setApiId(api.getId());

        Mockito.when(planRepository.save(Mockito.any(Plan.class))).thenReturn(plan);
        Mockito.when(apiService.find("1L")).thenReturn(api);

        planDTO.setDefaultPlan(true);
        planDTO.setApiId("1L");

        Plan saved = planService.save(planDTO);

        assertEquals(saved.getId(), api.getId());

        Mockito.when(planRepository.findById(Mockito.anyString())).thenReturn(Optional.of(plan));

        planDTO.setApiId("2L");

        Plan update = planService.update("1L", planDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void deleteDefaultPlan() {

        Mockito.when(planRepository.findById(Mockito.anyString())).thenReturn(Optional.of(plan));
        Mockito.when(appService.list()).thenReturn(Mockito.anyList());
        this.planService.delete("1L");
        Mockito.verify(this.planRepository, Mockito.times(1)).delete(plan);

    }
}
