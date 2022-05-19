package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

/**
 * @author wmin
 * @date 2022/2/22
 */
@Data
public class GitWebhook {

    //The ID or URL-encoded path of the project
    private String id;

    private String url;

    //The ID of the project hook
    private String hook_id;

    private String push_events;

    private String tag_push_events;

    private String merge_requests_events;

    //Trigger hook on push events for matching branches only.
    private String push_events_branch_filter;

    private String created_at;

    private String token;
}
