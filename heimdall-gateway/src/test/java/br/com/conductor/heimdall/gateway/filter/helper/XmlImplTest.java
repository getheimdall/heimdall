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
