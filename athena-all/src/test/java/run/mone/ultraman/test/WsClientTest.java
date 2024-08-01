package run.mone.ultraman.test;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.ultraman.http.WsClient;

/**
 * @author goodjava@qq.com
 * @date 2024/5/30 13:38
 */
public class WsClientTest {

    @SneakyThrows
    @Test
    public void test1() {
        WsClient wc = new WsClient();
        wc.init(msg -> {
            System.out.println(msg);
        });
        wc.send(new JsonObject());
        System.in.read();
    }

    @SneakyThrows
    public static void main(String[] args) {
        WsClient wc = new WsClient();
        wc.init(msg->{
            System.out.println(msg);
        });

        JsonObject req = new JsonObject();

        //req.addProperty("cmd","ping");

        req.addProperty("botId", "");
        req.addProperty("input", "System.out.println(123);");
        req.addProperty("topicId", "123");


        wc.send(req);

        System.in.read();
    }

}
