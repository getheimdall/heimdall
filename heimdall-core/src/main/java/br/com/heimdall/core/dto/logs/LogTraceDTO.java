
package br.com.heimdall.core.dto.logs;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import br.com.heimdall.core.entity.LogTrace;
import br.com.heimdall.core.trace.Trace;
import lombok.Data;

import java.util.Date;

/**
 * This class is a Data Transfer Object for the {@link LogTrace}.
 *
 * @author Marcelo Aguiar
 *
 */
@Data
public class LogTraceDTO {

    private String id;

    private Trace trace;

    private String logger;

    private String level;

    private String thread;

    private Date ts;

    public LogTraceDTO(LogTrace logTrace) {
        this.id = logTrace.getId().toString();
        this.trace = logTrace.getTrace();
        this.logger = logTrace.getLogger();
        this.level = logTrace.getLevel();
        this.thread = logTrace.getThread();
        this.ts = logTrace.getTs();
    }
}
