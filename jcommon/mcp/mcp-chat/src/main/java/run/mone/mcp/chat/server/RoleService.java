package run.mone.mcp.chat.server;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 09:49
 */
@Service
public class RoleService {

    private MinZai minZai= null;

    @PostConstruct
    public void init() {
        minZai = new MinZai();
    }



}
