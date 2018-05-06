
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

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import lombok.extern.slf4j.Slf4j;

/**
 * <h1>FileUtils</h1><br/>
 * 
 * This class provides a method to write files on disk.
 * 
 * @author Filipe Germano
 *
 */
@Slf4j
public class FileUtils {

	 /**
	  * Saves a file to disk with UTF-8 formating.
	  * 
	  * @param  content		- The String content of the file
	  * @param  file		- The {@link File} object to be saved
	  */
     public static void write(String content, File file) {
          try {
               Files.write(content, file, Charsets.UTF_8);
          } catch (IOException e) {
               log.error(e.getMessage(), e);
          }
     }
}
