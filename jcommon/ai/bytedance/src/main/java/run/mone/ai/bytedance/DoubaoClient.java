package run.mone.ai.bytedance;


import okhttp3.*;

/**
 * @author goodjava@qq.com
 * @date 2024/5/18 17:20
 */
public class DoubaoClient {

    private static String ARK_API_KEY = "";

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        String json = "{\n" +
                "    \"model\": \"\",\n" +
                "    \"messages\": [\n" +
                "      {\n" +
                "        \"role\": \"system\",\n" +
                "        \"content\": \"You are a helpful assistant.\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"role\": \"user\",\n" +
                "        \"content\": \"Hello!\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"stream\": false\n" +
                "}";

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url("")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + ARK_API_KEY) // 确保你已经设置了 ARK_API_KEY 变量
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // 处理响应
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println(responseBody);
            } else {
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
