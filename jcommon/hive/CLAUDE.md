# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hive is a Java-based AI agent framework that provides:
- Agent-based architecture with roles (roles package)
- Action execution system (actions package) 
- Long-term memory capabilities (memory/longterm package)
- MCP (Model Context Protocol) integration
- Multi-LLM provider support

The codebase is inspired by MetaGPT for agents, Spring AI and Cline for MCP implementation.

## Development Commands

### Build and Compile
```bash
mvn compile                    # Compile source code
mvn test-compile              # Compile test code
mvn clean compile             # Clean and compile
```

### Testing
```bash
mvn test                      # Run all tests
mvn test -Dtest=ClassName     # Run specific test class
mvn test -Dtest=ClassName#methodName  # Run specific test method
```

### IDE Configuration
For IDE debugging, add this JVM parameter:
```
-Deditable.java.test.console=true
```

### Application Packaging and Running
```bash
mvn package                   # Build JAR
java -jar target/app.jar      # Run the application
```

Alternative server configuration for app.jar:
```json
{
  "my-server": {
    "command": "java",
    "args": [
      "-jar",
      "/path/to/hive/target/app.jar"
    ]
  }
}
```

Kill running instances:
```bash
jps -l|grep app.jar|awk -F '' '{print $1}'|xargs kill -9
```

## Architecture Overview

### Core Agent Framework
- **Team-based architecture**: Multiple agents with different roles work together
- **Think->Act cycle**: Main reasoning pattern for agents (think->act->think->act)
- **Role-Action-Team** pattern: Agents have roles, execute actions, and work in teams
- **Collective intelligence**: "One bee is dumb, but a swarm of bees is smart"
- **Communication features**:
  - gRPC support with headers for clientId and token
  - Permission verification
  - Server push capability
  - Every MCP has a chat tool for user communication
- **Agent lifecycle**: Agents are removed when users disconnect or are inactive too long
- **Advanced capabilities**: Support for interruption, proactive agent communication, model switching

### Key Packages

#### Roles (`src/main/java/run/mone/hive/roles/`)
- `Architect`: System design and architecture planning
- `Writer`: Documentation and writing tasks
- `Teacher`: Educational content creation
- `Human`: Human interaction handling
- `DatabaseAssistant`: Database operations
- `Coordinator`: Team coordination

#### Actions (`src/main/java/run/mone/hive/actions/`)
- Programmer actions: `WriteCode`, `FixBug`, `RunCode`, `WriteTest`, `DebugError`
- Python-specific: `WritePythonCode`, `FixPythonBug`, `ExecutePythonCode`
- Database actions: `QueryDataAction`, `ModifyDataAction`, `DesignSchemaAction`
- Planning: `WritePlan`, `WriteDesign`, `UserRequirement`

#### Long-term Memory (`src/main/java/run/mone/hive/memory/longterm/`)
Java port of mem0 Python library with support for:
- Multiple LLM providers (OpenAI, Claude, Gemini, Ollama)
- Vector stores (Qdrant, Chroma, Weaviate, etc.)
- Graph databases (Neo4j, Memgraph)
- Multi-level memory (user, agent, session, procedural)

#### MCP Integration (`src/main/java/run/mone/hive/mcp/`)
Model Context Protocol implementation for tool integration:
- Server and client implementations
- Hub for managing multiple MCP connections
- Transport layer abstraction

### Configuration and Dependencies

#### Key Dependencies
- Java 17+ (compiled for 17, supports 21)
- gRPC for communication
- Jackson for JSON/YAML processing
- Akka for actor model
- OkHttp for HTTP clients
- JUnit 5 + Mockito for testing

#### Memory Module Dependencies
Long-term memory module supports:
- OpenAI GPT models
- Anthropic Claude
- Google Gemini
- Ollama (local models)
- Vector databases: Chroma
- Graph databases: Neo4j, Kuzu
- Embedded storage: SQLite
- Search ranking: Apache Lucene (BM25)

## Working with the Codebase

### Memory System
The long-term memory system is a key component:
- Located in `src/main/java/run/mone/hive/memory/longterm/`
- Configuration via `MemoryConfig`, `LlmConfig`, `EmbedderConfig`
- Examples in `examples/` subdirectory
- Comprehensive tests in `src/test/java/run/mone/hive/memory/longterm/`

### Adding New Actions
1. Extend `Action` base class
2. Implement required methods
3. Add to appropriate role's action list
4. Register in `ActionFactory` if needed

### Adding New Roles
1. Create role class with appropriate actions
2. Define role-specific prompts
3. Integrate with team selection logic

### Testing Strategy
- Unit tests for individual components
- Integration tests for memory system
- Role-specific tests for agent behaviors
- MCP integration tests

### Recent Updates
- **2025-06-09**: Agent removal on user disconnect and inactivity timeout
- **2025-09-08**:
  - Interruption support (via intent recognition)
  - Proactive agent communication
  - Model switching capabilities
  - Memory system implementation
  - Todo list functionality
  - Context compression features

## Project Structure Context

This is part of the larger `mone` project at path `jcommon/hive`. The codebase follows Maven conventions with clear separation between:
- Core framework (actions, roles, teams)
- Memory systems (short-term, long-term)
- Protocol integrations (MCP, gRPC)
- Utilities and common components

The project emphasizes modularity with factory patterns for LLM providers, vector stores, and embedding models to support easy extension and configuration.