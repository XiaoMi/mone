package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    private String username;
    /**
     * 10 => Guest access
     * 20 => Reporter access
     * 30 => Developer access
     * 40 => Maintainer access
     * 50 => Owner access # Only valid for groups
     */
    private int access_level;
}
