# Shannon - Claude Agent SDK for Java

Shannon is a Java port of the Python Claude Agent SDK, providing a type-safe, reactive API for interacting with Claude Code CLI.

## Features

- ✅ **Simple Query API**: One-shot queries with `ClaudeAgentSDK.query()`
- ✅ **Streaming Support**: Real-time message streaming with Reactor Flux
- ✅ **Bidirectional Client**: Full control with `ClaudeAgentClient`
- ✅ **Hooks System**: Intercept and control tool usage
- ✅ **MCP Integration**: Support for stdio, SSE, and SDK MCP servers
- ✅ **Control Protocol**: Interrupt, change models, rewind files dynamically
- ✅ **Type Safety**: Sealed interfaces with Java 17 for compile-time safety
- ✅ **Reactive**: Built on Project Reactor for async/streaming operations

## Quick Start

### Simple Query

```java
import run.mone.hive.shannon.ClaudeAgentSDK;
import run.mone.hive.shannon.ClaudeAgentOptions;

// Simple query with defaults
ResultMessage result = ClaudeAgentSDK.query("What is 2+2?").join();
System.out.println("Result: " + result);

// Query with custom options
ClaudeAgentOptions options = ClaudeAgentOptions.builder()
    .model("sonnet")
    .tool("Bash")
    .tool("Edit")
    .permissionMode(PermissionMode.AUTO)
    .build();

ResultMessage result = ClaudeAgentSDK.query("List files in current directory", options).join();
```

### Streaming Query

```java
// Stream messages as they arrive
ClaudeAgentSDK.queryStream("Analyze this codebase")
    .doOnNext(message -> {
        if (message.isAssistant()) {
            System.out.println("Assistant: " + message.asAssistant().content());
        }
    })
    .blockLast();
```

### Interactive Session

```java
try (ClaudeAgentClient client = new ClaudeAgentClient(options)) {
    // Connect and send initial prompt
    client.connect("Start coding session").join();

    // Send follow-up queries
    client.query("Create a new Java class").join();
    client.query("Add a method to calculate factorial").join();

    // Receive all messages
    client.receiveMessages()
        .doOnNext(msg -> System.out.println(msg))
        .subscribe();

    // Wait for completion
    ResultMessage result = client.waitForResult().join();
}
```

### Hooks Example

```java
// Hook to intercept Bash tool usage
HookMatcher bashHook = HookMatcher.builder()
    .toolNameMatcher("Bash")
    .callback((input, toolUseId, context) -> {
        String command = (String) ((Map<?, ?>) input.get("toolInput")).get("command");

        // Block dangerous commands
        if (command.contains("rm -rf")) {
            Map<String, Object> output = new HashMap<>();
            output.put("decision", "deny");
            output.put("reason", "Dangerous command blocked");
            return CompletableFuture.completedFuture(output);
        }

        // Log and allow
        System.out.println("Running: " + command);
        Map<String, Object> output = new HashMap<>();
        output.put("continue", true);
        return CompletableFuture.completedFuture(output);
    })
    .build();

ClaudeAgentOptions options = ClaudeAgentOptions.builder()
    .model("sonnet")
    .hook(HookEvent.PRE_TOOL_USE, List.of(bashHook))
    .build();
```

### MCP Server Configuration

```java
// Stdio MCP server
StdioMcpConfig weatherServer = StdioMcpConfig.builder()
    .command("node")
    .arg("weather-server.js")
    .build();

// SSE MCP server
SseMcpConfig apiServer = SseMcpConfig.builder()
    .url("https://api.example.com/mcp")
    .apiKey("your-api-key")
    .build();

ClaudeAgentOptions options = ClaudeAgentOptions.builder()
    .mcpServer("weather", weatherServer)
    .mcpServer("api", apiServer)
    .build();
```

### Control Protocol

```java
try (ClaudeAgentClient client = new ClaudeAgentClient(options)) {
    client.connect("Start working").join();

    // Interrupt execution
    client.interrupt().join();

    // Change model mid-conversation
    client.setModel("opus").join();

    // Change permission mode
    client.setPermissionMode(PermissionMode.DENY).join();

    // Rewind files to a checkpoint
    client.rewindFiles("message-uuid-123").join();
}
```

## Configuration Options

