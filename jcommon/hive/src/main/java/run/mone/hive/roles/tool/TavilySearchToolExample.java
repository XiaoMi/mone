package run.mone.hive.roles.tool;

import com.google.gson.JsonObject;
import run.mone.hive.roles.ReactorRole;

/**
 * TavilySearchTool 使用示例
 * 
 * @author assistant
 */
public class TavilySearchToolExample {
    
    public static void main(String[] args) {
        // 创建 TavilySearchTool 实例
        TavilySearchTool tool = new TavilySearchTool();
        
        // 构建请求参数
        JsonObject request = new JsonObject();
        request.addProperty("query", "who is Leo Messi?");
        request.addProperty("api_token", System.getenv("TAVILY_KEY")); // 需要替换为真实的 API Token
        request.addProperty("topic", "general");
        request.addProperty("search_depth", "basic");
        request.addProperty("max_results", 3);
        request.addProperty("include_answer", true);
        request.addProperty("include_raw_content", false);
        request.addProperty("days", 7);
        
        // 模拟调用（在实际使用中需要传入真实的 ReactorRole）
        ReactorRole role = null; // 在实际使用中应该是真实的 role 对象
        
        try {
            // 执行搜索
            JsonObject result = tool.execute(role, request);
            
            // 输出结果
            System.out.println("搜索结果:");
            System.out.println(result.toString());
            
            // 检查是否有错误
            if (result.has("error")) {
                System.err.println("搜索失败: " + result.get("error").getAsString());
                return;
            }
            
            // 输出直接答案（如果有）
            if (result.has("answer")) {
                System.out.println("\n直接答案:");
                System.out.println(result.get("answer").getAsString());
            }
            
            // 输出搜索结果
            if (result.has("results")) {
                System.out.println("\n搜索结果列表:");
                result.getAsJsonArray("results").forEach(item -> {
                    JsonObject resultItem = item.getAsJsonObject();
                    
                    System.out.println("---");
                    if (resultItem.has("title")) {
                        System.out.println("标题: " + resultItem.get("title").getAsString());
                    }
                    if (resultItem.has("url")) {
                        System.out.println("链接: " + resultItem.get("url").getAsString());
                    }
                    if (resultItem.has("content")) {
                        String content = resultItem.get("content").getAsString();
                        // 限制内容长度以便阅读
                        if (content.length() > 200) {
                            content = content.substring(0, 200) + "...";
                        }
                        System.out.println("内容: " + content);
                    }
                    if (resultItem.has("score")) {
                        System.out.println("相关度: " + resultItem.get("score").getAsDouble());
                    }
                });
            }
            
        } catch (Exception e) {
            System.err.println("执行搜索时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 