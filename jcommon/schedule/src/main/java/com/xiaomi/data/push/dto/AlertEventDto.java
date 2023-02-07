package com.xiaomi.data.push.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AlertEventDto {
    private Integer source_id;
    private Integer level;
    private Integer type;
    private Target target;
    private Meta meta;

    @Data
    @ToString
    @Builder
    public static class Target {
        private Integer type;
        private String name;
    }

    @Data
    @ToString
    @Builder
    public static class Meta {
        private Integer errorRetryNum;
        private Integer id;
        private String taskResult;
        private String title;
        private String url;
        private String summary;
        private String timestamp;
    }
    public enum LevelEnum {
        P0(100, "P0"),
        P1(101, "P1"),
        P2(102, "P2");

        private int code;
        private String name;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        LevelEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static boolean check(Integer level){
            if(level == null){
                return false;
            }
            LevelEnum[] values = LevelEnum.values();
            for (LevelEnum levelEnum : values) {
                if(level == levelEnum.getCode()){
                    return true;
                }
            }
            return false;
        }
    }

}
