package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

@Data
public class GitlabMerge {
    private String id;
    private String iid;
    private String state;
    private String merge_error;
    private String web_url;
}
