
package run.mone.mcp.neo4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.neo4j")
public class Neo4jMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(Neo4jMcpBootstrap.class, args);
    }
}
