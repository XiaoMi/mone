/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package run.mone.hive.mcp.client.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;
import run.mone.hive.mcp.util.Assert;


/**
 * ORIGINAL CODE IS FROM SPRING AI!!!
 * <p>
 * Implementation of the MCP Stdio transport that communicates with a server process using
 * standard input/output streams. Messages are exchanged as newline-delimited JSON-RPC
 * messages over stdin/stdout, with errors and debug information sent to stderr.
 */
public class StdioClientTransport implements ClientMcpTransport {

    private static final Logger logger = LoggerFactory.getLogger(StdioClientTransport.class);

    private final Sinks.Many<JSONRPCMessage> inboundSink;

    private final Sinks.Many<JSONRPCMessage> outboundSink;

    /**
     * The server process being communicated with
     */
    private Process process;

    private ObjectMapper objectMapper;

    /**
     * Scheduler for handling inbound messages from the server process
     */
    private Scheduler inboundScheduler;

    /**
     * Scheduler for handling outbound messages to the server process
     */
    private Scheduler outboundScheduler;

    /**
     * Scheduler for handling error messages from the server process
     */
    private Scheduler errorScheduler;

    /**
     * Parameters for configuring and starting the server process
     */
    private final ServerParameters params;

    private final Sinks.Many<String> errorSink;

    private volatile boolean isClosing = false;

    // visible for tests
    private Consumer<String> errorHandler = error -> logger.error("Error received: {}", error);

    /**
     * Creates a new StdioClientTransport with the specified parameters and default
     * ObjectMapper.
     *
     * @param params The parameters for configuring the server process
     */
    public StdioClientTransport(ServerParameters params) {
        this(params, new ObjectMapper());
    }

    /**
     * Creates a new StdioClientTransport with the specified parameters and ObjectMapper.
     *
     * @param params       The parameters for configuring the server process
     * @param objectMapper The ObjectMapper to use for JSON serialization/deserialization
     */
    public StdioClientTransport(ServerParameters params, ObjectMapper objectMapper) {
        Assert.notNull(params, "The params can not be null");
        Assert.notNull(objectMapper, "The ObjectMapper can not be null");

        this.inboundSink = Sinks.many().unicast().onBackpressureBuffer();
        this.outboundSink = Sinks.many().unicast().onBackpressureBuffer();

        this.params = params;

        this.objectMapper = objectMapper;

        this.errorSink = Sinks.many().unicast().onBackpressureBuffer();

        // Start threads
        this.inboundScheduler = Schedulers.fromExecutorService(Executors.newSingleThreadExecutor(), "inbound");
        this.outboundScheduler = Schedulers.fromExecutorService(Executors.newSingleThreadExecutor(), "outbound");
        this.errorScheduler = Schedulers.fromExecutorService(Executors.newSingleThreadExecutor(), "error");
    }

