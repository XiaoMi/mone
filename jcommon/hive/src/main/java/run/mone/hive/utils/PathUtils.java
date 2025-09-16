package run.mone.hive.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Path utility class inspired by Cline's path handling approach
 * 
 * This class provides utilities for path resolution, conversion between relative and absolute paths,
 * and safe path operations within workspace boundaries.
 * 
 * Key features:
 * - Convert relative paths to absolute paths for file operations
 * - Maintain consistent path separators across platforms
 * - Provide workspace-aware path resolution
 * - Safe path validation and normalization
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
@Slf4j
public class PathUtils {

    private static final String DEFAULT_WORKSPACE_PATH = System.getProperty("user.dir");
    
    /**
     * Resolve a relative path to an absolute path within the current workspace
     * This is the core method that mirrors Cline's resolveWorkspacePath functionality
     * 
     * @param workspacePath The workspace root directory (current working directory)
     * @param relativePath The relative path to resolve
     * @param context Optional context for debugging/logging
     * @return Absolute path string
     */
    public static String resolveWorkspacePath(String workspacePath, String relativePath, String context) {
        if (StringUtils.isBlank(relativePath)) {
            return workspacePath;
        }
        
        // If already absolute, return as-is (but validate it's safe)
        if (Paths.get(relativePath).isAbsolute()) {
            log.debug("[PathUtils] {} - Path is already absolute: {}", context, relativePath);
            return normalizePathSeparators(relativePath);
        }
        
        // Resolve relative path against workspace
        Path workspaceDir = Paths.get(workspacePath);
        Path resolvedPath = workspaceDir.resolve(relativePath).normalize();
        
        String absolutePath = resolvedPath.toString();
        log.debug("[PathUtils] {} - Resolved '{}' against '{}' to '{}'", 
                 context, relativePath, workspacePath, absolutePath);
        
        return normalizePathSeparators(absolutePath);
    }
    
    /**
     * Resolve relative path using default workspace (current directory)
     */
    public static String resolveWorkspacePath(String relativePath, String context) {
        return resolveWorkspacePath(DEFAULT_WORKSPACE_PATH, relativePath, context);
    }
    
    /**
     * Get a readable path for display purposes
     * This mirrors Cline's getReadablePath functionality
     * 
     * @param workspacePath The workspace root directory
     * @param filePath The file path to make readable (can be relative or absolute)
     * @return User-friendly path string (typically relative to workspace)
     */
    public static String getReadablePath(String workspacePath, String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return "";
        }
        
