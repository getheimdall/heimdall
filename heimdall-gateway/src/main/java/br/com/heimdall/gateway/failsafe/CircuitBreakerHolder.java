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
package br.com.heimdall.gateway.failsafe;

import lombok.Data;
import net.jodah.failsafe.CircuitBreaker;

/**
 * Circuit Breaker entity
 *
 * @author Marcelo Rodrigues
 */
@Data
public class CircuitBreakerHolder {

    private CircuitBreaker circuitBreaker;

    private Throwable throwable;

    /**
     * Returns the message that of the error that cause the circuit to open.
     *
     * First tries to get the message from the cause of the {@link Throwable},
     * if it does not exist tries to get the message from the {@link Throwable}
     * itself.
     *
     * @return message of the exception that cause the circuit to open
     */
    public String getMessage() {
        if (this.throwable == null)
            return "No Exception captured";

        if (this.throwable.getCause() != null && this.throwable.getCause().getMessage() != null)
            return this.throwable.getCause().getMessage();

        if (this.throwable.getMessage() != null)
            return this.throwable.getMessage();

        return "No error message found";

    }
}
