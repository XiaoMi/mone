package run.mone.m78.common.youdao;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class WebSocketUtil extends WebSocketListener {

    private static WebSocket webSocket = null;

    private static FileOutputStream savePathStream = null;

    /**
     * 初始化websocket连接
     *
     * @param url paas接口地址
     */
    public static void initConnection(String url) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketUtil());
    }

    public static void initConnection(String url, Map<String, String[]> params) {
        StringBuilder paramsBuilder = new StringBuilder();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                paramsBuilder.append(key).append("=").append(value).append("&");
            }
        }
        paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);
        initConnection(url + "?" + paramsBuilder);
    }

    /**
     * 配置接收binary message的文件路径
     *
     * @param path 文件路径
     */
    public static void binaryMessageConfig(String path) {
        File file = new File(path);
        try {
            savePathStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送text message
     *
     * @param textMsg text message
     */
    public static void sendTextMessage(String textMsg) {
        if (webSocket == null) {
            throw new RuntimeException("websocket connection not established");
        }
        webSocket.send(textMsg);
        System.out.println("send text message: " + textMsg);
    }

    /**
     * 发送binary message
     *
     * @param binaryMsg binary message
     */
    public static void sendBinaryMessage(ByteString binaryMsg) {
        if (webSocket == null) {
            throw new RuntimeException("websocket connection not established");
        }
        webSocket.send(binaryMsg);
        System.out.println("send binary message length: " + binaryMsg.size());
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("connection open");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("received text message: " + text);
        // 该判断方式仅用作demo展示, 生产环境请使用json解析
        if (!text.contains("\"errorCode\":\"0\"")) {
            System.exit(0);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("received text message length: " + bytes.size());
        if (savePathStream != null) {
            try {
                savePathStream.write(bytes.toByteArray());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("connection closed, code: " + code + ", reason: " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("connection failed.");
        t.printStackTrace();
    }
}
