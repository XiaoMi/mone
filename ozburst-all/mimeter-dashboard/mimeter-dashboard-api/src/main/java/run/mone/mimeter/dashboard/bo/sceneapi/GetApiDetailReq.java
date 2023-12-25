package run.mone.mimeter.dashboard.bo.sceneapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetApiDetailReq implements Serializable {
    @HttpApiDocClassDefine(value = "projectID", required = true, description = "mi-api中的项目id", defaultValue = "432")
    private Integer projectID;

    @HttpApiDocClassDefine(value = "apiID", required = true, description = "mi-api中的接口id", defaultValue = "666")
    private Integer apiID;

    /**
     * 1:http 3:dubbo 4:mione gateway
     */
    @HttpApiDocClassDefine(value = "apiProtocol", required = true, description = "该接口的协议类型 1:http 3:dubbo 4:mione gateway", defaultValue = "1")
    private Integer apiProtocol;
}
