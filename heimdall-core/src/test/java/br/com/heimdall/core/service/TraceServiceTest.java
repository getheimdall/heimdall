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

package br.com.heimdall.core.service;

import br.com.heimdall.core.dto.PageableDTO;
import br.com.heimdall.core.dto.logs.FiltersDTO;
import br.com.heimdall.core.dto.logs.LogTraceDTO;
import br.com.heimdall.core.dto.page.LogTraceDTOPage;
import br.com.heimdall.core.entity.LogTrace;
import br.com.heimdall.core.trace.Trace;
import br.com.heimdall.core.trace.RequestResponseParser;
import br.com.heimdall.core.util.MongoLogConnector;
import br.com.heimdall.core.util.Page;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 **/
@RunWith(MockitoJUnitRunner.class)
public class TraceServiceTest {

    @InjectMocks
    private TraceService traceService;

    @Mock
    private MongoLogConnector mongoLogConnector;

    private LogTrace logTrace;

    @Before
    public void before() {

        Trace trace = new Trace();
        trace.setAccessToken("4cc3ss-t0k3n");
        trace.setApiId(1L);
        trace.setApiName("Api");
        trace.setApp("App");
        trace.setAppDeveloper("App Developer");
        trace.setCache(true);
        trace.setClientId("cl13nt3-1d");
        trace.setDurationMillis(300L);
        trace.setFilters(new HashMap<>());
        trace.setMethod("GET");
        trace.setVersion("1.0.0");
        trace.setUrl("http://localhost:8080/api");
        trace.setTraces(new ArrayList<>());
        trace.setOperationId(1L);
        trace.setProfile("dev");
        trace.setReceivedFromAddress("http://127.0.0.1:433/");
        trace.setResultStatus(200);
        trace.setResourceId(1L);
        trace.setRequest(new RequestResponseParser());
        trace.setResponse(new RequestResponseParser());

        logTrace = new LogTrace();
        logTrace.setId(new ObjectId());
        logTrace.setLevel("INFO");
        logTrace.setLogger("logger-trace");
        logTrace.setThread("thread-trace");
        logTrace.setTs(new Date());
        logTrace.setTrace(trace);
    }

    @Test
    public void findById() {

        Mockito.when(mongoLogConnector.findOne(Mockito.anyObject())).thenReturn(logTrace);

        LogTraceDTO actual = traceService.findById(logTrace.getId().toString());

        assertEquals(logTrace.getTrace(), actual.getTrace());
    }

    @Test
    public void find() {
        Page<LogTrace> page = new Page<>();
        List<LogTrace> logTraces = new ArrayList<>();
        logTraces.add(logTrace);
        page.setContent(logTraces);
        page.setHasContent(true);
        page.setHasNextPage(false);
        page.setTotalElements(1);
        page.setTotalPages(1);
        page.setNumberOfElements(1);

        Mockito.when(mongoLogConnector.find(Mockito.anyListOf(FiltersDTO.class), Mockito.anyInt(), Mockito.anyInt())).thenReturn(page);
        PageableDTO pageableDTO = new PageableDTO();
        pageableDTO.setOffset(0);
        pageableDTO.setLimit(10);
        LogTraceDTOPage logTraceDTOPage = traceService.find(new ArrayList<>(), pageableDTO);

        assertEquals(logTraces.get(0).getTrace(), logTraceDTOPage.getContent().get(0).getTrace());
    }
}
