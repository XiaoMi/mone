# Complete File Tools Integration Demo

This document demonstrates the comprehensive usage of all three file tools: `ReadFileTool`, `WriteToFileTool`, and `ReplaceInFileTool` working together in real-world scenarios.

## Tool Overview

The three file tools provide complete file management capabilities:

- **ReadFileTool**: Examine existing file contents
- **WriteToFileTool**: Create new files or completely rewrite existing ones
- **ReplaceInFileTool**: Make precise modifications to existing files

## Complete Development Workflows

### Scenario 1: Spring Boot Project Setup and Enhancement

#### Step 1: Read existing project structure

```java
// Read main application class
ReadFileTool readTool = new ReadFileTool();
JsonObject readInput = new JsonObject();
readInput.addProperty("path", "src/main/java/com/example/Application.java");
JsonObject readResult = readTool.execute(role, readInput);

String existingApp = readResult.get("result").getAsString();
System.out.println("Current application: " + existingApp);
```

#### Step 2: Create new service class

```java
// Create UserService class
WriteToFileTool writeTool = new WriteToFileTool();
JsonObject writeInput = new JsonObject();
writeInput.addProperty("path", "src/main/java/com/example/service/UserService.java");
writeInput.addProperty("content", """
    package com.example.service;
    
    import org.springframework.stereotype.Service;
    import java.util.List;
    import java.util.ArrayList;
    
    @Service
    public class UserService {
        
        private List<String> users = new ArrayList<>();
        
        public List<String> getAllUsers() {
            return new ArrayList<>(users);
        }
        
        public void addUser(String username) {
            users.add(username);
        }
        
        public boolean removeUser(String username) {
            return users.remove(username);
        }
    }
    """);

JsonObject writeResult = writeTool.execute(role, writeInput);
```

#### Step 3: Add repository dependency to service

```java
// Read current service to understand structure
readInput.addProperty("path", "src/main/java/com/example/service/UserService.java");
readResult = readTool.execute(role, readInput);

// Add repository dependency using ReplaceInFileTool
ReplaceInFileTool replaceTool = new ReplaceInFileTool();
JsonObject replaceInput = new JsonObject();
replaceInput.addProperty("path", "src/main/java/com/example/service/UserService.java");
replaceInput.addProperty("diff", """
    ------- SEARCH
    import org.springframework.stereotype.Service;
    import java.util.List;
    import java.util.ArrayList;
    
    @Service
    public class UserService {
        
        private List<String> users = new ArrayList<>();
    =======
    import org.springframework.stereotype.Service;
    import org.springframework.beans.factory.annotation.Autowired;
    import java.util.List;
    import java.util.ArrayList;
    
    @Service
    public class UserService {
        
        @Autowired
        private UserRepository userRepository;
        
        private List<String> users = new ArrayList<>();
    +++++++ REPLACE
    """);

JsonObject replaceResult = replaceTool.execute(role, replaceInput);
```

#### Step 4: Create controller class

```java
// Create UserController
writeInput = new JsonObject();
writeInput.addProperty("path", "src/main/java/com/example/controller/UserController.java");
writeInput.addProperty("content", """
    package com.example.controller;
    
    import com.example.service.UserService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;
    import java.util.List;
    
    @RestController
    @RequestMapping("/api/users")
    public class UserController {
        
        @Autowired
        private UserService userService;
        
        @GetMapping
        public List<String> getAllUsers() {
            return userService.getAllUsers();
        }
        
        @PostMapping
        public void addUser(@RequestBody String username) {
            userService.addUser(username);
        }
        
        @DeleteMapping("/{username}")
        public boolean removeUser(@PathVariable String username) {
            return userService.removeUser(username);
        }
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 5: Update application properties

```java
// Read existing properties
readInput.addProperty("path", "src/main/resources/application.properties");
readResult = readTool.execute(role, readInput);

