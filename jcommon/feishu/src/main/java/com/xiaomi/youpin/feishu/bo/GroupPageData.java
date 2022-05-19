package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

import java.util.List;

@Data
public class GroupPageData {
    /**
     * 还有群未读取完
     */
    private boolean has_more;

    /**
     * 分页标记，第一次请求不填，表示从头开始遍历；分页查询还有更多群时会同时返回新的 page_token, 下次遍历可采用该 page_token 获取更多群
     */
    private String page_token;

    List<GroupDetail> groups;

}
