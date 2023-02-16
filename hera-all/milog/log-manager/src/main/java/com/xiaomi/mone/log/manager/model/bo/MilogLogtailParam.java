package com.xiaomi.mone.log.manager.model.bo;

import com.xiaomi.mone.log.manager.model.dto.MotorRoomDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilogLogtailParam {
    private Long id;
    private Long spaceId;
    private Long storeId;
    private Long milogAppId;
    private Long appId;
    private String appName;
    private Long envId;
    private String envName;
    private List<String> ips;
    private String tail;
    private Integer parseType;
    private String parseScript;
    private String logPath;
    private String valueList;
    private String tailRate;
    private Long ctime;
    private Long utime;
    private Long middlewareConfigId;

    private String logSplitExpress;
    /**
     * 应用类型 0:mione应用 1:mis 应用
     **/
    private Integer appType;
    /**
     * 机器类型 0.容器 1.物理机（mis应用）
     */
    private Integer machineType;
    /**
     * mis 应用所在的机房 节点信息
     */
    private List<MotorRoomDTO> motorRooms;

    private String topicName;
    private List<?> middlewareConfig;
    /**
     * 部署方式 1-mione; 2-miline; 3-k8s
     */
    private Integer deployWay;

    private Integer batchSendSize;
}
