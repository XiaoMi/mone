package run.mone.docean.plugin.rpc.interceptor;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/10/20 10:13
 */
@Data
public class Context implements Serializable {

    private Map<String, Object> data = Maps.newHashMap();
}