### Essential Options (MVP)

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `model` | String | `"sonnet"` | Model to use: "sonnet", "opus", "haiku" |
| `apiKey` | String | `null` | API key (or use ANTHROPIC_API_KEY env var) |
| `systemPrompt` | String | `null` | System prompt for the conversation |
| `tools` | List<String> | `[]` | Tools to enable (e.g., "Bash", "Edit", "Read") |
| `disabledTools` | List<String> | `[]` | Tools to disable |
| `mcpServers` | Map | `{}` | MCP server configurations |
| `permissionMode` | PermissionMode | `AUTO` | Permission mode: ALLOW, DENY, PROMPT, AUTO |
| `canUseTool` | Callback | `null` | Dynamic tool permission callback |
| `allowedDirectories` | List<Path> | `[]` | Directories allowed for file operations |
| `hooks` | Map | `{}` | Hook configurations |
| `cwd` | Path | `null` | Working directory for CLI process |
| `env` | Map<String,String> | `{}` | Environment variables |
| `sessionId` | String | `null` | Session ID for resuming |
| `maxBudget` | Double | `null` | Maximum budget in USD |
| `maxRounds` | Integer | `null` | Maximum execution rounds |
| `stream` | boolean | `true` | Enable streaming mode |
| `cliPath` | String | `null` | Path to CLI binary (auto-detect if null) |

### Preset Configurations

```java
// Default configuration
ClaudeAgentOptions.defaults()

// Quick query (no tools, minimal permissions)
ClaudeAgentOptions.quickQuery()

// Full access (all tools enabled)
ClaudeAgentOptions.fullAccess()
```

## Architecture

### Type System

Shannon uses Java 17 sealed interfaces for type-safe discriminated unions:

```java
// Messages
public sealed interface Message
    permits UserMessage, AssistantMessage, SystemMessage, ResultMessage, StreamEvent

// Content Blocks
public sealed interface ContentBlock
    permits TextBlock, ThinkingBlock, ToolUseBlock, ToolResultBlock

// Pattern matching
if (message instanceof AssistantMessage assistant) {
    for (ContentBlock block : assistant.content()) {
        if (block instanceof TextBlock text) {
            System.out.println(text.text());
        }
    }
}
```

### Async/Reactive

- **CompletableFuture**: For single-value async operations
- **Reactor Flux**: For streaming operations
- **Schedulers**: Separate schedulers for stdin/stdout/stderr

### Transport Layer

- **Transport**: Interface for different transport types
- **SubprocessCliTransport**: Manages Claude Code CLI subprocess
- **MessageParser**: Incremental JSON parsing

### Control Protocol

- **ControlProtocol**: Bidirectional communication for advanced features
- **CorrelationManager**: Request/response correlation with timeouts

## Requirements

- Java 17 or higher
- Claude Code CLI installed (`npm install -g @anthropic-ai/claude-code`)
- Anthropic API key (set via `ANTHROPIC_API_KEY` env var or in options)

## Dependencies

All major dependencies are already available in Hive:
- Jackson (JSON serialization)
- Reactor (reactive streams)
- Lombok (builders)
- SLF4J (logging)
- Gson (JSON processing)

## Package Structure

```
run.mone.hive.shannon/
├── ClaudeAgentSDK.java              # Simple facade
├── ClaudeAgentClient.java           # Bidirectional client
├── ClaudeAgentOptions.java          # Configuration
│
├── types/
│   ├── messages/                    # Message types
│   ├── content/                     # Content blocks
│   ├── hooks/                       # Hook types
│   ├── permissions/                 # Permission types
│   └── mcp/                         # MCP configs
│
├── transport/                       # Transport layer
├── control/                         # Control protocol
├── hooks/                           # Hooks system
└── exceptions/                      # Exceptions
```

## Error Handling

```java
try {
    ResultMessage result = ClaudeAgentSDK.query("Test query").join();
    if (!result.isSuccessful()) {
        System.err.println("Query failed: " + result.error());
    }
} catch (CLINotFoundError e) {
    System.err.println("Claude Code CLI not found. Please install it.");
} catch (CLIConnectionError e) {
    System.err.println("Failed to connect to CLI: " + e.getMessage());
} catch (ControlProtocolError e) {
    System.err.println("Control protocol error: " + e.getMessage());
} catch (MessageParseError e) {
    System.err.println("Failed to parse message: " + e.getMessage());
}
```

## Integration with Hive

Shannon can be used by Hive roles:

```java
public class ClaudeCodeRole extends Role {
    private final ClaudeAgentClient client;

    public ClaudeCodeRole(String name, ClaudeAgentOptions options) {
        super(name, "Claude Code Assistant");
        this.client = new ClaudeAgentClient(options);
    }

    @Override
    protected CompletableFuture<Message> act(ActionContext context) {
        Message lastMessage = rc.getMemory().getLastMessage();

        return client.query(lastMessage.getContent())
            .thenApply(result -> Message.builder()
                .role(getName())
                .content(ClaudeAgentSDK.extractText(List.of(result)))
                .build());
    }

    @Override
    public void dispose() {
        client.disconnect().join();
    }
}
```

## Future Enhancements

Features deferred for future releases:
- Advanced session management (fork, resume)
- Structured outputs (JSON schema validation)
- All 67 configuration fields from Python SDK
- Advanced sandbox configuration
- Plugin system
- Extended context presets
- Full HTTP MCP implementation

## License

Apache License 2.0 (same as Hive framework)
