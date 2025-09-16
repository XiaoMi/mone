package run.mone.hive.roles.tool.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.utils.PathUtils;
import run.mone.hive.workspace.WorkspaceResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Path resolution interceptor for tool execution
 * 
 * This interceptor automatically converts relative paths to absolute paths
 * before tool execution, ensuring file operations work correctly regardless
 * of the current working directory.
 * 
 * Key features:
 * - Automatically detect path parameters in tool calls
 * - Convert relative paths to absolute paths
 * - Maintain workspace boundaries for security
 * - Support multiple path parameter names
 * - Provide debugging and tracing capabilities
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class PathResolutionInterceptor {
    
    private static final WorkspaceResolver workspaceResolver = new WorkspaceResolver();
    
    // Common parameter names that contain file paths
    private static final Set<String> PATH_PARAMETER_NAMES = new HashSet<>(Arrays.asList(
        "path",           // Most common
        "file_path",      // Alternative naming
        "filePath",       // camelCase variant
        "directory",      // Directory paths
        "dir",           // Short form
        "folder",        // Alternative
        "location",      // Generic location
        "target",        // Target path
        "source",        // Source path
        "input",         // Input file
        "output",        // Output file
        "filename",      // File name (might include path)
        "file"           // Generic file parameter
    ));
    
    // Tools that definitely need path resolution
    private static final Set<String> PATH_DEPENDENT_TOOLS = new HashSet<>(Arrays.asList(
        "read_file",
        "write_to_file", 
        "replace_in_file",
        "list_files",
        "search_files",
        "list_code_definition_names",
        "execute_command"  // May have path-related parameters
    ));
    
    /**
     * Process tool parameters before execution, converting relative paths to absolute paths
     * 
     * @param toolName The name of the tool being executed
     * @param params The tool parameters (will be modified in place)
     * @param extraParams Additional parameters that might contain context
     * @param workspacePath The workspace root path (if null, uses current directory)
     */
    public static void resolvePathParameters(String toolName, JsonObject params, 
                                           Map<String, String> extraParams, String workspacePath) {
        if (params == null || params.size() == 0) {
            return;
        }
        
        // Use current directory if workspace not specified
        String workspace = StringUtils.isNotBlank(workspacePath) ? 
                          workspacePath : PathUtils.getCurrentWorkingDirectory();
        
        log.debug("Processing path parameters for tool '{}' in workspace '{}'", toolName, workspace);
        
        // Process each parameter
        for (Map.Entry<String, JsonElement> entry : params.entrySet()) {
            String paramName = entry.getKey();
            JsonElement paramValue = entry.getValue();
            
            if (shouldResolveParameter(toolName, paramName, paramValue)) {
                String originalPath = paramValue.getAsString();
                String resolvedPath = resolvePathParameter(originalPath, workspace, toolName, paramName);
                
                if (!originalPath.equals(resolvedPath)) {
                    params.addProperty(paramName, resolvedPath);
                    log.debug("Resolved path parameter '{}': '{}' -> '{}'", 
                             paramName, originalPath, resolvedPath);
                }
            }
        }
        
        // Also check extra parameters for paths
        if (extraParams != null) {
            for (Map.Entry<String, String> entry : extraParams.entrySet()) {
                String paramName = entry.getKey();
                String paramValue = entry.getValue();
                
                if (shouldResolveExtraParameter(paramName, paramValue)) {
                    String resolvedPath = resolvePathParameter(paramValue, workspace, toolName, paramName);
                    if (!paramValue.equals(resolvedPath)) {
                        extraParams.put(paramName, resolvedPath);
                        log.debug("Resolved extra parameter '{}': '{}' -> '{}'", 
                                 paramName, paramValue, resolvedPath);
                    }
                }
            }
        }
    }
    
    /**
     * Determine if a parameter should be resolved as a path
     */
    private static boolean shouldResolveParameter(String toolName, String paramName, JsonElement paramValue) {
        // Must be a string parameter
        if (paramValue == null || !paramValue.isJsonPrimitive() || !paramValue.getAsJsonPrimitive().isString()) {
            return false;
        }
        
        String value = paramValue.getAsString();
        if (StringUtils.isBlank(value)) {
            return false;
        }
        
        // Check if tool is known to be path-dependent
        if (PATH_DEPENDENT_TOOLS.contains(toolName)) {
            // For known tools, resolve common path parameter names
            if (PATH_PARAMETER_NAMES.contains(paramName.toLowerCase())) {
                return true;
            }
        }
        
        // Heuristic: looks like a file path?
        if (looksLikeFilePath(value)) {
            log.debug("Parameter '{}' with value '{}' looks like a file path", paramName, value);
            return true;
        }
        
        return false;
    }
    
    /**
     * Determine if an extra parameter should be resolved as a path
     */
    private static boolean shouldResolveExtraParameter(String paramName, String paramValue) {
        if (StringUtils.isBlank(paramValue)) {
            return false;
        }
        
        // Check parameter name
        if (PATH_PARAMETER_NAMES.contains(paramName.toLowerCase())) {
            return true;
        }
        
        // Check if value looks like a path
        return looksLikeFilePath(paramValue);
    }
    
    /**
     * Heuristic to determine if a string looks like a file path
     */
    private static boolean looksLikeFilePath(String value) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        
        // Avoid resolving URLs or other non-file paths
        if (value.startsWith("http://") || value.startsWith("https://") || 
            value.startsWith("ftp://") || value.startsWith("mailto:")) {
            return false;
        }
        
        // Contains path separators
        if (value.contains("/") || value.contains("\\")) {
            return true;
        }
        
        // Starts with current/parent directory markers
        if (value.startsWith("./") || value.startsWith("../") || value.equals(".") || value.equals("..")) {
            return true;
        }
        
        // Contains file extension
        if (value.matches(".*\\.[a-zA-Z0-9]{1,10}$")) {
            return true;
        }
        
        // Common file/directory names
        if (value.matches("^[a-zA-Z0-9_.-]+$") && (
            value.contains("src") || value.contains("test") || value.contains("main") ||
            value.contains("config") || value.contains("lib") || value.contains("bin") ||
            value.endsWith(".txt") || value.endsWith(".log") || value.endsWith(".tmp")
        )) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Resolve a single path parameter
     */
    private static String resolvePathParameter(String originalPath, String workspacePath, 
                                             String toolName, String paramName) {
        try {
            // If already absolute and safe, return as-is
            if (PathUtils.fileExists(originalPath) && 
                PathUtils.isLocatedInWorkspace(workspacePath, originalPath)) {
                return PathUtils.normalizePathSeparators(originalPath);
            }
            
            // Resolve relative path against workspace
            String resolvedPath = workspaceResolver.resolveWorkspacePath(
                workspacePath, originalPath, toolName + "." + paramName);
            
            // Validate the resolved path is safe
            if (!PathUtils.isSafePath(workspacePath, resolvedPath)) {
                log.warn("Resolved path '{}' is not safe within workspace '{}'", 
                        resolvedPath, workspacePath);
                return originalPath; // Return original if resolved path is unsafe
            }
            
            return resolvedPath;
            
        } catch (Exception e) {
            log.warn("Error resolving path parameter '{}' = '{}': {}", 
                    paramName, originalPath, e.getMessage());
            return originalPath; // Return original on error
        }
    }
    
    /**
     * Get the workspace resolver instance
     */
    public static WorkspaceResolver getWorkspaceResolver() {
        return workspaceResolver;
    }
    
    /**
     * Configure additional path parameter names
     */
    public static void addPathParameterName(String parameterName) {
        if (StringUtils.isNotBlank(parameterName)) {
            PATH_PARAMETER_NAMES.add(parameterName.toLowerCase());
        }
    }
    
    /**
     * Configure additional path-dependent tools
     */
    public static void addPathDependentTool(String toolName) {
        if (StringUtils.isNotBlank(toolName)) {
            PATH_DEPENDENT_TOOLS.add(toolName);
        }
    }
    
    /**
     * Get current path parameter names (for debugging)
     */
    public static Set<String> getPathParameterNames() {
        return new HashSet<>(PATH_PARAMETER_NAMES);
    }
    
    /**
     * Get current path-dependent tools (for debugging)
     */
    public static Set<String> getPathDependentTools() {
        return new HashSet<>(PATH_DEPENDENT_TOOLS);
    }
}
