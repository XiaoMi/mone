package run.mone.hive.workspace;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import run.mone.hive.utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Workspace path resolution with usage tracking
 * 
 * This class provides workspace-aware path resolution similar to Cline's WorkspaceResolver.
 * It handles single workspace scenarios and provides foundation for multi-workspace support.
 * 
 * Key features:
 * - Resolve relative paths against workspace root
 * - Track usage patterns for debugging and optimization
 * - Provide safe path operations within workspace boundaries
 * - Handle cross-platform path differences
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class WorkspaceResolver {
    
    private final Map<String, UsageStats> usageMap = new ConcurrentHashMap<>();
    private final boolean traceEnabled;
    private final String defaultWorkspacePath;
    
    /**
     * Usage statistics for tracking path resolution patterns
     */
    public static class UsageStats {
        private int count = 0;
        private final Map<String, Integer> examplePaths = new HashMap<>();
        private long lastUsed = System.currentTimeMillis();
        
        public void incrementUsage(String examplePath) {
            count++;
            lastUsed = System.currentTimeMillis();
            examplePaths.merge(examplePath, 1, Integer::sum);
        }
        
        public int getCount() { return count; }
        public Map<String, Integer> getExamplePaths() { return examplePaths; }
        public long getLastUsed() { return lastUsed; }
    }
    
    public WorkspaceResolver() {
        this(System.getProperty("user.dir"));
    }
    
    public WorkspaceResolver(String defaultWorkspacePath) {
        this.defaultWorkspacePath = defaultWorkspacePath;
        this.traceEnabled = "true".equalsIgnoreCase(System.getProperty("hive.workspace.trace")) ||
                           "development".equalsIgnoreCase(System.getProperty("hive.env"));
    }
    
    /**
     * Resolve a relative path against the workspace root
     * This is the main method that mirrors Cline's resolveWorkspacePath functionality
     * 
     * @param workspacePath The workspace root directory
     * @param relativePath The relative path to resolve
     * @param context Component/handler name for tracking usage
     * @return Absolute path string
     */
    public String resolveWorkspacePath(String workspacePath, String relativePath, String context) {
        // Track usage for debugging and optimization
        if (StringUtils.isNotBlank(context)) {
            trackUsage(context, relativePath);
            
            if (traceEnabled) {
                log.debug("[WORKSPACE-TRACE] {}: resolving '{}' against '{}'", 
                         context, relativePath, workspacePath);
            }
        }
        
        return PathUtils.resolveWorkspacePath(workspacePath, relativePath, context);
    }
    
    /**
     * Resolve path using default workspace
     */
    public String resolveWorkspacePath(String relativePath, String context) {
        return resolveWorkspacePath(defaultWorkspacePath, relativePath, context);
    }
    
    /**
     * Get basename with usage tracking
     * This mirrors Cline's getBasename functionality
     */
    public String getBasename(String filePath, String context) {
        if (StringUtils.isNotBlank(context)) {
            trackUsage(context, filePath);
            
            if (traceEnabled) {
                log.debug("[WORKSPACE-TRACE] {}: getting basename for '{}'", context, filePath);
            }
        }
        
        return PathUtils.getBasename(filePath);
    }
    
    /**
     * Get readable path for display purposes
     */
    public String getReadablePath(String workspacePath, String filePath) {
        return PathUtils.getReadablePath(workspacePath, filePath);
    }
    
    /**
     * Check if path is within workspace
     */
    public boolean isLocatedInWorkspace(String workspacePath, String pathToCheck) {
        return PathUtils.isLocatedInWorkspace(workspacePath, pathToCheck);
    }
    
    /**
     * Convert to relative path within workspace
     */
    public String asRelativePath(String workspacePath, String filePath) {
        return PathUtils.asRelativePath(workspacePath, filePath);
    }
    
    /**
     * Validate that a path is safe to operate on
     */
    public boolean isSafePath(String workspacePath, String pathToCheck) {
        return PathUtils.isSafePath(workspacePath, pathToCheck);
    }
    
    /**
     * Track usage statistics for a given context and path
     */
    private void trackUsage(String context, String examplePath) {
        if (StringUtils.isBlank(context) || StringUtils.isBlank(examplePath)) {
            return;
        }
        
        usageMap.computeIfAbsent(context, k -> new UsageStats())
                .incrementUsage(examplePath);
    }
    
    /**
     * Get usage statistics for analysis
     */
    public Map<String, UsageStats> getUsageStats() {
        return new HashMap<>(usageMap);
    }
    
    /**
     * Clear usage statistics (useful for testing)
     */
    public void clearUsageStats() {
        usageMap.clear();
    }
    
    /**
     * Generate usage report for debugging
     */
    public String generateUsageReport() {
        if (usageMap.isEmpty()) {
            return "No workspace path usage recorded.";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("Workspace Path Usage Report\n");
        report.append("===========================\n\n");
        
        usageMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getCount(), e1.getValue().getCount()))
                .forEach(entry -> {
                    String context = entry.getKey();
                    UsageStats stats = entry.getValue();
                    
                    report.append(String.format("Context: %s\n", context));
                    report.append(String.format("  Total calls: %d\n", stats.getCount()));
                    report.append(String.format("  Last used: %tF %tT\n", stats.getLastUsed(), stats.getLastUsed()));
                    report.append("  Example paths:\n");
                    
                    stats.getExamplePaths().entrySet().stream()
                            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                            .limit(5)
                            .forEach(pathEntry -> 
                                report.append(String.format("    %s (used %d times)\n", 
                                             pathEntry.getKey(), pathEntry.getValue())));
                    report.append("\n");
                });
        
        return report.toString();
    }
    
    /**
     * Get the default workspace path
     */
    public String getDefaultWorkspacePath() {
        return defaultWorkspacePath;
    }
    
    /**
     * Check if tracing is enabled
     */
    public boolean isTraceEnabled() {
        return traceEnabled;
    }
    
    /**
     * Validate workspace path exists and is accessible
     */
    public boolean isValidWorkspace(String workspacePath) {
        if (StringUtils.isBlank(workspacePath)) {
            return false;
        }
        
        try {
            File workspace = new File(workspacePath);
            return workspace.exists() && workspace.isDirectory() && workspace.canRead();
        } catch (Exception e) {
            log.warn("Error validating workspace: {}", workspacePath, e);
            return false;
        }
    }
    
    /**
     * Get workspace root for a given file path
     * This method can be extended to support multiple workspace roots
     */
    public String getWorkspaceRoot(String filePath) {
        // For now, return the default workspace
        // In future versions, this could search for workspace markers like .git, pom.xml, etc.
        return defaultWorkspacePath;
    }
    
    /**
     * Find workspace root by looking for common markers
     */
    public String findWorkspaceRoot(String startPath) {
        if (StringUtils.isBlank(startPath)) {
            return defaultWorkspacePath;
        }
        
        Path current = Paths.get(startPath);
        if (!current.isAbsolute()) {
            current = Paths.get(defaultWorkspacePath).resolve(startPath);
        }
        
        // Look for workspace markers
        String[] markers = {".git", ".hg", ".svn", "pom.xml", "package.json", "Cargo.toml", "go.mod"};
        
        while (current != null && current.getParent() != null) {
            for (String marker : markers) {
                if (current.resolve(marker).toFile().exists()) {
                    return PathUtils.normalizePathSeparators(current.toString());
                }
            }
            current = current.getParent();
        }
        
        // Fallback to default workspace
        return defaultWorkspacePath;
    }
}
