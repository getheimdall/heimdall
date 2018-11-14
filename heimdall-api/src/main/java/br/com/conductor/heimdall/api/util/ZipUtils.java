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
public class ZipUtils {

    /**
     * Returns a zipped middleware file.
     *
     * @param middleware {@link Middleware}
     * @return zipped middleware file as a byte array
     */
    public byte[] zipFiles(Middleware middleware) {

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

        } catch (IOException ignored) { }

        return new byte[0];
    }

}
