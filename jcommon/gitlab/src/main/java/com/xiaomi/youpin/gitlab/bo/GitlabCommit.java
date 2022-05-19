package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoyibo
 */
@Data
public class GitlabCommit implements Serializable {
    private String id;
    private String short_id;
    private String title;
    private String author_name;
    private String author_email;
    private String authored_date;
    private String committer_name;
    private String committer_email;
    private String committed_date;
    private String created_at;
    private String message;
    private String[] parent_ids;
}
