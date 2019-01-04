
package br.com.conductor.heimdall.gateway.listener;

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

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.InterceptorRepository;
import br.com.conductor.heimdall.core.repository.MiddlewareRepository;
import br.com.conductor.heimdall.core.service.FileService;
import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.configuration.HeimdallHandlerMapping;
import br.com.conductor.heimdall.gateway.service.InterceptorFileService;
import br.com.conductor.heimdall.gateway.util.HeimdallFilterFileManager;
import br.com.twsoftware.alfred.io.Arquivo;
import br.com.twsoftware.alfred.object.Objeto;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.conductor.heimdall.core.util.Constants.MIDDLEWARE_API_ROOT;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

/**
 * StartServer
 * 
 * {@link ServletContextListener} implementation.
 *
 * @author Filipe Germano
 * @author Thiago Sampaio
 * @author Marcelo Aguiar Rodrigues
 *
 */
@Slf4j
public class StartServer implements ServletContextListener {

     @Autowired
     private InterceptorFileService interceptorFileService;

     @Autowired
     private InterceptorRepository interceptorRepository;

     @Autowired
     private ApiRepository apiRepository;

     @Autowired
     private MiddlewareRepository middlewareRepository;

     @Autowired 
     private FileService fileService;

     @Autowired
     private HeimdallHandlerMapping heimdallHandlerMapping;

     @Value("${zuul.filter.root}")
     private String zuulFilterRoot;

     @Value("${zuul.filter.interval}")
     private int zuulFilterInterval;

     private List<Api> apis;

     @Override
     public void contextInitialized(ServletContextEvent sce) {

          log.info("Initializing Groovy Interceptors");
          heimdallHandlerMapping.initHandlers();
          initGroovyFilterManager();

     }

     @Override
     public void contextDestroyed(ServletContextEvent sce) { }

     private void initGroovyFilterManager() {

          try {

               Set<String> filesAbsolutePath = filesAbsolutePath();

               FilterLoader.getInstance().setCompiler(new GroovyCompiler());

               HeimdallFilterFileManager.setFilenameFilter(new GroovyFileFilter());
               HeimdallFilterFileManager.init(zuulFilterInterval, filesAbsolutePath);

          } catch (Exception e) {
               throw new RuntimeException(e);
          }

     }

     private Set<String> filesAbsolutePath() {
          String[] types = {PRE_TYPE, POST_TYPE, ROUTE_TYPE, MIDDLEWARE_API_ROOT};

          Set<String> filesAbsolutePath = new HashSet<>();

          for (String t : types) {
               File folder = new File(zuulFilterRoot, t);
               filesAbsolutePath.add(folder.getAbsolutePath());
          }

          for (Api api : apis) {
               File apiFolder = new File(zuulFilterRoot, MIDDLEWARE_API_ROOT + File.separator + api.getId().toString());
               filesAbsolutePath.add(apiFolder.getAbsolutePath());
          }

          return filesAbsolutePath;
     }

     /**
      * Initializes the application
      */
     @PostConstruct
     public void initApplication() {
          
          try {
               
               createFolders();
               cleanFilesFolder(zuulFilterRoot);
               loadAllMiddlewareFiles();
               createInterceptors();
          } catch (Exception e) {
               
               log.error(e.getMessage(), e);
          }
     }
     
     /**
      * Creates all {@link Interceptor} from the repository
      */
     private void createInterceptors() {
          
          List<Interceptor> interceptors = interceptorRepository.findAll();
          if (Objeto.notBlank(interceptors)) {
               
               interceptors.forEach(interceptor -> interceptorFileService.createFileInterceptor(interceptor.getId()));

          }
     }

     /**
      * Creates the {@link Interceptor} from the {@link Middleware} by the {@link Middleware} Id.
      * 
      * @param middlewareId		The {@link Middleware} Id
      */
     public void createMiddlewaresInterceptor(Long middlewareId) {

          Middleware middleware = middlewareRepository.findOne(middlewareId);
          if (Objeto.notBlank(middleware) && Objeto.notBlank(middleware.getInterceptors())) {

               middleware.getInterceptors().forEach(interceptor -> interceptorFileService.createFileInterceptor(interceptor.getId()));
          }
     }

     /**
      * Cleans all the files from a specific folder.
      * 
      * @param root		The folder root
      */
     private void cleanFilesFolder(String root) {
          
          File interceptorsFolder = new File(root);
          Collection<File> files = Arquivo.listarArquivos(interceptorsFolder,
                  (dir, name) -> name.contains(".groovy") || name.contains(".java") || name.contains(".jar"),
                  true);
          
          files.forEach(f -> {
               try {
                    
                    FileUtils.forceDelete(f);
               } catch (IOException e) {
                    
                    log.error(e.getMessage(), e);
               }
          });
     }
     
     /**
      * Creates the folders necessary for the routes.
      */
     private void createFolders() {

          String[] types = {PRE_TYPE, POST_TYPE, ROUTE_TYPE, MIDDLEWARE_API_ROOT};

          for (String t : types) {
               File folder = new File(zuulFilterRoot, t);
               if (!folder.exists()) {
                    folder.mkdirs();
               }
          }

          apis = apiRepository.findAll();
          for (Api api : apis) {
               
               File apiFolder = new File(zuulFilterRoot, MIDDLEWARE_API_ROOT + File.separator + api.getId().toString() + File.separator + Constants.MIDDLEWARE_ROOT);
               if (!apiFolder.exists()) {
                    apiFolder.mkdirs();
               }
          }          
     }
     
     /**
      * Loads all Middleware files.
      */
     private void loadAllMiddlewareFiles() {
          
          try {
               
               List<Middleware> middlewares = middlewareRepository.findByStatus(Status.ACTIVE);
               
               for (Middleware middleware : middlewares) {
                    
                    String rootMiddlewares = middleware.getPath();
                    cleanFilesFolder(rootMiddlewares);
                    fileService.save(middleware.getFile(), rootMiddlewares + "/" + middleware.getName() + "." + middleware.getVersion() + "." + middleware.getType());
               }
               
          } catch (Exception e) {
               
               log.error(e.getMessage(), e);
          }
     }

     /**
      * Loads the {@link Middleware} files.
      * 
      * @param middlewareId		The {@link Middleware} Id
      */
     void loadMiddlewareFiles(Long middlewareId) {
          
          try {
               
               Middleware middleware = middlewareRepository.findOne(middlewareId);
               
               if (Objeto.notBlank(middleware)) {
               
                    if (Status.ACTIVE.equals(middleware.getStatus())) {
                         
                         fileService.save(middleware.getFile(), middleware.getPath() + "/" + middleware.getName() + "." + middleware.getVersion() + "." + middleware.getType());                         
                    }
               }
               
          } catch (Exception e) {
               
               log.error(e.getMessage(), e);
          }
     }

     /**
      * Remove the {@link Middleware} files.
      * 
      * @param path			The path to the {@link Middleware} files
      */
     void removeMiddlewareFiles(String path) {
          
          try {
               
               cleanFilesFolder(path);
               HeimdallFilterFileManager.getInstance().removeDirectory(path);
               
          } catch (Exception e) {
               
               log.error(e.getMessage(), e);
          }
     }

     /**
      * Include a new api directory to the file path
      *
      * @param api new Api
      */
     public void addApiDirectoryToPath(Api api) {
          File apiFolder = new File(zuulFilterRoot, MIDDLEWARE_API_ROOT + File.separator + api.getId().toString());
          HeimdallFilterFileManager.getInstance().addNewDirectory(apiFolder.getAbsolutePath());
     }
}
