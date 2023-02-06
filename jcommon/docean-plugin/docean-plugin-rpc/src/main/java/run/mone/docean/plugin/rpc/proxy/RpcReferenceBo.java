package run.mone.docean.plugin.rpc.proxy;

import com.xiaomi.data.push.uds.codes.CodeType;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/1/8 16:04
 */
@Data
@Builder
public class RpcReferenceBo {

    private String app;

    private String name;

    @Builder.Default
    private long timeout = 1000L;

    private String serviceName;

    private String sidecarServiceName;

    private String methodName;

    private String sidecarMethodName;

    @Builder.Default
    private byte serializeType = CodeType.HESSIAN;

    private String group;

    private String version;

    @Builder.Default
    private Map<String,String> meta = new HashMap<>();

}
