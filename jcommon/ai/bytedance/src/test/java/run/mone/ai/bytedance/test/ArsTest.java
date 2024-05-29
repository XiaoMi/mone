package run.mone.ai.bytedance.test;

import org.junit.Test;
import run.mone.ai.bytedance.ArsClientOut;
import run.mone.ai.bytedance.ArsRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ArsTest {

    @Test
    public void testCall() {

        try {
            ArsRequest request = new ArsRequest();
            request.setAppId("");  // 项目的 appid
            request.setToken("");  // 项目的 token
            request.setCluster(""); // 请求的集群
            String audio_path = "audio.mp3";  // 本地音频文件路径；

            File file = new File(audio_path);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            byte[] fileContent = outputStream.toByteArray();

            request.setAudio(fileContent);
            String s = ArsClientOut.callArsClient(request);
            System.out.println(s);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
