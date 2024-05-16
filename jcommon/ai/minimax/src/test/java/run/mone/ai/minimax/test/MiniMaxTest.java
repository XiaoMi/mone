package run.mone.ai.minimax.test;

import com.google.gson.JsonObject;
import org.junit.Test;
import run.mone.ai.minimax.MiniMax;

public class MiniMaxTest {

    @Test
    public void testCall() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("voice_id", "male-qn-qingse");
            json.addProperty("text", "你好，北京今天天气很好!");
            json.addProperty("model", "speech-01");
            String content = json.toString();
            MiniMax.call_Text_To_Speech("", "", content);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
