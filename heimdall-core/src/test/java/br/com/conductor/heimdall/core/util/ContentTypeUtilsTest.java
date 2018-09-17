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
package br.com.conductor.heimdall.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContentTypeUtilsTest {

     @Test
     public void permitEmptyContentTypeInBlackList() {
          String contentType = "";
          
          String[] types = contentType.split(";");
          
          boolean belongsToBlackList = ContentTypeUtils.belongsToBlackList(types);
          
          assertEquals(false, belongsToBlackList);
     }
     
     @Test
     public void blockPdfContentType() {
          String contentType = "application/pdf;charset=UTF-8";
          
          String[] types = contentType.split(";");
          
          boolean belongsToBlackList = ContentTypeUtils.belongsToBlackList(types);
          
          assertEquals(true, belongsToBlackList);
     }
     
     @Test
     public void permitJSONContentTypeInBlackList() {
          String contentType = "application/json";
          
          String[] types = contentType.split(";");
          
          boolean belongsToBlackList = ContentTypeUtils.belongsToBlackList(types);
          
          assertEquals(false, belongsToBlackList);
     }
     
}
