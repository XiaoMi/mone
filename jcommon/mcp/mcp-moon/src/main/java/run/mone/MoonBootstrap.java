package run.mone;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.moon.config", "run.mone.moon.server"})
@DubboComponentScan("run.mone.moon")
public class MoonBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MoonBootstrap.class, args);
    }
}