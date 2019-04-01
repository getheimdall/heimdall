
package br.com.conductor.heimdall.core.enums;

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

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.interceptor.HeimdallInterceptor;
import br.com.conductor.heimdall.core.interceptor.impl.*;
import lombok.Getter;

/**
 * Enum that hold the types of {@link Interceptor}.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 *
 */
@Getter
public enum TypeInterceptor {

	MOCK(new MockHeimdallInterceptor()),
	RATTING(new RattingHeimdallInterceptor()),
	ACCESS_TOKEN(new AccessTokenHeimdallInterceptor()),
	CLIENT_ID(new ClientIdHeimdallInterceptor()),
	CUSTOM(new CustomHeimdallInterceptor()),
	MIDDLEWARE(new MiddlewareHeimdallInterceptor()),
	OAUTH(new OAuthHeimdallInterceptor()),
	BLACKLIST(new BlacklistHeimdallInterceptor()),
	WHITELIST(new WhitelistHeimdallInterceptor()),
	CACHE(new CacheHeimdallInterceptor()),
	CACHE_CLEAR(new CacheClearHeimdallInterceptor()),
	IDENTIFIER(new IdentifierHeimdallInterceptor()),
	LOG_MASKER(new LogMaskerHeimdallInterceptor()),
	CORS(new CORSHeimdallInterceptor());

	private HeimdallInterceptor heimdallInterceptor;

	TypeInterceptor(HeimdallInterceptor heimdallInterceptor) {
		this.heimdallInterceptor = heimdallInterceptor;
	}

}
