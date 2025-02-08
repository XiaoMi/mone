package run.mone.m78.service.database;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/15/24 15:32
 */
@Configuration
@Getter
public class UserDBConfig {

    @Value("${db_host_cus}")
    private String userDh;

    @Value("${db_port_cus}")
    private String userDp;

    @Value("${db_name_cus}")
    private String userDn;

    @Value("${db_uname_cus}")
    private String userUname;

    @Value("${db_pwd_cus}")
    private String userPwd;

    @Value("${db_url_cus}")
    private String userUrl;

}
