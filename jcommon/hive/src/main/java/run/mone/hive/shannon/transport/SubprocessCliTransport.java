package run.mone.hive.shannon.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import run.mone.hive.shannon.ClaudeAgentOptions;
import run.mone.hive.shannon.exceptions.CLIConnectionError;
import run.mone.hive.shannon.exceptions.CLINotFoundError;
import run.mone.hive.shannon.types.messages.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Transport implementation using subprocess stdio communication with Claude Code CLI.
 * Based on the pattern from Hive's StdioClientTransport.
 */
public class SubprocessCliTransport implements Transport {

    private static final Logger logger = LoggerFactory.getLogger(SubprocessCliTransport.class);
    private static final String CLI_COMMAND = "claude-code";
    private static final String MIN_CLI_VERSION = "2.0.0";

    private final ClaudeAgentOptions options;
    private final ObjectMapper objectMapper;
    private final Gson gson;

    // Process and streams
    private Process process;
    private BufferedWriter stdin;
    private BufferedReader stdout;
    private BufferedReader stderr;

    // Reactor infrastructure (following StdioClientTransport pattern)
    private final Sinks.Many<Message> inboundSink;
    private final Sinks.Many<Map<String, Object>> outboundSink;
    private final Sinks.Many<String> errorSink;

    private Scheduler inboundScheduler;
    private Scheduler outboundScheduler;
    private Scheduler errorScheduler;

    // State
    private volatile boolean isConnected = false;
    private volatile boolean isClosing = false;

    private MessageParser messageParser;

