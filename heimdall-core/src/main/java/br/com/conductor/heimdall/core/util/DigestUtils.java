
package br.com.conductor.heimdall.core.util;

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

import br.com.twsoftware.alfred.object.Objeto;

/**
 * DigestMD5 wrapper class
 * 
 * @author Marcos Filho
 *
 */
public abstract class DigestUtils {

	 /**
	  * Return a hexadecimal string representation of the MD5 digest of the given bytes.
	  * 
	  * @param  value		The value to be converted
	  * @return				The converted value. Returns the input value if its blank.
	  */
     public static String digestMD5(String value) {

          if (Objeto.notBlank(value)) {

               return org.springframework.util.DigestUtils.md5DigestAsHex(value.getBytes());
          } else {

               return value;
          }
     }
}
