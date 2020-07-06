/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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
package br.com.conductor.heimdall.api.util;

import br.com.conductor.heimdall.core.entity.Middleware;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Provides a method to zip a Middleware file
 *
 * @author Marcelo Aguiar Rodrigues
 */
public final class ZipUtils {

    private ZipUtils() { }

    /**
     * Returns a zipped middleware file.
     *
     * @param middleware {@link Middleware}
     * @return zipped middleware file as a byte array
     */
    public static byte[] zipFiles(Middleware middleware) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
             ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {

            String fileName = middleware.getName() + "." + middleware.getType();
            zipOutputStream.putNextEntry(new ZipEntry(fileName));

            File file = new File(middleware.getPath() + File.separator + "downloaded" + File.separator + fileName);
            FileUtils.writeByteArrayToFile(file, middleware.getFile());
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();

            zipOutputStream.finish();
            zipOutputStream.flush();
            return byteArrayOutputStream.toByteArray();

        } catch (IOException ignored) {
            // This exception should be ignored.
        }

        return new byte[0];
    }

}
