package com.xiaomi.mone.log.manager.model.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * @Auther: wtt
 * @Date: 2022/3/24 10:36
 * @Description:
 */
@Data
public class RadarAppInfoDTO {
    private Long id;
    private String name;
    private List<Member> members;
    private String createTime;
    private String updateTime;
    private boolean joined;


    @Data
    public static class Member {
        @SerializedName(value = "user_id")
        private String userId;
        @SerializedName(value = "user_name")
        private String userName;
    }
}
