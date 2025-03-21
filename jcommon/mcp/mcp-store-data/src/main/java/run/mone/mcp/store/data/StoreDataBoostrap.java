package run.mone.mcp.store.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.store.data")
public class StoreDataBoostrap {
    public static void main(String[] args) {
        try {
            SpringApplication.run(StoreDataBoostrap.class, args);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}