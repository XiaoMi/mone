package run.mone.hive.memory.longterm.graph;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

/**
 * 图数据库工具定义
 * 定义LLM调用图数据库操作时使用的工具
 * 完全基于mem0的tools.py实现
 */
public class GraphTools {
    
    /**
     * 更新图记忆工具定义
     */
    public static final Map<String, Object> UPDATE_MEMORY_TOOL_GRAPH = createTool(
        "update_graph_memory",
        "Update the relationship key of an existing graph memory based on new information. This function should be called when there's a need to modify an existing relationship in the knowledge graph. The update should only be performed if the new information is more recent, more accurate, or provides additional context compared to the existing information. The source and destination nodes of the relationship must remain the same as in the existing graph memory; only the relationship itself can be updated.",
        Map.of(
            "source", Map.of(
                "type", "string",
                "description", "The identifier of the source node in the relationship to be updated. This should match an existing node in the graph."
            ),
            "destination", Map.of(
                "type", "string", 
                "description", "The identifier of the destination node in the relationship to be updated. This should match an existing node in the graph."
            ),
            "relationship", Map.of(
                "type", "string",
                "description", "The new or updated relationship between the source and destination nodes. This should be a concise, clear description of how the two nodes are connected."
            )
        ),
        Arrays.asList("source", "destination", "relationship"),
        false
    );
    
    /**
     * 添加图记忆工具定义
     */
    public static final Map<String, Object> ADD_MEMORY_TOOL_GRAPH = createTool(
        "add_graph_memory",
        "Add a new graph memory to the knowledge graph. This function creates a new relationship between two nodes, potentially creating new nodes if they don't exist.",
        Map.of(
            "source", Map.of(
                "type", "string",
                "description", "The identifier of the source node in the new relationship. This can be an existing node or a new node to be created."
            ),
            "destination", Map.of(
                "type", "string",
                "description", "The identifier of the destination node in the new relationship. This can be an existing node or a new node to be created."
            ),
            "relationship", Map.of(
                "type", "string",
                "description", "The type of relationship between the source and destination nodes. This should be a concise, clear description of how the two nodes are connected."
            ),
            "source_type", Map.of(
                "type", "string",
                "description", "The type or category of the source node. This helps in classifying and organizing nodes in the graph."
            ),
            "destination_type", Map.of(
                "type", "string",
                "description", "The type or category of the destination node. This helps in classifying and organizing nodes in the graph."
            )
        ),
        Arrays.asList("source", "destination", "relationship", "source_type", "destination_type"),
        false
    );
    
    /**
     * 删除图记忆工具定义
     */
    public static final Map<String, Object> DELETE_MEMORY_TOOL_GRAPH = createTool(
        "delete_graph_memory",
        "Delete the relationship between two nodes. This function deletes the existing relationship.",
        Map.of(
            "source", Map.of(
                "type", "string",
                "description", "The identifier of the source node in the relationship."
            ),
            "relationship", Map.of(
                "type", "string",
                "description", "The existing relationship between the source and destination nodes that needs to be deleted."
            ),
            "destination", Map.of(
                "type", "string",
                "description", "The identifier of the destination node in the relationship."
            )
        ),
        Arrays.asList("source", "relationship", "destination"),
        false
    );
    
    /**
     * 无操作工具定义
     */
    public static final Map<String, Object> NOOP_TOOL = createTool(
        "noop",
        "No operation should be performed to the graph entities. This function is called when the system determines that no changes or additions are necessary based on the current input or context. It serves as a placeholder action when no other actions are required, ensuring that the system can explicitly acknowledge situations where no modifications to the graph are needed.",
        Map.of(),
        Arrays.asList(),
        false
    );
    
