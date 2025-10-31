# Biz 业务项目模板

用于生成标准的Spring Boot业务项目，包含完整的安全认证、日志、异常处理等基础组件。

## 模板列表

### 基础文件
- **bootstrap.tml** - Spring Boot启动类
- **pom_xml.tml** - Maven项目配置
- **application_properties.tml** - 应用配置文件
- **logback.tml** - 日志配置文件

### 配置类 (config/)
- **security_config.tml** - Spring Security安全配置
- **web_mvc_config.tml** - Web MVC配置
- **app_config.tml** - 应用通用配置

### 过滤器 (filter/)
- **jwt_authentication_filter.tml** - JWT认证过滤器

### 工具类 (util/)
- **jwt_util.tml** - JWT工具类

### DTO (dto/)
- **api_response.tml** - 统一API响应格式

### AOP切面 (aop/)
- **http_logging_aspect.tml** - HTTP接口日志切面
- **exception_handling_aspect.tml** - 异常处理切面

### 异常处理 (exception/)
- **global_exception_handler.tml** - 全局异常处理器

## 功能特性

### 1. 安全认证
- Spring Security集成
- JWT Token认证
- BCrypt密码加密
- 基于角色的访问控制

### 2. 日志管理
- Logback日志框架
- 日志文件按日期和大小滚动
- 错误日志单独输出
- HTTP接口请求/响应日志

### 3. 异常处理
- 全局异常处理器
- AOP统一异常拦截
- 统一错误响应格式

### 4. 数据持久化
- Spring Data JPA
- H2内存数据库（可切换其他数据库）
- 自动建表

### 5. 其他特性
- Lombok简化代码
- RestTemplate HTTP客户端
- 参数校验
- GSON JSON处理

## 变量说明

### 项目基础变量
- `${package}` - 项目包名
- `${bootstrapClassName}` - 启动类名
- `${projectName}` - 项目名称
- `${description}` - 项目描述
- `${author}` - 作者
- `${date}` - 日期

### Maven相关变量
- `${groupId}` - Maven GroupId
- `${artifactId}` - Maven ArtifactId
- `${version}` - 版本号
- `${springBootVersion}` - Spring Boot版本
- `${javaVersion}` - Java版本

### 配置相关变量
- `${serverPort}` - 服务端口
- `${dbName}` - 数据库名称
- `${jwtSecret}` - JWT密钥
- `${jwtExpiration}` - JWT过期时间（毫秒）

## 项目结构

```
project-name/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── ${package}/
│       │       ├── Application.java          # 启动类
│       │       ├── config/                   # 配置类
│       │       │   ├── SecurityConfig.java
│       │       │   ├── WebMvcConfig.java
│       │       │   └── AppConfig.java
│       │       ├── filter/                   # 过滤器
│       │       │   └── JwtAuthenticationFilter.java
│       │       ├── util/                     # 工具类
│       │       │   └── JwtUtil.java
│       │       ├── dto/                      # 数据传输对象
│       │       │   └── ApiResponse.java
│       │       ├── aop/                      # AOP切面
│       │       │   ├── HttpLoggingAspect.java
│       │       │   └── ExceptionHandlingAspect.java
│       │       ├── exception/                # 异常处理
│       │       │   └── GlobalExceptionHandler.java
│       │       ├── controller/               # 控制器
│       │       ├── service/                  # 服务层
│       │       ├── repository/               # 数据访问层
│       │       └── model/                    # 实体类
│       └── resources/
│           ├── application.properties
│           └── logback.xml
└── logs/                                     # 日志目录
```

## 使用示例

生成的项目包含以下特性：

1. **JWT认证**：用户登录后获取token，后续请求携带token访问
2. **自动日志**：所有HTTP请求自动记录参数、返回值、执行时间
3. **统一异常**：所有异常统一处理，返回JSON格式错误信息
4. **安全配置**：可配置不同URL的访问权限
5. **数据库支持**：默认使用H2，可轻松切换到MySQL/PostgreSQL

## 依赖技术栈

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- JWT (jjwt)
- Lombok
- H2 Database
- Logback
- AOP

