package run.mone.hive.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.dto.WebSocketCallRequest;
import run.mone.hive.dto.WebSocketCallResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * WebSocket 文件操作工具类
 * 通过 WebSocket 连接操作远程文件系统
 * 使用 WebSocketCaller 进行同步调用（异步逻辑已在 WebSocketCaller 中处理）
 *
 * @author goodjava@qq.com
 * @date 2025/12/11
 */
@Slf4j
public class WebSocketFileUtils {

    private static final Gson gson = new Gson();

    /**
     * WebSocket 调用器接口
     * 用于发送请求并等待响应
     */
    public interface WebSocketCallerInterface {
        Map<String, Object> call(String clientId, String action, Map<String, Object> data) throws TimeoutException;
    }

    /**
     * WebSocket 调用器实例
     * 需要在运行时注入具体实现
     */
    private static WebSocketCallerInterface webSocketCaller;

    /**
     * 设置 WebSocket 调用器
     *
     * @param caller 调用器实现
     */
    public static void setWebSocketCaller(WebSocketCallerInterface caller) {
        webSocketCaller = caller;
    }

    /**
     * 发送请求并等待响应
     *
     * @param clientId    客户端 ID
     * @param requestType 请求类型
     * @param params      请求参数
     * @return 响应结果
     * @throws IOException 如果调用失败
     */
    private static Map<String, Object> sendAndWait(String clientId, String requestType, Map<String, Object> params)
            throws IOException {

        if (webSocketCaller == null) {
            throw new IOException("WebSocket 调用器未初始化");
        }

        try {
            log.debug("发送 WebSocket 请求: clientId={}, type={}", clientId, requestType);
            Map<String, Object> response = webSocketCaller.call(clientId, requestType, params);
            log.debug("收到 WebSocket 响应: clientId={}, type={}", clientId, requestType);
            return response;
        } catch (TimeoutException e) {
            throw new IOException("WebSocket 调用超时: " + requestType, e);
        } catch (Exception e) {
            throw new IOException("WebSocket 调用失败: " + e.getMessage(), e);
        }
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
        Map<String, Object> params = new HashMap<>();
        params.put("path", request.getPath());
        params.put("recursive", request.getRecursive());

        Map<String, Object> responseMap = sendAndWait(clientId, "list_files", params);
        log.info("通过 WebSocket 成功列出文件: clientId={}, path={}", clientId, request.getPath());

        return WebSocketCallResponse.builder()
                .success(true)
                .clientId(clientId)
                .directoryPath(request.getPath())
                .recursive(request.getRecursive())
                .mode("REMOTE_WS")
                .result(gson.toJson(responseMap))
                .response(responseMap)
                .build();
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
        Map<String, Object> params = new HashMap<>();
        params.put("path", request.getPath());

        Map<String, Object> responseMap = sendAndWait(clientId, "read_file", params);
        log.info("通过 WebSocket 成功读取文件: clientId={}, path={}", clientId, request.getPath());

        // 获取文件内容
        String content = responseMap.containsKey("content") ?
                String.valueOf(responseMap.get("content")) : gson.toJson(responseMap);

        return WebSocketCallResponse.builder()
                .success(true)
                .clientId(clientId)
                .mode("REMOTE_WS")
                .result(content)
                .response(responseMap)
                .build();
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
        Map<String, Object> params = new HashMap<>();
        params.put("path", request.getPath());
        params.put("content", request.getContent());

        Map<String, Object> responseMap = sendAndWait(clientId, "write_file", params);
        log.info("通过 WebSocket 成功写入文件: clientId={}, path={}", clientId, request.getPath());

        // 获取结果消息
        String resultMsg = responseMap.containsKey("message") ?
                String.valueOf(responseMap.get("message")) : "文件写入成功";

        return WebSocketCallResponse.builder()
                .success(true)
                .clientId(clientId)
                .mode("REMOTE_WS")
                .result(resultMsg)
                .response(responseMap)
                .build();
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
        Map<String, Object> params = new HashMap<>();
        params.put("path", request.getPath());
        params.put("regex", request.getRegex());
        params.put("filePattern", request.getFilePattern());

        Map<String, Object> responseMap = sendAndWait(clientId, "search_files", params);
        log.info("通过 WebSocket 成功搜索文件: clientId={}, path={}", clientId, request.getPath());

        return WebSocketCallResponse.builder()
                .success(true)
                .clientId(clientId)
                .mode("REMOTE_WS")
                .result(gson.toJson(responseMap))
                .response(responseMap)
                .build();
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
        Map<String, Object> params = new HashMap<>();
        params.put("path", fileName);

        Map<String, Object> responseMap = sendAndWait(clientId, "delete_file", params);
        log.info("通过 WebSocket 成功删除文件: clientId={}, fileName={}", clientId, fileName);
        return gson.toJson(responseMap);
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
        Map<String, Object> params = new HashMap<>();
        params.put("path", directoryPath);

        Map<String, Object> responseMap = sendAndWait(clientId, "create_directory", params);
        log.info("通过 WebSocket 成功创建目录: clientId={}, directory={}", clientId, directoryPath);
        return gson.toJson(responseMap);
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
        Map<String, Object> params = new HashMap<>();
        params.put("path", directoryPath);

        Map<String, Object> responseMap = sendAndWait(clientId, "delete_directory", params);
        log.info("通过 WebSocket 成功删除目录: clientId={}, directory={}", clientId, directoryPath);
        return gson.toJson(responseMap);
    }

}