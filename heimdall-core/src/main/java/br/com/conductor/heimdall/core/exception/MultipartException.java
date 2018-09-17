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
package br.com.conductor.heimdall.core.exception;

/**
 * This class represents the exception related to errors with MultipartFile
 *
 * @author Dijalma Silva
 *
 * @see HeimdallException
 */
public class MultipartException extends HeimdallException{

    /**
     * Creates a new Heimdall Exception.
     *
     * @param exceptionMessage {@link ExceptionMessage}
     */
    public MultipartException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }
}
