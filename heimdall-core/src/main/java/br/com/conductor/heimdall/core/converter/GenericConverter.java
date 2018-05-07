
package br.com.conductor.heimdall.core.converter;

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

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;

/**
 * This class provides generic methods to convert between types.
 *
 * @author Filipe Germano
 *
 */
public abstract class GenericConverter {

	 /**
	  * Converts a source to a type destination.
	  * 
	  * @param source					The source object
	  * @param typeDestination			The type destination
	  * @return							The object created
	  */
     public static <T, E> E mapper(T source, Class<E> typeDestination) {

          ModelMapper modelMapper = new ModelMapper();
          modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
          return modelMapper.map(source, typeDestination);

     }

     /**
      * Converts a source to a type destination.
      * 
      * @param source				The source object
      * @param destination			The destination object
      * @return						The object created
      */
     public static <T, E> E mapper(T source, E destination) {

          ModelMapper modelMapper = new ModelMapper();
          modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
          modelMapper.map(source, destination);

          return destination;
     }

     /**
      * Converts a source to a type destination.
      * 
      * @param source				The souce object
      * @param typeDestination		The type destination 
      * @param mapping				The properties for the mapping process
      * @return						The object created
      */
     public static <T, E> E mapperWithMapping(T source, Class<E> typeDestination, PropertyMap<T, E> mapping) {

          ModelMapper modelMapper = new ModelMapper();
          modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
          modelMapper.addMappings(mapping);

          return modelMapper.map(source, typeDestination);
     }

     /**
      * Converts a source to a type destination.
      * 
      * @param source				The souce object
      * @param destination			The destination object
      * @param mapping				The properties for the mapping process
      * @return						The object created
      */
     public static <T, E> E mapperWithMapping(T source, E destination, PropertyMap<T, E> mapping) {
          
          ModelMapper modelMapper = new ModelMapper();
          modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
          modelMapper.addMappings(mapping);
          modelMapper.map(source, destination);
          
          return destination; 
     }
     
     /**
      * Converts a source to a type destination.
      * 
      * @param source				The souce object
      * @param typeDestination		The type destination 
      * @return						The object created
      */
     public static <E, T> List<E> mapper(List<T> source, Type destinationType) {

          List<E> model = null;
          if (source != null && destinationType != null) {

               ModelMapper modelMapper = new ModelMapper();

               modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
               model = modelMapper.map(source, destinationType);
          }

          return model;
     }
}
