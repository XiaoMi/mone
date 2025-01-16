package run.mone.m78.common.youdao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileUtil {

    public static String loadMediaAsBase64(String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        byte[] temp = new byte[1024 * 1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int l = 0;
        while ((l = fileInputStream.read(temp)) != -1) {
            bos.write(temp, 0, l);
        }
        fileInputStream.close();
        bos.close();
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    public static String saveFile(String path, byte[] data, boolean needDecode) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        byte[] bytes = data;
        if (needDecode) {
            String base64 = new String(data, StandardCharsets.UTF_8);
            bytes = Base64.getDecoder().decode(base64);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
        return path;
    }
}
