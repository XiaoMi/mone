package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;
import run.mone.mimeter.dashboard.bo.agent.AgentDTO;
import run.mone.mimeter.dashboard.bo.sla.SlaDto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SceneDTO implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = false, description = "场景id", defaultValue = "1")
    private Integer id;

    @HttpApiDocClassDefine(value = "name", required = true, description = "场景名", defaultValue = "测试场景1")
    private String name;

    @HttpApiDocClassDefine(value = "groupID", required = true, description = "所属分组id", defaultValue = "1")
    private Integer groupID;

    @HttpApiDocClassDefine(value = "creator", description = "创建人", defaultValue = "dzx")
    private String creator;

    @HttpApiDocClassDefine(value = "更新人", description = "更新人", defaultValue = "dzx")
    private String updater;

    @HttpApiDocClassDefine(value = "uTime", required = true, description = "更新时间", defaultValue = "1124141241241")
    private Long uTime;

    @HttpApiDocClassDefine(value = "cTime", required = true, description = "创建时间", defaultValue = "1124141241241")
    private Long cTime;

    @HttpApiDocClassDefine(value = "benchCount", required = true, description = "压测次数", defaultValue = "12")
    private Integer benchCount;

    @HttpApiDocClassDefine(value = "lastBenchTime", required = true, description = "最近压测时间", defaultValue = "1234234422")
    private Long lastBenchTime;

    @HttpApiDocClassDefine(value = "apiNum", required = true, description = "有效接口数", defaultValue = "12")
    private Integer apiNum;

    @HttpApiDocClassDefine(value = "remark", description = "备注", defaultValue = "这是一个http链路场景")
    private String remark;

    @HttpApiDocClassDefine(value = "sceneType", description = "场景类型 0:http 1:dubbo", defaultValue = "1")
    private Integer sceneType;

    @HttpApiDocClassDefine(value = "serialLinkDTOs", description = "串联链路信息列表")
    private List<SerialLinkDTO> serialLinkDTOs;

    @HttpApiDocClassDefine(value = "benchMode", required = true, description = "压力模式 0 RPS （目前仅支持该模式）", defaultValue = "0")
    private Integer benchMode;

    @HttpApiDocClassDefine(value = "incrementMode", required = true, description = "rps模式下，0 固定rps、1 手动控制rps、2 百分比递增", defaultValue = "0")
    private Integer incrementMode;

    @HttpApiDocClassDefine(value = "increasePercent", description = "rps百分比递增模式下，可选 5、10、20、25、50，默认10", defaultValue = "10")
    private Integer increasePercent;

    @HttpApiDocClassDefine(value = "benchTime", required = true, description = "施压时间（s）", defaultValue = "60")
    private Integer benchTime;

    @HttpApiDocClassDefine(value = "maxBenchQps", required = true, description = "最大施压qps（s）", defaultValue = "60")
    private Integer maxBenchQps;

    @HttpApiDocClassDefine(value = "rpsRate", required = true, description = "施压比例，百分比", defaultValue = "60")
    private Integer rpsRate;

    @HttpApiDocClassDefine(value = "apiBenchInfos", description = "场景各接口施压配置，最大、起始RPS，json格式", defaultValue = "")
    private List<ApiBenchInfo> apiBenchInfos;

    @HttpApiDocClassDefine(value = "slaBo", description = "SLA配置", defaultValue = "")
    private SlaDto slaDto;

    @HttpApiDocClassDefine(value = "datasetIds", description = "数据集id列表", defaultValue = "")
    private List<Integer> datasetIds;

    @HttpApiDocClassDefine(value = "refDatasetIds", description = "引用的数据集id列表", defaultValue = "")
    private Map<String,Integer> refDatasetIds;

    @HttpApiDocClassDefine(value = "logRate", description = "日志采样率 百分比", defaultValue = "50")
    private Integer logRate;

    @HttpApiDocClassDefine(value = "requestTimeout", description = "通用请求超时时间  ms", defaultValue = "500")
    private Integer requestTimeout;

    @HttpApiDocClassDefine(value = "successCode", description = "请求成功状态码(默认200) ", defaultValue = "400,300")
    private String successCode;

    @HttpApiDocClassDefine(value = "globalTspAuth", description = "全局汽车部tsp验权信息", defaultValue = "")
    private TspAuthInfo globalTspAuth;

    @HttpApiDocClassDefine(value = "globalHeaderList", required = true, description = "全局header", defaultValue = "全局请求头")
    private List<GlobalHeader> globalHeaderList;

    @HttpApiDocClassDefine(value = "agentIdList", required = true, description = "绑定的压测机id列表", defaultValue = "")
    private List<Integer> agentIdList;

    @HttpApiDocClassDefine(value = "agentDTOList", required = true, description = "绑定的压测机信息列表", defaultValue = "")
    private List<AgentDTO> agentDTOList;

    @HttpApiDocClassDefine(value = "sceneEnv", description = "场景环境 ", defaultValue = "500")
    private Integer sceneEnv;

    @HttpApiDocClassDefine(value = "tenant",ignore = true, description = "租户", defaultValue = "")
    private String tenant;

    @HttpApiDocClassDefine(value = "sceneSource",description = "场景来源", defaultValue = "0：控制台创建 1:openapi创建")
    private Integer sceneSource;

    @HttpApiDocClassDefine(value = "useSla", description = "是否使用sla", defaultValue = "0：否，1：是")
    private Integer useSla;

    @HttpApiDocClassDefine(value = "personsInCharge", description = "负责人列表", defaultValue = "")
    private List<String> personsInCharge;

    @HttpApiDocClassDefine(value = "benchCalendar", description = "压测日历", defaultValue = "")
    private BenchCalendar benchCalendar;
}
