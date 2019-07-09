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

import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;

/**
 * This class provides a methods to format {@link String}s to be used by Heimdall.
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */

public final class StringUtils {

    private StringUtils() {
    }

    /**
     * Creates a String that represents a order. It adds leading zeros ("0").
     *
     * @param prefix The number to be prefixed
     * @param order  The order
     * @return The prefixed order created
     */
    public static String generateOrder(Integer prefix, Integer order) {

        HeimdallException.checkThrow(order > 99, ExceptionMessage.INTERCEPTOR_LIMIT_REACHED);

        int value = prefix * 100 + order;

        return Integer.toString(value);
    }

    /**
     * Converts multiple parameters of type String from UPPER_SPLIT case to camelCase and concatenate them.
     *
     * @param strings Multiple String parameters
     * @return The concatenated String
     */
    public static String concatCamelCase(String... strings) {

        StringBuilder value = new StringBuilder();
        for (String string : strings) {

            String[] splits = string.split("_");
            for (String split : splits) {

                if (split != null && !split.isEmpty()) {

                    value.append(split.substring(0, 1).toUpperCase()).append(split.substring(1).toLowerCase());
                }
            }
        }

        return value.toString();
    }

    /**
     * Removes all instances of double forward slash from a path and
     * makes sure that there is one forward slash at the start of the
     * path.
     *
     * @param path Path to be parsed
     * @return The path with one forward slash at the start and
     * single forward slashes where there were double
     */
    public static String removeMultipleSlashes(String path) {
        path = "/" + path;
        return path.replaceAll("//+", "/");
    }
}
