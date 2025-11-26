# Biz模板索引

## 模板文件清单

### 📁 根目录文件
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `bootstrap.tml` | Spring Boot启动类 | package, bootstrapClassName |
| `pom_xml.tml` | Maven项目配置 | groupId, artifactId, version, springBootVersion, javaVersion, projectName, description |
| `application_properties.tml` | 应用配置文件 | dbName, serverPort, jwtSecret, jwtExpiration |
| `logback.tml` | 日志配置 | projectName |
| `readme.tml` | 项目README | projectName, version, author, description, bootstrapClassName, package, artifactId, dbName, serverPort, jwtSecret, jwtExpiration, springBootVersion, javaVersion |

### 📁 config/ - 配置类
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `security_config.tml` | Spring Security安全配置 | package, author, date |
| `web_mvc_config.tml` | Web MVC配置（参数解析器） | package, author, date |
| `app_config.tml` | 应用通用配置（RestTemplate） | package, author, date |

### 📁 filter/ - 过滤器
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `jwt_authentication_filter.tml` | JWT认证过滤器 | package, author, date |

### 📁 util/ - 工具类
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `jwt_util.tml` | JWT Token生成和验证工具 | package, author, date |

### 📁 dto/ - 数据传输对象
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `api_response.tml` | 统一API响应格式 | package, author, date |

### 📁 aop/ - AOP切面
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `http_logging_aspect.tml` | HTTP接口日志记录切面 | package, author, date |
| `exception_handling_aspect.tml` | 异常处理切面 | package, author, date |

### 📁 exception/ - 异常处理
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `global_exception_handler.tml` | 全局异常处理器 | package, author, date |

### 📁 hive/ - Hive Agent 配置
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `agent.tml` | 全栈开发助手配置 | javaVersion, package, projectName |
| `backend-agent.tml` | 后端开发助手配置 | javaVersion, package, projectName |
| `frontend-agent.tml` | 前端开发助手配置 | 无 |

### 📁 security/ - 安全认证
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `custom_user_details.tml` | 自定义用户详情实现类 | package, author, date |

### 📁 model/ - 实体类
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `user.tml` | 用户实体类 | package, author, date |

### 📁 repository/ - 数据访问层
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `user_repository.tml` | 用户数据访问接口 | package, author, date |

### 📁 service/ - 服务层
| 文件名 | 说明 | 变量 |
|--------|------|------|
| `custom_user_details_service.tml` | 用户详情服务 | package, author, date |

## 变量字典

### 基础变量
- `${package}` - 项目包名，例如：run.mone.shop
- `${bootstrapClassName}` - 启动类名，例如：ShopApplication
- `${projectName}` - 项目名称，例如：ecommerce
- `${description}` - 项目描述
- `${author}` - 作者邮箱
- `${date}` - 生成日期

### Maven变量
- `${groupId}` - Maven GroupId，例如：run.mone
- `${artifactId}` - Maven ArtifactId，例如：shop
- `${version}` - 版本号，例如：1.0.0
- `${springBootVersion}` - Spring Boot版本，例如：3.2.0
- `${javaVersion}` - Java版本，例如：21

### 配置变量
- `${serverPort}` - 服务端口，例如：8080
- `${dbName}` - 数据库名称，例如：mydb
- `${jwtSecret}` - JWT密钥，建议32字符以上
- `${jwtExpiration}` - JWT过期时间（毫秒），例如：86400000（24小时）

## 使用流程

1. **选择模板类型**：根据项目需求选择biz模板
2. **准备变量值**：根据变量字典准备所有变量的值
3. **生成代码**：调用生成器，传入变量值
4. **编译运行**：编译生成的项目并运行

## Hive Agent 配置

### Agent 配置文件
项目包含三个 Hive Agent 配置文件，用于配置不同角色的 AI 开发助手：

1. **agent.md** - 全栈开发助手
   - 同时负责前后端开发
   - 适合小型项目或快速开发

2. **backend-agent.md** - 后端开发助手
   - 专注 Java 后端开发
   - 适合前后端分离项目

3. **frontend-agent.md** - 前端开发助手
   - 专注 Vue.js 前端开发
   - 适合前后端分离项目

### 配置内容
每个配置包含：
- **Profile** - 角色定位和技术栈
- **Goal** - 开发目标
- **Constraints** - 开发规范和约束
- **Workflow** - 工作流程
- **Agent Prompt** - 核心提示词

### 使用方式
生成的配置文件应放在项目根目录的 `.hive/` 目录下，供 Hive AI 开发助手使用。

## 模板特性

### ✅ 已包含功能
- Spring Boot基础框架
- Spring Security + JWT认证
- 用户认证体系（User实体、Repository、Service）
- JPA数据持久化
- H2内存数据库
- Logback日志管理
- AOP日志记录
- 全局异常处理
- 统一响应格式
- BCrypt密码加密
- RestTemplate HTTP客户端
- CustomUserDetails用户详情封装

### 🔧 可扩展点
- 数据库：可从H2切换到MySQL/PostgreSQL
- 认证方式：可扩展OAuth2/LDAP等
- 日志：可集成ELK/Splunk
- 监控：可集成Prometheus/Micrometer
- 缓存：可集成Redis/Caffeine
- 消息队列：可集成RabbitMQ/Kafka

## 目录结构映射

```
生成的项目结构：
src/main/java/${package}/
├── ${bootstrapClassName}.java          ← bootstrap.tml
├── config/
│   ├── SecurityConfig.java             ← config/security_config.tml
│   ├── WebMvcConfig.java               ← config/web_mvc_config.tml
│   └── AppConfig.java                  ← config/app_config.tml
├── filter/
│   └── JwtAuthenticationFilter.java    ← filter/jwt_authentication_filter.tml
├── util/
│   └── JwtUtil.java                    ← util/jwt_util.tml
├── dto/
│   └── ApiResponse.java                ← dto/api_response.tml
├── aop/
│   ├── HttpLoggingAspect.java          ← aop/http_logging_aspect.tml
│   └── ExceptionHandlingAspect.java    ← aop/exception_handling_aspect.tml
├── exception/
│   └── GlobalExceptionHandler.java     ← exception/global_exception_handler.tml
├── security/
│   └── CustomUserDetails.java          ← security/custom_user_details.tml
├── model/
│   └── User.java                       ← model/user.tml
├── repository/
│   └── UserRepository.java             ← repository/user_repository.tml
└── service/
    └── CustomUserDetailsService.java   ← service/custom_user_details_service.tml

src/main/resources/
├── application.properties               ← application_properties.tml
└── logback.xml                         ← logback.tml

根目录/
├── pom.xml                             ← pom_xml.tml
├── README.md                           ← readme.tml
└── .hive/
    ├── agent.md                        ← hive/agent.tml
    ├── backend-agent.md                ← hive/backend-agent.tml
    └── frontend-agent.md               ← hive/frontend-agent.tml
```

## 注意事项

1. **JWT密钥**：生产环境务必使用强密钥，至少32字符
2. **数据库**：H2仅用于开发测试，生产环境请切换到MySQL等
3. **日志级别**：生产环境建议调整为WARN或ERROR
4. **端口配置**：确保端口未被占用
5. **包名规范**：建议使用公司域名倒序，如：com.company.project

## 更新日志

- 2025/10/07: 初始版本，包含完整的Spring Boot业务项目模板
- 2025/10/07: 添加 Hive Agent 配置模板（全栈/后端/前端助手）
- 2025/10/07: 添加用户认证体系（User, UserRepository, CustomUserDetailsService, CustomUserDetails）

