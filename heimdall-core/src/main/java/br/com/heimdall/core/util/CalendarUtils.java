
package br.com.heimdall.core.util;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for calendar operations
 *
 * @author Marcelo Aguiar
 *
 */
public final class CalendarUtils {

    private CalendarUtils() { }

    private static final int MONDAY = 1;
    private static final int SUNDAY = 7;

    /**
     * Returns the first and last day of a week considering a week starts on a Monday and ends on a Sunday.
     *
     * @param date Reference date
     * @return Map with first and last day of week
     */
    public static Map<String, LocalDate> firstAndLastDaysOfWeek(LocalDate date) {

        int today = date.getDayOfWeek().getValue();

        Map<String, LocalDate> week = new HashMap<>();

        week.put("first", date.minusDays(today == MONDAY ? 0 : today - MONDAY));
        week.put("last", date.plusDays(today == SUNDAY ? 0 : SUNDAY - today));

        return week;
    }

    /**
     * Returns the year and month of a reference date.
     *
     * @param date Reference date
     * @return String yyyy-mm
     */
    public static String yearAndMonth(LocalDate date) {

        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    /**
     * Returns the year of a reference date.
     *
     * @param date Reference date
     * @return String yyyy
     */
    public static String year(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy"));
    }

}
