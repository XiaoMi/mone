package run.mone.hive.memory.longterm.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 记忆工具类
 * 提供记忆相关的工具方法
 */
@Slf4j
public class MemoryUtils {
    
    private static final Gson gson = new Gson();
    
    /**
     * 从JSON响应中解析事实列表
     * 
     * @param jsonResponse JSON响应字符串
     * @return 事实列表
     */
    public static List<String> parseFactsFromJson(String jsonResponse) {
        List<String> facts = new ArrayList<>();
        
        try {
            // 移除代码块标记
            String cleanResponse = removeCodeBlocks(jsonResponse);
            
            JsonObject jsonObject = gson.fromJson(cleanResponse, JsonObject.class);
            
            if (jsonObject.has("facts")) {
                JsonArray factsArray = jsonObject.getAsJsonArray("facts");
                
                for (int i = 0; i < factsArray.size(); i++) {
                    String fact = factsArray.get(i).toString();
                    if (fact != null && !fact.trim().isEmpty()) {
                        facts.add(fact.trim());
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Error parsing facts from JSON: {}", jsonResponse, e);
        }
        
        return facts;
    }
    
    /**
     * 从JSON响应中解析记忆操作列表
     * 
     * @param jsonResponse JSON响应字符串
     * @return 记忆操作列表
     */
    public static List<Map<String, Object>> parseMemoryActionsFromJson(String jsonResponse) {
        List<Map<String, Object>> actions = new ArrayList<>();
        
        try {
            // 移除代码块标记
            String cleanResponse = removeCodeBlocks(jsonResponse);
            
            JsonObject jsonObject = gson.fromJson(cleanResponse, JsonObject.class);
            
            if (jsonObject.has("memory")) {
                JsonArray memoryArray = jsonObject.getAsJsonArray("memory");
                
                for (int i = 0; i < memoryArray.size(); i++) {
                    JsonObject actionObj = memoryArray.get(i).getAsJsonObject();
                    Map<String, Object> action = new HashMap<>();
                    
                    if (actionObj.has("text")) {
                        action.put("text", actionObj.get("text").getAsString());
                    }
                    
                    if (actionObj.has("event")) {
                        action.put("event", actionObj.get("event").getAsString());
                    }
                    
                    if (actionObj.has("id")) {
                        action.put("id", actionObj.get("id").getAsString());
                    }
                    
                    if (actionObj.has("old_memory")) {
                        action.put("old_memory", actionObj.get("old_memory").getAsString());
                    }
                    
                    actions.add(action);
                }
            }
            
        } catch (Exception e) {
            log.error("Error parsing memory actions from JSON: {}", jsonResponse, e);
        }
        
        return actions;
    }
    
    /**
     * 移除JSON响应中的代码块标记
     * 
     * @param response 原始响应
     * @return 清理后的响应
     */
    public static String removeCodeBlocks(String response) {
        if (response == null) {
            return "";
        }
        
        // 移除```json和```标记
        String cleaned = response.trim();
        
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        return cleaned.trim();
    }
    
    /**
     * 验证JSON格式
     * 
     * @param jsonString JSON字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String jsonString) {
        try {
            gson.fromJson(jsonString, JsonObject.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 构建过滤条件字符串
     * 
     * @param filters 过滤条件Map
     * @return 过滤条件描述
     */
    public static String buildFilterDescription(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return "No filters";
        }
        
        List<String> filterParts = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            filterParts.add(entry.getKey() + "=" + entry.getValue());
        }
        
        return String.join(", ", filterParts);
    }
    
    /**
     * 计算文本的哈希值
     * 
     * @param text 文本内容
     * @return MD5哈希值
     */
    public static String calculateMd5Hash(String text) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (Exception e) {
            log.error("Error calculating MD5 hash", e);
            return "";
        }
    }
    
    /**
     * 验证向量维度
     * 
     * @param vector 向量
     * @param expectedDims 期望维度
     * @return 是否有效
     */
    public static boolean validateVectorDimensions(List<Double> vector, int expectedDims) {
        return vector != null && vector.size() == expectedDims;
    }
    
    /**
     * 标准化向量（L2范数）
     * 
     * @param vector 原始向量
     * @return 标准化后的向量
     */
    public static List<Double> normalizeVector(List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            return vector;
        }
        
        // 计算L2范数
        double norm = 0.0;
        for (Double val : vector) {
            norm += val * val;
        }
        norm = Math.sqrt(norm);
        
        if (norm == 0.0) {
            return vector;
        }
        
        // 标准化
        List<Double> normalized = new ArrayList<>();
        for (Double val : vector) {
            normalized.add(val / norm);
        }
        
        return normalized;
    }
    
    /**
     * 计算余弦相似度
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度 (0-1)
     */
    public static double cosineSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimensions");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.size(); i++) {
            double v1 = vector1.get(i);
            double v2 = vector2.get(i);
            
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }


    /**
     * 验证url是否有效
     */
    public static boolean validateUrl(String url) {
        try {
            return url.trim().startsWith("http") || url.trim().startsWith("https");
        } catch (Exception e) {
            return false;
        }
    }
}
