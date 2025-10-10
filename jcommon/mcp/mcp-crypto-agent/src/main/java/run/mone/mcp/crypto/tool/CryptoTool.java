package run.mone.mcp.crypto.tool;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;

import java.io.IOException;
import java.util.Base64;

/**
 * @author hive
 * @date 2025/10/10
 * 虚拟币交易工具，支持查询余额和内部转账
 */
@Slf4j
public class CryptoTool implements ITool {

    public static final String name = "crypto_trade";
    private static final String BASE_URL = System.getenv("CRYPTO_API_URL") != null
            ? System.getenv("CRYPTO_API_URL")
            : "http://127.0.0.1/api";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public boolean completed() {
        return false;
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override
    public boolean show() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String description() {
        return """
                A tool for cryptocurrency operations including balance queries and internal transfers.
                Supports three operations:
                1. Get balance for a specific user
                2. Get all users' balances
                3. Execute internal trade (transfer) between users
                Use this tool when users need to check their cryptocurrency balance or transfer coins to other users.
                """;
    }

    @Override
    public String parameters() {
        return """
                - 用户没提供的参数就用提供的默认值
                - operation: (required) The operation to perform. Must be one of: "get_balance", "get_all_balance", "inner_trade"
                - name: (required for get_balance) The username to query balance for
                - sender: (required for inner_trade) The sender's username 默认值:%s
                - senderAccount: (required for inner_trade) The sender's account address 默认值:%s
                - receiver: (required for inner_trade) The receiver's username
                - receiverAccount: (required for inner_trade) The receiver's account address 默认值:%s
                - amount: (required for inner_trade) The amount to transfer (numeric value)
                - senderPwd: (required for inner_trade) The sender's password (will be base64 encoded automatically if not already encoded) 默认值:%s
                - tradeDesc: (optional for inner_trade) Description of the trade
                """.formatted("sender", "senderAccount", "receiverAccount", "senderPwd");
    }

    @Override
    public String usage() {
        String taskProgress = """
                <task_progress>
                Checklist here (optional)
                </task_progress>
                """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                (Attention: Provide responses in Chinese 中文):
                Example 1: Query single user balance
                <crypto_trade>
                <operation>get_balance</operation>
                <name>zhangxiaowei6</name>
                %s
                </crypto_trade>
                
                Example 2: Query all balances
                <crypto_trade>
                <operation>get_all_balance</operation>
                %s
                </crypto_trade>
                
                Example 3: Execute internal trade
                <crypto_trade>
                <operation>inner_trade</operation>
                <sender>zhangxiaowei</sender>
                <senderAccount>abc</senderAccount>
                <receiver>go@qq.com</receiver>
                <receiverAccount>abc</receiverAccount>
                <amount>1</amount>
                <senderPwd>SSSS==</senderPwd>
                <tradeDesc>Transfer for service</tradeDesc>
                %s
                </crypto_trade>
                """.formatted(taskProgress, taskProgress, taskProgress);
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject req) {
        JsonObject result = new JsonObject();
        try {
            String operation = req.has("operation") ? req.get("operation").getAsString() : "";

            switch (operation) {
                case "get_balance" -> {
                    String name = req.has("name") ? req.get("name").getAsString() : "";
                    if (name.isEmpty()) {
                        result.addProperty("success", false);
                        result.addProperty("message", "缺少参数: name");
                        return result;
                    }
                    return getBalance(name);
                }
                case "get_all_balance" -> {
                    return getAllBalance();
                }
                case "inner_trade" -> {
                    return executeInnerTrade(req);
                }
                default -> {
                    result.addProperty("success", false);
                    result.addProperty("message", "无效的操作类型: " + operation);
                }
            }
        } catch (Exception e) {
            log.error("执行虚拟币操作失败", e);
            result.addProperty("success", false);
            result.addProperty("message", "操作失败: " + e.getMessage());
        }
        return result;
    }

    private JsonObject getBalance(String name) {
        JsonObject result = new JsonObject();
        try {
            String url = BASE_URL + "/getBalance?name=" + name;
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    result.addProperty("success", true);
                    result.addProperty("data", responseBody);
                    result.addProperty("message", "成功查询用户 " + name + " 的余额");
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", "查询失败，HTTP状态码: " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("查询余额失败", e);
            result.addProperty("success", false);
            result.addProperty("message", "网络请求失败: " + e.getMessage());
        }
        return result;
    }

    private JsonObject getAllBalance() {
        JsonObject result = new JsonObject();
        try {
            String url = BASE_URL + "/getAllBalance";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    result.addProperty("success", true);
                    result.addProperty("data", responseBody);
                    result.addProperty("message", "成功查询所有用户余额");
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", "查询失败，HTTP状态码: " + response.code());
                }
            }
        } catch (IOException e) {
            log.error("查询所有余额失败", e);
            result.addProperty("success", false);
            result.addProperty("message", "网络请求失败: " + e.getMessage());
        }
        return result;
    }

    private JsonObject executeInnerTrade(JsonObject req) {
        JsonObject result = new JsonObject();
        try {
            // 验证必填参数
            String[] requiredFields = {"sender", "senderAccount", "receiver", "receiverAccount", "amount", "senderPwd"};
            for (String field : requiredFields) {
                if (!req.has(field) || req.get(field).getAsString().isEmpty()) {
                    result.addProperty("success", false);
                    result.addProperty("message", "缺少必填参数: " + field);
                    return result;
                }
            }
            if (req.get("sender").getAsString().equals("sender")) {
                req.addProperty("sender", System.getenv("sender"));
            }

            if (req.get("senderPwd").getAsString().equals("senderPwd")) {
                req.addProperty("senderPwd", System.getenv("senderPwd"));
            }

            if (req.get("receiverAccount").getAsString().equals("receiverAccount")) {
                req.addProperty("receiverAccount", System.getenv("senderAccount"));
            }


            String sender = req.get("sender").getAsString();
            String senderAccount = req.get("senderAccount").getAsString();
            String receiver = req.get("receiver").getAsString();
            String receiverAccount = req.get("receiverAccount").getAsString();
            double amount = req.get("amount").getAsDouble();
            String senderPwd = req.get("senderPwd").getAsString();
            String tradeDesc = req.has("tradeDesc") ? req.get("tradeDesc").getAsString() : "";

            // 如果密码不是Base64格式，进行编码
            if (!isBase64(senderPwd)) {
                senderPwd = Base64.getEncoder().encodeToString(senderPwd.getBytes());
            }

            // 构建请求JSON
            JsonObject tradeReq = new JsonObject();
            tradeReq.addProperty("sender", sender);
            tradeReq.addProperty("senderAccount", senderAccount);
            tradeReq.addProperty("receiver", receiver);
            tradeReq.addProperty("receiverAccount", receiverAccount);
            tradeReq.addProperty("amount", amount);
            tradeReq.addProperty("senderPwd", senderPwd);
            tradeReq.addProperty("tradeDesc", tradeDesc);

            String url = BASE_URL + "/innerTrade";
            RequestBody body = RequestBody.create(
                    tradeReq.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    result.addProperty("success", true);
                    result.addProperty("data", responseBody);
                    result.addProperty("message", "成功执行转账: " + sender + " -> " + receiver + ", 金额: " + amount);
                } else {
                    result.addProperty("success", false);
                    result.addProperty("message", "转账失败，HTTP状态码: " + response.code());
                }
            }
        } catch (Exception e) {
            log.error("执行内部转账失败", e);
            result.addProperty("success", false);
            result.addProperty("message", "转账失败: " + e.getMessage());
        }
        return result;
    }

    private boolean isBase64(String str) {
        try {
            Base64.getDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

