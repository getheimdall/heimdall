/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.conductor.heimdall.gateway.filter.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.conductor.heimdall.core.entity.Variable;

@RunWith(MockitoJUnitRunner.class)
public class XmlImplTest {

     @InjectMocks
     private XmlImpl xmlParser;
     
     
     @Test
     public void parseStringToObjectWithXmlSpec() {
          Variable variable = new Variable();
          variable.setId(20L);
          variable.setKey("Name");
          variable.setValue("valueName");
          String parse = xmlParser.parse(variable);
          assertNotNull(parse);
          assertEquals("<Variable><id>20</id><key>Name</key><value>valueName</value><environment/></Variable>", parse);
     }
     
     @Test
     public void ignoreWhenNotExistField() {
          Variable variable = xmlParser.parse("<Variable><id>20</id><notExist>20</notExist><key>Name</key><value>valueName</value><environment/></Variable>", Variable.class);
          assertNotNull(variable);
          assertEquals(new Long(20), variable.getId());
     }
}
