package run.mone.neo4j;

import com.google.gson.JsonObject;
import lombok.Setter;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/8/21 13:50
 */
public class BotCall {

    //调用bot的地址
    @Setter
    private static String url = "";


    public static String call(String desc, String input) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // 使用 Gson 的 JsonObject 构建请求体
        JsonObject mainObject = new JsonObject();
        mainObject.addProperty("userName", "");
        mainObject.addProperty("botId", "");
        mainObject.addProperty("input", "");

        JsonObject paramsObject = new JsonObject();
        paramsObject.addProperty("desc", desc);
        paramsObject.addProperty("input", input);
        mainObject.add("params", paramsObject);

        // 将 JsonObject 转换为字符串
        String jsonBody = mainObject.toString();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
