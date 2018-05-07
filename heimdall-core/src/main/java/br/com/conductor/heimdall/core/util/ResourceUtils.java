
package br.com.conductor.heimdall.core.util;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * This class provides methods to recover resource files.
 * 
 * @author Filipe Germano
 *
 */
public class ResourceUtils {
     
	 /**
	  * Return a File from a resource path.
	  * 
	  * @param  name			The path to the resource
	  * @return					The File from the resource
	  * @throws IOException
	  */
     public static File getFile(String name) throws IOException {
          
          File file = File.createTempFile("template", ".mustache");
          InputStream inputStream = new ClassPathResource(name).getInputStream();
          FileUtils.copyInputStreamToFile(inputStream, file);
          inputStream.close();
          
          return file;
     }

     /**
      * Opens a InputStream.
      * 
      * @param 	name				The path to open the InputStream
      * @return						The opened InputStream
      * @throws IOException
      */
     public static InputStream getInputStream(String name) throws IOException {
          
          InputStream inputStream = new ClassPathResource(name).getInputStream();
          
          return inputStream;
     }

}
