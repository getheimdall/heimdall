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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.dto.metrics.Metric;
import br.com.conductor.heimdall.core.enums.Periods;
import br.com.conductor.heimdall.core.util.MongoLogConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides the metrics from the logs that Heimdall saves in mongodb.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class MetricsService {

    @Autowired
    private MongoLogConnector mongoLogConnector;

    /**
     * Finds the top n apps from a period
     *
     * @param limit  max number of elements to return
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findByTopApps(Integer limit, Periods period) {
        limit = validateLimit(limit);

        return this.findTop("trace.app", limit, period);
    }

    /**
     * Finds the top n apis from a period
     *
     * @param limit  max number of elements to return
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findByTopApis(Integer limit, Periods period) {
        limit = validateLimit(limit);

        return this.findTop("trace.apiName", limit, period);
    }

    /**
     * Finds the top n access tokens from a period
     *
     * @param limit  max number of elements to return
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findByTopAccessTokens(Integer limit, Periods period) {
        limit = validateLimit(limit);

        return this.findTop("trace.accessToken", limit, period);
    }

    /**
     * Finds the top n result status from a period
     *
     * @param limit  max number of elements to return
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findByTopResultStatus(Integer limit, Periods period) {
        limit = validateLimit(limit);

        return this.findTop("trace.resultStatus", limit, period);
    }


    /**
     * Finds a list of sums of result status per Api per period
     *
     * @param id     Api name
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findMetricApiPerResultStatus(String id, Periods period) {
        return this.findMetricXperSumY(id, "trace.apiName", "trace.resultStatus", period);
    }

    /**
     * Finds a list of sums of result status per Api per period
     *
     * @param id     Api name
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findMetricApiPerOperation(String id, Periods period) {
        return this.findMetricXperSumY(id, "trace.apiName", "trace.pattern", period);
    }

    /**
     * Finds a list of sums of result status per Access Token per period
     *
     * @param id     Access Token
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findMetricAccessTokenPerResultStatus(String id, Periods period) {
        return this.findMetricXperSumY(id, "trace.accessToken", "trace.resultStatus", period);
    }

    /**
     * Finds a list of sums of result status per App per period
     *
     * @param id     App name
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findMetricAppPerResultStatus(String id, Periods period) {
        return this.findMetricXperSumY(id, "trace.app", "trace.resultStatus", period);
    }


    /**
     * Finds a list of averages of result status per App per period
     *
     * @param id     App name
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findMetricAppPerAvgResponseTime(String id, Periods period) {
        return this.findMetricXperAvgY(id, "trace.app", "trace.durationMillis", period);
    }

    /**
     * Finds a list of averages of result status per Api per period
     *
     * @param id     Api name
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findMetricApiPerAvgResponseTime(String id, Periods period) {
        return this.findMetricXperAvgY(id, "trace.apiName", "trace.durationMillis", period);
    }

    /**
     * Finds a list of averages of result status per Access Token per period
     *
     * @param id     Access Token
     * @param period {@link Periods}
     * @return list of metrics found
     */
    public List<Metric> findMetricAccessTokenPerAvgResponseTime(String id, Periods period) {
        return this.findMetricXperAvgY(id, "trace.accessToken", "trace.durationMillis", period);
    }


    private List<Metric> findTop(String id, Integer limit, Periods period) {
        return mongoLogConnector.findByTop(id, limit, period);
    }

    private List<Metric> findMetricXperSumY(String id, String source, String metric, Periods period) {
        return mongoLogConnector.findByMetricBySum(id, source, metric, period);
    }

    private List<Metric> findMetricXperAvgY(String id, String source, String metric, Periods period) {
        return mongoLogConnector.findByMetricByAvg(id, source, metric, period);
    }

    private Integer validateLimit(Integer limit) {

        final int DEFAULT_LIMIT = 10;
        final int UPPER_BOUND = 50;
        final int LOWER_BOUND = 1;

        return (limit == null) ? DEFAULT_LIMIT : Math.max(LOWER_BOUND, Math.min(UPPER_BOUND, limit));
    }

}
