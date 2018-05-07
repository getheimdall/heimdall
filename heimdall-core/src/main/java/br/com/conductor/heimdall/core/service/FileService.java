
package br.com.conductor.heimdall.core.service;

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/**
 * This class provides a simple way to save a {@link MultipartFile} to the file system.
 * 
 * @author Filipe Germano
 * @author Marcelo Rodrigues
 *
 */
@Slf4j
@Service
public class FileService {
     
	 /**
	  * Saves a {@link MultipartFile} to the file system.
	  * 
	  * @param file				The {@link MultipartFile} file to be saved
	  * @param pathname			The path to save
	  */
     public void save(MultipartFile file, String pathname) {

          if (!file.isEmpty()) {

               try {

                    byte[] bytes = file.getBytes();
                    save(bytes, pathname);
               } catch (Exception e) {
                    
                    log.error(e.getMessage(), e);
               }
          }
     }

     /**
      * Saves a byte array to the file system.
      * 
      * @param file				The byte array to be saved
      * @param pathname			The path to save
      */
     public void save(byte[] file, String pathname) {
          
          if (file != null && file.length > 0) {
               
               try {
                    
                    byte[] bytes = file;
                    
                    @Cleanup
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(pathname));
                    
                    @Cleanup
                    BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream);
                    stream.write(bytes);
                    
               } catch (Exception e) {
                    
                    log.error(e.getMessage(), e);
               }
          }
     }

}
