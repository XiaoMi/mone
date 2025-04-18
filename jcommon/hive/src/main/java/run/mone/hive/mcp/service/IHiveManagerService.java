package run.mone.hive.mcp.service;

import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfo;

/**
 * @author goodjava@qq.com
 * @date 2025/4/18 13:48
 */
public interface IHiveManagerService {

    void register(RegInfo regInfo);

    void unregister(RegInfo regInfo);

    void heartbeat(HealthInfo healthInfo);
}
