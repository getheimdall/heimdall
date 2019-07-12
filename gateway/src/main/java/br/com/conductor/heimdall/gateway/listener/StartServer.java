/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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
package br.com.conductor.heimdall.gateway.listener;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.service.InterceptorService;
import br.com.conductor.heimdall.gateway.configuration.HeimdallHandlerMapping;
import br.com.conductor.heimdall.gateway.service.InterceptorFileService;
import com.netflix.zuul.FilterFileManager;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * StartServer
 * <p>
 * {@link ServletContextListener} implementation.
 *
 * @author Filipe Germano
 * @author Thiago Sampaio
 * @author Marcelo Aguiar Rodrigues
 */
@Slf4j
public class StartServer implements ServletContextListener {

    @Autowired
    private InterceptorFileService interceptorFileService;

    @Autowired
    private InterceptorService interceptorService;

    @Autowired
    private HeimdallHandlerMapping heimdallHandlerMapping;

    @Value("${zuul.filter.root}")
    private String zuulFilterRoot;

    @Value("${zuul.filter.interval}")
    private int zuulFilterInterval;

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

            FilterLoader.getInstance().setCompiler(new GroovyCompiler());

            FilterFileManager.setFilenameFilter(new GroovyFileFilter());

            FilterFileManager.init(
                    zuulFilterInterval,
                    new File(zuulFilterRoot, PRE_TYPE).getAbsolutePath(),
                    new File(zuulFilterRoot, POST_TYPE).getAbsolutePath()
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Initializes the application
     */
    @PostConstruct
    public void initApplication() {

        try {

            createFolders();
            cleanFilesFolder(zuulFilterRoot);
            createInterceptors();
        } catch (Exception e) {

            log.error(e.getMessage(), e);
        }
    }

    /**
     * Creates all {@link Interceptor} from the repository
     */
    private void createInterceptors() {

        List<Interceptor> interceptors = interceptorService.list();
        if (Objects.nonNull(interceptors)) {

            interceptors.forEach(interceptor -> interceptorFileService.createFileInterceptor(interceptor));
        }
    }

    /**
     * Cleans all the files from a specific folder.
     *
     * @param root The folder root
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

        String[] types = {PRE_TYPE, POST_TYPE};

        for (String t : types) {
            File folder = new File(zuulFilterRoot, t);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }

    private Set<File> listAllFiles(String interceptorFolder) {
        try (Stream<Path> walk = Files.walk(Paths.get(interceptorFolder))) {

            return walk.filter(Files::isRegularFile)
                    .filter(path -> path.endsWith(".groovy"))
                    .map(Path::toFile)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            return Collections.emptySet();
        }
    }

}
