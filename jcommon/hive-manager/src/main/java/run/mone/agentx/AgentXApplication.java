package run.mone.agentx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgentXApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentXApplication.class, args);
    }
}