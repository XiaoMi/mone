package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

@Data
public class GitlabProject {
    private int id;
    private String description;
    private String web_url;
}
