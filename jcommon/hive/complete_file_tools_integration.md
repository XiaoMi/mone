# Complete File Tools Integration Guide

This document demonstrates the comprehensive usage of all four file tools working together: `ListFilesTool`, `ReadFileTool`, `WriteToFileTool`, and `ReplaceInFileTool`.

## Tool Overview

The four file tools provide complete file system management capabilities:

- **ListFilesTool**: Explore directory structure and find files
- **ReadFileTool**: Examine existing file contents
- **WriteToFileTool**: Create new files or completely rewrite existing ones
- **ReplaceInFileTool**: Make precise modifications to existing files

## Complete Development Workflows

### Scenario 1: Microservice Development from Scratch

#### Step 1: Explore existing project structure

```java
// First, understand the current project layout
ListFilesTool listTool = new ListFilesTool();
JsonObject listInput = new JsonObject();
listInput.addProperty("path", ".");
listInput.addProperty("recursive", "true");
JsonObject projectStructure = listTool.execute(role, listInput);

// Analyze what we have
JsonArray files = projectStructure.getAsJsonArray("files");
System.out.println("Project contains " + files.size() + " items");

// Check if it's a Spring Boot project
boolean hasSpringBoot = false;
for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    String path = file.get("path").getAsString();
    if (path.contains("pom.xml") || path.contains("Application.java")) {
        hasSpringBoot = true;
        break;
    }
}
```

#### Step 2: Read existing configuration to understand setup

```java
ReadFileTool readTool = new ReadFileTool();

// Read pom.xml to understand dependencies
JsonObject readInput = new JsonObject();
readInput.addProperty("path", "pom.xml");
JsonObject pomResult = readTool.execute(role, readInput);

if (!pomResult.has("error")) {
    String pomContent = pomResult.get("result").getAsString();
    System.out.println("Current dependencies in pom.xml:");
    // Analyze dependencies...
}

// Read application.properties if it exists
readInput.addProperty("path", "src/main/resources/application.properties");
JsonObject propsResult = readTool.execute(role, readInput);
```

#### Step 3: Create new microservice structure

```java
WriteToFileTool writeTool = new WriteToFileTool();

// Create main service class
JsonObject writeInput = new JsonObject();
writeInput.addProperty("path", "src/main/java/com/example/userservice/UserServiceApplication.java");
writeInput.addProperty("content", """
    package com.example.userservice;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.openfeign.EnableFeignClients;
    
    @SpringBootApplication
    @EnableFeignClients
    public class UserServiceApplication {
        
        public static void main(String[] args) {
            SpringApplication.run(UserServiceApplication.class, args);
        }
    }
    """);

JsonObject appResult = writeTool.execute(role, writeInput);

// Create user entity
writeInput = new JsonObject();
writeInput.addProperty("path", "src/main/java/com/example/userservice/entity/User.java");
writeInput.addProperty("content", """
    package com.example.userservice.entity;
    
    import jakarta.persistence.*;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.AllArgsConstructor;
    import java.time.LocalDateTime;
    
    @Entity
    @Table(name = "users")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class User {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @Column(unique = true, nullable = false)
        private String username;
        
        @Column(nullable = false)
        private String email;
        
        @Column(name = "full_name")
        private String fullName;
        
        @Column(name = "created_at")
        private LocalDateTime createdAt;
        
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;
        
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }
        
        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 4: List and verify created structure

```java
// Check what we've created so far
listInput = new JsonObject();
listInput.addProperty("path", "src/main/java/com/example/userservice");
listInput.addProperty("recursive", "true");
JsonObject serviceStructure = listTool.execute(role, listInput);

