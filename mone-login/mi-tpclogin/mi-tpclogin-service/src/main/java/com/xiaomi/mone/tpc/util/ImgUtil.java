package com.xiaomi.mone.tpc.util;

import lombok.SneakyThrows;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImgUtil {

    @SneakyThrows
    public static String convertToBase64(String path) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(readImageFile(path));
    }

    private static byte[] readImageFile(String path) throws IOException {
        InputStream inputStream = ImgUtil.class.getClassLoader().getResourceAsStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        bos.close();

        return bos.toByteArray();
    }

}
