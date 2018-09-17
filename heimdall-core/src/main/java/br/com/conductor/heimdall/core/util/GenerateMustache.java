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
package br.com.conductor.heimdall.core.util;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * Class creates a Mustache file.
 * 
 * @author Filipe Germano
 *
 */
public abstract class GenerateMustache {
     
	 /**
	  * Generates a Mustache file.
	  * 
	  * @param template		The template for generation
	  * @param parameters	The Map<String, Object> with the parameters for creation
	  * @return				The content of the file created
	  */
     public static String generateTemplate(String template, Map<String, Object> parameters) {
          
          StringWriter writer = new StringWriter();
          MustacheFactory mf = new DefaultMustacheFactory();
          Mustache mustache = mf.compile(new StringReader(template), "example");
          mustache.execute(writer, parameters);
          
          return writer.toString();
     }

}