        try {
            Path workspace = Paths.get(workspacePath).normalize();
            Path file;
            
            // If relative, resolve against workspace first
            if (!Paths.get(filePath).isAbsolute()) {
                file = workspace.resolve(filePath).normalize();
            } else {
                file = Paths.get(filePath).normalize();
            }
            
            // If file is within workspace, return relative path
            if (file.startsWith(workspace)) {
                String relativePath = workspace.relativize(file).toString();
                return normalizePathSeparators(relativePath);
            } else {
                // File is outside workspace, return absolute path
                return normalizePathSeparators(file.toString());
            }
            
        } catch (Exception e) {
            log.warn("Error making path readable: {} -> {}", workspacePath, filePath, e);
            return normalizePathSeparators(filePath);
        }
    }
    
    /**
     * Check if two paths are equal, handling platform differences
     * This mirrors Cline's arePathsEqual functionality
     */
    public static boolean arePathsEqual(String path1, String path2) {
        if (path1 == null && path2 == null) {
            return true;
        }
        if (path1 == null || path2 == null) {
            return false;
        }
        
        try {
            Path p1 = Paths.get(path1).normalize();
            Path p2 = Paths.get(path2).normalize();
            
            // On Windows, do case-insensitive comparison
            if (isWindows()) {
                return p1.toString().toLowerCase().equals(p2.toString().toLowerCase());
            } else {
                return p1.equals(p2);
            }
        } catch (Exception e) {
            log.warn("Error comparing paths: {} vs {}", path1, path2, e);
            return false;
        }
    }
    
    /**
     * Check if a path is located within a workspace directory
     * This mirrors Cline's isLocatedInWorkspace functionality
     */
    public static boolean isLocatedInWorkspace(String workspacePath, String pathToCheck) {
        if (StringUtils.isBlank(workspacePath) || StringUtils.isBlank(pathToCheck)) {
            return false;
        }
        
        try {
            Path workspace = Paths.get(workspacePath).toRealPath().normalize();
            Path checkPath;
            
            // Resolve relative paths against workspace
            if (!Paths.get(pathToCheck).isAbsolute()) {
                checkPath = workspace.resolve(pathToCheck).normalize();
            } else {
                checkPath = Paths.get(pathToCheck).toRealPath().normalize();
            }
            
            return checkPath.startsWith(workspace);
            
        } catch (IOException e) {
            log.debug("Cannot resolve real path for workspace check: {} -> {}", workspacePath, pathToCheck);
            // Fallback to basic check without real path resolution
            try {
                Path workspace = Paths.get(workspacePath).normalize();
                Path checkPath = workspace.resolve(pathToCheck).normalize();
                return checkPath.startsWith(workspace);
            } catch (Exception ex) {
                log.warn("Error checking workspace location: {} -> {}", workspacePath, pathToCheck, ex);
                return false;
            }
        }
    }
    
    /**
     * Convert path to use forward slashes for consistency
     * This mirrors Cline's toPosixPath functionality
     */
    public static String normalizePathSeparators(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        
        // Handle Windows extended-length paths (don't modify them)
        if (path.startsWith("\\\\?\\")) {
            return path;
        }
        
        return path.replace("\\", "/");
    }
    
    /**
     * Convert a path to relative path within workspace
     * This mirrors Cline's asRelativePath functionality
     */
    public static String asRelativePath(String workspacePath, String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return "";
        }
        
        try {
            Path workspace = Paths.get(workspacePath).normalize();
            Path file = Paths.get(filePath);
            
            // If already relative, return as-is
            if (!file.isAbsolute()) {
                return normalizePathSeparators(filePath);
            }
            
            file = file.normalize();
            
            // If file is within workspace, return relative path
            if (file.startsWith(workspace)) {
                return normalizePathSeparators(workspace.relativize(file).toString());
            } else {
                // File is outside workspace, return absolute path
                return normalizePathSeparators(filePath);
            }
            
        } catch (Exception e) {
            log.warn("Error converting to relative path: {} -> {}", workspacePath, filePath, e);
            return normalizePathSeparators(filePath);
        }
    }
    
    /**
     * Get the current working directory
     */
    public static String getCurrentWorkingDirectory() {
        return normalizePathSeparators(System.getProperty("user.dir"));
    }
    
    /**
     * Validate that a path is safe to operate on (within workspace bounds)
     */
    public static boolean isSafePath(String workspacePath, String pathToCheck) {
        if (StringUtils.isBlank(pathToCheck)) {
            return false;
        }
        
        try {
            // Check if path is within workspace
            if (!isLocatedInWorkspace(workspacePath, pathToCheck)) {
                log.warn("Path is outside workspace: {}", pathToCheck);
                return false;
            }
            
            // Additional safety checks
            String normalizedPath = normalizePathSeparators(pathToCheck);
            
            // Reject paths with suspicious patterns
            if (normalizedPath.contains("../") || normalizedPath.contains("./")) {
                // Allow these if they resolve to safe locations
                String resolved = resolveWorkspacePath(workspacePath, pathToCheck, "safety-check");
                return isLocatedInWorkspace(workspacePath, resolved);
            }
            
            return true;
            
        } catch (Exception e) {
            log.warn("Error validating path safety: {}", pathToCheck, e);
            return false;
        }
    }
    
    /**
     * Check if running on Windows
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    
    /**
     * Get file extension from path
     */
    public static Optional<String> getFileExtension(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return Optional.empty();
        }
        
        String fileName = Paths.get(filePath).getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return Optional.of(fileName.substring(lastDotIndex + 1).toLowerCase());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get the basename (filename) from a path
     */
    public static String getBasename(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return "";
        }
        
        return Paths.get(filePath).getFileName().toString();
    }
    
    /**
     * Check if file exists at the given path
     */
    public static boolean fileExists(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }
        
        try {
            return new File(filePath).exists();
        } catch (Exception e) {
            log.debug("Error checking file existence: {}", filePath, e);
            return false;
        }
    }
    
    /**
     * Ensure a path uses the correct format for the current platform
     * while maintaining cross-platform compatibility
     */
    public static String toPlatformPath(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        
        // For display and internal consistency, we prefer forward slashes
        // Java handles both on Windows, but forward slashes are more universal
        return normalizePathSeparators(path);
    }
}
