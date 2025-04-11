package run.mone.supergateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class StdioToSseGateway {
    private final CommandLine cmd;
    private Process childProcess;
    
    public void start() {
        try {
            String stdioCmd = cmd.getOptionValue("stdio");
            ProcessBuilder processBuilder = new ProcessBuilder(stdioCmd.split("\\s+"));
            childProcess = processBuilder.start();
            
            // 启动子进程的输入输出处理线程
            startIOThreads();
            
        } catch (IOException e) {
            System.err.println("Error starting child process: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private void startIOThreads() {
        // 处理子进程的标准输出
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(childProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 处理输出并转发到SSE
                    handleChildOutput(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading from child process: " + e.getMessage());
            }
        }).start();
        
        // 处理子进程的标准错误
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(childProcess.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("Child stderr: " + line);
                }
            } catch (IOException e) {
                System.err.println("Error reading stderr from child process: " + e.getMessage());
            }
        }).start();
    }
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void handleChildOutput(String line) {
        try {
            // 转发消息到SSE控制器
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(line, headers);
            restTemplate.postForObject("http://localhost:8080/api/message", request, String.class);
        } catch (Exception e) {
            System.err.println("Error handling child output: " + e.getMessage());
        }
    }
}