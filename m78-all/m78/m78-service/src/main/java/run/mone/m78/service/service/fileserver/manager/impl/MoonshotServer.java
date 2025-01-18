package run.mone.m78.service.service.fileserver.manager.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import run.mone.m78.service.service.fileserver.manager.IFileServer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MoonshotServer implements IFileServer {

    private static final String MoonshotKey = "sk-6yRec7Pv9Fpm2d1SH7nJ5HKEAk9vrrppvwyM0Qk7nwGAAKtk";

    private static final String MoonshotHost = "https://api.moonshot.cn";

    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(200, 5, TimeUnit.MINUTES))
            .readTimeout(100000, TimeUnit.MILLISECONDS)
            .writeTimeout(50000, TimeUnit.MILLISECONDS)
            .connectTimeout(50000, TimeUnit.MILLISECONDS).build();

    @Override
    public String uploadFile(String key, File file, int expireSeconds, boolean inInner) {
        log.info("MoonshotServer#uploadFile key={}", key);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .addFormDataPart("purpose", "file-extract")
                .build();

        Request request = new Request.Builder()
                .url(MoonshotHost + "/v1/files")
                .header("Authorization", "Bearer " + MoonshotKey)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // 假设响应体包含上传文件的ID或URL
            String responseBody = response.body().string();
            log.info("File uploaded successfully. Response: {}", responseBody);
            // 返回响应体，可能需要进一步处理以提取文件ID或URL
            return responseBody;
        } catch (IOException e) {
            log.error("Error uploading file", e);
            return "";  // 或者抛出异常，取决于你的错误处理策略
        }
    }

    @Override
    public byte[] downloadFile(String downloadKey) {
        log.info("MoonshotServer#downloadFile key={}", downloadKey);
        // 构建 GET 请求
        Request request = new Request.Builder()
                .url(MoonshotHost + "/v1/files/" + downloadKey + "/content")
                .header("Authorization", "Bearer " + MoonshotKey)
                .get()  // 使用 GET 请求方法
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to download file. Response code: {}", response.code());
                return null;  // 下载失败
            }

            // 读取响应体为字节数组
            byte[] fileContent = response.body().bytes();
            log.info("File downloaded successfully. Size: {} bytes", fileContent.length);
            return fileContent;
        } catch (IOException e) {
            log.error("Error downloading file", e);
            return null;  // 处理异常时返回 null
        }
    }

    @Override
    public boolean deleteFile(String key) {
        log.info("MoonshotServer#deleteFile key={}", key);

        // 构建 DELETE 请求
        Request request = new Request.Builder()
                .url(MoonshotHost + "/v1/files/" + key)
                .header("Authorization", "Bearer " + MoonshotKey)
                .delete()  // 使用 DELETE 请求方法
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to delete file. Response code: {}", response.code());
                return false;  // 删除失败
            }

            // 假设删除成功时响应码为 204 No Content 或其他标识删除成功的状态码
            log.info("File deleted successfully. Response: {}", response.body().string());
            return true;  // 删除成功
        } catch (IOException e) {
            log.error("Error deleting file", e);
            return false;  // 处理异常时返回失败
        }
    }

}
