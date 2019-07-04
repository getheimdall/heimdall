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

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.http.MediaType;

/**
 * This class provides a method to verify if any content is part of the blacklisted types.
 *
 * @author Marcos Filho
 *
 */
public final class ContentTypeUtils {

	private ContentTypeUtils() { }

	/**
	  * The list of blacklisted types
	  */
	 private static final String[] blackList = new String[] { 
    		 MediaType.APPLICATION_PDF_VALUE,
    		 MediaType.IMAGE_GIF_VALUE,
    		 MediaType.IMAGE_JPEG_VALUE,
    		 MediaType.IMAGE_PNG_VALUE };

     /**
      * Checks if any of a list of files is part of the blacklisted types.
      * 
      * @param  content			The strings to be tested.
      * @return					boolean value if any of the types is blacklisted
      */
     public static boolean belongsToBlackList(String... content) {
          if (ArrayUtils.isEmpty(content)) return false;
          
          return Arrays.stream(blackList).anyMatch(Arrays.asList(content)::contains);
     }
}
