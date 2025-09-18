package run.mone.hive.memory.longterm.graph;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 图数据库工具类
 * 提供图数据库相关的提示词和工具方法
 * 完全基于mem0的utils.py实现
 */
@Slf4j
public class GraphUtils {
    
    /**
     * 更新图提示词模板
     */
    public static final String UPDATE_GRAPH_PROMPT = """
        You are an AI expert specializing in graph memory management and optimization. Your task is to analyze existing graph memories alongside new information, and update the relationships in the memory list to ensure the most accurate, current, and coherent representation of knowledge.
        
        Input:
        1. Existing Graph Memories: A list of current graph memories, each containing source, target, and relationship information.
        2. New Graph Memory: Fresh information to be integrated into the existing graph structure.
        
        Guidelines:
        1. Identification: Use the source and target as primary identifiers when matching existing memories with new information.
        2. Conflict Resolution:
           - If new information contradicts an existing memory:
             a) For matching source and target but differing content, update the relationship of the existing memory.
             b) If the new memory provides more recent or accurate information, update the existing memory accordingly.
        3. Comprehensive Review: Thoroughly examine each existing graph memory against the new information, updating relationships as necessary. Multiple updates may be required.
        4. Consistency: Maintain a uniform and clear style across all memories. Each entry should be concise yet comprehensive.
        5. Semantic Coherence: Ensure that updates maintain or improve the overall semantic structure of the graph.
        6. Temporal Awareness: If timestamps are available, consider the recency of information when making updates.
        7. Relationship Refinement: Look for opportunities to refine relationship descriptions for greater precision or clarity.
        8. Redundancy Elimination: Identify and merge any redundant or highly similar relationships that may result from the update.
        
        Memory Format:
        source -- RELATIONSHIP -- destination
        
        Task Details:
        ======= Existing Graph Memories:=======
        {existing_memories}
        
        ======= New Graph Memory:=======
        {new_memories}
        
        Output:
        Provide a list of update instructions, each specifying the source, target, and the new relationship to be set. Only include memories that require updates.
        """;
    
    /**
     * 提取关系提示词模板
     */
    public static final String EXTRACT_RELATIONS_PROMPT = """
        
        You are an advanced algorithm designed to extract structured information from text to construct knowledge graphs. Your goal is to capture comprehensive and accurate information. Follow these key principles:
        
        1. Extract only explicitly stated information from the text.
        2. Establish relationships among the entities provided.
        3. Use "USER_ID" as the source entity for any self-references (e.g., "I," "me," "my," etc.) in user messages.
        CUSTOM_PROMPT
        
        Relationships:
            - Use consistent, general, and timeless relationship types.
            - Example: Prefer "professor" over "became_professor."
            - Relationships should only be established among the entities explicitly mentioned in the user message.
        
        Entity Consistency:
            - Ensure that relationships are coherent and logically align with the context of the message.
            - Maintain consistent naming for entities across the extracted data.
        
        Strive to construct a coherent and easily understandable knowledge graph by establishing all the relationships among the entities and adherence to the user's context.
        
        Adhere strictly to these guidelines to ensure high-quality knowledge graph extraction.""";
    
    /**
     * 删除关系系统提示词
     */
    public static final String DELETE_RELATIONS_SYSTEM_PROMPT = """
        You are a graph memory manager specializing in identifying, managing, and optimizing relationships within graph-based memories. Your primary task is to analyze a list of existing relationships and determine which ones should be deleted based on the new information provided.
        Input:
        1. Existing Graph Memories: A list of current graph memories, each containing source, relationship, and destination information.
        2. New Text: The new information to be integrated into the existing graph structure.
        3. Use "USER_ID" as node for any self-references (e.g., "I," "me," "my," etc.) in user messages.
        
        Guidelines:
        1. Identification: Use the new information to evaluate existing relationships in the memory graph.
        2. Deletion Criteria: Delete a relationship only if it meets at least one of these conditions:
           - Outdated or Inaccurate: The new information is more recent or accurate.
           - Contradictory: The new information conflicts with or negates the existing information.
        3. DO NOT DELETE if their is a possibility of same type of relationship but different destination nodes.
        4. Comprehensive Analysis:
           - Thoroughly examine each existing relationship against the new information and delete as necessary.
           - Multiple deletions may be required based on the new information.
        5. Semantic Integrity:
           - Ensure that deletions maintain or improve the overall semantic structure of the graph.
           - Avoid deleting relationships that are NOT contradictory/outdated to the new information.
        6. Temporal Awareness: Prioritize recency when timestamps are available.
        7. Necessity Principle: Only DELETE relationships that must be deleted and are contradictory/outdated to the new information to maintain an accurate and coherent memory graph.
        
        Note: DO NOT DELETE if their is a possibility of same type of relationship but different destination nodes. 
        
        For example: 
        Existing Memory: alice -- loves_to_eat -- pizza
        New Information: Alice also loves to eat burger.
        
        Do not delete in the above example because there is a possibility that Alice loves to eat both pizza and burger.
        
        Memory Format:
        source -- relationship -- destination
        
        Provide a list of deletion instructions, each specifying the relationship to be deleted.
        """;
    
