package br.com.conductor.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2019 Conductor Tecnologia SA
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

public class DigestUtilsTest {

    @Test
    public void testDigestMD5() {
        Assert.assertNull(DigestUtils.digestMD5(null));

        Assert.assertEquals("", DigestUtils.digestMD5(""));
        Assert.assertEquals("acbd18db4cc2f85cedef654fccc4a4d8",
                DigestUtils.digestMD5("foo"));
    }
}
