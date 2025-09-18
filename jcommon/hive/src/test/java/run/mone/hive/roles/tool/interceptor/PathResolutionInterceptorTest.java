package run.mone.hive.roles.tool.interceptor;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for PathResolutionInterceptor
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class PathResolutionInterceptorTest {

    @TempDir
    Path tempDir;
    
    private String workspacePath;
    private JsonObject params;
    private Map<String, String> extraParams;
    
    @BeforeEach
    void setUp() {
        workspacePath = tempDir.toString();
        params = new JsonObject();
        extraParams = new HashMap<>();
    }
    
    @Test
    void testResolvePathParametersForReadFile() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);
        
        // Set up parameters with relative path
        params.addProperty("path", "test.txt");
        
        PathResolutionInterceptor.resolvePathParameters("read_file", params, extraParams, workspacePath);
        
        // Path should be resolved to absolute
        String resolvedPath = params.get("path").getAsString();
        assertTrue(resolvedPath.endsWith("test.txt"));
        assertTrue(resolvedPath.length() > "test.txt".length()); // Should be absolute now
    }
    
    @Test
    void testResolvePathParametersForListFiles() {
        // Set up parameters with relative directory path
        params.addProperty("path", "src/main/java");
        
        PathResolutionInterceptor.resolvePathParameters("list_files", params, extraParams, workspacePath);
        
        // Path should be resolved to absolute
        String resolvedPath = params.get("path").getAsString();
        assertTrue(resolvedPath.contains("src/main/java"));
        assertTrue(resolvedPath.length() > "src/main/java".length());
    }
    
    @Test
    void testResolvePathParametersWithAbsolutePath() {
        // Set up parameters with absolute path
        String absolutePath = tempDir.resolve("absolute.txt").toString();
        params.addProperty("path", absolutePath);
        
        PathResolutionInterceptor.resolvePathParameters("read_file", params, extraParams, workspacePath);
        
        // Path should remain unchanged (already absolute)
        String resolvedPath = params.get("path").getAsString();
        assertEquals(absolutePath.replace("\\", "/"), resolvedPath);
    }
    
    @Test
    void testResolvePathParametersWithDotPaths() {
        // Set up parameters with current directory reference
        params.addProperty("path", "./config/app.properties");
        
        PathResolutionInterceptor.resolvePathParameters("read_file", params, extraParams, workspacePath);
        
        // Path should be resolved
        String resolvedPath = params.get("path").getAsString();
        assertTrue(resolvedPath.contains("config/app.properties"));
        assertFalse(resolvedPath.startsWith("./"));
    }
    
    @Test
    void testResolvePathParametersWithParentDirectory() {
        // Set up parameters with parent directory reference
        params.addProperty("path", "../external/file.txt");
        
        PathResolutionInterceptor.resolvePathParameters("read_file", params, extraParams, workspacePath);
        
        // Path should be resolved
        String resolvedPath = params.get("path").getAsString();
        assertTrue(resolvedPath.contains("external/file.txt"));
        assertFalse(resolvedPath.startsWith("../"));
    }
    
    @Test
    void testResolvePathParametersIgnoresNonPathParameters() {
        // Set up parameters with non-path values
        params.addProperty("query", "search term");
        params.addProperty("recursive", "true");
        params.addProperty("limit", "100");
        
        String originalQuery = params.get("query").getAsString();
        String originalRecursive = params.get("recursive").getAsString();
        String originalLimit = params.get("limit").getAsString();
        
        PathResolutionInterceptor.resolvePathParameters("search_files", params, extraParams, workspacePath);
        
        // Non-path parameters should remain unchanged
        assertEquals(originalQuery, params.get("query").getAsString());
        assertEquals(originalRecursive, params.get("recursive").getAsString());
        assertEquals(originalLimit, params.get("limit").getAsString());
    }
    
    @Test
    void testResolvePathParametersIgnoresUrls() {
        // Set up parameters with URLs (should not be resolved as paths)
        params.addProperty("url", "https://example.com/api");
        params.addProperty("endpoint", "http://localhost:8080/test");
        
        String originalUrl = params.get("url").getAsString();
        String originalEndpoint = params.get("endpoint").getAsString();
        
        PathResolutionInterceptor.resolvePathParameters("web_fetch", params, extraParams, workspacePath);
        
        // URLs should remain unchanged
        assertEquals(originalUrl, params.get("url").getAsString());
        assertEquals(originalEndpoint, params.get("endpoint").getAsString());
    }
    
    @Test
    void testResolvePathParametersWithExtraParams() {
        // Set up extra parameters with path-like values
        extraParams.put("input_file", "data/input.csv");
        extraParams.put("output_dir", "results");
        extraParams.put("config", "settings.json");
        
        PathResolutionInterceptor.resolvePathParameters("custom_tool", params, extraParams, workspacePath);
        
        // Extra parameters should be resolved if they look like paths
        String inputFile = extraParams.get("input_file");
        String outputDir = extraParams.get("output_dir");
        String config = extraParams.get("config");
        
        assertTrue(inputFile.contains("data/input.csv"));
        assertTrue(outputDir.contains("results"));
        assertTrue(config.contains("settings.json"));
        
        // All should be longer than original (absolute paths)
        assertTrue(inputFile.length() > "data/input.csv".length());
        assertTrue(outputDir.length() > "results".length());
        assertTrue(config.length() > "settings.json".length());
    }
    
    @Test
    void testResolvePathParametersWithVariousPathParameterNames() {
        // Test different parameter names that should be recognized as paths
        params.addProperty("file_path", "doc.txt");
        params.addProperty("directory", "src");
        params.addProperty("target", "build/output");
        params.addProperty("source", "input/data");
        
        PathResolutionInterceptor.resolvePathParameters("list_files", params, extraParams, workspacePath);
        
        // All should be resolved to absolute paths
        assertTrue(params.get("file_path").getAsString().contains("doc.txt"));
        assertTrue(params.get("directory").getAsString().contains("src"));
        assertTrue(params.get("target").getAsString().contains("build/output"));
        assertTrue(params.get("source").getAsString().contains("input/data"));
        
        // All should be longer than original (absolute paths)
        assertTrue(params.get("file_path").getAsString().length() > "doc.txt".length());
        assertTrue(params.get("directory").getAsString().length() > "src".length());
        assertTrue(params.get("target").getAsString().length() > "build/output".length());
        assertTrue(params.get("source").getAsString().length() > "input/data".length());
    }
    
    @Test
    void testResolvePathParametersWithNullOrEmptyValues() {
        // Test with null and empty values
        params.addProperty("path", "");
        params.add("nullPath", null);
        
        // Should not throw exceptions
        assertDoesNotThrow(() -> {
            PathResolutionInterceptor.resolvePathParameters("read_file", params, extraParams, workspacePath);
        });
        
        // Values should remain unchanged
        assertEquals("", params.get("path").getAsString());
        assertTrue(params.get("nullPath").isJsonNull());
    }
    
    @Test
    void testGetPathParameterNames() {
        var pathNames = PathResolutionInterceptor.getPathParameterNames();
        assertTrue(pathNames.contains("path"));
        assertTrue(pathNames.contains("file_path"));
        assertTrue(pathNames.contains("directory"));
        assertTrue(pathNames.contains("target"));
    }
    
    @Test
    void testGetPathDependentTools() {
        var pathTools = PathResolutionInterceptor.getPathDependentTools();
        assertTrue(pathTools.contains("read_file"));
        assertTrue(pathTools.contains("list_files"));
        assertTrue(pathTools.contains("search_files"));
    }
    
    @Test
    void testAddCustomPathParameterName() {
        // Add a custom parameter name
        PathResolutionInterceptor.addPathParameterName("custom_path");
        
        params.addProperty("custom_path", "custom/file.txt");
        PathResolutionInterceptor.resolvePathParameters("custom_tool", params, extraParams, workspacePath);
        
        // Should be resolved
        String customPath = params.get("custom_path").getAsString();
        assertTrue(customPath.contains("custom/file.txt"));
        assertTrue(customPath.length() > "custom/file.txt".length());
    }
    
    @Test
    void testAddCustomPathDependentTool() {
        // Add a custom tool
        PathResolutionInterceptor.addPathDependentTool("custom_file_tool");
        
        params.addProperty("path", "custom.txt");
        PathResolutionInterceptor.resolvePathParameters("custom_file_tool", params, extraParams, workspacePath);
        
        // Should be resolved for the custom tool
        String path = params.get("path").getAsString();
        assertTrue(path.contains("custom.txt"));
        assertTrue(path.length() > "custom.txt".length());
    }
}
