
package run.mone.mcp.filesystem.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FilesystemFunctionTest {

    private FilesystemFunction filesystemFunction;
    private static final String TEST_DIR = "/tmp" + File.separator;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUpTestDirectory() throws IOException {
        Files.createDirectories(Paths.get(TEST_DIR));
    }

    @AfterAll
    static void tearDownTestDirectory() throws IOException {
//        deleteDirectory(Paths.get(TEST_DIR));
    }

    @BeforeEach
    void setUp() {
        List<String> allowedDirectories = Collections.singletonList(TEST_DIR);
        filesystemFunction = new FilesystemFunction(allowedDirectories, objectMapper);
    }

    @Test
    void testReadFile() throws IOException {
        String fileName = "test_read.txt";
        String content = "Hello, World!";
        Path filePath = createTestFile(fileName, content);

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "read_file");
        args.put("path", filePath.toString());

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testWriteFile() throws IOException {
        String fileName = "test_write.txt";
        String content = "Test content";
        Path filePath = Paths.get(TEST_DIR, fileName);

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "write_file");
        args.put("path", filePath.toString());
        args.put("content", content);

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testEditFile() throws IOException {
        String fileName = "test_edit.txt";
        String initialContent = "Initial content";
        Path filePath = createTestFile(fileName, initialContent);

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "edit_file");
        args.put("path", filePath.toString());
        args.put("edits", Collections.singletonList(
                Map.of("oldText", "Initial", "newText", "Updated")
        ));
        args.put("dryRun", false);

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
        assertEquals("Updated content", Files.readString(filePath));
    }

    @Test
    void testCreateDirectory() throws IOException {
        String dirName = "test_create_dir";
        Path dirPath = Paths.get(TEST_DIR, dirName);

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "create_directory");
        args.put("path", dirPath.toString());

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
        assertTrue(Files.exists(dirPath));
        assertTrue(Files.isDirectory(dirPath));
    }

    @Test
    void testListDirectory() throws IOException {
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "list_directory");
        args.put("path","/tmp");

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testMoveFile() throws IOException {
        String sourceFileName = "source.txt";
        String destFileName = "destination.txt";
        Path sourcePath = createTestFile(sourceFileName, "Move me");
        Path destPath = Paths.get(TEST_DIR, destFileName);

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "move_file");
        args.put("source", sourcePath.toString());
        args.put("destination", destPath.toString());

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
        assertFalse(Files.exists(sourcePath));
        assertTrue(Files.exists(destPath));
        assertEquals("Move me", Files.readString(destPath));
    }

    @Test
    void testSearchFiles() throws IOException {
        createTestFile("search_test1.txt", "Content 1");
        createTestFile("search_test2.txt", "Content 2");
        createTestFile("other_file.txt", "Other content");

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "search_files");
        args.put("path", TEST_DIR);
        args.put("pattern", "search_test");

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testGetFileInfo() throws IOException {
        String fileName = "info_test.txt";
        Path filePath = createTestFile(fileName, "File info test");

        Map<String, Object> args = new HashMap<>();
        args.put("operation", "get_file_info");
        args.put("path", filePath.toString());

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
    }

    @Test
    void testListAllowedDirectories() {
        Map<String, Object> args = new HashMap<>();
        args.put("operation", "list_allowed_directories");

        McpSchema.CallToolResult result = filesystemFunction.apply(args);

        assertFalse(result.isError());
    }

    private static Path createTestFile(String fileName, String content) throws IOException {
        Path filePath = Paths.get(TEST_DIR, fileName);
        Files.writeString(filePath, content);
        return filePath;
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}
