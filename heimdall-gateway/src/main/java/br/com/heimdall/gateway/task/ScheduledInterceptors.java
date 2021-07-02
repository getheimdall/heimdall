/*-
 * =========================LICENSE_START==================================
 * heimdall-api
 * ========================================================================
 *
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

package br.com.heimdall.gateway.task;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.heimdall.core.entity.Interceptor;
import br.com.heimdall.core.enums.TypeInterceptor;
import br.com.heimdall.core.repository.jdbc.InterceptorJDBCRepository;
import br.com.heimdall.core.util.StringUtils;
import br.com.heimdall.gateway.service.InterceptorFileService;

/**
 * @author @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Component
public class ScheduledInterceptors {

    @Autowired
    private InterceptorFileService interceptorFileService;

    @Autowired
    private InterceptorJDBCRepository interceptorJDBCRepository;

    @Value("${zuul.filter.root}")
    private String path;

    @Scheduled(fixedRateString = "${heimdall.interceptor.health.fixedRate}")
    public void checkFilesInterceptors() {
        List<Interceptor> interceptors = interceptorJDBCRepository.findAllInterceptorsSimplified();

        interceptors.forEach(interceptor -> {
            if (!checkInterceptorInDisk(interceptor)) {
                interceptorFileService.createFileInterceptor(interceptor);
            }
        });
    }

    private boolean checkInterceptorInDisk(Interceptor interceptor) {

        String filename = StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId().toString()) + ".groovy";
        String pathName = this.path;

        if (interceptor.getType() == TypeInterceptor.MIDDLEWARE) {
            pathName = pathName.concat(File.separator + "api" + File.separator + interceptor.getApi().getId());
        } else {
            pathName = pathName.concat(File.separator + interceptor.getExecutionPoint().getFilterType());
        }

        pathName = pathName.concat(File.separator + filename);
        File file = new File(pathName);
        
        return file.exists();
    }

}
