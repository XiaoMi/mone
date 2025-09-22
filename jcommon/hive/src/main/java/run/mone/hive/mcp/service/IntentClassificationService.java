package run.mone.hive.mcp.service;

import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Message;

import java.util.Arrays;

/**
 * @author goodjava@qq.com
 * @date 2025/6/6 14:30
 * 意图分类服务 - 封装所有二分类逻辑
 */
@Slf4j
public class IntentClassificationService {

    /**
     * 通用的意图分类方法
     * @param version 模型版本
     * @param modelType 模型类型
     * @param releaseServiceName 发布服务名称
     * @param msg 用户消息
     * @return 分类结果
     */
    public String getIntentClassification(String version, String modelType, String releaseServiceName, Message msg) {
        try {
            LLM llm = new LLM(LLMConfig.builder()
                    .llmProvider(LLMProvider.CLOUDML_CLASSIFY)
                    .url(System.getenv("ATLAS_URL"))
                    .build());
            
            String classify = llm.getClassifyScore(modelType, version, Arrays.asList(msg.getContent()), 1, releaseServiceName);
            classify = JsonParser.parseString(classify)
                    .getAsJsonObject()
                    .get("results")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .get("label")
                    .getAsString();
            
            log.debug("意图分类结果: {} -> {}", msg.getContent(), classify);
            return classify;
        } catch (Exception e) {
            log.error("意图分类失败: {}", e.getMessage(), e);
            return "未知";
        }
    }

    /**
     * 网络查询意图分类
     * @param webQuery 网络查询配置
     * @param msg 用户消息
     * @return 分类结果
     */
    public String getWebQueryClassification(WebQuery webQuery, Message msg) {
        return getIntentClassification(
            webQuery.getVersion(), 
            webQuery.getModelType(), 
            webQuery.getReleaseServiceName(), 
            msg
        );
    }

    /**
     * RAG意图分类
     * @param rag RAG配置
     * @param msg 用户消息
     * @return 分类结果
     */
    public String getRagClassification(Rag rag, Message msg) {
        return getIntentClassification(
            rag.getVersion(), 
            rag.getModelType(), 
            rag.getReleaseServiceName(), 
            msg
        );
    }

    /**
     * 知识库查询意图分类
     * @param knowledgeBaseQuery 知识库查询配置
     * @param msg 用户消息
     * @return 分类结果
     */
    public String getKnowledgeBaseQueryClassification(KnowledgeBaseQuery knowledgeBaseQuery, Message msg) {
        // TODO 简单的关键词匹配逻辑
        String content = msg.getContent().toLowerCase();
        if (content.contains("什么是") || content.contains("介绍") || content.contains("解释")
                || content.contains("定义") || content.contains("概念") || content.contains("功能")) {
            return "是";
        }
        return "否";
    }

    /**
     * 打断意图分类
     * @param interruptQuery 打断查询配置
     * @param msg 用户消息
     * @return 分类结果
     */
    public String getInterruptClassification(InterruptQuery interruptQuery, Message msg) {
        return getIntentClassification(
            interruptQuery.getVersion(), 
            interruptQuery.getModelType(), 
            interruptQuery.getReleaseServiceName(), 
            msg
        );
    }

    /**
     * 检查是否需要网络搜索
     * @param webQuery 网络查询配置
     * @param msg 用户消息
     * @return true如果需要网络搜索
     */
    public boolean shouldPerformWebQuery(WebQuery webQuery, Message msg) {
        if (!webQuery.isAutoWebQuery()) {
            return false;
        }
        
        try {
            String classify = getWebQueryClassification(webQuery, msg);
            return !"不需要搜索网络".equals(classify);
        } catch (Exception e) {
            log.error("网络搜索意图判断失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查是否需要RAG查询
     * @param rag RAG配置
     * @param msg 用户消息
     * @return true如果需要RAG查询
     */
    public boolean shouldPerformRagQuery(Rag rag, Message msg) {
        if (!rag.isAutoRag()) {
            return false;
        }
        
        try {
            String classify = getRagClassification(rag, msg);
            return "是".equals(classify);
        } catch (Exception e) {
            log.error("RAG查询意图判断失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查是否需要知识库查询
     * @param knowledgeBaseQuery 知识库查询配置
     * @param msg 用户消息
     * @return true如果需要知识库查询
     */
    public boolean shouldPerformKnowledgeBaseQuery(KnowledgeBaseQuery knowledgeBaseQuery, Message msg) {
        if (!knowledgeBaseQuery.isAutoQuery()) {
            return false;
        }
        
        try {
            String classify = getKnowledgeBaseQueryClassification(knowledgeBaseQuery, msg);
            return "是".equals(classify);
        } catch (Exception e) {
            log.error("知识库查询意图判断失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查是否需要打断执行
     * @param interruptQuery 打断查询配置
     * @param msg 用户消息
     * @return true如果需要打断
     */
    public boolean shouldInterruptExecution(InterruptQuery interruptQuery, Message msg) {
        if (!interruptQuery.isAutoInterruptQuery()) {
            return false;
        }
        
        try {
            String classify = getInterruptClassification(interruptQuery, msg);
            return "打断".equals(classify);
        } catch (Exception e) {
            log.error("打断意图判断失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
