package run.mone.hive.memory.longterm.graph.utils;

import java.util.List;
import java.util.Map;

/**
 * Graph utilities and prompts matching Python mem0 implementation.
 * Corresponds to mem0/graphs/utils.py
 */
public class GraphUtils {

    /**
     * Prompt for extracting relationships between entities.
     * Matches Python EXTRACT_RELATIONS_PROMPT
     */
    public static final String EXTRACT_RELATIONS_PROMPT = """
            You are an advanced algorithm designed to extract structured information from text to construct knowledge graphs. Your goal is to capture comprehensive and accurate information. Follow these key principles:

            1. Extract only explicitly stated information from the text.
            2. Establish relationships among the entities provided.
            3. Use "USER_ID" as the source entity for any self-references (e.g., "I," "me," "my," etc.) in user messages.
            CUSTOM_PROMPT

            Relationships:
            - Identify clear, explicit relationships between entities
            - Use descriptive relationship types (e.g., WORKS_AT, LIVES_IN, KNOWS, etc.)
            - Ensure relationships are bidirectional where appropriate
            - Focus on factual, observable connections

            Entity Guidelines:
            - Use the exact entity names provided in the entity list
            - Normalize entity names (lowercase, underscore-separated)
            - Map self-references to USER_ID

            You can response with the entities in the text with following JSON format, and return json only!!!:
            {
                "entities": [
                    {
                        "source": "The source entity of the relationship.",
                        "relationship": "The relationship between the source and destination entities.",
                        "destination": "The destination entity of the relationship."
                    }
                ]
            }

            source: the source entity of the relationship
            relationship: the relationship between the source and destination entities
            destination: the destination entity of the relationship
            """;

    /**
     * Prompt for extracting entities from text.
     * Used in entity extraction phase.
     */
    public static final String EXTRACT_ENTITIES_PROMPT = """
            You are a smart assistant who understands entities and their types in a given text.
            If user message contains self reference such as 'I', 'me', 'my' etc. then use USER_ID as the source entity.
            Extract all the entities from the text. ***DO NOT*** answer the question itself if the given text is a question.

            If you cannot response with tool_calls, you can response with the entities in the text with following JSON format, and return json only!!!:
            {
                "entities": [
                    {
                        "entity": "entity_name",
                        "entity_type": "entity_type"
                    }
                ]
            }

            entity_name: the name of the entity
            entity_type: the type of the entity
            """;

    /**
     * Prompt for updating existing graph memories.
     * Matches Python UPDATE_MEMORY_PROMPT
     */
    public static final String UPDATE_MEMORY_PROMPT = """
            You are a memory update specialist for a graph-based memory system. Your task is to analyze existing graph memories and update them based on new information provided. You will receive:

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
     * Format entities for display or processing.
     * Matches Python format_entities function
     *
     * @param entities List of entity maps with source, relationship, destination
     * @return Formatted string representation
     */
    public static String formatEntities(List<Map<String, Object>> entities) {
        if (entities == null || entities.isEmpty()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        for (Map<String, Object> entity : entities) {
            String source = entity.get("source") != null ? entity.get("source").toString() : "";
            String relationship = entity.get("relationship") != null ? entity.get("relationship").toString() : "";
            String destination = entity.get("destination") != null ? entity.get("destination").toString() : "";

            formatted.append(source)
                    .append(" -- ")
                    .append(relationship)
                    .append(" -- ")
                    .append(destination)
                    .append("\n");
        }

        return formatted.toString().trim();
    }

    /**
     * Get delete messages for LLM prompting.
     * Matches Python get_delete_messages function
     *
     * @param searchOutputString Formatted search output
     * @param data New data to compare against
     * @param userIdentity User identification string
     * @return Array containing [system_prompt, user_prompt]
     */
    public static String[] getDeleteMessages(String searchOutputString, String data, String userIdentity) {
        String systemPrompt = """
                You are a memory deletion specialist for a graph-based memory system. Your task is to identify which existing graph relationships should be deleted based on new information.

                Analyze the existing relationships and determine if any should be removed because:
                1. The new information contradicts them
                2. The new information makes them obsolete
                3. The new information provides more accurate alternatives

                User Identity: """ + userIdentity + """

                Guidelines:
                - Only mark relationships for deletion if there's clear evidence they are incorrect or obsolete
                - Be conservative - when in doubt, don't delete
                - Consider temporal aspects - newer information may supersede older information
                - Focus on factual contradictions, not subjective differences

                You can response with the relationships to be deleted in the text with following JSON format, and return json only!!!:
                {
                    "toBeDeleted": [
                        {
                            "source": "The source entity of the relationship to be deleted.",
                            "relationship": "The relationship between the source and destination entities to be deleted.",
                            "destination": "The destination entity of the relationship to be deleted."
                        }
                    ]
                }
                """;

        String userPrompt = String.format("""
                Existing relationships:
                %s

                New information:
                %s

                Identify which existing relationships should be deleted based on the new information.
                """, searchOutputString, data);

        return new String[]{systemPrompt, userPrompt};
    }

    /**
     * Normalize entity names by converting to lowercase and replacing spaces with underscores.
     * Matches Python entity normalization logic
     *
     * @param entityName The entity name to normalize
     * @return Normalized entity name
     */
    public static String normalizeEntityName(String entityName) {
        if (entityName == null) {
            return "";
        }
        return entityName.toLowerCase().replace(" ", "_");
    }

    /**
     * Replace placeholders in prompts with actual values.
     *
     * @param prompt The prompt template
     * @param userIdentity User identification string
     * @param customPrompt Custom prompt to insert (can be null)
     * @return Processed prompt with replacements
     */
    public static String processPrompt(String prompt, String userIdentity, String customPrompt) {
        String processed = prompt.replace("USER_ID", userIdentity);

        if (customPrompt != null && !customPrompt.trim().isEmpty()) {
            processed = processed.replace("CUSTOM_PROMPT", "4. " + customPrompt);
        } else {
            processed = processed.replace("CUSTOM_PROMPT", "");
        }

        return processed;
    }

    /**
     * Build user identity string from filters.
     * Matches Python user identity composition logic
     *
     * @param filters Map containing user_id, agent_id, run_id
     * @return Formatted user identity string
     */
    public static String buildUserIdentity(Map<String, Object> filters) {
        StringBuilder identity = new StringBuilder();
        identity.append("user_id: ").append(filters.get("user_id"));

        if (filters.get("agent_id") != null) {
            identity.append(", agent_id: ").append(filters.get("agent_id"));
        }

        if (filters.get("run_id") != null) {
            identity.append(", run_id: ").append(filters.get("run_id"));
        }

        return identity.toString();
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
} 