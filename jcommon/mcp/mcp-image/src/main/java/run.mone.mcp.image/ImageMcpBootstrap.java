package run.mone.mcp.image;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


//生成图片
@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.image", "run.mone.hive.mcp.service"})
@Slf4j
public class ImageMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ImageMcpBootstrap.class, args);
    }
}