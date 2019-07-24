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

import java.time.LocalDate;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class CalendarUtilsTest {

    @Test
    public void testFirstAndLastDaysOfWeek() {
        HashMap<String, LocalDate> week1 = new HashMap<>();
        week1.put("first", LocalDate.of(2019, 7, 8));
        week1.put("last", LocalDate.of(2019, 7, 14));

        HashMap<String, LocalDate> week2 = new HashMap<>();
        week2.put("first", LocalDate.of(2019, 7, 1));
        week2.put("last", LocalDate.of(2019, 7, 7));

        Assert.assertEquals(week1, CalendarUtils.firstAndLastDaysOfWeek(
                LocalDate.of(2019, 7, 8)));
        Assert.assertEquals(week2, CalendarUtils.firstAndLastDaysOfWeek(
                LocalDate.of(2019, 7, 7)));
    }

    @Test
    public void testYearAndMonth() {
        Assert.assertEquals("2019-02",
                CalendarUtils.yearAndMonth(LocalDate.of(2019, 2, 8)));
    }

    @Test
    public void testYear() {
        Assert.assertEquals("2019",
                CalendarUtils.year(LocalDate.of(2019, 2, 8)));
    }
}
