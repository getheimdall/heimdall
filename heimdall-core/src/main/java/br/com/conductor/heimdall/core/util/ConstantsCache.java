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

/**
 * This class holds the cache constants.
 *
 * @author Marcelo Aguiar Rodrigues
 */
public final class ConstantsCache {

    private ConstantsCache() {
    }

    public static final String CACHE_TIME_TO_LIVE = "CACHE_TIME_TO_LIVE";

    public static final String CACHE_BUCKET = "CACHE_BUCKET";

    public static final String RATE_LIMIT_KEY_PREFIX = "ratelimit-interceptor:";

    public static final int RATE_LIMIT_DATABASE = 2;

    public static final int CACHE_INTERCEPTOR_DATABASE = 4;
}
