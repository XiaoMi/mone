package run.mone.hive.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.dto.WebSocketCallRequest;
import run.mone.hive.dto.WebSocketCallResponse;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * WebSocket 文件操作工具类
 * 通过 WebSocket 连接操作远程文件系统
 *
 * @author goodjava@qq.com
 * @date 2025/12/11
 */
@Slf4j
public class WebSocketFileUtils {

    /**
     * 等待响应的 Future 映射
     * key: requestId, value: CompletableFuture
     */
    private static final Map<String, CompletableFuture<String>> pendingRequests = new ConcurrentHashMap<>();

    /**
     * 默认超时时间（秒）
     */
    private static final int DEFAULT_TIMEOUT = 30;

    /**
     * WebSocket 消息发送器接口
     * 用于发送消息到 WebSocket 客户端
     */
    public interface WebSocketMessageSender {
        void sendMessage(String clientId, String message) throws IOException;
    }

    /**
     * WebSocket 消息发送器实例
     * 需要在运行时注入具体实现
     */
    private static WebSocketMessageSender messageSender;

    /**
     * 设置 WebSocket 消息发送器
     *
     * @param sender 消息发送器实现
     */
    public static void setMessageSender(WebSocketMessageSender sender) {
        messageSender = sender;
    }

    /**
     * 处理从客户端返回的响应
     * 这个方法应该在收到 WebSocket 消息时被调用
     *
     * @param responseJson 响应 JSON 字符串
     */
    public static void handleResponse(String responseJson) {
        try {
            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

            if (!response.has("requestId")) {
                log.warn("收到的响应缺少 requestId: {}", responseJson);
                return;
            }

            String requestId = response.get("requestId").getAsString();
            CompletableFuture<String> future = pendingRequests.remove(requestId);

            if (future != null) {
                if (response.has("error")) {
                    String error = response.get("error").getAsString();
                    future.completeExceptionally(new IOException(error));
                } else {
                    future.complete(responseJson);
                }
            } else {
                log.warn("未找到对应的请求 ID: {}", requestId);
            }
        } catch (Exception e) {
            log.error("处理 WebSocket 响应时发生异常", e);
        }
    }

    /**
     * 发送请求并等待响应
     *
     * @param clientId    客户端 ID
     * @param requestType 请求类型
     * @param params      请求参数
     * @param timeout     超时时间（秒）
     * @return 响应结果
     * @throws IOException          如果发送失败
     * @throws TimeoutException     如果等待超时
     * @throws InterruptedException 如果等待被中断
     */
    private static String sendAndWait(String clientId, String requestType, JsonObject params, int timeout)
            throws IOException, TimeoutException, InterruptedException {

        if (messageSender == null) {
            throw new IOException("WebSocket 消息发送器未初始化");
        }

        String requestId = UUID.randomUUID().toString();
        CompletableFuture<String> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        try {
            // 构建请求消息
            JsonObject request = new JsonObject();
            request.addProperty("requestId", requestId);
            request.addProperty("type", requestType);
            request.add("params", params);

            // 发送消息
            messageSender.sendMessage(clientId, request.toString());
            log.debug("发送 WebSocket 请求: clientId={}, requestId={}, type={}", clientId, requestId, requestType);

            // 等待响应
            try {
                return future.get(timeout, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof IOException) {
                    throw (IOException) cause;
                }
                throw new IOException("执行请求时发生异常: " + cause.getMessage(), cause);
            }
        } finally {
            // 清理未完成的请求
            pendingRequests.remove(requestId);
        }
    }

    /**
     * 列出远程文件或目录（旧版，保持向后兼容）
     *
     * @param clientId  客户端 ID
     * @param dirName   目录路径
     * @param recursive 是否递归
     * @return 文件列表结果
     * @throws IOException 如果操作失败
     */
    @Deprecated
    public static String listFiles(String clientId, String dirName, boolean recursive) throws IOException {
        WebSocketCallRequest request = WebSocketCallRequest.builder()
                .path(dirName)
                .recursive(recursive)
                .build();
        WebSocketCallResponse response = listFiles(clientId, request);
        return response.getResult();
    }

