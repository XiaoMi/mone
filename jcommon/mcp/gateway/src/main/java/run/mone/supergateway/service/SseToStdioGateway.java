package run.mone.supergateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class SseToStdioGateway {
    private final String sseUrl;
    private final String command;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private Process process;
    private BufferedReader reader;
    private BufferedWriter writer;
    private final ExecutorService executorService;
    private volatile boolean running;

    public SseToStdioGateway(String sseUrl, String command) {
        this.sseUrl = sseUrl;
        this.command = command;
        this.objectMapper = new ObjectMapper();
        this.webClient = WebClient.create();
        this.executorService = Executors.newCachedThreadPool();
        this.running = false;
    }

    public void start() throws IOException {
        log.info("Starting process with command: {}", command);
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        process = processBuilder.start();
        
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        
        running = true;
        
        // 只在有SSE URL时才连接SSE
        if (sseUrl != null && !sseUrl.isEmpty()) {
            connectToSse();
        }
        
        // 启动输出读取
        executorService.submit(this::readOutput);
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private void connectToSse() {
        webClient.get()
                .uri(sseUrl)
                .retrieve()
                .bodyToFlux(String.class)
                .subscribe(
                    message -> {
                        try {
                            if (writer != null && running) {
                                writer.write(message);
                                writer.newLine();
                                writer.flush();
                            }
                        } catch (IOException e) {
                            log.error("Error writing to process", e);
                        }
                    },
                    error -> log.error("SSE connection error", error),
                    () -> log.info("SSE connection closed")
                );
    }

    private void readOutput() {
        String line;
        try {
            while (running && (line = reader.readLine()) != null) {
                try {
                    // 尝试解析JSON
                    Object jsonMessage = objectMapper.readValue(line, Object.class);
                    log.debug("Received JSON message: {}", jsonMessage);
                } catch (Exception e) {
                    log.warn("Received non-JSON message: {}", line);
                }
            }
        } catch (IOException e) {
            log.error("Error reading from process", e);
        }
    }

    public void sendMessage(Object message) throws IOException {
        if (writer != null && running) {
            String jsonMessage = objectMapper.writeValueAsString(message);
            writer.write(jsonMessage);
            writer.newLine();
            writer.flush();
        }
    }

    public void stop() {
        running = false;
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (process != null) {
                process.destroy();
                process.waitFor();
            }
        } catch (Exception e) {
            log.error("Error stopping process", e);
        } finally {
            executorService.shutdown();
        }
    }
}