package org.arabellan.tdas.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileIO {

    public String loadFileAsString(String path) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(path);
        if (inputStream == null) throw new FileNotFoundException(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString(StandardCharsets.UTF_8.name());
    }

}
