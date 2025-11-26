package run.mone.hive.memory.longterm.graph.tools;

/**
 * Graph tools and tool definitions matching Python mem0 implementation.
 * Corresponds to mem0/graphs/tools.py
 */
public class GraphTools {

    /**
     * Tool for establishing relationships between entities.
     * Matches Python RELATIONS_TOOL
     */
    public static final String RELATIONS_TOOL = """
            {
                "type": "function",
                "function": {
                    "name": "establish_relationships",
                    "description": "Establish relationships among the entities based on the provided text.",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "entities": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "source": {"type": "string", "description": "The source entity of the relationship."},
                                        "relationship": {"type": "string", "description": "The relationship between the source and destination entities."},
                                        "destination": {"type": "string", "description": "The destination entity of the relationship."}
                                    },
                                    "required": ["source", "relationship", "destination"],
                                    "additionalProperties": false
                                }
                            }
                        },
                        "required": ["entities"],
                        "additionalProperties": false
                    }
                }
            }
            """;

    /**
     * Structured version of RELATIONS_TOOL for structured LLM providers.
     * Matches Python RELATIONS_STRUCT_TOOL
     */
    public static final String RELATIONS_STRUCT_TOOL = """
            {
                "type": "function",
                "function": {
                    "name": "establish_relationships",
                    "description": "Establish relationships among the entities based on the provided text.",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "entities": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "source": {"type": "string", "description": "The source entity of the relationship."},
                                        "relationship": {"type": "string", "description": "The relationship between the source and destination entities."},
                                        "destination": {"type": "string", "description": "The destination entity of the relationship."}
                                    },
                                    "required": ["source", "relationship", "destination"],
                                    "additionalProperties": false
                                }
                            }
                        },
                        "required": ["entities"],
                        "additionalProperties": false
                    }
                }
            }
            """;

    /**
     * Tool for extracting entities from text.
     * Matches Python EXTRACT_ENTITIES_TOOL
     */
    public static final String EXTRACT_ENTITIES_TOOL = """
            {
                "type": "function",
                "function": {
                    "name": "extract_entities",
                    "description": "Extract entities and their types from the given text.",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "entities": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "entity": {"type": "string", "description": "The entity name."},
                                        "entity_type": {"type": "string", "description": "The type of the entity."}
                                    },
                                    "required": ["entity", "entity_type"],
                                    "additionalProperties": false
                                }
                            }
                        },
                        "required": ["entities"],
                        "additionalProperties": false
                    }
                }
            }
            """;

    /**
     * Structured version of EXTRACT_ENTITIES_TOOL for structured LLM providers.
     * Matches Python EXTRACT_ENTITIES_STRUCT_TOOL
     */
    public static final String EXTRACT_ENTITIES_STRUCT_TOOL = """
            {
                "type": "function",
                "function": {
                    "name": "extract_entities",
                    "description": "Extract entities and their types from the given text.",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "entities": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "entity": {"type": "string", "description": "The entity name."},
                                        "entity_type": {"type": "string", "description": "The type of the entity."}
                                    },
                                    "required": ["entity", "entity_type"],
                                    "additionalProperties": false
                                }
                            }
                        },
                        "required": ["entities"],
                        "additionalProperties": false
                    }
                }
            }
            """;

    /**
     * Tool for deleting graph memory.
     * Matches Python DELETE_MEMORY_TOOL_GRAPH
     */
    public static final String DELETE_MEMORY_TOOL_GRAPH = """
            {
                "type": "function",
                "function": {
                    "name": "delete_graph_memory",
                    "description": "Delete a specific relationship from the graph memory.",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "source": {"type": "string", "description": "The source entity of the relationship to delete."},
                            "relationship": {"type": "string", "description": "The relationship type to delete."},
                            "destination": {"type": "string", "description": "The destination entity of the relationship to delete."}
                        },
                        "required": ["source", "relationship", "destination"],
                        "additionalProperties": false
                    }
                }
            }
            """;

    /**
     * Structured version of DELETE_MEMORY_TOOL_GRAPH for structured LLM providers.
     * Matches Python DELETE_MEMORY_STRUCT_TOOL_GRAPH
     */
    public static final String DELETE_MEMORY_STRUCT_TOOL_GRAPH = """
            {
                "type": "function",
                "function": {
                    "name": "delete_graph_memory",
                    "description": "Delete a specific relationship from the graph memory.",
                    "parameters": {
                        "type": "object",
                        "properties": {
                            "source": {"type": "string", "description": "The source entity of the relationship to delete."},
                            "relationship": {"type": "string", "description": "The relationship type to delete."},
                            "destination": {"type": "string", "description": "The destination entity of the relationship to delete."}
                        },
                        "required": ["source", "relationship", "destination"],
                        "additionalProperties": false
                    }
                }
            }
            """;

    /**
     * No-operation tool for when no graph changes are needed.
     * Matches Python NOOP_TOOL
     */
    public static final String NOOP_TOOL = """
            {
                "type": "function",
                "function": {
                    "name": "noop",
                    "description": "No operation should be performed to the graph entities. This function is called when the system determines that no changes or additions are necessary based on the current input or context.",
                    "parameters": {
                        "type": "object",
                        "properties": {},
                        "required": [],
                        "additionalProperties": false
                    }
                }
            }
            """;
}