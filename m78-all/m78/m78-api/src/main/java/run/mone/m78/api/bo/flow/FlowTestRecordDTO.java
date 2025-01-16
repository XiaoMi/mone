package run.mone.m78.api.bo.flow;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author liuchuankang
 * @Type FlowTestRecordDTO.java
 * @Desc
 * @date 2024/8/19 14:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowTestRecordDTO implements Serializable {
	@HttpApiDocClassDefine(value = "executeType", required = false, description = "workSpaceId", defaultValue = "0")
	private int executeType;
	@HttpApiDocClassDefine(value = "flowBaseId", required = false, description = "flowbaseId", defaultValue = "0")
	private Integer flowBaseId;
	@HttpApiDocClassDefine(value = "startTime", required = false, description = "开始时间", defaultValue = "0")
	private long startTime;
	@HttpApiDocClassDefine(value = "status", required = false, description = "状态", defaultValue = "0")
	private int status;
	@HttpApiDocClassDefine(value = "runner", required = false, description = "执行人", defaultValue = "0")
	private String runner;
	@HttpApiDocClassDefine(value = "duration", required = false, description = "时长", defaultValue = "0")
	private long duration;
	@HttpApiDocClassDefine(value = "endFlowOutput", required = false, description = "结果", defaultValue = "0")
	private SyncFlowStatus.EndFlowOutput endFlowOutput;
	@HttpApiDocClassDefine(value = "nodeInputsMap", required = false, description = "各节点入参", defaultValue = "0")
	private Map<String, SyncFlowStatus.SyncNodeInput> nodeInputsMap;
	@HttpApiDocClassDefine(value = "nodeOutputsMap", required = false, description = "各节点结果", defaultValue = "0")
	private Map<String, SyncFlowStatus.SyncNodeOutput> nodeOutputsMap;

}
