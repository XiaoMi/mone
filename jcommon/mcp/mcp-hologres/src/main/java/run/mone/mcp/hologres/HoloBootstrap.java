package run.mone.mcp.hologres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"run.mone.mcp.hologres"})
public class HoloBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(HoloBootstrap.class, args);
    }
}
