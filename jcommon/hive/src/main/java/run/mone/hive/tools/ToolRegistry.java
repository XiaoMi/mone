package run.mone.hive.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class ToolRegistry {
    private Map<String, Tool> tools = new ConcurrentHashMap<>();
    private Map<String, Map<String, Tool>> toolsByTags = new ConcurrentHashMap<>();
    
    private static final ToolRegistry INSTANCE = new ToolRegistry();
    private static final String TOOL_SCHEMA_PATH = "schemas/tools/";
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    
    private ToolRegistry() {}
    
    public static ToolRegistry getInstance() {
        return INSTANCE;
    }

    public void registerTool(
            String toolName,
            String toolPath,
            Map<String, Object> schemas,
            String schemaPath,
            String toolCode,
            List<String> tags,
            Object toolSourceObject,
            List<String> includeFunctions,
            boolean verbose
    ) {
        if (hasTool(toolName)) {
            return;
        }

        schemaPath = schemaPath.isEmpty() ? 
                TOOL_SCHEMA_PATH + toolName + ".yml" : schemaPath;

        if (schemas == null) {
            schemas = makeSchema(toolSourceObject, includeFunctions, schemaPath);
        }

        if (schemas == null || schemas.isEmpty()) {
            return;
        }

        schemas.put("tool_path", toolPath);
        
        try {
            validateSchema(schemas);
        } catch (Exception e) {
            log.warn("Schema validation failed for {}: {}", toolName, e.getMessage());
        }

        tags = tags != null ? tags : new ArrayList<>();
        Tool tool = new Tool(toolName, toolPath, schemas, toolCode, tags);
        tools.put(toolName, tool);
        
        for (String tag : tags) {
            toolsByTags.computeIfAbsent(tag, k -> new HashMap<>())
                    .put(toolName, tool);
        }

        if (verbose) {
            log.info("{} registered", toolName);
            log.info("Schema made at {}, can be used for checking", schemaPath);
        }
    }

    public boolean hasTool(String key) {
        return tools.containsKey(key);
    }

    public Tool getTool(String key) {
        return tools.get(key);
    }

    public Map<String, Tool> getToolsByTag(String tag) {
        return toolsByTags.getOrDefault(tag, new HashMap<>());
    }

    public Map<String, Tool> getAllTools() {
        return tools;
    }

    public boolean hasToolTag(String tag) {
        return toolsByTags.containsKey(tag);
    }

    public List<String> getToolTags() {
        return new ArrayList<>(toolsByTags.keySet());
    }

    private Map<String, Object> makeSchema(Object toolSourceObject, List<String> include, String path) {
        try {
            Files.createDirectories(Paths.get(path).getParent());
            Map<String, Object> schema = ToolConvertor.convertCodeToToolSchema(
                    toolSourceObject, include);
            yamlMapper.writeValue(new File(path), schema);
            return schema;
        } catch (Exception e) {
            log.error("Failed to make schema: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private void validateSchema(Map<String, Object> schema) {
        // TODO: Implement schema validation
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RegisterTool {
        String[] tags() default {};
        String schemaPath() default "";
    }

    @Data
    public static class Tool {
        private String name;
        private String path;
        private Map<String, Object> schemas;
        private String code;
        private List<String> tags;

        public Tool(String name, String path, Map<String, Object> schemas, 
                   String code, List<String> tags) {
            this.name = name;
            this.path = path;
            this.schemas = schemas;
            this.code = code;
            this.tags = tags;
        }
    }

    public static Map<String, Tool> validateToolNames(List<String> tools) {
        if (tools == null) {
            throw new IllegalArgumentException("tools must be a list");
        }

        Map<String, Tool> validTools = new HashMap<>();
        for (String key : tools) {
            if (new File(key).isDirectory() || new File(key).isFile()) {
                validTools.putAll(registerToolsFromPath(key));
            } else if (INSTANCE.hasTool(key)) {
                validTools.put(key, INSTANCE.getTool(key));
            } else if (INSTANCE.hasToolTag(key)) {
                validTools.putAll(INSTANCE.getToolsByTag(key));
            } else {
                log.warn("Invalid tool name or tool type name: {}, skipped", key);
            }
        }
        return validTools;
    }

    public static Map<String, Tool> registerToolsFromPath(String path) {
        Map<String, Tool> toolsRegistered = new HashMap<>();
        File file = new File(path);
        
        if (file.isFile()) {
            toolsRegistered.putAll(registerToolsFromFile(path));
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    toolsRegistered.putAll(registerToolsFromFile(f.getPath()));
                }
            }
        }
        return toolsRegistered;
    }

    private static Map<String, Tool> registerToolsFromFile(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        if (!fileName.endsWith(".java") || fileName.equals("setup.java") 
                || fileName.startsWith("test")) {
            return new HashMap<>();
        }

        Map<String, Tool> registeredTools = new HashMap<>();
        try {
            String code = Files.readString(Path.of(filePath));
            Map<String, Map<String, Object>> toolSchemas = 
                    ToolConvertor.convertCodeToToolSchemaAst(code);
            
            for (Map.Entry<String, Map<String, Object>> entry : toolSchemas.entrySet()) {
                String name = entry.getKey();
                Map<String, Object> schemas = entry.getValue();
                String toolCode = (String) schemas.remove("code");
                
                INSTANCE.registerTool(name, filePath, schemas, "", toolCode, 
                        null, null, null, false);
                registeredTools.put(name, INSTANCE.getTool(name));
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", filePath, e);
        }
        return registeredTools;
    }
}
