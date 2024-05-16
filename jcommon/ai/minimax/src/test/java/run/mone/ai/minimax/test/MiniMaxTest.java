package run.mone.ai.minimax.test;

import com.google.gson.JsonObject;
import org.junit.Test;
import run.mone.ai.minimax.MiniMax;
import run.mone.ai.minimax.bo.RequestBodyContent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MiniMaxTest {

    @Test
    public void testCall() {
        try {
            RequestBodyContent requestBodyContent = new RequestBodyContent();
            requestBodyContent.setText("你好，今天北京的天气非常晴朗");
            byte[] bytes = MiniMax.call_Text_To_Speech("", "", requestBodyContent);
            try (OutputStream outputStream = new FileOutputStream("audio.mp3")) {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