    /**
     * Starts the server process and initializes the message processing streams. This
     * method sets up the process with the configured command, arguments, and environment,
     * then starts the inbound, outbound, and error processing threads.
     *
     * @throws RuntimeException if the process fails to start or if the process streams
     *                          are null
     */
    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        return Mono.<Void>fromRunnable(() -> {
            handleIncomingMessages(handler);
            handleIncomingErrors();

            // Prepare command and environment
            List<String> fullCommand = new ArrayList<>();
            fullCommand.add(params.getCommand());
            fullCommand.addAll(params.getArgs());

            ProcessBuilder processBuilder = this.getProcessBuilder();
            processBuilder.command(fullCommand);
            processBuilder.environment().putAll(params.getEnv());

            logger.info("goto start jar:{}", new Gson().toJson(processBuilder));
            // Start the process
            try {
                this.process = processBuilder.start();
            } catch (IOException e) {
                logger.error("processBuilder.start() IOException", e);
                throw new RuntimeException("Failed to start process with command: " + fullCommand, e);
            }

            // Validate process streams
            if (this.process.getInputStream() == null || process.getOutputStream() == null) {
                this.process.destroy();
                throw new RuntimeException("Process input or output stream is null");
            }

            // Start threads
            startInboundProcessing();
            startOutboundProcessing();
            startErrorProcessing();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Creates and returns a new ProcessBuilder instance. Protected to allow overriding in
     * tests.
     *
     * @return A new ProcessBuilder instance
     */
    protected ProcessBuilder getProcessBuilder() {
        // 创建命令列表
        List<String> command = new ArrayList<>();
        command.add(params.getCommand());
        command.addAll(params.getArgs());
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Map<String, String> env = processBuilder.environment();
        
        // 合并环境变量
        env.putAll(params.getEnv());
        
        // 获取操作系统类型
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String pathSeparator = isWindows ? ";" : ":";
        
        // 处理 HOME 环境变量
        if (!env.containsKey("HOME") && !isWindows) {
            env.put("HOME", System.getProperty("user.home"));
        } else if (isWindows && !env.containsKey("USERPROFILE")) {
            env.put("USERPROFILE", System.getProperty("user.home"));
        }
        
        // 获取 PATH 环境变量（Windows 可能是 "Path"）
        String pathKey = findPathKey(env);
        String currentPath = env.get(pathKey);
        
        // 根据操作系统添加常用路径
        List<String> systemPaths = getSystemSpecificPaths(isWindows);
        
        if (currentPath != null) {
            String additionalPaths = systemPaths.stream()
                .filter(p -> !currentPath.contains(p))
                .collect(Collectors.joining(pathSeparator));
                
            if (!additionalPaths.isEmpty()) {
                env.put(pathKey, additionalPaths + pathSeparator + currentPath);
            }
        }
        
        // 日志输出
        logger.info("OS: {}", System.getProperty("os.name"));
        logger.info("Starting process with command: {}", String.join(" ", command));
        logger.info("Working directory: {}", 
            processBuilder.directory() != null ? processBuilder.directory().getAbsolutePath() : "current directory");
        logger.info("Environment PATH: {}", env.get(pathKey));
        
        return processBuilder;
    }

    private String findPathKey(Map<String, String> env) {
        // Windows 可能使用 "Path" 或 "PATH"
        if (env.containsKey("Path")) return "Path";
        if (env.containsKey("PATH")) return "PATH";
        return "PATH"; // 默认值
    }

    private List<String> getSystemSpecificPaths(boolean isWindows) {
        if (isWindows) {
            return Arrays.asList(
                "C:\\Windows",
                "C:\\Windows\\System32",
                "C:\\Program Files",
                "C:\\Program Files (x86)"
            );
        } else {
            return Arrays.asList(
                "/opt/homebrew/bin",  // macOS Homebrew
                "/usr/local/bin",
                "/usr/bin",
                "/bin",
                "/usr/sbin",
                "/sbin"
            );
        }
    }

    /**
     * Sets the handler for processing transport-level errors.
     *
     * <p>
     * The provided handler will be called when errors occur during transport operations,
     * such as connection failures or protocol violations.
     * </p>
     *
     * @param errorHandler a consumer that processes error messages
     */
    public void setErrorHandler(Consumer<String> errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Waits for the server process to exit.
     *
     * @throws RuntimeException if the process is interrupted while waiting
     */
    public void awaitForExit() {
        try {
            this.process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException("Process interrupted", e);
        }
    }

    /**
     * Starts the error processing thread that reads from the process's error stream.
     * Error messages are logged and emitted to the error sink.
     */
    private void startErrorProcessing() {
        this.errorScheduler.schedule(() -> {
            try (BufferedReader processErrorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                String line;
                while (!isClosing && (line = processErrorReader.readLine()) != null) {
                    try {
                        logger.error("Received error line: {}", line);
                        if (!this.errorSink.tryEmitNext(line).isSuccess()) {
                            if (!isClosing) {
                                logger.error("Failed to emit error message");
                            }
                            break;
                        }
                    } catch (Exception e) {
                        if (!isClosing) {
                            logger.error("Error processing error message", e);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                if (!isClosing) {
                    logger.error("Error reading from error stream", e);
                }
            } finally {
                isClosing = true;
                errorSink.tryEmitComplete();
            }
        });
    }

    private void handleIncomingMessages(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> inboundMessageHandler) {
        this.inboundSink.asFlux()
                .flatMap(message -> Mono.just(message)
                        .transform(inboundMessageHandler)
                        .contextWrite(ctx -> ctx.put("observation", "myObservation")))
                .subscribe();
    }

    private void handleIncomingErrors() {
        this.errorSink.asFlux().subscribe(e -> {
            this.errorHandler.accept(e);
        });
    }

    @Override
    public Mono<Object> sendMessage(JSONRPCMessage message) {
        if (this.outboundSink.tryEmitNext(message).isSuccess()) {
            // TODO: essentially we could reschedule ourselves in some time and make
            // another attempt with the already read data but pause reading until
            // success
            // In this approach we delegate the retry and the backpressure onto the
            // caller. This might be enough for most cases.
            return Mono.empty();
        } else {
            return Mono.error(new RuntimeException("Failed to enqueue message"));
        }
    }

    public boolean isJson(String str) {
        try {
            McpSchema.deserializeJsonRpcMessage(this.objectMapper, str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Starts the inbound processing thread that reads JSON-RPC messages from the
     * process's input stream. Messages are deserialized and emitted to the inbound sink.
     */
    private void startInboundProcessing() {
        this.inboundScheduler.schedule(() -> {
            try (BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while (!isClosing && (line = processReader.readLine()) != null) {
                    try {
                        logger.info("line:{}", line);
                        if (!isJson(line) || "".equals(line)) {
                            continue;
                        }
                        JSONRPCMessage message = McpSchema.deserializeJsonRpcMessage(this.objectMapper, line);
                        if (!this.inboundSink.tryEmitNext(message).isSuccess()) {
                            if (!isClosing) {
                                logger.error("Failed to enqueue inbound message: {}", message);
                            }
                            break;
                        }
                    } catch (Exception e) {
                        if (!isClosing) {
                            logger.error("Error processing inbound message for line: " + line, e);
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                if (!isClosing) {
                    logger.error("Error reading from input stream", e);
                }
            } finally {
                isClosing = true;
                inboundSink.tryEmitComplete();
            }
        });
    }

    /**
     * Starts the outbound processing thread that writes JSON-RPC messages to the
     * process's output stream. Messages are serialized to JSON and written with a newline
     * delimiter.
     */
    private void startOutboundProcessing() {
        this.handleOutbound(messages -> messages
                // this bit is important since writes come from user threads and we
                // want to ensure that the actual writing happens on a dedicated thread
                .publishOn(outboundScheduler)
                .handle((message, s) -> {
                    if (message != null && !isClosing) {
                        try {
                            String jsonMessage = objectMapper.writeValueAsString(message);
                            // Escape any embedded newlines in the JSON message as per spec:
                            // https://spec.modelcontextprotocol.io/specification/basic/transports/#stdio
                            // - Messages are delimited by newlines, and MUST NOT contain
                            // embedded newlines.
                            jsonMessage = jsonMessage.replace("\r\n", "\\n").replace("\n", "\\n").replace("\r", "\\n");

                            var os = this.process.getOutputStream();
                            synchronized (os) {
                                os.write(jsonMessage.getBytes(StandardCharsets.UTF_8));
                                os.write("\n".getBytes(StandardCharsets.UTF_8));
                                os.flush();
                            }
                            s.next(message);
                        } catch (IOException e) {
                            s.error(new RuntimeException(e));
                        }
                    }
                }));
    }

    protected void handleOutbound(Function<Flux<JSONRPCMessage>, Flux<JSONRPCMessage>> outboundConsumer) {
        outboundConsumer.apply(outboundSink.asFlux()).doOnComplete(() -> {
            isClosing = true;
            outboundSink.tryEmitComplete();
        }).doOnError(e -> {
            if (!isClosing) {
                logger.error("Error in outbound processing", e);
                isClosing = true;
                outboundSink.tryEmitComplete();
            }
        }).subscribe();
    }

    /**
     * Gracefully closes the transport by destroying the process and disposing of the
     * schedulers. This method sends a TERM signal to the process and waits for it to exit
     * before cleaning up resources.
     *
     * @return A Mono that completes when the transport is closed
     */
    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(() -> {
            isClosing = true;
            logger.debug("Initiating graceful shutdown");
        }).then(Mono.defer(() -> {
            // First complete all sinks to stop accepting new messages
            inboundSink.tryEmitComplete();
            outboundSink.tryEmitComplete();
            errorSink.tryEmitComplete();

            // Give a short time for any pending messages to be processed
            return Mono.delay(Duration.ofMillis(100));
        })).then(Mono.fromFuture(() -> {
            logger.info("Sending TERM to process");
            if (this.process != null) {
                this.process.destroy();
                return process.onExit();
            } else {
                return CompletableFuture.failedFuture(new RuntimeException("Process not started"));
            }
        })).doOnNext(process -> {
            if (process.exitValue() != 0) {
                logger.warn("Process terminated with code " + process.exitValue());
            }
        }).then(Mono.fromRunnable(() -> {
            try {
                // The Threads are blocked on readLine so disposeGracefully would not
                // interrupt them, therefore we issue an async hard dispose.
                inboundScheduler.dispose();
                errorScheduler.dispose();
                outboundScheduler.dispose();

                logger.info("Graceful shutdown completed");
            } catch (Exception e) {
                logger.error("Error during graceful shutdown", e);
            }
        })).then().subscribeOn(Schedulers.boundedElastic());
    }

    public Sinks.Many<String> getErrorSink() {
        return this.errorSink;
    }

    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return this.objectMapper.convertValue(data, typeRef);
    }

}
