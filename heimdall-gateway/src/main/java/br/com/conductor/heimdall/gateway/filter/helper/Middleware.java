
package br.com.conductor.heimdall.gateway.filter.helper;

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

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import groovy.lang.GroovyClassLoader;

/**
 * Middleware representation.
 *
 * @author Filipe Germano
 *
 */
public class Middleware {

     private GroovyClassLoader classLoader;

     private final String JAR = ".jar";
     
     private final String pathReferences;

     /**
      * Construct a Middleware with preferences.
      * 
      * @param pathReferences	- The path with references
      */
     public Middleware(String pathReferences) {

          this.pathReferences = pathReferences;
          classLoad(pathReferences);
     }

     /**
      * Returns a instance of a class by its name.
      * 
      * @param className				- The name of the class
      * @return							The instance created
      * @throws ClassNotFoundException
      * @throws InstantiationException
      * @throws IllegalAccessException
      */
     @SuppressWarnings("unchecked")
     public <T> T instance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

          Class<?> clazz = classLoader.loadClass(className);

          Object newInstance = clazz.newInstance();

          return (T) newInstance;
     }

     /**
      * Returns a instance of a class inside a specific package.
      * 
      * @param className				- The name of the class
      * @param packageName				- The package of the class
      * @return							The instance created
      * @throws ClassNotFoundException
      * @throws InstantiationException
      * @throws IllegalAccessException
      */
     public <T> T instance(String className, String packageName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

          return instance(packageName + "." + className);
     }

     /**
      * Returns a {@link GroovyClassLoader} created from a reference path.
      * 
      * @param pathReferences	- The path with the references
      * @return					{@link GroovyClassLoader}
      */
     private GroovyClassLoader classLoad(String pathReferences) {

          classLoader = new GroovyClassLoader();
          classLoader.addClasspath(pathReferences);
          Collection<File> listFiles = listFiles(fileFilter(JAR));
          
          listFiles.forEach(f -> {

               classLoader.addClasspath(pathReferences + File.separator + f.getName());
          });
          
          return classLoader;
     }

     /*
      * List all files from a path using a FilenameFilter.
      * 
      * @param filter		- The FilenameFilter
      * @return				List<Files>
      */
     private List<File> listFiles(FilenameFilter filter) {

          File directory = new File(pathReferences);
          
          List<File> files = Lists.newArrayList();
          File[] entries = directory.listFiles();
          Long lastModified = 0l;
          for (File entry : entries) {
               if (filter == null || filter.accept(directory, entry.getName())) {                    
                    if (lastModified == 0l) {
                         
                         lastModified = entry.lastModified();     
                         files = Lists.newArrayList();
                         files.add(entry);
                    } else if (entry.lastModified() > lastModified) {
                         
                         lastModified = entry.lastModified();     
                         files = Lists.newArrayList();
                         files.add(entry);
                    }
               }
               if (entry.isDirectory()) {
                    files.addAll(listFiles(filter));
               }
          }

          return files;
     }

     /*
      * Creates a new FilenameFilter with specific extension.
      * 
      * @param		- The file extension
      */
     private FilenameFilter fileFilter(String fileExtension) {

          return new FilenameFilter() {

               @Override
               public boolean accept(File dir, String name) {

                    return name.contains(fileExtension);
               }

          };
     }
     
}