if (readResult.has("error")) {
    // File doesn't exist, create it
    writeInput = new JsonObject();
    writeInput.addProperty("path", "src/main/resources/application.properties");
    writeInput.addProperty("content", """
        server.port=8080
        spring.application.name=user-management-app
        
        # Database Configuration
        spring.datasource.url=jdbc:h2:mem:testdb
        spring.datasource.driverClassName=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=
        
        # JPA Configuration
        spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
        spring.jpa.hibernate.ddl-auto=create-drop
        spring.jpa.show-sql=true
        
        # H2 Console
        spring.h2.console.enabled=true
        """);
    writeTool.execute(role, writeInput);
} else {
    // File exists, add new properties
    replaceInput = new JsonObject();
    replaceInput.addProperty("path", "src/main/resources/application.properties");
    replaceInput.addProperty("diff", """
        ------- SEARCH
        server.port=8080
        =======
        server.port=8080
        spring.application.name=user-management-app
        
        # Database Configuration
        spring.datasource.url=jdbc:h2:mem:testdb
        spring.datasource.driverClassName=org.h2.Driver
        spring.datasource.username=sa
        spring.datasource.password=
        +++++++ REPLACE
        """);
    replaceTool.execute(role, replaceInput);
}
```

### Scenario 2: Frontend Development Workflow

#### Step 1: Create HTML template

```java
// Create main HTML file
writeInput = new JsonObject();
writeInput.addProperty("path", "public/index.html");
writeInput.addProperty("content", """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>User Management App</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <div class="container">
            <h1>User Management</h1>
            <div class="user-form">
                <input type="text" id="usernameInput" placeholder="Enter username">
                <button onclick="addUser()">Add User</button>
            </div>
            <div class="user-list">
                <h2>Users</h2>
                <ul id="usersList"></ul>
            </div>
        </div>
        <script src="app.js"></script>
    </body>
    </html>
    """);

