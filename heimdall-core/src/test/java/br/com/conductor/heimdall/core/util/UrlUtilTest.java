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

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;

public class UrlUtilTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetCurrentUrl() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo");
        request.setQueryString("param1=value1&param");

        Assert.assertNull(UrlUtil.getCurrentUrl(null));

        Assert.assertEquals("http://localhost/foo?param1=value1&param",
                UrlUtil.getCurrentUrl(request));
        Assert.assertEquals("http://localhost",
                UrlUtil.getCurrentUrl(new MockHttpServletRequest()));
    }

    @Test
    public void testGetUrl() throws MalformedURLException {
        Assert.assertEquals(new URL("https://www.foo.com"),
                UrlUtil.getUrl("https://www.foo.com"));

        thrown.expect(IllegalStateException.class);
        UrlUtil.getUrl("foo");
    }
}