    /**
     * 生成更新图记忆的提示词
     * 
     * @param existingMemories 现有图记忆字符串
     * @param newMemories 新图记忆字符串
     * @return 格式化的提示词
     */
    public static String getUpdateGraphPrompt(String existingMemories, String newMemories) {
        return UPDATE_GRAPH_PROMPT
            .replace("{existing_memories}", existingMemories != null ? existingMemories : "")
            .replace("{new_memories}", newMemories != null ? newMemories : "");
    }
    
    /**
     * 生成提取关系的提示词
     * 
     * @param customPrompt 自定义提示词（可选）
     * @return 格式化的提示词
     */
    public static String getExtractRelationsPrompt(String customPrompt) {
        String prompt = EXTRACT_RELATIONS_PROMPT;
        if (customPrompt != null && !customPrompt.trim().isEmpty()) {
            prompt = prompt.replace("CUSTOM_PROMPT", "\n" + customPrompt);
        } else {
            prompt = prompt.replace("CUSTOM_PROMPT", "");
        }
        return prompt;
    }
    
    /**
     * 生成删除关系的消息
     * 
     * @param existingMemoriesString 现有记忆字符串
     * @param data 新数据
     * @param userId 用户ID
     * @return 系统提示词和用户消息的数组
     */
    public static String[] getDeleteMessages(String existingMemoriesString, String data, String userId) {
        String systemPrompt = DELETE_RELATIONS_SYSTEM_PROMPT.replace("USER_ID", userId != null ? userId : "USER");
        String userMessage = String.format("Here are the existing memories: %s \n\n New Information: %s", 
            existingMemoriesString != null ? existingMemoriesString : "", 
            data != null ? data : "");
        
        return new String[]{systemPrompt, userMessage};
    }
    
    /**
     * 格式化图记忆为字符串
     * 
     * @param memories 图记忆列表
     * @return 格式化的字符串
     */
    public static String formatMemoriesToString(List<Map<String, Object>> memories) {
        if (memories == null || memories.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> memory : memories) {
            String source = (String) memory.get("source");
            String relationship = (String) memory.get("relationship");
            String destination = (String) memory.get("destination");
            
            if (source != null && relationship != null && destination != null) {
                sb.append(String.format("%s -- %s -- %s\n", source, relationship, destination));
            }
        }
        
        return sb.toString().trim();
    }
    
    /**
     * 解析图记忆字符串为结构化数据
     * 
     * @param memoriesString 格式化的图记忆字符串
     * @return 解析后的图记忆列表
     */
    public static List<Map<String, Object>> parseMemoriesFromString(String memoriesString) {
        List<Map<String, Object>> memories = new java.util.ArrayList<>();
        
        if (memoriesString == null || memoriesString.trim().isEmpty()) {
            return memories;
        }
        
        String[] lines = memoriesString.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            // 解析格式: source -- relationship -- destination
            String[] parts = line.split("\\s*--\\s*");
            if (parts.length == 3) {
                Map<String, Object> memory = new java.util.HashMap<>();
                memory.put("source", parts[0].trim());
                memory.put("relationship", parts[1].trim());
                memory.put("destination", parts[2].trim());
                memories.add(memory);
            }
        }
        
