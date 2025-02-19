
package run.mone.mcp.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.calendar")
public class CalendarMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(CalendarMcpBootstrap.class, args);
    }
}
