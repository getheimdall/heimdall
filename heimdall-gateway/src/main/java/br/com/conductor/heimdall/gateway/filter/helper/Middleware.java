
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

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Middleware representation.
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
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
          loadClassPath();
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
      * @return					{@link GroovyClassLoader}
      */
     private GroovyClassLoader loadClassPath() {

          classLoader = new GroovyClassLoader();
          File lastModifiedFile = lastModified((dir, name) -> name.contains(JAR));

          if (lastModifiedFile != null)
               classLoader.addClasspath(pathReferences + File.separator + lastModifiedFile.getName());
          
          return classLoader;
     }

     /*
      * List all files from a path using a FilenameFilter.
      * 
      * @param filter		- The FilenameFilter
      * @return				List<Files>
      */
     private File lastModified(FilenameFilter filter) {

          File directory = new File(pathReferences);
          
          File[] entries = directory.listFiles();

          return (entries != null)
             ? Arrays.stream(entries)
                  .filter(e -> filter.accept(directory, e.getName()))
                  .max(Comparator.comparingLong(File::lastModified))
                  .orElse(null)
             : null;

     }

     /*
      * Converts the middleware version from the name of the file.
      *
      * This functionality is not used yet but it will be needed when we implement the automatic middleware versioning.
      *
      * Ex.: version 2.15.57
      *      integer generated: 2015057
      */
     private static Integer version(File file) {
          String[] values = file.getName().replaceAll(".+\\.\\d{14}\\.(.+)\\.jar", "$1").split("\\.");
          return Integer.parseInt(values[0])*1000000 +
                  Integer.parseInt(values[1])*1000 +
                  Integer.parseInt(values[2]);
     }

}
