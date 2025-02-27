package run.mone.mcp.ali.email.function;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.ali.email.model.Email;
import run.mone.mcp.ali.email.model.EmailFolder;
import run.mone.mcp.ali.email.model.Messages;
import run.mone.mcp.ali.email.model.CreateDraftRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author caobaoyu
 * @description:
 * @date 2025-02-19 17:16
 */
@Data
@Slf4j
public class AliEmailFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "aliEmail";

    private String desc = "Ali Email operations including getting mail list, mail details, creating drafts and sending emails";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "description": "Operation type for Ali Email:\\n1. getMailFolders: Get mail folder list\\n2. getMailList: Get mail list from specified folder\\n3. getMailDetail: Get mail details\\n4. createDraft: Create draft mail\\n5. sendMail: Send mail\\n\\nExample: 'getMailFolders'"
                    },
                    "folderId": {
                        "type": "string",
                        "description": "Folder ID. Example: 'folder_inbox'"
                    },
                    "messageId": {
                        "type": "string",
                        "description": "Message ID. Example: 'msg_123456'"
                    },
                    "draft": {
                        "type": "object",
                        "description": "Draft mail content. Example: {'subject': 'Test Mail', 'toRecipients': [{'email': 'test@example.com', 'name': 'Test User'}], 'body': {'bodyText': 'Mail content', 'bodyHtml': '<p>Mail content</p>'}}"
                    }
                },
                "required": ["operation"]
            }
            """;

    private static final String GET_MAIL_FOLDERS = "https://alimail-cn.aliyuncs.com/v2/users/%s/mailFolders";
    private static final String GET_MAIL_LIST = "https://alimail-cn.aliyuncs.com/v2/users/%s/mailFolders/%s/messages";
    private static final String GET_MAIL_DETAIL = "https://alimail-cn.aliyuncs.com/v2/users/%s/messages/%s";
    private static final String CREATE_DRAFT = "https://alimail-cn.aliyuncs.com/v2/users/%s/messages";
    private static final String SEND_MAIL = "https://alimail-cn.aliyuncs.com/v2/users/%s/messages/%s/send";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson gson = new Gson();

    private static String USER_EMAIL_ADDRESS = System.getenv("USER_EMAIL_ADDRESS");

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        String operation = (String) args.get("operation");
        log.info("Executing Ali email operation: {}", operation);

        try {
            String result = switch (operation) {
                case "getMailFolders" -> gson.toJson(getMailsFolders());
                case "getMailList" -> {
                    String folderId = (String) args.get("folderId");
                    if (folderId == null) {
                        throw new IllegalArgumentException("folderId is required for getMailList operation");
                    }
                    yield gson.toJson(getMails(folderId));
                }
                case "getMailDetail" -> {
                    String messageId = (String) args.get("messageId");
                    if (messageId == null) {
                        throw new IllegalArgumentException("messageId is required for getMailDetail operation");
                    }
                    yield gson.toJson(getMailDetail(messageId));
                }
                case "createDraft" -> {
                    Map<String, Object> draft = (Map<String, Object>) args.get("draft");
                    if (draft == null) {
                        throw new IllegalArgumentException("draft is required for createDraft operation");
                    }
                    CreateDraftRequest request = gson.fromJson(gson.toJson(draft), CreateDraftRequest.class);
                    yield createDraft(request);
                }
                case "sendMail" -> {
                    String messageId = (String) args.get("messageId");
                    if (messageId == null) {
                        throw new IllegalArgumentException("messageId is required for sendMail operation");
                    }
                    yield String.valueOf(sendMail(messageId));
                }
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            log.error("Failed to execute Ali email operation: {}", e.getMessage());
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String getAccessToken() {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + System.getenv("ALI_MAIL_APP_KEY") + "&client_secret=" + System.getenv("ALI_MAIL_APP_SECRET"));
        Request request = new Request.Builder()
                .url("https://alimail-cn.aliyuncs.com/oauth2/v2.0/token")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String string = response.body().string();
                JsonObject jsonObject = gson.fromJson(string, JsonObject.class);
                return jsonObject.get("access_token").getAsString();
            }
            log.error("getAccessToken error:{}", response.body().string());
            return "";
        } catch (Exception e) {
            log.error("getAccessToken exception", e);
            return "";
        }
    }

    private List<EmailFolder> getMailsFolders() {
        String mailFolderUrl = String.format(GET_MAIL_FOLDERS, USER_EMAIL_ADDRESS);
        String accessToken = getAccessToken();
        Request request = new Request.Builder()
                .url(mailFolderUrl)
                .get()
                .addHeader("Authorization", "bearer " + accessToken)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String string = response.body().string();
                JsonObject jsonObject = gson.fromJson(string, JsonObject.class);
                JsonArray folders = jsonObject.getAsJsonArray("folders");
                return gson.fromJson(folders, new TypeToken<List<EmailFolder>>() {
                }.getType());
            }
            log.error("getMailsFolders error:{}", response.body().string());
            return Lists.newArrayList();
        } catch (Exception e) {
            log.error("getMailsFolders exception:", e);
            return Lists.newArrayList();
        }
    }

    private Messages getMails(String folderId) {
        String messagesUrl = String.format(GET_MAIL_LIST, USER_EMAIL_ADDRESS, folderId);
        String accessToken = getAccessToken();
        Request request = new Request.Builder()
                .url(messagesUrl)
                .get()
                .addHeader("Authorization", "bearer " + accessToken)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String string = response.body().string();
                return gson.fromJson(string, new TypeToken<Messages>() {
                }.getType());
            }
            log.error("getMails error:{}", response.body().string());
            return null;
        } catch (Exception e) {
            log.error("getMails exception:", e);
            return null;
        }
    }

    private Email getMailDetail(String messageId) {
        String detailUrl = String.format(GET_MAIL_DETAIL, USER_EMAIL_ADDRESS, messageId);
        String accessToken = getAccessToken();
        Request request = new Request.Builder()
                .url(detailUrl)
                .get()
                .addHeader("Authorization", "bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String string = response.body().string();
                JsonObject jsonObject = gson.fromJson(string, JsonObject.class);
                JsonObject message = jsonObject.getAsJsonObject("message");
                return gson.fromJson(message, new TypeToken<Email>() {
                }.getType());
            }
            log.error("getMailDetail error:{}", response.body().string());
            return null;
        } catch (Exception e) {
            log.error("getMailDetail exception:", e);
            return null;
        }
    }

    /**
     * 创建草稿邮件
     * @param request 创建草稿请求
     * @return 创建成功的邮件ID
     */
    private String createDraft(CreateDraftRequest request) {
        String draftUrl = String.format(CREATE_DRAFT, USER_EMAIL_ADDRESS);
        String accessToken = getAccessToken();
        
        // 构建请求体
        JsonObject messageObj = new JsonObject();
        JsonObject emailObj = gson.toJsonTree(request).getAsJsonObject();
        messageObj.add("message", emailObj);
        String jsonBody = gson.toJson(messageObj);
        
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request httpRequest = new Request.Builder()
                .url(draftUrl)
                .post(body)
                .addHeader("Authorization", "bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();
                
        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (response.isSuccessful()) {
                String string = response.body().string();
                JsonObject jsonObject = gson.fromJson(string, JsonObject.class);
                JsonObject message = jsonObject.getAsJsonObject("message");
                return message.get("id").getAsString();
            }
            log.error("createDraft error:{}", response.body().string());
            return null;
        } catch (Exception e) {
            log.error("createDraft exception:", e);
            return null;
        }
    }

    /**
     * 发送邮件
     * @param messageId 草稿邮件ID
     * @return 是否发送成功
     */
    private boolean sendMail(String messageId) {
        String sendUrl = String.format(SEND_MAIL, USER_EMAIL_ADDRESS, messageId);
        String accessToken = getAccessToken();
        
        // 构建请求体
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("saveToSentItems", true);
        
        RequestBody body = RequestBody.create(gson.toJson(jsonBody), JSON);
        Request request = new Request.Builder()
                .url(sendUrl)
                .post(body)
                .addHeader("Authorization", "bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();
                
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return true;
            }
            log.error("sendMail error:{}", response.body().string());
            return false;
        } catch (Exception e) {
            log.error("sendMail exception:", e);
            return false;
        }
    }

}
