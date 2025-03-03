package run.mone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan("run.mone.moon")
public class MoonBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MoonBootstrap.class, args);
    }
}