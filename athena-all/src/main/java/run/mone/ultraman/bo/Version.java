package run.mone.ultraman.bo;

import com.xiaomi.youpin.tesla.ip.common.VersionUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/7 14:06
 */
@Data
public class Version implements Serializable {

    private String ATHENA_TYPE = "IDEA";

    @Override
    public String toString() {
        return ATHENA_TYPE + ":" + getVersion();
    }

    public String getVersion() {
        return VersionUtil.getAthenaPluginVersion();
    }

    public String getName() {
        return ATHENA_TYPE;
    }

}
