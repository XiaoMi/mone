package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.llm.LLM;
import run.mone.hive.memory.LongTermMemoryManager;
import run.mone.hive.roles.ReactorRole;

import java.util.List;
import java.util.Map;

/**
 * 记忆工具
 * 用于查询、保存和管理长期记忆
 *
 * @author wangmin
 */
@Slf4j
public class MemoryTool implements ITool {

    private static LongTermMemoryManager memoryManager;

    /**
     * 初始化长期记忆管理器
     */
    private synchronized void initializeMemoryManager(String roleName) {
        if (memoryManager == null) {
            try {
                memoryManager = new LongTermMemoryManager(roleName);
                log.info("记忆管理器初始化成功: {}", roleName);
            } catch (Exception e) {
                log.error("记忆管理器初始化失败", e);
            }
        }
    }

    @Override
    public String getName() {
        return "memory";
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
    public String description() {
        return """
                A comprehensive memory tool that supports searching, saving, and managing long-term memory information.
                Use this tool when you need to retrieve relevant information from previous conversations,
                store new knowledge, or manage user context that might be helpful for current and future tasks.
                
                **When to use:** Choose this tool when you need to access historical information,
                save important context, user preferences, or manage memory from previous interactions.
                
                **Common conversation triggers:**
                
                **Search operations (action: search):**
                1. "你还记得我上次说过的兴趣爱好吗？" → Search previous interests
                2. "我之前提到过什么编程语言偏好？" → Search programming preferences
                3. "还记得我们上次讨论的项目需求吗？" → Search project requirements
                4. "我以前说过喜欢什么类型的音乐？" → Search music preferences
                5. "你还记得我的工作经历吗？" → Search work experience
                6. "之前我们聊过什么技术话题？" → Search technical discussions
                
                **Save operations (action: save):**
                1. "记住我喜欢游泳" → Save hobby information
                2. "我现在在学习React，请记住" → Save learning status
                3. "记录一下我的新邮箱地址是xxx" → Save contact info
                4. "记住我更喜欢用Python做数据分析" → Save tool preferences
                5. "请记住我的生日是XX月XX日" → Save personal information
                
                **Get all operations (action: get_all):**
                1. "你都知道我哪些信息？" → List all memories
                2. "显示你记住的所有关于我的内容" → Show all stored info
                3. "总结一下我们的历史对话记录" → Summarize conversation history
                
                **Reset operations (action: reset):**
                1. "忘记之前的对话吧" → Clear all memories
                2. "清除我的所有个人信息" → Reset personal data
                3. "重新开始，清空记忆" → Start fresh conversation
                
                **Output:** The tool will return relevant memory entries with similarity scores and metadata for queries,
                or confirmation messages for save/management operations.
                """;
    }

    @Override
    public String parameters() {
        return """
                - action: (required) The operation to perform: "search", "save", "reset", "get_all"
                - query: (required for search) The search query string to find relevant memories
                - content: (required for save) The content to save to memory
                - max_results: (optional) Maximum number of results to return (default: 5, max: 20)
                - threshold: (optional) Minimum similarity threshold for results (default: 0.7, range: 0.0-1.0)
                - metadata: (optional for save) Additional metadata to store with the memory
                """;
    }

    @Override
    public String usage() {
        return """
                (Attention: If you are using this tool, you MUST return the results within the <memory> tag):
                
                Example 1: Searching for user preferences
                <memory>
                  <action>search</action>
                  <query>user programming language preferences</query>
                </memory>
                
                Example 2: Saving new information
                <memory>
                  <action>save</action>
                  <content>User prefers Python for data analysis projects</content>
                  <metadata>{"category": "preference", "domain": "programming"}</metadata>
                </memory>
                """;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject inputJson) {
        JsonObject result = new JsonObject();

        try {
            // 验证必需参数
            if (!inputJson.has("action") || StringUtils.isBlank(inputJson.get("action").getAsString())) {
                log.error("记忆操作请求缺少必需的action参数");
                result.addProperty("error", "缺少必需参数'action'");
                return result;
            }

            String action = inputJson.get("action").getAsString().toLowerCase();
            log.info("开始记忆操作，操作类型：{}", action);


            initializeMemoryManager(role.getOwner());

            // 检查记忆管理器是否可用
            if (memoryManager == null) {
                log.warn("长期记忆管理器未初始化");
                result.addProperty("error", "记忆系统未初始化");
                return result;
            }

            // 从环境变量获取配置参数？？
            String userId = role.getOwner();
            String agentId = "agentId";
            String sessionId = "sessionId";

            // 根据操作类型执行不同的功能
            switch (action) {
                case "search":
                    return executeSearch(inputJson, userId, agentId, sessionId);
                case "save":
                    return executeSave(inputJson, userId, agentId, sessionId);
                case "reset":
                    return executeReset();
                case "get_all":
                    return executeGetAll(inputJson, userId, agentId, sessionId);
                default:
                    result.addProperty("error", "不支持的操作类型: " + action + "。支持的操作: search, save, reset, get_all");
                    return result;
            }

        } catch (Exception e) {
            log.error("记忆操作处理发生异常", e);
            result.addProperty("error", "操作失败: " + e.getMessage());
            return result;
        }
    }

    private JsonObject executeSearch(JsonObject inputJson, String userId, String agentId, String sessionId) {
        JsonObject result = new JsonObject();

        if (!inputJson.has("query") || StringUtils.isBlank(inputJson.get("query").getAsString())) {
            result.addProperty("error", "search操作缺少必需的query参数");
            return result;
        }

        String query = inputJson.get("query").getAsString();
        int maxResults = inputJson.has("max_results") ? inputJson.get("max_results").getAsInt() : 5;
        double threshold = inputJson.has("threshold") ? inputJson.get("threshold").getAsDouble() : 0.7;

        // 限制参数范围
        maxResults = Math.min(Math.max(maxResults, 1), 20);
        threshold = Math.min(Math.max(threshold, 0.0), 1.0);

        try {
            var searchResult = memoryManager.searchMemory(query, userId, agentId, sessionId, maxResults, threshold);

            // 使用带超时的异步调用处理
            Map<String, Object> searchMap;
            try {
                searchMap = searchResult.get(60, java.util.concurrent.TimeUnit.SECONDS);
            } catch (java.util.concurrent.TimeoutException e) {
                log.warn("记忆搜索超时: {}", e.getMessage());
                result.addProperty("error", "搜索超时，请稍后重试");
                return result;
            } catch (java.util.concurrent.ExecutionException e) {
                log.error("记忆搜索执行失败: {}", e.getCause().getMessage());
                result.addProperty("error", "搜索执行失败: " + e.getCause().getMessage());
                return result;
            }

            JsonArray memories = new JsonArray();
            addMemoriesToResult(memories, searchMap, "search");

            result.add("memories", memories);
            result.addProperty("action", "search");
            result.addProperty("query", query);
            result.addProperty("total_results", memories.size());
            result.addProperty("threshold_used", threshold);

            log.info("记忆搜索完成，返回{}条结果", memories.size());

        } catch (Exception e) {
            log.error("执行记忆搜索时发生异常", e);
            result.addProperty("error", "搜索失败: " + e.getMessage());
        }

        return result;
    }

    private JsonObject executeSave(JsonObject inputJson, String userId, String agentId, String sessionId) {
        JsonObject result = new JsonObject();

        if (!inputJson.has("content") || StringUtils.isBlank(inputJson.get("content").getAsString())) {
            result.addProperty("error", "save操作缺少必需的content参数");
            return result;
        }

        String content = inputJson.get("content").getAsString();
        Map<String, Object> metadata = new java.util.HashMap<>();

        // 处理可选的元数据
        if (inputJson.has("metadata")) {
            try {
                // 简单解析JSON字符串作为元数据
                String metadataStr = inputJson.get("metadata").getAsString();
                metadata.put("custom", metadataStr);
            } catch (Exception e) {
                log.warn("解析元数据失败，使用默认值: {}", e.getMessage());
            }
        }

        metadata.put("saved_by", "MemoryTool");
        metadata.put("timestamp", System.currentTimeMillis());

        try {
            var saveResult = memoryManager.addMemory(content, userId, agentId, sessionId, metadata);

            // 使用带超时的异步调用处理
            Map<String, Object> saveMap;
            try {
                saveMap = saveResult.get(10, java.util.concurrent.TimeUnit.SECONDS);
            } catch (java.util.concurrent.TimeoutException e) {
                log.warn("记忆保存超时: {}", e.getMessage());
                result.addProperty("error", "保存超时，请稍后重试");
                result.addProperty("success", false);
                return result;
            } catch (java.util.concurrent.ExecutionException e) {
                log.error("记忆保存执行失败: {}", e.getCause().getMessage());
                result.addProperty("error", "保存执行失败: " + e.getCause().getMessage());
                result.addProperty("success", false);
                return result;
            }

            result.addProperty("action", "save");
            result.addProperty("content", content);
            result.addProperty("success", !saveMap.containsKey("error"));

            if (saveMap.containsKey("error")) {
                result.addProperty("error", saveMap.get("error").toString());
            } else {
                result.addProperty("message", "记忆保存成功");
                if (saveMap.containsKey("memory_id")) {
                    result.addProperty("memory_id", saveMap.get("memory_id").toString());
                }
            }

            log.info("记忆保存完成");

        } catch (Exception e) {
            log.error("执行记忆保存时发生异常", e);
            result.addProperty("error", "保存失败: " + e.getMessage());
            result.addProperty("success", false);
        }

        return result;
    }

    private JsonObject executeReset() {
        JsonObject result = new JsonObject();

        try {
            var resetResult = memoryManager.resetMemory();

            // 使用带超时的异步调用处理
            boolean success;
            try {
                success = resetResult.get(30, java.util.concurrent.TimeUnit.SECONDS);
            } catch (java.util.concurrent.TimeoutException e) {
                log.warn("记忆重置超时: {}", e.getMessage());
                result.addProperty("error", "重置超时，请稍后重试");
                result.addProperty("success", false);
                return result;
            } catch (java.util.concurrent.ExecutionException e) {
                log.error("记忆重置执行失败: {}", e.getCause().getMessage());
                result.addProperty("error", "重置执行失败: " + e.getCause().getMessage());
                result.addProperty("success", false);
                return result;
            }

            result.addProperty("action", "reset");
            result.addProperty("success", success);
            result.addProperty("message", success ? "记忆重置成功" : "记忆重置失败");

            log.info("记忆重置操作完成，结果: {}", success);

        } catch (Exception e) {
            log.error("执行记忆重置时发生异常", e);
            result.addProperty("error", "重置失败: " + e.getMessage());
            result.addProperty("success", false);
        }

        return result;
    }

    private JsonObject executeGetAll(JsonObject inputJson, String userId, String agentId, String sessionId) {
        JsonObject result = new JsonObject();

        int maxResults = inputJson.has("max_results") ? inputJson.get("max_results").getAsInt() : 50;
        maxResults = Math.min(Math.max(maxResults, 1), 100); // 限制范围1-100

        try {
            var getAllResult = memoryManager.getAllMemories(userId, agentId, sessionId, maxResults);

            // 使用带超时的异步调用处理
            Map<String, Object> getAllMap;
            try {
                getAllMap = getAllResult.get(15, java.util.concurrent.TimeUnit.SECONDS);
            } catch (java.util.concurrent.TimeoutException e) {
                log.warn("获取所有记忆超时: {}", e.getMessage());
                result.addProperty("error", "获取超时，请稍后重试");
                return result;
            } catch (java.util.concurrent.ExecutionException e) {
                log.error("获取所有记忆执行失败: {}", e.getCause().getMessage());
                result.addProperty("error", "获取执行失败: " + e.getCause().getMessage());
                return result;
            }

            JsonArray memories = new JsonArray();
            addMemoriesToResult(memories, getAllMap, "get_all");

            result.add("memories", memories);
            result.addProperty("action", "get_all");
            result.addProperty("total_results", memories.size());
            result.addProperty("max_results", maxResults);

            log.info("获取所有记忆完成，返回{}条结果", memories.size());

        } catch (Exception e) {
            log.error("执行获取所有记忆时发生异常", e);
            result.addProperty("error", "获取失败: " + e.getMessage());
        }

        return result;
    }

    private void addMemoriesToResult(JsonArray memories, Object memoryResults, String type) {
        if (memoryResults == null) {
            return;
        }

        try {
            if (memoryResults instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> resultMap = (Map<String, Object>) memoryResults;

                // 检查是否有错误
                if (resultMap.containsKey("error")) {
                    JsonObject errorObj = new JsonObject();
                    errorObj.addProperty("error", resultMap.get("error").toString());
                    errorObj.addProperty("type", "error");
                    memories.add(errorObj);
                    return;
                }

                if (resultMap.containsKey("relations")) {
                    memories.add(GsonUtils.gson.toJsonTree(resultMap.get("relations")));
                }

                // 处理搜索结果
                if (resultMap.containsKey("results")) {
                    Object resultsObj = resultMap.get("results");

                    // 处理List<MemoryItem>的情况
                    if (resultsObj instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> results = (List<Object>) resultsObj;

                        for (Object resultObj : results) {
                            JsonObject memoryObj = new JsonObject();

                            if (resultObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> result = (Map<String, Object>) resultObj;

                                // 提取记忆ID
                                if (result.containsKey("id")) {
                                    memoryObj.addProperty("id", result.get("id").toString());
                                }

                                // 提取分数
                                if (result.containsKey("score")) {
                                    memoryObj.addProperty("score", result.get("score").toString());
                                }

                                // 提取记忆内容 - 从metadata.data获取
                                String content = extractMemoryContent(result);
                                if (content != null && !content.trim().isEmpty()) {
                                    memoryObj.addProperty("content", content);
                                } else if (result.containsKey("memory")) {
                                    memoryObj.addProperty("content", result.get("memory").toString());
                                }

                                // 提取用户标识信息
                                if (result.containsKey("userId")) {
                                    memoryObj.addProperty("userId", result.get("userId").toString());
                                }
                                if (result.containsKey("agentId")) {
                                    memoryObj.addProperty("agentId", result.get("agentId").toString());
                                }
                                if (result.containsKey("runId")) {
                                    memoryObj.addProperty("sessionId", result.get("runId").toString());
                                }

                                // 提取时间信息
                                if (result.containsKey("createdAt")) {
                                    memoryObj.addProperty("createdAt", result.get("createdAt").toString());
                                }
                                if (result.containsKey("updatedAt")) {
                                    memoryObj.addProperty("updatedAt", result.get("updatedAt").toString());
                                }

                                // 提取元数据
                                if (result.containsKey("metadata")) {
                                    memoryObj.addProperty("metadata", result.get("metadata").toString());
                                }

                            } else {
                                // 如果是MemoryItem对象，直接转换为字符串
                                String itemStr = resultObj.toString();
                                memoryObj.addProperty("content", extractContentFromMemoryItemString(itemStr));
                                memoryObj.addProperty("raw", itemStr);
                            }

                            memoryObj.addProperty("type", type);
                            memoryObj.addProperty("timestamp", System.currentTimeMillis());
                            memories.add(memoryObj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("处理{}级别记忆结果时发生异常: {}", type, e);
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "处理记忆结果异常: " + e.getMessage());
            errorObj.addProperty("type", "error");
            memories.add(errorObj);
        }
    }

    /**
     * 从MemoryItem的Map结构中提取记忆内容
     */
    private String extractMemoryContent(Map<String, Object> result) {
        try {
            // 首先尝试从metadata.data获取
            if (result.containsKey("metadata")) {
                Object metadataObj = result.get("metadata");
                if (metadataObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> metadata = (Map<String, Object>) metadataObj;
                    if (metadata.containsKey("data")) {
                        return metadata.get("data").toString();
                    }
                }
            }

            // 如果metadata.data不存在，尝试从memory字段获取
            if (result.containsKey("memory")) {
                String memory = result.get("memory").toString();
                if (memory != null && !memory.trim().isEmpty()) {
                    return memory;
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("提取记忆内容时发生异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从MemoryItem的toString字符串中提取记忆内容
     */
    private String extractContentFromMemoryItemString(String itemStr) {
        try {
            // 从toString字符串中提取data字段
            // 格式：data=The user likes swimming.
            String dataPrefix = "data=";
            int dataIndex = itemStr.indexOf(dataPrefix);
            if (dataIndex != -1) {
                int startIndex = dataIndex + dataPrefix.length();
                int endIndex = itemStr.indexOf(",", startIndex);
                if (endIndex == -1) {
                    endIndex = itemStr.indexOf("}", startIndex);
                }
                if (endIndex != -1) {
                    return itemStr.substring(startIndex, endIndex).trim();
                } else {
                    return itemStr.substring(startIndex).trim();
                }
            }

            // 如果找不到data字段，返回原始字符串的摘要
            return "记忆项: " + (itemStr.length() > 100 ? itemStr.substring(0, 100) + "..." : itemStr);
        } catch (Exception e) {
            log.warn("从字符串提取记忆内容时发生异常: {}", e.getMessage());
            return "无法解析的记忆内容";
        }
    }
}