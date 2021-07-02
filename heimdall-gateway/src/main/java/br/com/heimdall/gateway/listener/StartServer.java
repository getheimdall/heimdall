/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 *
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */
package br.com.heimdall.gateway.listener;

import static br.com.heimdall.core.util.Constants.MIDDLEWARE_API_ROOT;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ROUTE_TYPE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.com.heimdall.gateway.configuration.HeimdallHandlerMapping;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;

import br.com.heimdall.core.entity.Api;
import br.com.heimdall.core.entity.Interceptor;
import br.com.heimdall.core.entity.Middleware;
import br.com.heimdall.core.enums.Status;
import br.com.heimdall.core.repository.jdbc.ApiJDBCRepository;
import br.com.heimdall.core.repository.jdbc.InterceptorJDBCRepository;
import br.com.heimdall.core.repository.jdbc.MiddlewareJDBCRepository;
import br.com.heimdall.core.service.FileService;
import br.com.heimdall.core.util.Constants;
import br.com.heimdall.gateway.service.InterceptorFileService;
import br.com.heimdall.gateway.util.HeimdallFilterFileManager;
import lombok.extern.slf4j.Slf4j;

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
	private InterceptorJDBCRepository interceptorJDBCRepository;

	@Autowired
	private ApiJDBCRepository apiJDBCRepository;

	@Autowired
	private MiddlewareJDBCRepository middlewareJDBCRepository;

	@Autowired
	private FileService fileService;

	@Autowired
	private HeimdallHandlerMapping heimdallHandlerMapping;

	@Value("${zuul.filter.root}")
	private String zuulFilterRoot;

	@Value("${zuul.filter.interval}")
	private int zuulFilterInterval;

	private List<Long> apiIds;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		log.info("Initializing Groovy Interceptors");
		heimdallHandlerMapping.initHandlers();
		initGroovyFilterManager();

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

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
		String[] types = { PRE_TYPE, POST_TYPE, ROUTE_TYPE, MIDDLEWARE_API_ROOT };

		Set<String> filesAbsolutePath = new HashSet<>();

		for (String t : types) {
			File folder = new File(zuulFilterRoot, t);
			filesAbsolutePath.add(folder.getAbsolutePath());
		}

		for (Long id : apiIds) {
			File apiFolder = new File(zuulFilterRoot, MIDDLEWARE_API_ROOT + File.separator + id);
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

		List<Interceptor> interceptors = interceptorJDBCRepository.findAllInterceptorsSimplified();
		if (Objects.nonNull(interceptors)) {

			interceptors.forEach(interceptor -> interceptorFileService.createFileInterceptor(interceptor));
		}
	}

	/**
	 * Creates the {@link Interceptor} from the {@link Middleware} by the
	 * {@link Middleware} Id.
	 *
	 * @param middleware
	 *                       The {@link Middleware} Id
	 */
	public void createMiddlewaresInterceptor(Middleware middleware) {

		if (middleware != null) {
			List<Interceptor> interceptors = interceptorJDBCRepository.findInterceptorsSimplifiedFromMiddleware(middleware.getId());
			
			if (interceptors != null && !interceptors.isEmpty()) {
				interceptors.forEach(interceptor -> interceptorFileService.createFileInterceptor(interceptor));
			}
		}
	}

     /**
      * Cleans all the files from a specific folder.
      * 
      * @param root		The folder root
      */
     private void cleanFilesFolder(String root) {
          
          Set<File> files = listAllFiles(root);
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

		String[] types = { PRE_TYPE, POST_TYPE, ROUTE_TYPE, MIDDLEWARE_API_ROOT };

		for (String t : types) {
			File folder = new File(zuulFilterRoot, t);
			if (!folder.exists()) {
				folder.mkdirs();
			}
		}

		apiIds = apiJDBCRepository.findAllIds();
		for (Long id : apiIds) {

			File apiFolder = new File(zuulFilterRoot, MIDDLEWARE_API_ROOT + File.separator + id + File.separator + Constants.MIDDLEWARE_ROOT);
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

			List<Middleware> middlewares = middlewareJDBCRepository.findAllActive();

			for (Middleware middleware : middlewares) {

				String rootMiddlewares = middleware.getPath();
				cleanFilesFolder(rootMiddlewares);
				fileService.save(middleware.getFile(), rootMiddlewares + "/" + middleware.getName() + "."
						+ middleware.getVersion() + "." + middleware.getType());
			}
		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Loads the {@link Middleware} files.
	 *
	 * @param middleware The {@link Middleware}
	 */
	void loadMiddlewareFiles(Middleware middleware) {

		try {

			if (Objects.nonNull(middleware)) {

				if (Status.ACTIVE.equals(middleware.getStatus())) {
					fileService.save(middleware.getFile(), middleware.getPath() + "/" + middleware.getName() + "."
							+ middleware.getVersion() + "." + middleware.getType());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Remove the {@link Middleware} files.
	 *
	 * @param path
	 *                 The path to the {@link Middleware} files
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

     private Set<File> listAllFiles(String interceptorFolder) {
          try (Stream<Path> walk = Files.walk(Paths.get(interceptorFolder))) {

               return walk.filter(Files::isRegularFile)
                       .filter(path -> path.endsWith(".jar") || path.endsWith(".groovy") || path.endsWith(".java"))
                       .map(Path::toFile)
                       .collect(Collectors.toSet());
          } catch (IOException e) {
               return Collections.emptySet();
          }

     }
}
