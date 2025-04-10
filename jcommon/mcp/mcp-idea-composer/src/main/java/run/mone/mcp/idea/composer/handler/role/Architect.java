package run.mone.mcp.idea.composer.handler.role;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.roles.Role;


/**
 * @author goodjava@qq.com
 * 架构师 (分析项目)
 */
@Slf4j
public class Architect extends Role {

    public Architect() {
        super("Architect","优秀的软件架构师,了解项目架构的");
    }

}