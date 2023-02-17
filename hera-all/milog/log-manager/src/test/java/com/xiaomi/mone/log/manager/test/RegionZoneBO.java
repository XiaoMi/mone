package com.xiaomi.mone.log.manager.test;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/22 17:37
 */
@Data
public class RegionZoneBO {
    private Integer code;
    private String message;
    private String userMessage;
    private String level;
    private List<InnerClass> data;

    @Data
    public static class InnerClass {
        private Integer id;
        private String zone_name_en;
        private String zone_name_cn;
        private String region_cn;
        private String region_en;
        private Boolean is_used;
    }
}
