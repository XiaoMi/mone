package com.xiaomi.miapi.bo;

import lombok.Data;

import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class InvitePartnerBo {
    Integer inviterUserID;
    List<Integer> userIds;
    Integer projectID;
    Integer groupID;
    Integer roleType;
}
