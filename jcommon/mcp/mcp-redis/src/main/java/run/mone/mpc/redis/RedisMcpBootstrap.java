package run.mone.mpc.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mpc.redis")
public class RedisMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(RedisMcpBootstrap.class, args);
    }
}