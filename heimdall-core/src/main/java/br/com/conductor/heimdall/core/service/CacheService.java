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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * This class provides a method for manipulate the cache.
 * 
 * @author Filipe Germano
 *
 */
@Service
public class CacheService {
     
     @Autowired
     CacheManager cacheManager;
     
     /**
      * Returns the list of cached names.
      * 
      * @return	List of cached names
      */
     public List<String> list() {
          
          List<String> cacheNames = new ArrayList<>(cacheManager.getCacheNames());
          
          return cacheNames;
     }

     /**
      * Cleans a specific entry of a cache by key and id.
      * 
      * @param key The name of the cache
      * @param id The id to remove
      */
     public void clean(String key, String id) {
          
          cacheManager.getCache(key).evict(id);
     }
     
     /**
      * Cleans the entire cache
      */
     public void clean() {
          
          Collection<String> cacheNames = cacheManager.getCacheNames();
          for (String name : cacheNames) {
               
               cacheManager.getCache(name).clear();
          }
     }

     /**
      * Cleans all references of a cache entry.
      * 
      * @param key The name of the cache
      */
     public void clean(String key) {
          
          cacheManager.getCache(key).clear();
     }

}
