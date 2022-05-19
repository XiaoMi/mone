package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

/**
 * 群详情
 */
@Data
public class GroupDetail {
    /**
     * 群头像
     */
    private String avatar;

    /**
     * 群描述
     */
    private String description;

    /**
     * 群ID
     */
    private String chat_id;

    /**
     * 群名称
     */
    private String name;

    /**
     * 群主的 open_id
     */
    private String owner_open_id;

    /**
     * 群主的 user_id（机器人是群主的时候没有这个字段）
     */
    private String owner_user_id;

}
