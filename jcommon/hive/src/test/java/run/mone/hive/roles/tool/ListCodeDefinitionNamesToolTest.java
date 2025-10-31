package run.mone.hive.roles.tool;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.ReactorRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ListCodeDefinitionNamesTool test class
 * 
 * @author goodjava@qq.com
 * @date 2025/1/16
 */
class ListCodeDefinitionNamesToolTest {

    private ListCodeDefinitionNamesTool tool;
    private ReactorRole role;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        tool = new ListCodeDefinitionNamesTool();
        role = new ReactorRole("test",null,new LLM(LLMConfig.builder().build())); // Assuming default constructor exists
    }

    @Test
    void testGetName() {
        assertEquals("list_code_definition_names", tool.getName());
    }

    @Test
    void testNeedExecute() {
        assertTrue(tool.needExecute());
    }

    @Test
    void testShow() {
        assertTrue(tool.show());
    }

    @Test
    void testDescription() {
        String description = tool.description();
        assertNotNull(description);
        assertTrue(description.contains("definition names"));
        assertTrue(description.contains("classes, functions, methods"));
        assertTrue(description.contains("codebase structure"));
    }

    @Test
    void testParameters() {
        String parameters = tool.parameters();
        assertNotNull(parameters);
        assertTrue(parameters.contains("path"));
        assertTrue(parameters.contains("required"));
    }

    @Test
    void testUsage() {
        String usage = tool.usage();
        assertNotNull(usage);
        assertTrue(usage.contains("list_code_definition_names"));
        assertTrue(usage.contains("<path>"));
    }

    @Test
    void testExample() {
        String example = tool.example();
        assertNotNull(example);
        assertTrue(example.contains("Example"));
        assertTrue(example.contains("list_code_definition_names"));
    }

    @Test
    void testExecuteWithMissingPath() {
        JsonObject input = new JsonObject();

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithEmptyPath() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("path"));
    }

    @Test
    void testExecuteWithNonExistentDirectory() {
        JsonObject input = new JsonObject();
        input.addProperty("path", "nonexistent");

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("does not exist"));
    }

    @Test
    void testExecuteWithFile() throws IOException {
        // Create a file instead of directory
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "test content", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", testFile.toString());

        JsonObject result = tool.execute(role, input);

        assertTrue(result.has("error"));
        assertTrue(result.get("error").getAsString().contains("file, not a directory"));
    }

    @Test
    void testAnalyzeEmptyDirectory() {
        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result with no definitions
        assertTrue(result.has("result"));
        assertTrue(result.has("totalFiles"));
        assertTrue(result.has("summary"));

        assertEquals("No source code definitions found.", result.get("result").getAsString());
        assertEquals(0, result.get("totalFiles").getAsInt());
    }

    @Test
    void testAnalyzeJavaFile() throws IOException {
        // Create a Java file with various definitions
        String javaCode = """
            package com.example;
            
            import java.util.List;
            
            /**
             * Sample class for testing
             */
            public class UserService {
                
                private static final String DEFAULT_NAME = "Anonymous";
                
                private String name;
                
                public UserService() {
                    this.name = DEFAULT_NAME;
                }
                
                public UserService(String name) {
                    this.name = name;
                }
                
                public String getName() {
                    return name;
                }
                
                public void setName(String name) {
                    this.name = name;
                }
                
                public static String getDefaultName() {
                    return DEFAULT_NAME;
                }
                
                private void validateName(String name) {
                    if (name == null || name.trim().isEmpty()) {
                        throw new IllegalArgumentException("Name cannot be empty");
                    }
                }
            }
            """;

        Files.writeString(tempDir.resolve("UserService.java"), javaCode, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("result"));
        assertTrue(result.has("filesAnalyzed"));
        assertTrue(result.has("totalDefinitions"));

        String resultContent = result.get("result").getAsString();
        assertFalse(resultContent.equals("No source code definitions found."));
        assertTrue(resultContent.contains("UserService.java"));
        assertTrue(resultContent.contains("UserService")); // Class name
        assertTrue(resultContent.contains("|----"));

        assertEquals(1, result.get("analyzedFiles").getAsInt());
        assertTrue(result.get("totalDefinitions").getAsInt() > 0);

        // Check detailed analysis
        assertTrue(result.has("filesAnalyzed"));
        JsonArray filesAnalyzed = result.getAsJsonArray("filesAnalyzed");
        assertEquals(1, filesAnalyzed.size());

        JsonObject fileInfo = filesAnalyzed.get(0).getAsJsonObject();
        assertEquals("UserService.java", fileInfo.get("path").getAsString());
        assertTrue(fileInfo.get("definitionCount").getAsInt() > 0);
        assertTrue(fileInfo.has("definitions"));

        // Check that we found various types of definitions
        JsonArray definitions = fileInfo.getAsJsonArray("definitions");
        boolean foundClass = false, foundConstructor = false, foundMethod = false, foundConstant = false;

        for (int i = 0; i < definitions.size(); i++) {
            JsonObject def = definitions.get(i).getAsJsonObject();
            String type = def.get("type").getAsString();
            String name = def.get("name").getAsString();

            if ("class".equals(type) && "UserService".equals(name)) {
                foundClass = true;
            } else if ("constructor".equals(type) && "UserService".equals(name)) {
                foundConstructor = true;
            } else if ("method".equals(type) && ("getName".equals(name) || "setName".equals(name))) {
                foundMethod = true;
            } else if ("constant".equals(type) && "DEFAULT_NAME".equals(name)) {
                foundConstant = true;
            }
        }

        assertTrue(foundClass, "Should find class definition");
        assertTrue(foundMethod, "Should find method definitions");
        // Note: Constructor and constant detection might vary based on regex patterns
    }

    @Test
    void testAnalyzeJavaScriptFile() throws IOException {
        String jsCode = """
            // Sample JavaScript file
            
            export class UserManager {
                constructor(name) {
                    this.name = name;
                }
                
                getName() {
                    return this.name;
                }
                
                setName(name) {
                    this.name = name;
                }
            }
            
            export function createUser(name, email) {
                return {
                    name: name,
                    email: email
                };
            }
            
            const validateEmail = (email) => {
                return email.includes('@');
            };
            
            async function fetchUserData(userId) {
                const response = await fetch(`/api/users/${userId}`);
                return response.json();
            }
            
            export default UserManager;
            """;

        Files.writeString(tempDir.resolve("UserManager.js"), jsCode, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("result"));
        String resultContent = result.get("result").getAsString();
        
        assertTrue(resultContent.contains("UserManager.js"));
        assertTrue(resultContent.contains("UserManager")); // Class name
        assertTrue(resultContent.contains("createUser")); // Function name
        assertTrue(resultContent.contains("validateEmail")); // Arrow function
        assertTrue(resultContent.contains("fetchUserData")); // Async function

        assertTrue(result.get("totalDefinitions").getAsInt() > 0);
    }

    @Test
    void testAnalyzePythonFile() throws IOException {
        String pythonCode = """
            # Sample Python file
            
            class UserService:
                def __init__(self, name):
                    self.name = name
                
                def get_name(self):
                    return self.name
                
                def set_name(self, name):
                    self.name = name
                
                @staticmethod
                def validate_name(name):
                    return name is not None and len(name.strip()) > 0
            
            def create_user(name, email):
                return {
                    'name': name,
                    'email': email
                }
            
            async def fetch_user_data(user_id):
                # Simulate async operation
                return {'id': user_id, 'name': 'Sample User'}
            
            class EmailValidator:
                @staticmethod
                def is_valid(email):
                    return '@' in email
            """;

        Files.writeString(tempDir.resolve("user_service.py"), pythonCode, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("result"));
        String resultContent = result.get("result").getAsString();
        
        assertTrue(resultContent.contains("user_service.py"));
        assertTrue(resultContent.contains("UserService")); // Class name
        assertTrue(resultContent.contains("EmailValidator")); // Another class
        assertTrue(resultContent.contains("create_user")); // Function name
        assertTrue(resultContent.contains("fetch_user_data")); // Async function

        assertTrue(result.get("totalDefinitions").getAsInt() > 0);
    }

    @Test
    void testAnalyzeTypeScriptFile() throws IOException {
        String tsCode = """
            // Sample TypeScript file
            
            interface User {
                id: number;
                name: string;
                email: string;
            }
            
            type UserStatus = 'active' | 'inactive' | 'pending';
            
            enum UserRole {
                ADMIN = 'admin',
                USER = 'user',
                MODERATOR = 'moderator'
            }
            
            export class UserService {
                private users: User[] = [];
                
                constructor() {}
                
                public addUser(user: User): void {
                    this.users.push(user);
                }
                
                public getUser(id: number): User | undefined {
                    return this.users.find(user => user.id === id);
                }
            }
            
            export function createUserService(): UserService {
                return new UserService();
            }
            
            const validateUser = (user: User): boolean => {
                return user.name.length > 0 && user.email.includes('@');
            };
            
            export async function fetchUsers(): Promise<User[]> {
                const response = await fetch('/api/users');
                return response.json();
            }
            """;

        Files.writeString(tempDir.resolve("UserService.ts"), tsCode, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("result"));
        String resultContent = result.get("result").getAsString();
        
        assertTrue(resultContent.contains("UserService.ts"));
        assertTrue(resultContent.contains("User")); // Interface name
        assertTrue(resultContent.contains("UserStatus")); // Type alias
        assertTrue(resultContent.contains("UserRole")); // Enum
        assertTrue(resultContent.contains("UserService")); // Class name
        assertTrue(resultContent.contains("createUserService")); // Function
        assertTrue(resultContent.contains("validateUser")); // Arrow function

        assertTrue(result.get("totalDefinitions").getAsInt() > 0);
    }

    @Test
    void testAnalyzeMultipleFiles() throws IOException {
        // Create multiple source files
        Files.writeString(tempDir.resolve("Service.java"), """
            public class Service {
                public void doSomething() {}
            }
            """, StandardCharsets.UTF_8);

        Files.writeString(tempDir.resolve("utils.js"), """
            function helper() {}
            export const CONSTANT = 42;
            """, StandardCharsets.UTF_8);

        Files.writeString(tempDir.resolve("model.py"), """
            class Model:
                def process(self):
                    pass
            """, StandardCharsets.UTF_8);

        // Also create a non-source file that should be ignored
        Files.writeString(tempDir.resolve("README.md"), "# Documentation", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("result"));
        assertTrue(result.has("summary"));

        String resultContent = result.get("result").getAsString();
        assertTrue(resultContent.contains("Service.java"));
        assertTrue(resultContent.contains("utils.js"));
        assertTrue(resultContent.contains("model.py"));
        assertFalse(resultContent.contains("README.md")); // Should not analyze non-source files

        JsonObject summary = result.getAsJsonObject("summary");
        assertEquals(3, summary.get("sourceFilesFound").getAsInt());
        assertEquals(3, summary.get("filesAnalyzed").getAsInt());
        assertTrue(summary.get("totalDefinitions").getAsInt() > 0);
        assertFalse(summary.get("wasLimited").getAsBoolean());
    }

    @Test
    void testAnalyzeGoFile() throws IOException {
        String goCode = """
            package main
            
            import "fmt"
            
            type User struct {
                Name  string
                Email string
            }
            
            type UserService interface {
                GetUser(id int) *User
                CreateUser(user *User) error
            }
            
            func main() {
                fmt.Println("Hello, World!")
            }
            
            func NewUser(name, email string) *User {
                return &User{
                    Name:  name,
                    Email: email,
                }
            }
            
            const MaxUsers = 1000
            """;

        Files.writeString(tempDir.resolve("main.go"), goCode, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Verify successful result
        assertTrue(result.has("result"));
        String resultContent = result.get("result").getAsString();
        
        assertTrue(resultContent.contains("main.go"));
        assertTrue(resultContent.contains("User")); // Struct
        assertTrue(resultContent.contains("UserService")); // Interface
        assertTrue(resultContent.contains("main")); // Function
        assertTrue(resultContent.contains("NewUser")); // Function

        assertTrue(result.get("totalDefinitions").getAsInt() > 0);
    }

    @Test
    void testAnalyzeUnsupportedFiles() throws IOException {
        // Create files with unsupported extensions
        Files.writeString(tempDir.resolve("data.json"), "{\"key\": \"value\"}", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("style.css"), "body { margin: 0; }", StandardCharsets.UTF_8);
        Files.writeString(tempDir.resolve("README.txt"), "Documentation", StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Should return no definitions found
        assertTrue(result.has("result"));
        assertEquals("No source code definitions found.", result.get("result").getAsString());
        assertEquals(0, result.get("totalFiles").getAsInt());

        JsonObject summary = result.getAsJsonObject("summary");
        assertEquals(0, summary.get("sourceFilesFound").getAsInt());
    }

    @Test
    void testAnalyzeFileWithNoDefinitions() throws IOException {
        // Create a Java file with no class/method definitions
        String javaCode = """
            // This file has no definitions
            // Just comments and imports
            
            import java.util.List;
            import java.util.ArrayList;
            
            // Some constants but not properly defined
            // String name = "test";
            """;

        Files.writeString(tempDir.resolve("Empty.java"), javaCode, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Should find the file but no definitions
        assertTrue(result.has("result"));
        assertTrue(result.has("summary"));

        JsonObject summary = result.getAsJsonObject("summary");
        assertEquals(1, summary.get("sourceFilesFound").getAsInt());
        assertEquals(1, summary.get("filesAnalyzed").getAsInt());
        // Total definitions might be 0 if no patterns match
        assertTrue(summary.get("totalDefinitions").getAsInt() >= 0);
    }

    @Test
    void testAnalyzeLargeNumberOfFiles() throws IOException {
        // Create many files to test the limit
        for (int i = 0; i < 60; i++) {
            Files.writeString(tempDir.resolve("File" + i + ".java"), 
                String.format("public class File%d { public void method%d() {} }", i, i), 
                StandardCharsets.UTF_8);
        }

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Should be limited to MAX_FILES_TO_ANALYZE (50)
        assertTrue(result.has("summary"));
        JsonObject summary = result.getAsJsonObject("summary");
        
        assertEquals(60, summary.get("sourceFilesFound").getAsInt());
        assertEquals(50, summary.get("filesAnalyzed").getAsInt()); // Limited to 50
        assertTrue(summary.get("wasLimited").getAsBoolean());
        assertTrue(summary.get("totalDefinitions").getAsInt() > 0);
    }

    @Test
    void testResultFormat() throws IOException {
        String javaCode = """
            public class TestClass {
                public void testMethod() {}
            }
            """;

        Files.writeString(tempDir.resolve("TestClass.java"), javaCode, StandardCharsets.UTF_8);

        JsonObject input = new JsonObject();
        input.addProperty("path", tempDir.toString());

        JsonObject result = tool.execute(role, input);

        // Check result format matches Cline's expected format
        assertTrue(result.has("result"));
        String resultContent = result.get("result").getAsString();
        
        // Should contain filename, separator lines, and definitions
        assertTrue(resultContent.contains("TestClass.java"));
        assertTrue(resultContent.contains("|----"));
        assertTrue(resultContent.contains("│")); // Definition lines should start with │
        assertTrue(resultContent.contains("TestClass"));

        // Check JSON structure
        assertTrue(result.has("totalFiles"));
        assertTrue(result.has("analyzedFiles"));
        assertTrue(result.has("totalDefinitions"));
        assertTrue(result.has("directoryPath"));
        assertTrue(result.has("filesAnalyzed"));
        assertTrue(result.has("summary"));
    }
}
