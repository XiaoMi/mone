package run.mone.mimeter.dashboard.es;

import com.xiaomi.mone.es.EsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dingpei
 */
@Slf4j
@Configuration
public class Es {

    @Value("${es.address}")
    private String address;

    @Value("${es.user}")
    private String user;

    @Value("${es.pwd}")
    private String pwd;

    @Bean
    public EsClient esClient() throws Exception {
        try {
            return new EsClient(address, user, pwd);
        } catch (Exception e) {
            log.error("Es.esClient error, address:{}, user:{}, pwd:{}, msg:{}", address, user, pwd, e.getMessage(), e);
            throw new Exception();
        }
    }


}
