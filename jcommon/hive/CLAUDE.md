# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Hive** is a multi-agent AI framework for Java that enables collaborative AI systems. Core philosophy: "One bee is dumb, but a swarm of bees is smart" - multiple AI agents with different roles work together on complex tasks.

The project is inspired by MetaGPT for agent design and borrows MCP (Model Context Protocol) implementation concepts from Spring AI and Cline.

**Artifact**: `run.mone:hive` version `1.6.2-jdk21-SNAPSHOT`
**Java Version**: 17 (source/target)
**Parent**: `run.mone:jcommon:1.6.0-jdk21-SNAPSHOT`

## Build Commands

### Compile and Build
```bash
mvn clean install
```

### Run Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=MarkdownParserServiceTest

# Specific test method
mvn test -Dtest=MarkdownParserServiceTest#testParseMarkdownWithFrontMatter
```

### Generate Protobuf Sources
Protobuf compilation happens automatically during build, but to regenerate manually:
```bash
mvn protobuf:compile
mvn protobuf:compile-custom
```

Proto files are located in `src/main/proto/` and generated sources go to `target/generated-sources/protobuf/`.

### Run Example Classes
```bash
mvn exec:java -Dexec.mainClass="run.mone.hive.ClassName"
```

## High-Level Architecture

### Core Components and Their Interactions

#### 1. Team & Environment (Orchestration Layer)
- **Team** (`Team.java`): Top-level orchestrator managing a collection of Roles, budget tracking via CostManager, and project execution with configurable rounds
- **Environment** (`Environment.java`): Message bus for inter-agent communication with role registry, message history, address-based routing, and lifecycle management

#### 2. Role System (Agent Layer)
**Role** (`roles/Role.java`) is the core agent abstraction with:
- Properties: name, profile, goal, constraints
- Components: `List<Action>` actions, `RoleContext` (memory/state), `LLM`, `Environment`, `watchList` (message filters)

**Execution Modes** (`ReactMode` enum):
- `BY_ORDER`: Execute actions sequentially
- `REACT`: LLM decides which action based on state
- `PLAN_AND_ACT`: Create plan first, then execute
- `MANUAL`: No automatic reactions

**Think-Act Cycle**:
```
observe() → think() → act() → react() → publishMessage()
```

**Concrete Roles**: Architect, Writer, Teacher, DatabaseAssistant, Coordinator, Human

#### 3. Action System (Execution Layer)
**Action** (`actions/Action.java`): Task execution unit with:
- Core: name, description, prompt
- Execution: `TriFunction<ActionReq, Action, ActionContext, Message> function`
- Integration: LLM, Role reference

**Action Categories**: Programmer actions (WriteCode, FixBug, RunCode, WriteTest), Python actions, Database actions, Planning actions, Special actions (ActionSelectionAction, TeamSelectionAction)

#### 4. Memory System (Persistence Layer)

**Short-term Memory** (`memory/Memory.java`):
- Message storage with indexing by action type
- Eviction policies (LRU, FIFO) with configurable limits
- Thread-safe with ReadWriteLock
- Key methods: `add()`, `getByRole()`, `getByAction()`, `tryRemember()`, `triggerEviction()`

**Long-term Memory** (`memory/longterm/`):
- Java port of mem0 Python library
- Multi-level: User Memory, Agent Memory, Session Memory, Procedural Memory
- Architecture: LLMBase (fact extraction) + EmbeddingBase (vectors) + VectorStoreBase (search) + GraphStoreBase (relationships)
- Providers: OpenAI/Claude/Gemini/Ollama (LLM), OpenAI/HuggingFace/Ollama (embeddings), Chroma/Qdrant (vector), Kuzu/Neo4j (graph)

#### 5. MCP Integration (Tool Layer)
**McpHub** (`mcp/hub/McpHub.java`):
- Manages multiple MCP server connections
- File-based configuration with hot reload
- Connection types: gRPC, stdio, SSE, HTTP
- Health monitoring with auto-reconnect
- Methods: `callTool()`, `callToolStream()`

**Transport Types**: GrpcClientTransport, StdioClientTransport, HttpClientSseClientTransport, HttpClientStreamableHttpTransport

#### 6. LLM Integration (Model Layer)
**LLM** (`llm/LLM.java`) - Comprehensive abstraction supporting:
- 20+ providers with streaming/synchronous modes
- Multimodal (text + images), audio generation/transcription
- Token usage tracking, cache control (Claude)
- Key methods: `chat()`, `call()`/`callStream()`, `chatCompletion()`, `classifyIntent()`, `shouldInitiateChat()`
- RAG, classification, web search integration

### Data Flow: Typical Multi-Agent Workflow

```
Team.runProject(idea)
  ↓
Environment.publishMessage(UserRequirement)
  ↓
Role.observe() → filters by watchList
  ↓
Role.think() → ReactMode determines action
  ↓
Role.act() → executes Action with ActionReq
  ↓
Action.function → calls LLM, MCP tools
  ↓
Result stored in RoleContext.memory
  ↓
