package run.mone.mcp.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.elasticsearch")
public class ElasticsearchMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchMcpApplication.class, args);
    }

}
