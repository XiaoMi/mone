package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.util.List;

@Data
public class InvitePartnerBo {
    Integer inviterUserID;
    List<Integer> userIds;
    Integer projectID;
    Integer groupID;
    Integer roleType;
}