writeTool.execute(role, writeInput);
```

#### Step 2: Create CSS styles

```java
// Create CSS file
writeInput = new JsonObject();
writeInput.addProperty("path", "public/styles.css");
writeInput.addProperty("content", """
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }
    
    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f5f5f5;
        padding: 20px;
    }
    
    .container {
        max-width: 800px;
        margin: 0 auto;
        background: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    
    h1 {
        color: #333;
        margin-bottom: 30px;
        text-align: center;
    }
    
    .user-form {
        margin-bottom: 30px;
        display: flex;
        gap: 10px;
    }
    
    input[type="text"] {
        flex: 1;
        padding: 12px;
        border: 2px solid #ddd;
        border-radius: 5px;
        font-size: 16px;
    }
    
    button {
        padding: 12px 24px;
        background-color: #007bff;
        color: white;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 16px;
        transition: background-color 0.3s;
    }
    
    button:hover {
        background-color: #0056b3;
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 3: Create JavaScript functionality

```java
// Create JavaScript file
writeInput = new JsonObject();
writeInput.addProperty("path", "public/app.js");
writeInput.addProperty("content", """
    const API_BASE_URL = '/api/users';
    
    // Load users when page loads
    document.addEventListener('DOMContentLoaded', loadUsers);
    
    async function loadUsers() {
        try {
            const response = await fetch(API_BASE_URL);
            const users = await response.json();
            displayUsers(users);
        } catch (error) {
            console.error('Error loading users:', error);
        }
    }
    
    function displayUsers(users) {
        const usersList = document.getElementById('usersList');
        usersList.innerHTML = '';
        
        users.forEach(username => {
            const li = document.createElement('li');
            li.innerHTML = `
                <span>${username}</span>
                <button onclick="removeUser('${username}')" class="delete-btn">Remove</button>
            `;
            usersList.appendChild(li);
        });
    }
    
    async function addUser() {
        const usernameInput = document.getElementById('usernameInput');
        const username = usernameInput.value.trim();
        
        if (!username) {
            alert('Please enter a username');
            return;
        }
        
        try {
            await fetch(API_BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(username)
            });
            
            usernameInput.value = '';
            loadUsers();
        } catch (error) {
            console.error('Error adding user:', error);
        }
    }
    """);

writeTool.execute(role, writeInput);
```

#### Step 4: Add delete functionality to CSS

```java
// Read current CSS to add delete button styles
readInput.addProperty("path", "public/styles.css");
readResult = readTool.execute(role, readInput);

// Add delete button styles
replaceInput = new JsonObject();
replaceInput.addProperty("path", "public/styles.css");
replaceInput.addProperty("diff", """
    ------- SEARCH
    button:hover {
        background-color: #0056b3;
    }
    =======
    button:hover {
        background-color: #0056b3;
    }
    
    .user-list {
        margin-top: 20px;
    }
    
    .user-list h2 {
        color: #333;
        margin-bottom: 15px;
    }
    
    .user-list ul {
        list-style: none;
    }
    
    .user-list li {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px;
        margin: 5px 0;
        background-color: #f8f9fa;
        border-radius: 5px;
        border: 1px solid #e9ecef;
    }
    
    .delete-btn {
        background-color: #dc3545;
        padding: 5px 10px;
        font-size: 14px;
    }
    
    .delete-btn:hover {
        background-color: #c82333;
    }
    +++++++ REPLACE
    """);

replaceTool.execute(role, replaceInput);
```

### Scenario 3: Configuration Management Workflow

#### Step 1: Read existing configuration

```java
// Check if configuration file exists
readInput = new JsonObject();
readInput.addProperty("path", "config/app-config.json");
readResult = readTool.execute(role, readInput);

if (readResult.has("error")) {
    System.out.println("Configuration file not found, creating new one...");
    
    // Create initial configuration
    writeInput = new JsonObject();
    writeInput.addProperty("path", "config/app-config.json");
    writeInput.addProperty("content", """
        {
          "application": {
            "name": "User Management System",
            "version": "1.0.0",
            "environment": "development"
          },
          "server": {
            "port": 8080,
            "host": "localhost",
            "ssl": false
          },
          "database": {
            "type": "h2",
            "url": "jdbc:h2:mem:testdb",
            "username": "sa",
            "password": "",
            "pool": {
              "minSize": 5,
              "maxSize": 20
            }
          },
          "logging": {
            "level": "INFO",
            "file": "logs/application.log",
            "maxSize": "10MB"
          }
        }
        """);
    
    writeTool.execute(role, writeInput);
} else {
    System.out.println("Configuration file found:");
    System.out.println(readResult.get("result").getAsString());
}
```

#### Step 2: Update specific configuration values

```java
// Update database configuration for production
replaceInput = new JsonObject();
replaceInput.addProperty("path", "config/app-config.json");
replaceInput.addProperty("diff", """
    ------- SEARCH
          "database": {
            "type": "h2",
            "url": "jdbc:h2:mem:testdb",
            "username": "sa",
            "password": "",
    =======
          "database": {
            "type": "mysql",
            "url": "jdbc:mysql://localhost:3306/userdb",
            "username": "app_user",
            "password": "${DB_PASSWORD}",
    +++++++ REPLACE
    """);

replaceTool.execute(role, replaceInput);

// Update environment to production
replaceInput = new JsonObject();
replaceInput.addProperty("path", "config/app-config.json");
replaceInput.addProperty("diff", """
    ------- SEARCH
            "environment": "development"
    =======
            "environment": "production"
    +++++++ REPLACE
    """);

replaceTool.execute(role, replaceInput);
```

#### Step 3: Create environment-specific configurations

```java
// Create production configuration
writeInput = new JsonObject();
writeInput.addProperty("path", "config/production.properties");
writeInput.addProperty("content", """
    # Production Configuration
    spring.profiles.active=production
    
    # Database
    spring.datasource.url=jdbc:mysql://prod-db:3306/userdb
    spring.datasource.username=prod_user
    spring.datasource.password=${DB_PASSWORD}
    spring.datasource.hikari.maximum-pool-size=20
    spring.datasource.hikari.minimum-idle=5
    
    # Security
    server.ssl.enabled=true
    server.ssl.key-store=classpath:keystore.jks
    server.ssl.key-store-password=${KEYSTORE_PASSWORD}
    
    # Logging
    logging.level.com.example=WARN
    logging.level.org.springframework.security=DEBUG
    logging.file.name=logs/production.log
    logging.file.max-size=50MB
    logging.file.max-history=30
    """);

writeTool.execute(role, writeInput);
```

### Scenario 4: Documentation Generation Workflow

#### Step 1: Read source files to generate documentation

```java
// Read service class for API documentation
readInput = new JsonObject();
readInput.addProperty("path", "src/main/java/com/example/service/UserService.java");
readResult = readTool.execute(role, readInput);

String serviceContent = readResult.get("result").getAsString();

// Read controller class
readInput.addProperty("path", "src/main/java/com/example/controller/UserController.java");
readResult = readTool.execute(role, readInput);

String controllerContent = readResult.get("result").getAsString();
```

#### Step 2: Generate API documentation

```java
// Create comprehensive API documentation
writeInput = new JsonObject();
writeInput.addProperty("path", "docs/API.md");
writeInput.addProperty("content", """
    # User Management API Documentation
    
    ## Overview
    
    This API provides endpoints for managing users in the system.
    
    ## Base URL
    
    ```
    http://localhost:8080/api/users
    ```
    
    ## Endpoints
    
    ### Get All Users
    
    **GET** `/api/users`
    
    Returns a list of all users in the system.
    
    **Response:**
    ```json
    [
      "user1",
      "user2",
      "user3"
    ]
    ```
    
    **Status Codes:**
    - `200 OK` - Successfully retrieved users
    - `500 Internal Server Error` - Server error
    
    ### Add User
    
    **POST** `/api/users`
    
    Adds a new user to the system.
    
    **Request Body:**
    ```json
    "newUsername"
    ```
    
    **Response:**
    - `200 OK` - User successfully added
    - `400 Bad Request` - Invalid username
    - `409 Conflict` - User already exists
    
    ### Remove User
    
    **DELETE** `/api/users/{username}`
    
    Removes a user from the system.
    
    **Parameters:**
    - `username` (path) - The username to remove
    
    **Response:**
    ```json
    true
    ```
    
    **Status Codes:**
    - `200 OK` - User successfully removed
    - `404 Not Found` - User not found
    
    ## Error Handling
    
    All errors return a JSON response with the following structure:
    
    ```json
    {
      "error": "Error message",
      "timestamp": "2025-01-16T10:30:00Z",
      "status": 400
    }
    ```
    
    ## Authentication
    
    Currently, the API does not require authentication. In production, implement appropriate security measures.
    
    ## Rate Limiting
    
    No rate limiting is currently implemented. Consider adding rate limiting for production use.
    """);

writeTool.execute(role, writeInput);
```

#### Step 3: Create README file

```java
// Create comprehensive README
writeInput = new JsonObject();
writeInput.addProperty("path", "README.md");
writeInput.addProperty("content", """
    # User Management System
    
    A simple Spring Boot application for managing users with a web interface.
    
    ## Features
    
    - Add new users
    - Remove existing users  
    - List all users
    - Web-based user interface
    - RESTful API
    - H2 in-memory database (development)
    - MySQL support (production)
    
    ## Technology Stack
    
    - **Backend:** Spring Boot 3.x, Java 17+
    - **Frontend:** HTML5, CSS3, Vanilla JavaScript
    - **Database:** H2 (development), MySQL (production)
    - **Build Tool:** Maven
    
    ## Getting Started
    
    ### Prerequisites
    
    - Java 17 or higher
    - Maven 3.6+
    - MySQL 8.0+ (for production)
    
    ### Installation
    
    1. Clone the repository:
    ```bash
    git clone https://github.com/example/user-management.git
    cd user-management
    ```
    
    2. Build the project:
    ```bash
    mvn clean compile
    ```
    
    3. Run tests:
    ```bash
    mvn test
    ```
    
    4. Start the application:
    ```bash
    mvn spring-boot:run
    ```
    
    5. Open your browser and navigate to:
    ```
    http://localhost:8080
    ```
    
    ## Configuration
    
    ### Development
    The application uses H2 in-memory database by default. No additional setup required.
    
    ### Production
    Update `src/main/resources/application.properties` with your MySQL configuration:
    
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/userdb
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```
    
    ## API Documentation
    
    See [API.md](docs/API.md) for detailed API documentation.
    
    ## Project Structure
    
    ```
    src/
    ├── main/
    │   ├── java/com/example/
    │   │   ├── Application.java
    │   │   ├── controller/
    │   │   │   └── UserController.java
    │   │   └── service/
    │   │       └── UserService.java
    │   └── resources/
    │       └── application.properties
    ├── test/
    └── public/
        ├── index.html
        ├── styles.css
        └── app.js
    ```
    
    ## Contributing
    
    1. Fork the repository
    2. Create a feature branch
    3. Commit your changes
    4. Push to the branch
    5. Create a Pull Request
    
    ## License
    
    This project is licensed under the MIT License.
    """);

writeTool.execute(role, writeInput);
```

## Tool Integration Best Practices

### 1. Read-Analyze-Modify Pattern

```java
// Always read before modifying
JsonObject readResult = readTool.execute(role, readInput);
if (readResult.has("result")) {
    String content = readResult.get("result").getAsString();
    // Analyze content structure
    // Plan modifications
    // Execute targeted changes with ReplaceInFileTool
} else {
    // File doesn't exist, create with WriteToFileTool
}
```

### 2. Error Handling Chain

```java
public void safeFileOperation(String path, String operation) {
    try {
        JsonObject result = performOperation(path, operation);
        if (result.has("error")) {
            log.error("Operation failed: {}", result.get("error").getAsString());
            // Implement retry or alternative strategy
        }
    } catch (Exception e) {
        log.error("Exception during file operation", e);
        // Handle exception appropriately
    }
}
```

### 3. Content Validation

```java
// Validate content before writing
if (isValidJavaCode(content)) {
    writeResult = writeTool.execute(role, writeInput);
} else {
    log.warn("Invalid Java code detected, skipping write operation");
}
```

This comprehensive integration demonstrates how the three file tools work together to provide complete file management capabilities, from reading and analyzing existing files to creating new ones and making precise modifications.
