package run.mone.mcp.playwright.role;

import run.mone.hive.Environment;
import run.mone.hive.roles.Role;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 16:19
 */
public class RoleClassifier extends Role {

    public RoleClassifier() {
        this.name = "RoleClassifier";
        setEnvironment(new Environment());
    }
}