    /**
     * 建立关系工具定义
     */
    public static final Map<String, Object> RELATIONS_TOOL = createTool(
        "establish_relationships",
        "Establish relationships among the entities based on the provided text.",
        Map.of(
            "entities", Map.of(
                "type", "array",
                "items", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "source", Map.of(
                            "type", "string",
                            "description", "The source entity of the relationship."
                        ),
                        "relationship", Map.of(
                            "type", "string", 
                            "description", "The relationship between the source and destination entities."
                        ),
                        "destination", Map.of(
                            "type", "string",
                            "description", "The destination entity of the relationship."
                        )
                    ),
                    "required", Arrays.asList("source", "relationship", "destination"),
                    "additionalProperties", false
                )
            )
        ),
        Arrays.asList("entities"),
        false
    );
    
    /**
     * 提取实体工具定义
     */
    public static final Map<String, Object> EXTRACT_ENTITIES_TOOL = createTool(
        "extract_entities",
        "Extract entities and their types from the text.",
        Map.of(
            "entities", Map.of(
                "type", "array",
                "description", "An array of entities with their types.",
                "items", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "entity", Map.of(
                            "type", "string",
                            "description", "The name or identifier of the entity."
                        ),
                        "entity_type", Map.of(
                            "type", "string",
                            "description", "The type or category of the entity."
                        )
                    ),
                    "required", Arrays.asList("entity", "entity_type"),
                    "additionalProperties", false
                )
            )
        ),
        Arrays.asList("entities"),
        false
    );
    
    // 结构化工具（带strict参数）
    
    /**
     * 结构化更新图记忆工具定义
     */
    public static final Map<String, Object> UPDATE_MEMORY_STRUCT_TOOL_GRAPH = createTool(
        "update_graph_memory",
        "Update the relationship key of an existing graph memory based on new information. This function should be called when there's a need to modify an existing relationship in the knowledge graph. The update should only be performed if the new information is more recent, more accurate, or provides additional context compared to the existing information. The source and destination nodes of the relationship must remain the same as in the existing graph memory; only the relationship itself can be updated.",
        Map.of(
            "source", Map.of(
                "type", "string",
                "description", "The identifier of the source node in the relationship to be updated. This should match an existing node in the graph."
            ),
            "destination", Map.of(
                "type", "string",
                "description", "The identifier of the destination node in the relationship to be updated. This should match an existing node in the graph."
            ),
            "relationship", Map.of(
                "type", "string",
                "description", "The new or updated relationship between the source and destination nodes. This should be a concise, clear description of how the two nodes are connected."
            )
        ),
        Arrays.asList("source", "destination", "relationship"),
        true
    );
    
    /**
     * 结构化添加图记忆工具定义
     */
    public static final Map<String, Object> ADD_MEMORY_STRUCT_TOOL_GRAPH = createTool(
        "add_graph_memory",
        "Add a new graph memory to the knowledge graph. This function creates a new relationship between two nodes, potentially creating new nodes if they don't exist.",
        Map.of(
            "source", Map.of(
                "type", "string",
                "description", "The identifier of the source node in the new relationship. This can be an existing node or a new node to be created."
            ),
            "destination", Map.of(
                "type", "string",
                "description", "The identifier of the destination node in the new relationship. This can be an existing node or a new node to be created."
            ),
            "relationship", Map.of(
                "type", "string",
                "description", "The type of relationship between the source and destination nodes. This should be a concise, clear description of how the two nodes are connected."
            ),
            "source_type", Map.of(
                "type", "string",
                "description", "The type or category of the source node. This helps in classifying and organizing nodes in the graph."
            ),
            "destination_type", Map.of(
                "type", "string",
                "description", "The type or category of the destination node. This helps in classifying and organizing nodes in the graph."
            )
        ),
        Arrays.asList("source", "destination", "relationship", "source_type", "destination_type"),
        true
    );
    
    /**
     * 结构化删除图记忆工具定义
     */
    public static final Map<String, Object> DELETE_MEMORY_STRUCT_TOOL_GRAPH = createTool(
        "delete_graph_memory",
        "Delete the relationship between two nodes. This function deletes the existing relationship.",
        Map.of(
            "source", Map.of(
                "type", "string",
                "description", "The identifier of the source node in the relationship."
            ),
            "relationship", Map.of(
                "type", "string",
                "description", "The existing relationship between the source and destination nodes that needs to be deleted."
            ),
            "destination", Map.of(
                "type", "string",
                "description", "The identifier of the destination node in the relationship."
            )
        ),
        Arrays.asList("source", "relationship", "destination"),
        true
    );
    
    /**
     * 结构化无操作工具定义
     */
    public static final Map<String, Object> NOOP_STRUCT_TOOL = createTool(
        "noop",
        "No operation should be performed to the graph entities. This function is called when the system determines that no changes or additions are necessary based on the current input or context. It serves as a placeholder action when no other actions are required, ensuring that the system can explicitly acknowledge situations where no modifications to the graph are needed.",
        Map.of(),
        Arrays.asList(),
        true
    );
    
    /**
     * 结构化建立关系工具定义
     */
    public static final Map<String, Object> RELATIONS_STRUCT_TOOL = createTool(
        "establish_relations",
        "Establish relationships among the entities based on the provided text.",
        Map.of(
            "entities", Map.of(
                "type", "array",
                "items", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "source", Map.of(
                            "type", "string",
                            "description", "The source entity of the relationship."
                        ),
                        "relationship", Map.of(
                            "type", "string",
                            "description", "The relationship between the source and destination entities."
                        ),
                        "destination", Map.of(
                            "type", "string",
                            "description", "The destination entity of the relationship."
                        )
                    ),
                    "required", Arrays.asList("source", "relationship", "destination"),
                    "additionalProperties", false
                )
            )
        ),
        Arrays.asList("entities"),
        true
    );
    
    /**
     * 结构化提取实体工具定义
     */
    public static final Map<String, Object> EXTRACT_ENTITIES_STRUCT_TOOL = createTool(
        "extract_entities",
        "Extract entities and their types from the text.",
        Map.of(
            "entities", Map.of(
                "type", "array",
                "description", "An array of entities with their types.",
                "items", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "entity", Map.of(
                            "type", "string",
                            "description", "The name or identifier of the entity."
                        ),
                        "entity_type", Map.of(
                            "type", "string",
                            "description", "The type or category of the entity."
                        )
                    ),
                    "required", Arrays.asList("entity", "entity_type"),
                    "additionalProperties", false
                )
            )
        ),
        Arrays.asList("entities"),
        true
    );
    
    /**
     * 获取所有工具列表
     * 
     * @param useStructured 是否使用结构化工具
     * @return 工具列表
     */
    public static List<Map<String, Object>> getAllTools(boolean useStructured) {
        if (useStructured) {
            return Arrays.asList(
                ADD_MEMORY_STRUCT_TOOL_GRAPH,
                UPDATE_MEMORY_STRUCT_TOOL_GRAPH,
                DELETE_MEMORY_STRUCT_TOOL_GRAPH,
                NOOP_STRUCT_TOOL,
                RELATIONS_STRUCT_TOOL,
                EXTRACT_ENTITIES_STRUCT_TOOL
            );
        } else {
            return Arrays.asList(
                ADD_MEMORY_TOOL_GRAPH,
                UPDATE_MEMORY_TOOL_GRAPH,
                DELETE_MEMORY_TOOL_GRAPH,
                NOOP_TOOL,
                RELATIONS_TOOL,
                EXTRACT_ENTITIES_TOOL
            );
        }
    }
    
    /**
     * 获取图操作工具
     * 
     * @param useStructured 是否使用结构化工具
     * @return 图操作工具列表
     */
    public static List<Map<String, Object>> getGraphOperationTools(boolean useStructured) {
        if (useStructured) {
            return Arrays.asList(
                ADD_MEMORY_STRUCT_TOOL_GRAPH,
                UPDATE_MEMORY_STRUCT_TOOL_GRAPH,
                DELETE_MEMORY_STRUCT_TOOL_GRAPH,
                NOOP_STRUCT_TOOL
            );
        } else {
            return Arrays.asList(
                ADD_MEMORY_TOOL_GRAPH,
                UPDATE_MEMORY_TOOL_GRAPH,
                DELETE_MEMORY_TOOL_GRAPH,
                NOOP_TOOL
            );
        }
    }
    
    /**
     * 获取实体处理工具
     * 
     * @param useStructured 是否使用结构化工具  
     * @return 实体处理工具列表
     */
    public static List<Map<String, Object>> getEntityProcessingTools(boolean useStructured) {
        if (useStructured) {
            return Arrays.asList(
                RELATIONS_STRUCT_TOOL,
                EXTRACT_ENTITIES_STRUCT_TOOL
            );
        } else {
            return Arrays.asList(
                RELATIONS_TOOL,
                EXTRACT_ENTITIES_TOOL
            );
        }
    }
    
    // 私有辅助方法
    
    /**
     * 创建工具定义
     */
    private static Map<String, Object> createTool(String name, String description, 
                                                 Map<String, Object> properties, 
                                                 List<String> required,
                                                 boolean strict) {
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");
        
        Map<String, Object> function = new HashMap<>();
        function.put("name", name);
        function.put("description", description);
        
        if (strict) {
            function.put("strict", true);
        }
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        parameters.put("properties", properties);
        parameters.put("required", required);
        parameters.put("additionalProperties", false);
        
        function.put("parameters", parameters);
        tool.put("function", function);
        
        return tool;
    }
}