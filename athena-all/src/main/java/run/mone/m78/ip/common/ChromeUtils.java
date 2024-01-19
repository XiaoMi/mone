package run.mone.m78.ip.common;

import com.google.gson.Gson;
import run.mone.m78.ip.bo.robot.AiChatMessage;
import lombok.SneakyThrows;

/**
 * @author goodjava@qq.com
 * @date 2023/6/2 16:44
 */
public class ChromeUtils {

    private static final Gson gson = new Gson();

    @SneakyThrows
    public static void call(String projectName, String method, String param, boolean useReq) {

    }


    public static void call(String projectName, String param, int code) {
        call(projectName, param, "", code);
    }

    public static void call(String projectName, String param, String sound, int code) {

    }

    public static void call(String projectName, AiChatMessage res) {

    }


    public static void call(String projectName, String method, String param) {
        call(projectName, method, param, false);
    }


}