    public SubprocessCliTransport(ClaudeAgentOptions options) {
        this.options = options;
        this.objectMapper = new ObjectMapper();
        this.gson = new Gson();

        // Create sinks for message flow (following StdioClientTransport pattern)
        this.inboundSink = Sinks.many().multicast().onBackpressureBuffer();
        this.outboundSink = Sinks.many().unicast().onBackpressureBuffer();
        this.errorSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @Override
    public CompletableFuture<Void> connect() {
        return CompletableFuture.runAsync(() -> {
            try {
                logger.info("Starting Claude Code CLI process...");

                // Find CLI binary
                String cliPath = findCliBinary();

                // Build command
                List<String> command = buildCommand(cliPath);
                logger.debug("Command: {}", command);

                // Start process
                ProcessBuilder pb = new ProcessBuilder(command);
                if (options.getCwd() != null) {
                    pb.directory(options.getCwd().toFile());
                }

                // Set environment variables
                Map<String, String> env = pb.environment();
                if (options.getEnv() != null) {
                    env.putAll(options.getEnv());
                }
                if (options.getApiKey() != null) {
                    env.put("ANTHROPIC_API_KEY", options.getApiKey());
                }

                process = pb.start();

                // Set up streams
                stdin = new BufferedWriter(new OutputStreamWriter(
                    process.getOutputStream(), StandardCharsets.UTF_8));
                stdout = new BufferedReader(new InputStreamReader(
                    process.getInputStream(), StandardCharsets.UTF_8));
                stderr = new BufferedReader(new InputStreamReader(
                    process.getErrorStream(), StandardCharsets.UTF_8));

                // Create schedulers
                inboundScheduler = Schedulers.newSingle("claude-inbound");
                outboundScheduler = Schedulers.newSingle("claude-outbound");
                errorScheduler = Schedulers.newSingle("claude-error");

                // Create message parser
                messageParser = new MessageParser(objectMapper, message -> {
                    inboundSink.tryEmitNext(message);
                });

                // Start reading threads
                startStdoutReader();
                startStderrReader();
                startStdinWriter();

                // Register shutdown hook
                Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));

                isConnected = true;
                logger.info("Claude Code CLI process started successfully");

            } catch (IOException e) {
                throw new CLIConnectionError("Failed to start Claude Code CLI", e);
            }
        });
    }

    /**
     * Find the Claude Code CLI binary.
     *
     * @return path to the CLI binary
     * @throws CLINotFoundError if CLI cannot be found
     */
    private String findCliBinary() throws CLINotFoundError {
        // Use custom path if specified
        if (options.getCliPath() != null && !options.getCliPath().isEmpty()) {
            Path path = Path.of(options.getCliPath());
            if (Files.exists(path) && Files.isExecutable(path)) {
                return path.toString();
            }
            throw new CLINotFoundError("CLI not found at specified path: " + options.getCliPath());
        }

        // Try to find in PATH
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            for (String dir : pathEnv.split(File.pathSeparator)) {
                Path cliPath = Path.of(dir, CLI_COMMAND);
                if (Files.exists(cliPath) && Files.isExecutable(cliPath)) {
                    return cliPath.toString();
                }
                // Try with .cmd extension on Windows
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Path cliPathCmd = Path.of(dir, CLI_COMMAND + ".cmd");
                    if (Files.exists(cliPathCmd)) {
                        return cliPathCmd.toString();
                    }
                }
            }
        }

        throw new CLINotFoundError();
    }

    /**
     * Build the command line for starting Claude Code CLI.
     *
     * @param cliPath path to the CLI binary
     * @return command line arguments
     */
    private List<String> buildCommand(String cliPath) {
        List<String> command = new ArrayList<>();
        command.add(cliPath);
        command.add("agent");

        // Add model
        if (options.getModel() != null) {
            command.add("--model");
            command.add(options.getModel());
        }

        // Add tools
        if (options.getTools() != null && !options.getTools().isEmpty()) {
            for (String tool : options.getTools()) {
                command.add("--tool");
                command.add(tool);
            }
        }

        // Add disabled tools
        if (options.getDisabledTools() != null && !options.getDisabledTools().isEmpty()) {
            for (String tool : options.getDisabledTools()) {
                command.add("--disabled-tool");
                command.add(tool);
            }
        }

        // Add permission mode
        if (options.getPermissionMode() != null) {
            command.add("--permission-mode");
            command.add(options.getPermissionMode().getValue());
        }

        // Add MCP servers
        if (options.getMcpServers() != null && !options.getMcpServers().isEmpty()) {
            command.add("--mcp-servers");
            command.add(gson.toJson(options.getMcpServers()));
        }

        // Add system prompt
        if (options.getSystemPrompt() != null) {
            command.add("--system-prompt");
            command.add(options.getSystemPrompt());
        }

        // Add streaming
        if (options.isStream()) {
            command.add("--stream");
        }

        // Add max budget
        if (options.getMaxBudget() != null) {
            command.add("--max-budget");
            command.add(options.getMaxBudget().toString());
        }

        // Add max rounds
        if (options.getMaxRounds() != null) {
            command.add("--max-rounds");
            command.add(options.getMaxRounds().toString());
        }

        // Add JSON output mode
        command.add("--output");
        command.add("json");

        return command;
    }

    /**
     * Start the stdout reader thread.
     */
    private void startStdoutReader() {
        inboundScheduler.schedule(() -> {
            try {
                String line;
                while (!isClosing && (line = stdout.readLine()) != null) {
                    logger.trace("Received line: {}", line);
                    messageParser.parseLine(line);
                }
                logger.debug("Stdout reader finished");
                messageParser.flush();
                inboundSink.tryEmitComplete();
            } catch (IOException e) {
                if (!isClosing) {
                    logger.error("Error reading from stdout", e);
                    inboundSink.tryEmitError(new CLIConnectionError("Error reading from CLI", e));
                }
            }
        });
    }

    /**
     * Start the stderr reader thread.
     */
    private void startStderrReader() {
        errorScheduler.schedule(() -> {
            try {
                String line;
                while (!isClosing && (line = stderr.readLine()) != null) {
                    logger.warn("CLI stderr: {}", line);
                    errorSink.tryEmitNext(line);
                }
                logger.debug("Stderr reader finished");
            } catch (IOException e) {
                if (!isClosing) {
                    logger.error("Error reading from stderr", e);
                }
            }
        });
    }

    /**
     * Start the stdin writer thread.
     */
    private void startStdinWriter() {
        outboundSink.asFlux()
            .subscribeOn(outboundScheduler)
            .subscribe(
                message -> {
                    try {
                        String json = gson.toJson(message);
                        logger.trace("Sending: {}", json);
                        stdin.write(json);
                        stdin.newLine();
                        stdin.flush();
                    } catch (IOException e) {
                        if (!isClosing) {
                            logger.error("Error writing to stdin", e);
                        }
                    }
                },
                error -> logger.error("Error in outbound stream", error),
                () -> logger.debug("Outbound stream completed")
            );
    }

    @Override
    public CompletableFuture<Void> write(Map<String, Object> message) {
        if (!isConnected) {
            return CompletableFuture.failedFuture(
                new CLIConnectionError("Transport not connected"));
        }

        outboundSink.tryEmitNext(message);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Flux<Message> readMessages() {
        return inboundSink.asFlux();
    }

    @Override
    public boolean isReady() {
        return isConnected && !isClosing && process != null && process.isAlive();
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public CompletableFuture<Void> endInput() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (stdin != null) {
                    stdin.close();
                    logger.debug("Closed stdin");
                }
            } catch (IOException e) {
                logger.error("Error closing stdin", e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> close() {
        return CompletableFuture.runAsync(this::cleanup);
    }

    /**
     * Clean up resources.
     */
    private void cleanup() {
        if (isClosing) {
            return;
        }

        isClosing = true;
        logger.info("Cleaning up Claude CLI transport...");

        try {
            // Close streams
            if (stdin != null) {
                stdin.close();
            }
            if (stdout != null) {
                stdout.close();
            }
            if (stderr != null) {
                stderr.close();
            }

            // Terminate process
            if (process != null && process.isAlive()) {
                process.destroy();
                process.waitFor();
            }

            // Dispose schedulers
            if (inboundScheduler != null) {
                inboundScheduler.dispose();
            }
            if (outboundScheduler != null) {
                outboundScheduler.dispose();
            }
            if (errorScheduler != null) {
                errorScheduler.dispose();
            }

            isConnected = false;
            logger.info("Claude CLI transport cleaned up");

        } catch (Exception e) {
            logger.error("Error during cleanup", e);
        }
    }
}
