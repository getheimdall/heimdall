/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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
package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.enums.Periods;
import br.com.conductor.heimdall.core.dto.metrics.Metric;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.service.MetricsService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_METRICS;

/**
 * @author Marcelo Aguiar Rodrigues
 */
@io.swagger.annotations.Api(value = PATH_METRICS, produces = MediaType.APPLICATION_JSON_VALUE, tags = {ConstantsTag.TAG_METRICS})
@RestController
@RequestMapping(value = PATH_METRICS)
public class MetricsResource {

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private Property property;

    @ResponseBody
    @ApiOperation(value = "Apps", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/apps-top")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> appsTop(Integer limit, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findByTopApps(limit, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Apis", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/apis-top")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> apisTop(Integer limit, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findByTopApis(limit, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Access Tokens", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/access-tokens-top")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> accessTokenTop(Integer limit, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findByTopAccessTokens(limit, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Result status", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/result-status-top")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> resultStatusTop(Integer limit, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findByTopResultStatus(limit, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Lists result status for an App", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/apps")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> apps(@RequestParam String app, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findMetricAppPerResultStatus(app, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Lists result status for an Api", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/apis")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> apis(@RequestParam String apiName, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findMetricApiPerResultStatus(apiName, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Lists result status for an Access Token", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/access-tokens")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> accessTokens(@RequestParam String accessToken, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findMetricAccessTokenPerResultStatus(accessToken, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Average App response time", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/apps-latency")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> appsAvgDuration(@RequestParam String app, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findMetricAppPerAvgResponseTime(app, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Average Api response time", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/apis-latency")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> apisAvgDuration(@RequestParam String apiName, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findMetricApiPerAvgResponseTime(apiName, period);

        return ResponseEntity.ok(response);
    }

    @ResponseBody
    @ApiOperation(value = "Average Access Token response time", responseContainer = "List", response = Metric.class)
    @GetMapping(value = "/access-tokens-latency")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_METRICS)
    public ResponseEntity<?> accessTokensAvgDuration(@RequestParam String accessToken, @RequestParam Periods period) {

        if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        final List<Metric> response = metricsService.findMetricAccessTokenPerAvgResponseTime(accessToken, period);

        return ResponseEntity.ok(response);
    }

}
