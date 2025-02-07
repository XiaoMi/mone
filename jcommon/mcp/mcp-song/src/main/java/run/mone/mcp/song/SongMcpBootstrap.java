package run.mone.mcp.song;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.song")
public class SongMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(SongMcpBootstrap.class, args);
    }

}