    /**
     * 列出远程文件或目录（新版，使用 DTO）
     *
     * @param clientId 客户端 ID
     * @param request  请求参数
     * @return 响应结果
     * @throws IOException 如果操作失败
     */
    public static WebSocketCallResponse listFiles(String clientId, WebSocketCallRequest request) throws IOException {
        try {
            // 将 DTO 转换为 JsonObject
            Gson gson = new Gson();
            String jsonStr = gson.toJson(request);
            JsonObject params = JsonParser.parseString(jsonStr).getAsJsonObject();

            String responseStr = sendAndWait(clientId, "list_files", params, DEFAULT_TIMEOUT);
            log.info("通过 WebSocket 成功列出文件: clientId={}, path={}", clientId, request.getPath());

            // 解析响应
            JsonObject responseJson = JsonParser.parseString(responseStr).getAsJsonObject();

            return WebSocketCallResponse.builder()
                    .success(true)
                    .clientId(clientId)
                    .directoryPath(request.getPath())
                    .recursive(request.getRecursive())
                    .mode("REMOTE_WS")
                    .result(responseStr)
                    .response(gson.fromJson(responseJson, Map.class))
                    .build();

        } catch (TimeoutException e) {
            throw new IOException("列出文件超时: " + request.getPath(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("列出文件被中断: " + request.getPath(), e);
        }
    }

    /**
     * 获取远程文件内容（旧版，保持向后兼容）
     *
     * @param clientId 客户端 ID
     * @param fileName 文件名
     * @return 文件内容
     * @throws IOException 如果获取失败
     */
    @Deprecated
    public static String getRemoteFileContent(String clientId, String fileName) throws IOException {
        WebSocketCallRequest request = WebSocketCallRequest.builder()
                .path(fileName)
                .build();
        WebSocketCallResponse response = readFile(clientId, request);
        return response.getResult();
    }

    /**
     * 读取远程文件（新版，使用 DTO）
     *
     * @param clientId 客户端 ID
     * @param request  请求参数
     * @return 响应结果
     * @throws IOException 如果读取失败
     */
    public static WebSocketCallResponse readFile(String clientId, WebSocketCallRequest request) throws IOException {
        try {
            // 将 DTO 转换为 JsonObject
            Gson gson = new Gson();
            String jsonStr = gson.toJson(request);
            JsonObject params = JsonParser.parseString(jsonStr).getAsJsonObject();

            String responseStr = sendAndWait(clientId, "read_file", params, DEFAULT_TIMEOUT);
            log.info("通过 WebSocket 成功读取文件: clientId={}, path={}", clientId, request.getPath());

            // 解析响应获取文件内容
            JsonObject responseJson = JsonParser.parseString(responseStr).getAsJsonObject();
            String content = responseJson.has("content") ? responseJson.get("content").getAsString() : responseStr;

            return WebSocketCallResponse.builder()
                    .success(true)
                    .clientId(clientId)
                    .mode("REMOTE_WS")
                    .result(content)
                    .response(gson.fromJson(responseJson, Map.class))
                    .build();

        } catch (TimeoutException e) {
            throw new IOException("读取文件超时: " + request.getPath(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("读取文件被中断: " + request.getPath(), e);
        }
    }

    /**
     * 上传文件到远程服务器（旧版，保持向后兼容）
     *
     * @param clientId    客户端 ID
     * @param fileName    文件名
     * @param fileContent 文件内容
     * @return 上传结果
     * @throws IOException 如果上传失败
     */
    @Deprecated
    public static String uploadFile(String clientId, String fileName, String fileContent) throws IOException {
        WebSocketCallRequest request = WebSocketCallRequest.builder()
                .path(fileName)
                .content(fileContent)
                .build();
        WebSocketCallResponse response = writeFile(clientId, request);
        return response.getResult();
    }

    /**
     * 写入远程文件（新版，使用 DTO）
     *
     * @param clientId 客户端 ID
     * @param request  请求参数
     * @return 响应结果
     * @throws IOException 如果写入失败
     */
    public static WebSocketCallResponse writeFile(String clientId, WebSocketCallRequest request) throws IOException {
        try {
            // 将 DTO 转换为 JsonObject
            Gson gson = new Gson();
            String jsonStr = gson.toJson(request);
            JsonObject params = JsonParser.parseString(jsonStr).getAsJsonObject();

            String responseStr = sendAndWait(clientId, "write_file", params, DEFAULT_TIMEOUT);
            log.info("通过 WebSocket 成功写入文件: clientId={}, path={}", clientId, request.getPath());

            // 解析响应
            JsonObject responseJson = JsonParser.parseString(responseStr).getAsJsonObject();
            String resultMsg = responseJson.has("message") ? responseJson.get("message").getAsString() : "文件写入成功";

            return WebSocketCallResponse.builder()
                    .success(true)
                    .clientId(clientId)
                    .mode("REMOTE_WS")
                    .result(resultMsg)
                    .response(gson.fromJson(responseJson, Map.class))
                    .build();

        } catch (TimeoutException e) {
            throw new IOException("写入文件超时: " + request.getPath(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("写入文件被中断: " + request.getPath(), e);
        }
    }

    /**
     * 搜索远程文件（旧版，保持向后兼容）
     *
     * @param clientId      客户端 ID
     * @param directoryPath 目录路径
     * @param regex         正则表达式
     * @param filePattern   文件模式
     * @return 搜索结果
     * @throws IOException 如果搜索失败
     */
    @Deprecated
    public static String searchFiles(String clientId, String directoryPath, String regex, String filePattern) throws IOException {
        WebSocketCallRequest request = WebSocketCallRequest.builder()
                .path(directoryPath)
                .regex(regex)
                .filePattern(filePattern)
                .build();
        WebSocketCallResponse response = searchFiles(clientId, request);
        return response.getResult();
    }

    /**
     * 搜索远程文件（新版，使用 DTO）
     *
     * @param clientId 客户端 ID
     * @param request  请求参数
     * @return 响应结果
     * @throws IOException 如果搜索失败
     */
    public static WebSocketCallResponse searchFiles(String clientId, WebSocketCallRequest request) throws IOException {
        try {
            // 将 DTO 转换为 JsonObject
            Gson gson = new Gson();
            String jsonStr = gson.toJson(request);
            JsonObject params = JsonParser.parseString(jsonStr).getAsJsonObject();

            String responseStr = sendAndWait(clientId, "search_files", params, DEFAULT_TIMEOUT);
            log.info("通过 WebSocket 成功搜索文件: clientId={}, path={}", clientId, request.getPath());

            return WebSocketCallResponse.builder()
                    .success(true)
                    .clientId(clientId)
                    .mode("REMOTE_WS")
                    .result(responseStr)
                    .response(gson.fromJson(JsonParser.parseString(responseStr).getAsJsonObject(), Map.class))
                    .build();

        } catch (TimeoutException e) {
            throw new IOException("搜索文件超时: " + request.getPath(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("搜索文件被中断: " + request.getPath(), e);
        }
    }

    /**
     * 删除远程文件
     *
     * @param clientId 客户端 ID
     * @param fileName 要删除的文件名
     * @return 删除结果
     * @throws IOException 如果删除失败
     */
    public static String deleteFile(String clientId, String fileName) throws IOException {
        try {
            JsonObject params = new JsonObject();
            params.addProperty("path", fileName);

            String response = sendAndWait(clientId, "delete_file", params, DEFAULT_TIMEOUT);
            log.info("通过 WebSocket 成功删除文件: clientId={}, fileName={}", clientId, fileName);
            return response;
        } catch (TimeoutException e) {
            throw new IOException("删除文件超时: " + fileName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("删除文件被中断: " + fileName, e);
        }
    }

    /**
     * 创建远程目录
     *
     * @param clientId      客户端 ID
     * @param directoryPath 目录路径
     * @return 创建结果
     * @throws IOException 如果创建失败
     */
    public static String createDirectory(String clientId, String directoryPath) throws IOException {
        try {
            JsonObject params = new JsonObject();
            params.addProperty("path", directoryPath);

            String response = sendAndWait(clientId, "create_directory", params, DEFAULT_TIMEOUT);
            log.info("通过 WebSocket 成功创建目录: clientId={}, directory={}", clientId, directoryPath);
            return response;
        } catch (TimeoutException e) {
            throw new IOException("创建目录超时: " + directoryPath, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("创建目录被中断: " + directoryPath, e);
        }
    }

    /**
     * 删除远程目录
     *
     * @param clientId      客户端 ID
     * @param directoryPath 目录路径
     * @return 删除结果
     * @throws IOException 如果删除失败
     */
    public static String deleteDirectory(String clientId, String directoryPath) throws IOException {
        try {
            JsonObject params = new JsonObject();
            params.addProperty("path", directoryPath);

            String response = sendAndWait(clientId, "delete_directory", params, DEFAULT_TIMEOUT);
            log.info("通过 WebSocket 成功删除目录: clientId={}, directory={}", clientId, directoryPath);
            return response;
        } catch (TimeoutException e) {
            throw new IOException("删除目录超时: " + directoryPath, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("删除目录被中断: " + directoryPath, e);
        }
    }

    /**
     * 远程执行命令行
     *
     * @param clientId  客户端 ID
     * @param command   要执行的命令
     * @param directory 执行命令的目录
     * @param timeout   超时时间（秒）
     * @return 命令执行结果
     * @throws IOException 如果执行失败
     */
    public static String executeCommand(String clientId, String command, String directory, int timeout) throws IOException {
        try {
            JsonObject params = new JsonObject();
            params.addProperty("command", command);
            params.addProperty("directory", directory != null ? directory : "");
            params.addProperty("timeout", timeout > 0 ? timeout : 30);

            String response = sendAndWait(clientId, "execute_command", params, timeout + 5); // 给额外的时间
            log.info("通过 WebSocket 成功执行命令: clientId={}, command={}", clientId, command);
            return response;
        } catch (TimeoutException e) {
            throw new IOException("执行命令超时: " + command, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("执行命令被中断: " + command, e);
        }
    }

    /**
     * 清理所有待处理的请求
     * 在关闭连接时调用
     */
    public static void clearPendingRequests() {
        pendingRequests.forEach((requestId, future) -> {
            future.completeExceptionally(new IOException("连接已关闭"));
        });
        pendingRequests.clear();
        log.info("已清理所有待处理的 WebSocket 文件请求");
    }

    /**
     * 获取待处理请求的数量
     *
     * @return 待处理请求数量
     */
    public static int getPendingRequestCount() {
        return pendingRequests.size();
    }
}