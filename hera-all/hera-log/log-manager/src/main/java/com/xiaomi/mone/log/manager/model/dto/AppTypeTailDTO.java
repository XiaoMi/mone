package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/26 19:43
 */
@Data
public class AppTypeTailDTO {

    private Integer appType;

    private String appTypName;

    private List<TailApp> tailAppList;

    @Data
    public static class TailApp {

        private String nameEn;

        private String nameCn;

        private List<TailInfo> tailInfos;
    }

    @Data
    public static class TailInfo {
        private Long id;
        private String tailName;
    }
}