        return memories;
    }
    
    /**
     * 验证图实体的完整性
     * 
     * @param source 源节点
     * @param destination 目标节点
     * @param relationship 关系
     * @return 是否有效
     */
    public static boolean validateGraphEntity(String source, String destination, String relationship) {
        return source != null && !source.trim().isEmpty() &&
               destination != null && !destination.trim().isEmpty() &&
               relationship != null && !relationship.trim().isEmpty();
    }
    
    /**
     * 清理和标准化实体名称
     * 
     * @param entityName 原始实体名称
     * @return 清理后的实体名称
     */
    public static String cleanEntityName(String entityName) {
        if (entityName == null) {
            return "";
        }
        
        // 移除多余的空格和特殊字符
        String cleaned = entityName.trim()
            .replaceAll("\\s+", " ")
            .replaceAll("[\"'`]", "");
        
        return cleaned;
    }
    
    /**
     * 清理和标准化关系名称
     * 
     * @param relationshipName 原始关系名称
     * @return 清理后的关系名称
     */
    public static String cleanRelationshipName(String relationshipName) {
        if (relationshipName == null) {
            return "";
        }
        
        // 移除多余的空格，转为小写，用下划线连接
        String cleaned = relationshipName.trim()
            .toLowerCase()
            .replaceAll("\\s+", "_")
            .replaceAll("[^a-zA-Z0-9_]", "");
        
        return cleaned;
    }
    
    /**
     * 检查两个图记忆是否重复
     * 
     * @param memory1 记忆1
     * @param memory2 记忆2
     * @return 是否重复
     */
    public static boolean isDuplicateMemory(Map<String, Object> memory1, Map<String, Object> memory2) {
        if (memory1 == null || memory2 == null) {
            return false;
        }
        
        String source1 = cleanEntityName((String) memory1.get("source"));
        String dest1 = cleanEntityName((String) memory1.get("destination"));
        String rel1 = cleanRelationshipName((String) memory1.get("relationship"));
        
        String source2 = cleanEntityName((String) memory2.get("source"));
        String dest2 = cleanEntityName((String) memory2.get("destination"));
        String rel2 = cleanRelationshipName((String) memory2.get("relationship"));
        
        return source1.equals(source2) && dest1.equals(dest2) && rel1.equals(rel2);
    }
    
    /**
     * 去重图记忆列表
     * 
     * @param memories 原始图记忆列表
     * @return 去重后的图记忆列表
     */
    public static List<Map<String, Object>> deduplicateMemories(List<Map<String, Object>> memories) {
        if (memories == null || memories.isEmpty()) {
            return memories;
        }
        
        List<Map<String, Object>> deduplicated = new java.util.ArrayList<>();
        
        for (Map<String, Object> memory : memories) {
            boolean isDuplicate = false;
            for (Map<String, Object> existing : deduplicated) {
                if (isDuplicateMemory(memory, existing)) {
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                deduplicated.add(memory);
            }
        }
        
        return deduplicated;
    }
    
    /**
     * 统计图记忆的节点和关系数量
     * 
     * @param memories 图记忆列表
     * @return 统计信息
     */
    public static Map<String, Object> getGraphStats(List<Map<String, Object>> memories) {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        if (memories == null || memories.isEmpty()) {
            stats.put("total_memories", 0);
            stats.put("unique_nodes", 0);
            stats.put("unique_relationships", 0);
            return stats;
        }
        
        java.util.Set<String> nodes = new java.util.HashSet<>();
        java.util.Set<String> relationships = new java.util.HashSet<>();
        
        for (Map<String, Object> memory : memories) {
            String source = (String) memory.get("source");
            String destination = (String) memory.get("destination");
            String relationship = (String) memory.get("relationship");
            
            if (source != null) nodes.add(cleanEntityName(source));
            if (destination != null) nodes.add(cleanEntityName(destination));
            if (relationship != null) relationships.add(cleanRelationshipName(relationship));
        }
        
        stats.put("total_memories", memories.size());
        stats.put("unique_nodes", nodes.size());
        stats.put("unique_relationships", relationships.size());
        stats.put("node_list", new java.util.ArrayList<>(nodes));
        stats.put("relationship_types", new java.util.ArrayList<>(relationships));
        
        return stats;
    }
}