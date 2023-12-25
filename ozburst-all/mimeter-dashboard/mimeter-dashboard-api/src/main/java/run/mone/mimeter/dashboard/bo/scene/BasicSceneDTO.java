package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class BasicSceneDTO implements Serializable {
    @HttpApiDocClassDefine(value = "id", required = true, description = "场景id", defaultValue = "1")
    private Integer id;

    @HttpApiDocClassDefine(value = "name", required = true, description = "场景名", defaultValue = "测试场景1")
    private String name;

    @HttpApiDocClassDefine(value = "remark", description = "备注", defaultValue = "这是一个http链路场景")
    private String remark;

    @HttpApiDocClassDefine(value = "status", description = "场景状态 0 待启动 1 执行中", defaultValue = "1")
    private Integer status;

    @HttpApiDocClassDefine(value = "benchTime", required = true, description = "施压时间（s）", defaultValue = "60")
    private Integer benchTime;

    @HttpApiDocClassDefine(value = "sceneType", required = true, description = "场景类型 0:http 1:dubbo", defaultValue = "1")
    private Integer sceneType;

    @HttpApiDocClassDefine(value = "curReportId", required = true, description = "当前报告id", defaultValue = "1jhlq3r1")
    private String curReportId;

    @HttpApiDocClassDefine(value = "creator", required = true, description = "创建人", defaultValue = "dongzhenxing")
    private String creator;

    @HttpApiDocClassDefine(value = "rpsRate", required = true, description = "rps比例", defaultValue = "10")
    private Integer rpsRate;

    @HttpApiDocClassDefine(value = "uTime", required = true, description = "更新时间", defaultValue = "1124141241241")
    private Long uTime;

    @HttpApiDocClassDefine(value = "cTime", required = true, description = "创建时间", defaultValue = "1124141241241")
    private Long cTime;

}
