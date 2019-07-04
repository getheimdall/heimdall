/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
 */
package br.com.conductor.heimdall.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is used to manage the Beans from the system.
 * 
 * @author Thiago Sampaio
 *
 */
@Scope("singleton")
@Component
@Slf4j
public class BeanManager implements ApplicationContextAware {

     @Getter
     private static ApplicationContext applicationContext;

     /**
      * {@inheritDoc}
      */
     public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

          BeanManager.init(applicationContext);
     }

     /**
      * Initializes factory with {@link ApplicationContext}.
      * 
      * @param 	applicationContext		The {@link ApplicationContext} that will be used to initialize the factory
      */
     private static void init(ApplicationContext applicationContext) {

          BeanManager.applicationContext = applicationContext;
     }

     /**
      * Gets the Bean managed by Spring by its Name Id.
      * 
      * @return 			The Spring bean
      */
     public static Object getBean(String beanName) {

          Object bean = null;
          try {
               
               bean = BeanManager.applicationContext.getBean(beanName);
          } catch (Exception e) {
               log.error(e.getMessage(), e);
          }
          
          return bean;
     }

     /**
      * Gets the Bean managed by Spring by its Name Id.
      * 
      * @return 			The Spring bean
      */
     public static Object getBean(Class<?> klass) {

          Object bean = null;
          try {
               bean = BeanManager.applicationContext.getBean(klass);

          } catch (Exception e) {
               log.error(e.getMessage(), e);
          }
          
          return bean;
     }

}
