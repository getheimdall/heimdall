package br.com.heimdall.core.exception;

import org.junit.Test;

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


/**
 *
 * @author <a href="https://github.com/felipe-brito" target="_blank" >Felipe Brito</a>
 * 
 */
public class ExceptionMessageTest {
    
    @Test(expected = BadRequestException.class)
    public void throwBadRequestException(){
        ExceptionMessage.GLOBAL_JSON_INVALID_FORMAT.raise();
    }
    
    @Test(expected = UnauthorizedException.class)
    public void throwUnauthorizedException(){
        ExceptionMessage.PROVIDER_USER_UNAUTHORIZED.raise();
    }
    
    @Test(expected = ForbiddenException.class)
    public void throwForbiddenException(){
        ExceptionMessage.DEFAULT_PROVIDER_CAN_NOT_UPDATED_OR_REMOVED.raise();
    }
    
    @Test(expected = NotFoundException.class)
    public void throwNotFoundException(){
        ExceptionMessage.GLOBAL_REQUEST_NOT_FOUND.raise();
    }
    
    @Test(expected = TimeoutException.class)
    public void throwTimeoutException(){
        ExceptionMessage.GLOBAL_TIMEOUT.raise();
    }
    
    @Test(expected = ServerErrorException.class)
    public void throwServerErrorException(){
        ExceptionMessage.GLOBAL_ERROR_ZUUL.raise();
    }
    
}
