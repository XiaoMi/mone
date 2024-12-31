package run.mone.mimeter.dashboard.bo.openapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;
import run.mone.mimeter.dashboard.bo.scene.SerialLinkDTO;

import java.io.Serializable;
import java.util.List;

@Data
public class OpenSceneDTO implements Serializable {

    @HttpApiDocClassDefine(value = "name", required = true, description = "场景名", defaultValue = "测试场景1")
    private String name;

    @HttpApiDocClassDefine(value = "remark", description = "备注", defaultValue = "这是一个http链路场景")
    private String remark;

    @HttpApiDocClassDefine(value = "sceneType", description = "场景类型 0:http 1:dubbo", defaultValue = "1")
    private Integer sceneType;

    @HttpApiDocClassDefine(value = "serialLinkDTOs", description = "串联链路信息列表")
    private List<SerialLinkDTO> serialLinkDTOs;

    @HttpApiDocClassDefine(value = "benchMode", required = true, description = "压力模式 0 RPS （目前仅支持该模式）", defaultValue = "0")
    private Integer benchMode;

    @HttpApiDocClassDefine(value = "incrementMode", required = true, description = "递增模式 0 手动（Rps下仅支持手动模式）", defaultValue = "0")
    private Integer incrementMode;

}
