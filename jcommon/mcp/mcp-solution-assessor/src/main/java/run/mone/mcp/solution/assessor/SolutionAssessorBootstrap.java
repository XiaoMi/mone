package run.mone.mcp.solution.assessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.solution.assessor")
public class SolutionAssessorBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(SolutionAssessorBootstrap.class, args);
    }
}
