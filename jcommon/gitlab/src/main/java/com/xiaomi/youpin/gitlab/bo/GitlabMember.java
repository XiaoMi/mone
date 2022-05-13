package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

/**
 * @author tsingfu
 */
@Data
public class GitlabMember {
    private int id;
    private String name;
    private String username;
    private String state;
    private int access_level;
}
