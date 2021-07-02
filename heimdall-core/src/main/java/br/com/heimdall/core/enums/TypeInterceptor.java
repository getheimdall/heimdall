
package br.com.heimdall.core.enums;

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

import br.com.heimdall.core.entity.Interceptor;
import br.com.heimdall.core.interceptor.HeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.*;
import br.com.heimdall.core.interceptor.impl.AccessTokenHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.BlacklistHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.CORSHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.CacheClearHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.CacheHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.ClientIdHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.CustomHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.IdentifierHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.LogMaskerHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.LogWriterHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.MiddlewareHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.MockHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.OAuthHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.RattingHeimdallInterceptor;
import br.com.heimdall.core.interceptor.impl.WhitelistHeimdallInterceptor;
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
	LOG_WRITER(new LogWriterHeimdallInterceptor()),
	CORS(new CORSHeimdallInterceptor()),

	;

	private HeimdallInterceptor heimdallInterceptor;

	TypeInterceptor(HeimdallInterceptor heimdallInterceptor) {
		this.heimdallInterceptor = heimdallInterceptor;
	}

}
