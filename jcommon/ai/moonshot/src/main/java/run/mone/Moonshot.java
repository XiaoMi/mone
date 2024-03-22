package run.mone;


import com.google.gson.Gson;
import okhttp3.*;
import run.mone.bo.ChatCompletion;
import run.mone.bo.Message;
import run.mone.bo.RequestBodyContent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/3/26 15:27
 */
public class Moonshot {


    //okhttp get 请求,网址:https://api.moonshot.cn/v1/files
    public static String getFiles() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + System.getenv("moonshot"))
                .url("https://api.moonshot.cn/v1/files")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //okhttp post 上传文件 网址:https://api.moonshot.cn/v1/files python参考代码:file_object = client.files.create(file=Path("xlnet.pdf"), purpose="file-extract")
    public static String uploadFile(Path filePath, String purpose) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", filePath.getFileName().toString(),
                        RequestBody.create(mediaType, filePath.toFile()))
                .addFormDataPart("purpose", purpose)
                .build();
        Request request = new Request.Builder()
                .url("https://api.moonshot.cn/v1/files")
                .post(body)
                .header("Authorization", "Bearer " + System.getenv("moonshot"))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload failed: " + e.getMessage();
        }
    }

    //okhttp 删除文件,网址:DELETE https://api.moonshot.cn/v1/files/{file_id} (class)
    public static String deleteFile(String fileId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.moonshot.cn/v1/files/" + fileId)
                .delete()
                .header("Authorization", "Bearer " + System.getenv("moonshot"))
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Delete failed: " + e.getMessage();
        }
    }

    //okhttp 获取文件内容 网址GET https://api.moonshot.cn/v1/files/{file_id}/content (class)
    public static String getFileContent(String fileId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + System.getenv("moonshot"))
                .url("https://api.moonshot.cn/v1/files/" + fileId + "/content")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Get file content failed: " + e.getMessage();
        }
    }

    //okhttp 获取模型列表 GET https://api.moonshot.cn/v1/models (class)
    public static String getModels() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + System.getenv("moonshot"))
                .url("https://api.moonshot.cn/v1/models")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Get models failed: " + e.getMessage();
        }
    }

    public static ChatCompletion call(List<Message> messageList) {
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.MINUTES).build();
        // 设置请求体的内容类型和内容
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBodyContent requestBodyContent = new RequestBodyContent("moonshot-v1-8k", messageList, 0.3);

        Gson gson = new Gson();
        String jsonRequestBody = gson.toJson(requestBodyContent);

        // 构建请求
        Request request = new Request.Builder()
                .url("https://api.moonshot.cn/v1/chat/completions")
                .post(RequestBody.create(mediaType, jsonRequestBody))
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + System.getenv("moonshot"))
                .build();

        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            // 打印响应体的内容
            String str = response.body().string();
            return new Gson().fromJson(str, ChatCompletion.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