System.out.println("Created service structure:");
String formattedOutput = serviceStructure.get("result").getAsString();
System.out.println(formattedOutput);
```

#### Step 5: Create repository layer

```java
// Create user repository
writeInput = new JsonObject();
writeInput.addProperty("path", "src/main/java/com/example/userservice/repository/UserRepository.java");
writeInput.addProperty("content", """
    package com.example.userservice.repository;
    
    import com.example.userservice.entity.User;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;
    
    import java.util.List;
    import java.util.Optional;
    
    @Repository
    public interface UserRepository extends JpaRepository<User, Long> {
        
        Optional<User> findByUsername(String username);
        
        Optional<User> findByEmail(String email);
        
        @Query("SELECT u FROM User u WHERE u.fullName LIKE %:name%")
        List<User> findByFullNameContaining(@Param("name") String name);
        
        boolean existsByUsername(String username);
        
        boolean existsByEmail(String email);
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 6: Read existing service patterns and create service layer

```java
// First, check if there are existing service patterns to follow
listInput = new JsonObject();
listInput.addProperty("path", "src/main/java");
listInput.addProperty("recursive", "true");
JsonObject javaFiles = listTool.execute(role, listInput);

// Look for existing service patterns
JsonArray files = javaFiles.getAsJsonArray("files");
List<String> serviceFiles = new ArrayList<>();
for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    String path = file.get("path").getAsString();
    if (path.contains("Service") && path.endsWith(".java")) {
        serviceFiles.add(path);
    }
}

// If we found existing services, read one to understand the pattern
if (!serviceFiles.isEmpty()) {
    readInput = new JsonObject();
    readInput.addProperty("path", serviceFiles.get(0));
    JsonObject existingService = readTool.execute(role, readInput);
    
    if (!existingService.has("error")) {
        System.out.println("Found existing service pattern:");
        String serviceContent = existingService.get("result").getAsString();
        // Analyze the pattern and adapt our service accordingly
    }
}

// Create user service
writeInput = new JsonObject();
writeInput.addProperty("path", "src/main/java/com/example/userservice/service/UserService.java");
writeInput.addProperty("content", """
    package com.example.userservice.service;
    
    import com.example.userservice.entity.User;
    import com.example.userservice.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    
    import java.util.List;
    import java.util.Optional;
    
    @Service
    @RequiredArgsConstructor
    @Slf4j
    @Transactional
    public class UserService {
        
        private final UserRepository userRepository;
        
        public List<User> getAllUsers() {
            log.debug("Fetching all users");
            return userRepository.findAll();
        }
        
        public Optional<User> getUserById(Long id) {
            log.debug("Fetching user by id: {}", id);
            return userRepository.findById(id);
        }
        
        public Optional<User> getUserByUsername(String username) {
            log.debug("Fetching user by username: {}", username);
            return userRepository.findByUsername(username);
        }
        
        public User createUser(User user) {
            log.info("Creating new user: {}", user.getUsername());
            
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + user.getUsername());
            }
            
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + user.getEmail());
            }
            
            return userRepository.save(user);
        }
        
        public User updateUser(Long id, User userDetails) {
            log.info("Updating user with id: {}", id);
            
            return userRepository.findById(id)
                .map(user -> {
                    user.setEmail(userDetails.getEmail());
                    user.setFullName(userDetails.getFullName());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        }
        
        public void deleteUser(Long id) {
            log.info("Deleting user with id: {}", id);
            
            if (!userRepository.existsById(id)) {
                throw new IllegalArgumentException("User not found with id: " + id);
            }
            
            userRepository.deleteById(id);
        }
        
        public List<User> searchUsersByName(String name) {
            log.debug("Searching users by name: {}", name);
            return userRepository.findByFullNameContaining(name);
        }
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 7: Enhance service with additional features using ReplaceInFileTool

```java
ReplaceInFileTool replaceTool = new ReplaceInFileTool();

// Add caching to the service
JsonObject replaceInput = new JsonObject();
replaceInput.addProperty("path", "src/main/java/com/example/userservice/service/UserService.java");
replaceInput.addProperty("diff", """
    ------- SEARCH
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    =======
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.cache.annotation.CacheEvict;
    import org.springframework.cache.annotation.CachePut;
    +++++++ REPLACE
    """);

replaceTool.execute(role, replaceInput);

// Add caching annotations to methods
replaceInput = new JsonObject();
replaceInput.addProperty("path", "src/main/java/com/example/userservice/service/UserService.java");
replaceInput.addProperty("diff", """
    ------- SEARCH
        public Optional<User> getUserById(Long id) {
            log.debug("Fetching user by id: {}", id);
            return userRepository.findById(id);
        }
    =======
        @Cacheable(value = "users", key = "#id")
        public Optional<User> getUserById(Long id) {
            log.debug("Fetching user by id: {}", id);
            return userRepository.findById(id);
        }
    +++++++ REPLACE
    """);

replaceTool.execute(role, replaceInput);
```

#### Step 8: Create REST controller

```java
writeInput = new JsonObject();
writeInput.addProperty("path", "src/main/java/com/example/userservice/controller/UserController.java");
writeInput.addProperty("content", """
    package com.example.userservice.controller;
    
    import com.example.userservice.entity.User;
    import com.example.userservice.service.UserService;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    
    import jakarta.validation.Valid;
    import java.util.List;
    
    @RestController
    @RequestMapping("/api/users")
    @RequiredArgsConstructor
    @Slf4j
    @CrossOrigin(origins = "*")
    public class UserController {
        
        private final UserService userService;
        
        @GetMapping
        public ResponseEntity<List<User>> getAllUsers() {
            log.info("GET /api/users - Fetching all users");
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
        
        @GetMapping("/{id}")
        public ResponseEntity<User> getUserById(@PathVariable Long id) {
            log.info("GET /api/users/{} - Fetching user by id", id);
            return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
        }
        
        @GetMapping("/username/{username}")
        public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
            log.info("GET /api/users/username/{} - Fetching user by username", username);
            return userService.getUserByUsername(username)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
        }
        
        @PostMapping
        public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
            log.info("POST /api/users - Creating new user: {}", user.getUsername());
            try {
                User createdUser = userService.createUser(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
            } catch (IllegalArgumentException e) {
                log.error("Error creating user: {}", e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }
        
        @PutMapping("/{id}")
        public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
            log.info("PUT /api/users/{} - Updating user", id);
            try {
                User updatedUser = userService.updateUser(id, userDetails);
                return ResponseEntity.ok(updatedUser);
            } catch (IllegalArgumentException e) {
                log.error("Error updating user: {}", e.getMessage());
                return ResponseEntity.notFound().build();
            }
        }
        
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
            log.info("DELETE /api/users/{} - Deleting user", id);
            try {
                userService.deleteUser(id);
                return ResponseEntity.noContent().build();
            } catch (IllegalArgumentException e) {
                log.error("Error deleting user: {}", e.getMessage());
                return ResponseEntity.notFound().build();
            }
        }
        
        @GetMapping("/search")
        public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
            log.info("GET /api/users/search?name={} - Searching users", name);
            List<User> users = userService.searchUsersByName(name);
            return ResponseEntity.ok(users);
        }
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 9: Update application configuration

```java
// Read current application.properties
readInput = new JsonObject();
readInput.addProperty("path", "src/main/resources/application.properties");
JsonObject currentProps = readTool.execute(role, readInput);

if (currentProps.has("error")) {
    // Create new application.properties
    writeInput = new JsonObject();
    writeInput.addProperty("path", "src/main/resources/application.properties");
    writeInput.addProperty("content", """
        # Server Configuration
        server.port=8081
        server.servlet.context-path=/user-service
        
        # Database Configuration
        spring.datasource.url=jdbc:mysql://localhost:3306/userservice
        spring.datasource.username=userservice
        spring.datasource.password=${DB_PASSWORD:userservice123}
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        
        # JPA Configuration
        spring.jpa.hibernate.ddl-auto=update
        spring.jpa.show-sql=true
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
        spring.jpa.properties.hibernate.format_sql=true
        
        # Logging Configuration
        logging.level.com.example.userservice=DEBUG
        logging.level.org.hibernate.SQL=DEBUG
        logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
        
        # Cache Configuration
        spring.cache.type=caffeine
        spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=30m
        
        # Actuator Configuration
        management.endpoints.web.exposure.include=health,info,metrics
        management.endpoint.health.show-details=when-authorized
        """);
    
    writeTool.execute(role, writeInput);
} else {
    // Update existing properties
    replaceInput = new JsonObject();
    replaceInput.addProperty("path", "src/main/resources/application.properties");
    replaceInput.addProperty("diff", """
        ------- SEARCH
        server.port=8080
        =======
        server.port=8081
        server.servlet.context-path=/user-service
        +++++++ REPLACE
        """);
    
    replaceTool.execute(role, replaceInput);
}
```

#### Step 10: Create comprehensive tests

```java
// Create unit tests for service
writeInput = new JsonObject();
writeInput.addProperty("path", "src/test/java/com/example/userservice/service/UserServiceTest.java");
writeInput.addProperty("content", """
    package com.example.userservice.service;
    
    import com.example.userservice.entity.User;
    import com.example.userservice.repository.UserRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    
    import java.util.Arrays;
    import java.util.List;
    import java.util.Optional;
    
    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.*;
    import static org.mockito.Mockito.*;
    
    @ExtendWith(MockitoExtension.class)
    class UserServiceTest {
        
        @Mock
        private UserRepository userRepository;
        
        @InjectMocks
        private UserService userService;
        
        private User testUser;
        
        @BeforeEach
        void setUp() {
            testUser = new User();
            testUser.setId(1L);
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");
            testUser.setFullName("Test User");
        }
        
        @Test
        void getAllUsers_ShouldReturnAllUsers() {
            // Given
            List<User> users = Arrays.asList(testUser);
            when(userRepository.findAll()).thenReturn(users);
            
            // When
            List<User> result = userService.getAllUsers();
            
            // Then
            assertEquals(1, result.size());
            assertEquals(testUser.getUsername(), result.get(0).getUsername());
            verify(userRepository).findAll();
        }
        
        @Test
        void getUserById_WhenUserExists_ShouldReturnUser() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            
            // When
            Optional<User> result = userService.getUserById(1L);
            
            // Then
            assertTrue(result.isPresent());
            assertEquals(testUser.getUsername(), result.get().getUsername());
            verify(userRepository).findById(1L);
        }
        
        @Test
        void createUser_WhenValidUser_ShouldCreateUser() {
            // Given
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            
            // When
            User result = userService.createUser(testUser);
            
            // Then
            assertNotNull(result);
            assertEquals(testUser.getUsername(), result.getUsername());
            verify(userRepository).save(testUser);
        }
        
        @Test
        void createUser_WhenUsernameExists_ShouldThrowException() {
            // Given
            when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(testUser)
            );
            
            assertTrue(exception.getMessage().contains("Username already exists"));
            verify(userRepository, never()).save(any(User.class));
        }
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 11: Final project structure verification

```java
// Get final project overview
listInput = new JsonObject();
listInput.addProperty("path", ".");
listInput.addProperty("recursive", "true");
JsonObject finalStructure = listTool.execute(role, listInput);

JsonObject summary = finalStructure.getAsJsonObject("summary");
System.out.println("\nFinal Project Structure:");
System.out.println("- Total files: " + summary.get("totalFiles").getAsLong());
System.out.println("- Total directories: " + summary.get("totalDirectories").getAsLong());
System.out.println("- Total size: " + summary.get("totalSizeFormatted").getAsString());

// Analyze file types
JsonArray allFiles = finalStructure.getAsJsonArray("files");
Map<String, Integer> fileTypes = new HashMap<>();
for (int i = 0; i < allFiles.size(); i++) {
    JsonObject file = allFiles.get(i).getAsJsonObject();
    if (!file.get("isDirectory").getAsBoolean() && file.has("extension")) {
        String ext = file.get("extension").getAsString();
        fileTypes.put(ext, fileTypes.getOrDefault(ext, 0) + 1);
    }
}

System.out.println("\nFile types created:");
fileTypes.forEach((ext, count) -> 
    System.out.println("- ." + ext + ": " + count + " files"));
```

### Scenario 2: Legacy Code Refactoring

#### Step 1: Analyze existing codebase

```java
// Explore the legacy codebase structure
listInput = new JsonObject();
listInput.addProperty("path", "src/main/java");
listInput.addProperty("recursive", "true");
JsonObject legacyStructure = listTool.execute(role, listInput);

// Find all Java files
JsonArray files = legacyStructure.getAsJsonArray("files");
List<String> javaFiles = new ArrayList<>();
for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    if ("java".equals(file.get("extension").getAsString())) {
        javaFiles.add(file.get("path").getAsString());
    }
}

System.out.println("Found " + javaFiles.size() + " Java files to analyze");
```

#### Step 2: Read and analyze legacy code patterns

```java
// Read key files to understand patterns
for (String javaFile : javaFiles.subList(0, Math.min(5, javaFiles.size()))) {
    readInput = new JsonObject();
    readInput.addProperty("path", javaFile);
    JsonObject fileContent = readTool.execute(role, readInput);
    
    if (!fileContent.has("error")) {
        String content = fileContent.get("result").getAsString();
        
        // Analyze for common patterns
        if (content.contains("@Controller") || content.contains("@RestController")) {
            System.out.println("Found controller: " + javaFile);
            analyzeController(content, javaFile);
        } else if (content.contains("@Service")) {
            System.out.println("Found service: " + javaFile);
            analyzeService(content, javaFile);
        } else if (content.contains("@Repository")) {
            System.out.println("Found repository: " + javaFile);
            analyzeRepository(content, javaFile);
        }
    }
}
```

#### Step 3: Refactor step by step

```java
// Example: Add logging to all service classes
for (String javaFile : javaFiles) {
    if (javaFile.contains("Service")) {
        // Read current service
        readInput = new JsonObject();
        readInput.addProperty("path", javaFile);
        JsonObject serviceContent = readTool.execute(role, readInput);
        
        if (!serviceContent.has("error")) {
            String content = serviceContent.get("result").getAsString();
            
            // Add logging if not present
            if (!content.contains("@Slf4j") && !content.contains("Logger")) {
                replaceInput = new JsonObject();
                replaceInput.addProperty("path", javaFile);
                replaceInput.addProperty("diff", """
                    ------- SEARCH
                    import org.springframework.stereotype.Service;
                    =======
                    import org.springframework.stereotype.Service;
                    import lombok.extern.slf4j.Slf4j;
                    +++++++ REPLACE
                    """);
                
                replaceTool.execute(role, replaceInput);
                
                // Add @Slf4j annotation
                replaceInput = new JsonObject();
                replaceInput.addProperty("path", javaFile);
                replaceInput.addProperty("diff", """
                    ------- SEARCH
                    @Service
                    public class
                    =======
                    @Service
                    @Slf4j
                    public class
                    +++++++ REPLACE
                    """);
                
                replaceTool.execute(role, replaceInput);
                
                System.out.println("Added logging to: " + javaFile);
            }
        }
    }
}
```

### Scenario 3: Documentation Generation

#### Step 1: Scan project for documentable components

```java
// Find all main source files
listInput = new JsonObject();
listInput.addProperty("path", "src/main/java");
listInput.addProperty("recursive", "true");
JsonObject sourceFiles = listTool.execute(role, sourceFiles);

// Categorize files
Map<String, List<String>> componentsByType = new HashMap<>();
JsonArray files = sourceFiles.getAsJsonArray("files");

for (int i = 0; i < files.size(); i++) {
    JsonObject file = files.get(i).getAsJsonObject();
    if ("java".equals(file.get("extension").getAsString())) {
        String path = file.get("path").getAsString();
        
        if (path.contains("controller")) {
            componentsByType.computeIfAbsent("controllers", k -> new ArrayList<>()).add(path);
        } else if (path.contains("service")) {
            componentsByType.computeIfAbsent("services", k -> new ArrayList<>()).add(path);
        } else if (path.contains("repository")) {
            componentsByType.computeIfAbsent("repositories", k -> new ArrayList<>()).add(path);
        } else if (path.contains("entity") || path.contains("model")) {
            componentsByType.computeIfAbsent("entities", k -> new ArrayList<>()).add(path);
        }
    }
}
```

#### Step 2: Generate API documentation

```java
// Read all controllers and generate API docs
StringBuilder apiDocs = new StringBuilder();
apiDocs.append("# API Documentation\n\n");

List<String> controllers = componentsByType.get("controllers");
if (controllers != null) {
    for (String controllerPath : controllers) {
        readInput = new JsonObject();
        readInput.addProperty("path", controllerPath);
        JsonObject controllerContent = readTool.execute(role, readInput);
        
        if (!controllerContent.has("error")) {
            String content = controllerContent.get("result").getAsString();
            
            // Extract API information
            String className = extractClassName(content);
            String baseMapping = extractRequestMapping(content);
            List<String> endpoints = extractEndpoints(content);
            
            apiDocs.append("## ").append(className).append("\n\n");
            apiDocs.append("Base URL: `").append(baseMapping).append("`\n\n");
            
            for (String endpoint : endpoints) {
                apiDocs.append("### ").append(endpoint).append("\n\n");
            }
        }
    }
}

// Write API documentation
writeInput = new JsonObject();
writeInput.addProperty("path", "docs/API.md");
writeInput.addProperty("content", apiDocs.toString());
writeTool.execute(role, writeInput);
```

## Tool Integration Patterns

### Pattern 1: Explore-Read-Modify

```java
public void exploreReadModify(String targetPath) {
    // 1. Explore structure
    JsonObject listResult = listTool.execute(role, createListInput(targetPath, true));
    
    // 2. Read interesting files
    JsonArray files = listResult.getAsJsonArray("files");
    for (int i = 0; i < files.size(); i++) {
        JsonObject file = files.get(i).getAsJsonObject();
        if (isInterestingFile(file)) {
            JsonObject readResult = readTool.execute(role, createReadInput(file.get("path").getAsString()));
            
            // 3. Modify if needed
            if (needsModification(readResult)) {
                modifyFile(file.get("path").getAsString(), readResult);
            }
        }
    }
}
```

### Pattern 2: Template-Based Generation

```java
public void generateFromTemplate(String templateDir, String targetDir) {
    // 1. List template files
    JsonObject templates = listTool.execute(role, createListInput(templateDir, true));
    
    // 2. Process each template
    JsonArray templateFiles = templates.getAsJsonArray("files");
    for (int i = 0; i < templateFiles.size(); i++) {
        JsonObject template = templateFiles.get(i).getAsJsonObject();
        if (!template.get("isDirectory").getAsBoolean()) {
            
            // 3. Read template content
            JsonObject templateContent = readTool.execute(role, 
                createReadInput(template.get("path").getAsString()));
            
            // 4. Process and write to target
            String processedContent = processTemplate(templateContent.get("result").getAsString());
            String targetPath = calculateTargetPath(template.get("path").getAsString(), targetDir);
            
            writeTool.execute(role, createWriteInput(targetPath, processedContent));
        }
    }
}
```

### Pattern 3: Incremental Refactoring

```java
public void incrementalRefactoring(String sourceDir) {
    // 1. Find all source files
    JsonObject sourceStructure = listTool.execute(role, createListInput(sourceDir, true));
    
    // 2. Process in batches
    JsonArray files = sourceStructure.getAsJsonArray("files");
    List<JsonObject> javaFiles = filterJavaFiles(files);
    
    int batchSize = 10;
    for (int i = 0; i < javaFiles.size(); i += batchSize) {
        List<JsonObject> batch = javaFiles.subList(i, Math.min(i + batchSize, javaFiles.size()));
        
        for (JsonObject file : batch) {
            // 3. Read current state
            JsonObject currentContent = readTool.execute(role, 
                createReadInput(file.get("path").getAsString()));
            
            // 4. Apply refactoring rules
            List<RefactoringRule> applicableRules = findApplicableRules(currentContent);
            
            for (RefactoringRule rule : applicableRules) {
                replaceTool.execute(role, rule.createReplaceInput(file.get("path").getAsString()));
            }
        }
        
        // 5. Verify changes
        verifyBatch(batch);
    }
}
```

This comprehensive integration demonstrates how all four file tools work together to provide complete file system management capabilities, from exploration and analysis to creation and modification, supporting complex development workflows and automation scenarios.
