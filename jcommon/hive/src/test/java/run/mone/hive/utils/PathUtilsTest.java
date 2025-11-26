package run.mone.hive.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for PathUtils class
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class PathUtilsTest {

    @TempDir
    Path tempDir;
    
    private String workspacePath;
    
    @BeforeEach
    void setUp() {
        workspacePath = tempDir.toString();
    }
    
    @Test
    void testResolveWorkspacePath() {
        // Test relative path resolution
        String relativePath = "src/main/java";
        String resolved = PathUtils.resolveWorkspacePath(workspacePath, relativePath, "test");
        
        assertTrue(resolved.contains("src/main/java"));
        assertTrue(Paths.get(resolved).isAbsolute());
    }
    
    @Test
    void testResolveWorkspacePathWithAbsolutePath() {
        // Test absolute path handling
        String absolutePath = "/tmp/test.txt";
        String resolved = PathUtils.resolveWorkspacePath(workspacePath, absolutePath, "test");
        
        assertEquals("/tmp/test.txt", PathUtils.normalizePathSeparators(resolved));
    }
    
    @Test
    void testGetReadablePath() throws IOException {
        // Create a test file in workspace
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);
        
        String readable = PathUtils.getReadablePath(workspacePath, testFile.toString());
        assertEquals("test.txt", readable);
    }
    
    @Test
    void testGetReadablePathOutsideWorkspace() {
        String outsidePath = "/tmp/outside.txt";
        String readable = PathUtils.getReadablePath(workspacePath, outsidePath);
        
        // Should return absolute path for files outside workspace
        assertTrue(readable.startsWith("/tmp") || readable.startsWith("tmp"));
    }
    
    @Test
    void testArePathsEqual() {
        assertTrue(PathUtils.arePathsEqual("/path/to/file", "/path/to/file"));
        assertTrue(PathUtils.arePathsEqual("relative/path", "relative/path"));
        assertFalse(PathUtils.arePathsEqual("/path/to/file1", "/path/to/file2"));
        assertTrue(PathUtils.arePathsEqual(null, null));
        assertFalse(PathUtils.arePathsEqual("/path", null));
    }
    
    @Test
    void testIsLocatedInWorkspace() throws IOException {
        // Create a subdirectory in workspace
        Path subDir = tempDir.resolve("subdir");
        Files.createDirectories(subDir);
        
        assertTrue(PathUtils.isLocatedInWorkspace(workspacePath, "subdir"));
        assertTrue(PathUtils.isLocatedInWorkspace(workspacePath, subDir.toString()));
        assertFalse(PathUtils.isLocatedInWorkspace(workspacePath, "/tmp/outside"));
    }
    
    @Test
    void testNormalizePathSeparators() {
        assertEquals("path/to/file", PathUtils.normalizePathSeparators("path\\to\\file"));
        assertEquals("path/to/file", PathUtils.normalizePathSeparators("path/to/file"));
        assertEquals("\\\\?\\C:\\Windows\\Path", PathUtils.normalizePathSeparators("\\\\?\\C:\\Windows\\Path"));
    }
    
    @Test
    void testAsRelativePath() throws IOException {
        // Create a test file in workspace
        Path testFile = tempDir.resolve("nested/test.txt");
        Files.createDirectories(testFile.getParent());
        Files.createFile(testFile);
        
        String relative = PathUtils.asRelativePath(workspacePath, testFile.toString());
        assertEquals("nested/test.txt", relative);
    }
    
    @Test
    void testGetFileExtension() {
        assertEquals("txt", PathUtils.getFileExtension("test.txt").orElse(""));
        assertEquals("java", PathUtils.getFileExtension("path/to/Test.java").orElse(""));
        assertTrue(PathUtils.getFileExtension("noextension").isEmpty());
        assertTrue(PathUtils.getFileExtension("").isEmpty());
    }
    
    @Test
    void testGetBasename() {
        assertEquals("test.txt", PathUtils.getBasename("path/to/test.txt"));
        assertEquals("file", PathUtils.getBasename("/absolute/path/to/file"));
        assertEquals("", PathUtils.getBasename(""));
    }
    
    @Test
    void testIsSafePath() throws IOException {
        // Create a test file in workspace
        Path testFile = tempDir.resolve("safe.txt");
        Files.createFile(testFile);
        
        assertTrue(PathUtils.isSafePath(workspacePath, "safe.txt"));
        assertTrue(PathUtils.isSafePath(workspacePath, testFile.toString()));
        assertFalse(PathUtils.isSafePath(workspacePath, "/tmp/unsafe"));
    }
    
    @Test
    void testFileExists() throws IOException {
        // Create a test file
        Path testFile = tempDir.resolve("exists.txt");
        Files.createFile(testFile);
        
        assertTrue(PathUtils.fileExists(testFile.toString()));
        assertFalse(PathUtils.fileExists(tempDir.resolve("nonexistent.txt").toString()));
        assertFalse(PathUtils.fileExists(""));
    }
    
    @Test
    void testGetCurrentWorkingDirectory() {
        String cwd = PathUtils.getCurrentWorkingDirectory();
        assertNotNull(cwd);
        assertFalse(cwd.isEmpty());
        // Should be normalized with forward slashes
        assertFalse(cwd.contains("\\"));
    }
}