Role.react() → publishes response Message
  ↓
Environment routes to subscribed roles
  ↓
Repeat until idle or budget exhausted
```

### Key Data Models

- **Message** (`schema/Message.java`): id, role, content, causeBy, sendTo, receivers, type, createTime, images (multimodal), sink (reactive)
- **RoleContext** (`schema/RoleContext.java`): Memory, todo Action, ReactMode, news queue, state, retry logic
- **Context** (`context/Context.java`): defaultLLM, CostManager, Team, kwargs map
- **ActionReq**: Request context for action execution
- **Plan**: Planning structure with tasks
- **MemoryConfig**: Hierarchical config for LLM, embeddings, vector store, graph store

## Architectural Patterns Used

1. **Actor Model**: Akka actors for concurrency, message-based communication
2. **Pub-Sub**: Environment as message bus, roles subscribe via watchList, address-based routing
3. **Strategy**: ReactMode for reaction strategies, EvictionPolicy for memory, multiple LLM providers
4. **Factory**: LLMFactory, ActionFactory, EmbeddingFactory, VectorStoreFactory, GraphStoreFactory
5. **Builder**: Extensively used for configs (LLMConfig, MemoryConfig, ServerParameters)
6. **Template Method**: Role.react() workflow with overridable hooks (beforeReact, postReact, doReact)
7. **Observer**: File watching for MCP configuration, message observation

## Package Structure

- `a2a/`: Agent-to-agent communication types
- `actions/`: Action implementations (programmer, python, database, planning)
- `checkpoint/`: State persistence
- `context/`: Context management and compression
- `llm/`: LLM provider implementations
- `markdown/`: Markdown parsing with YAML front matter support
- `mcp/`: Model Context Protocol client/server/hub implementations
- `memory/`: Short-term and long-term memory systems
- `planner/`: Team planning strategies
- `roles/`: Concrete role implementations
- `schema/`: Core data models
- `task/`: Task execution framework
- `tools/`: Utility tools
- `workspace/`: Workspace management

## Important Dependencies

- **gRPC** (1.60.0): For MCP gRPC transport
- **Akka** (2.6.11): Actor system for concurrency
- **Jackson** (2.17.0) + YAML (2.15.3): JSON/YAML processing
- **Flexmark** (0.64.8): Markdown processing
- **JavaParser** (3.25.8): Java code parsing
- **Reactor** (3.7.0): Reactive streams
- **Spring Boot** (3.2.3): Optional web support
- **MCP SDK** (0.15.0): Model Context Protocol
- **ChromaDB** (0.1.7), **Qdrant** (1.15.0): Vector stores
- **Kuzu** (0.11.2), **Neo4j** (5.27.0): Graph stores
- **Lucene** (9.9.2): BM25 search ranking
- **CFR** (0.152): JAR decompilation

## Code Conventions

### YAML Front Matter in Markdown
When working with markdown files that have YAML front matter (definition tables), use `MarkdownParserService`:

```java
MarkdownParserService service = new MarkdownParserService();
MarkdownDocument doc = service.parseMarkdown(content);

// Access definitions (YAML front matter)
String name = doc.getDefinitionAsString("name");
Map<String, Object> allDefs = doc.getDefinitions();

// Access content
String content = doc.getContent();
```

### Protobuf Changes
When modifying `.proto` files in `src/main/proto/`, regenerate sources:
```bash
mvn clean compile
```

Generated code goes to `target/generated-sources/protobuf/java/` and `target/generated-sources/protobuf/grpc-java/`.

### Memory Management
- Short-term memory auto-evicts based on configured policy
- Long-term memory requires explicit initialization with MemoryConfig
- Always check eviction policies when adding to Memory

### LLM Provider Configuration
LLM instances require LLMConfig with:
- `url`: Provider endpoint
- `token`: API key
- `model`: Model identifier
- Optional: temperature, maxTokens, stream, json response flags

### MCP Server Configuration
McpHub uses file-based configuration (typically JSON/YAML) with:
```java
ServerParameters {
  command, args, env  // For stdio
  url                 // For SSE/HTTP
  type               // "grpc" | "stdio" | "sse" | "http"
}
```

Hot reload watches config file for changes.

## Testing Notes

- Tests use JUnit 5 Jupiter API
- Mockito for mocking (version 1.10.19, older version)
- Test resources in `src/test/resources/`
- Integration tests may require external services (vector stores, graph DBs)
- Some tests create temporary files - always clean up in finally blocks

## Key Design Principles

1. **Hybrid Memory**: Both short-term (queue-based) and long-term (vector/graph) for different retrieval patterns
2. **Flexible Execution**: Multiple react modes adapt to different task types
3. **Protocol Agnostic**: MCP abstraction supports multiple transports seamlessly
4. **Cost Management**: Built-in budget tracking prevents runaway costs
5. **Multimodal First**: Native support for text + images throughout
6. **Streaming**: Reactive streams (Project Reactor) for real-time responses
7. **Resilience**: Retry logic, health checks, auto-reconnect for robustness
