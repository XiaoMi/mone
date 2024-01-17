package run.mone.mimeter.dashboard.bootstrap;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dongzhenxing
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"run.mone.mimeter.dashboard", "com.xiaomi.youpin"})
@DubboComponentScan(basePackages = "run.mone.mimeter.dashboard")
public class MimeterDashboardBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(MimeterDashboardBootstrap.class);

    public static void main(String... args) {
        try {
            SpringApplication.run(MimeterDashboardBootstrap.class, args);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}