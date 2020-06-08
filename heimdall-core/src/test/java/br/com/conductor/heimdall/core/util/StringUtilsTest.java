package br.com.conductor.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testGenerateOrder() {
        Assert.assertEquals("104", StringUtils.generateOrder(1, 4));
    }

    @Test
    public void testConcatCamelCase() {
        Assert.assertEquals("FooBarBaz",
                StringUtils.concatCamelCase("foo", "bar", "baz"));
        Assert.assertEquals("FOOBARBAZ",
                StringUtils.concatCamelCase("f_o_o", "b_a_r", "b_a_z"));
    }

    @Test
    public void testRemoveMultipleSlashes() {
        Assert.assertEquals("/foo/+bar/+baz",
                StringUtils.removeMultipleSlashes("foo//+bar//+baz"));
    }
}
