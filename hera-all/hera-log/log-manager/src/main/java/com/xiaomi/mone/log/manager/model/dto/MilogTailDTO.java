package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MilogTailDTO {
    private Long id;
    private Long ctime;
    private Long utime;
    private Long spaceId;
    private Long storeId;
    private Long appId;
    private Long milogAppId;
    private String appName;
    private Long envId;
    private String envName;
    private List<String> ips;
    private String tail;
    private Integer parseType;
    private String parseScript;
    private String logPath;
    private String logSplitExpress;
    private String valueList;
    private String tailRate;
    /**
     * 服务 部署空间
     **/
    private String deploySpace;

    /**
     * 行首正则
     **/
    private String firstLineReg;

    /**
     *   china
     */
    private String source;
    /**
     * 应用类型 0:mione应用
     **/
    private Integer appType;
    /**
     * 机器类型 0.容器 1.物理机
     */
    private Integer machineType;
    /**
     *  应用所在的机房 节点信息
     */
    private List<MotorRoomDTO> motorRooms;

    private String topicName;
    private List<?> middlewareConfig;

    private Integer deployWay;

    private Integer projectSource;

    private Integer batchSendSize;
}
