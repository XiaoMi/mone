# Hive Manager - AI Agent Registry & Management Platform

## Project Overview
Hive Manager (AgentX) is a multi-agent management platform built with Spring Boot 3.2.3 (Java 21) backend and dual frontend UIs - a Vue 3 web interface and an Ink-based TUI (Terminal UI). It serves as a registry, access control layer, and orchestration hub for AI agents with MCP (Model Context Protocol) integration.

## Architecture

### Backend Structure
- **Framework**: Spring Boot 3.2.3 with reactive R2DBC (MySQL)
- **Package**: `run.mone.agentx.*`
- **Core Dependencies**: 
  - Internal Hive framework (`run.mone.hive.*`) for agent orchestration, LLM integration, MCP clients, and team coordination
  - Reactive stack: reactor-core, spring-boot-starter-webflux, spring-boot-starter-data-jpa
  - WebSocket for real-time agent chat and streaming
  - JWT-based authentication with optional filter (`AUTH_FILTER_ENABLED`)

### Data Model (see [src/main/resources/sql/init.sql](../src/main/resources/sql/init.sql))
- **t_agent**: Agent registry with versioning (`uk_agent_name_group_version`), profiles, goals, tool maps
- **t_agent_instance**: Running agent instances tracked via heartbeat (ip/port)
- **t_agent_access**: Access control via app_id/access_key pairs
- **t_agent_config**: Key-value configs per agent/user (workspace paths, MCP settings)
- **t_task**: Task execution tracking with status (pending/running/completed/failed)
- **t_invoke_history**: LLM call reporting and history

### Critical Components
1. **WebSocket Endpoints** ([websocket/WebSocketConfig.java](../src/main/java/run/mone/agentx/websocket/WebSocketConfig.java)):
   - `/ws/agent/chat` - Agent chat with streaming responses
   - `/ws/realtime/minimaxi` - Real-time AI service integration
   - `/ws/proxy` - WebSocket proxying
   - STOMP over SockJS at `/ws` with `/topic` and `/queue` brokers

2. **MCP Integration** ([service/McpService.java](../src/main/java/run/mone/agentx/service/McpService.java)):
   - Uses `MonerMcpClient` and `McpHub` from internal Hive framework
   - Custom interceptor in [interceptor/CustomMcpInterceptor.java](../src/main/java/run/mone/agentx/interceptor/CustomMcpInterceptor.java)
   - Configurable via `mcp.llm`, `mcp.transport.type`, `mcp.grpc.port` in [application.yml](../src/main/resources/application.yml)

3. **Task Orchestration** ([service/TaskService.java](../src/main/java/run/mone/agentx/service/TaskService.java)):
   - Uses Hive's `Team` and `ReactorRole` for agent-to-agent collaboration
   - Reactive task execution with `Flux`/`Mono` streaming
   - LLM integration via `OpenAILLM` and configurable providers (deepseek, grok, doubao)

### Frontend Options
**Web UI** ([src/main/resources/static/](../src/main/resources/static/)):
- Vue 3 + Vite, Element Plus components
- Build: `cd src/main/resources/static && pnpm install && pnpm run build-only`
- Served from `/agent-manager/agents` after build

**TUI** ([src/main/resources/tui/](../src/main/resources/tui/)):
- Ink + React terminal interface
- Run: `cd src/main/resources/tui && npm run dev`
- See [tui/README.md](../src/main/resources/tui/README.md) for features (auth, agent browsing, chat, task management)

## Development Workflows

### Build & Run
```bash
# Build frontend first (required)
cd src/main/resources/static
pnpm install && pnpm run build-only

# Run Spring Boot (from project root)
mvn clean package
java -jar target/hive-manager-*.jar

# Access: http://localhost:8080/agent-manager/agents
```

### Database Setup
- Configure R2DBC connection via env vars: `TEST_DB_URL`, `TEST_DB_USER_NAME`, `TEST_DB_PASSWORD`, `TEST_DB_NAME`
- Schema auto-init from [sql/init.sql](../src/main/resources/sql/init.sql) (see migrations: [sql/20250513.sql](../src/main/resources/sql/20250513.sql), [sql/20251202_extend_invoke_history.sql](../src/main/resources/sql/20251202_extend_invoke_history.sql))
- Use R2DBC repositories in `run.mone.agentx.repository.*`

### Docker Deployment
- See [Dockerfile](../Dockerfile) for Java 21 runtime with required JVM flags (`--add-opens`, `--enable-preview`)
- Required env vars: DB credentials, `hive_manager_token`

## Coding Conventions

### General Code Generation Rules
- **No documentation files**: When generating or modifying code, do NOT create separate markdown documentation files (e.g., CHANGES.md, UPDATES.md) unless explicitly requested
- Focus on code implementation and inline comments only

### REST Controllers
- Prefix: `/api/v1/{resource}` (see [api.md](../api.md))
- Return `ApiResponse<T>` wrapper for consistency
- Use `@AuthenticationPrincipal User user` for authenticated endpoints
- Example: [AgentController](../src/main/java/run/mone/agentx/controller/AgentController.java) shows agent CRUD + MCP tool fetching

### Service Layer Patterns
- Reactive programming: return `Mono<T>` or `Flux<T>`, never block
- Use `@RequiredArgsConstructor` for dependency injection
- Example async pattern in [TaskService](../src/main/java/run/mone/agentx/service/TaskService.java#L46):
  ```java
  public Flux<String> executeTask(Long taskId, User user) {
      return taskRepository.findById(taskId)
          .flatMapMany(task -> teamExecute(task, user));
  }
  ```

### Security & Auth
- JWT handled by [JwtAuthenticationFilter](../src/main/java/run/mone/agentx/config/JwtAuthenticationFilter.java)
- Exclude public endpoints in `server.cas.ignoreUrl` ([application.yml](../src/main/resources/application.yml))
- Optional Aegis SDK token parsing via `auth.token.parse.url`

### Hive Framework Integration
- Import from `run.mone.hive.*` (external dependency, not in this repo)
- Key classes: `MonerMcpClient`, `McpHub`, `Team`, `ReactorRole`, `LLM`
- MCP tool data: Use `ToolDataInfo` for tool metadata
- Agent health: `HealthInfo`, `RegInfoDto` for registration

## API Examples
See [api.md](../api.md) for full documentation. Key endpoints:
- `POST /api/v1/agents/config` - Get agent config by agentId/userId
- `POST /api/v1/agents/config/save` - Save agent config (mcp, workspacePath, etc.)
- `GET /api/v1/agents/{id}/instances` - List running instances
- WebSocket: Connect to `/ws/agent/chat` with session ID for streaming chat

## Testing
- Run backend tests: `./src/main/resources/tui/scripts/test-backend.sh`
- Frontend dev mode: `cd src/main/resources/static && pnpm run dev`
- TUI dev mode: `cd src/main/resources/tui && npm run dev:debug`

## Key Decisions & Constraints
- **Java 21 required**: Uses preview features, virtual threads
- **Frontend build BEFORE backend**: Static assets must exist in `static/dist/`
- **No Spring Security**: Excluded in `@SpringBootApplication`, custom JWT filter used
- **R2DBC reactive**: Do not mix blocking DB calls
- **MCP transport**: Configurable (gRPC default on port 9286)
- **Agent versioning**: Unique constraint on (name, agent_group, version)
