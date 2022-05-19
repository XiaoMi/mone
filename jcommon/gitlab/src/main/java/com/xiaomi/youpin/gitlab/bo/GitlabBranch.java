package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

/**
 *
 * {
 *     "name": "master",
 *     "merged": false,
 *     "protected": true,
 *     "default": true,
 *     "developers_can_push": false,
 *     "developers_can_merge": false,
 *     "can_push": true,
 *     "commit": {
 *       "author_email": "john@example.com",
 *       "author_name": "John Smith",
 *       "authored_date": "2012-06-27T05:51:39-07:00",
 *       "committed_date": "2012-06-28T03:44:20-07:00",
 *       "committer_email": "john@example.com",
 *       "committer_name": "John Smith",
 *       "id": "7b5c3cc8be40ee161ae89a06bba6229da1032a0c",
 *       "short_id": "7b5c3cc",
 *       "title": "add projects API",
 *       "message": "add projects API",
 *       "parent_ids": [
 *         "4ad91d3c1144c406e50c7b33bae684bd6837faf8"
 *       ]
 *     }
 *   }
 * @author tsingfu
 */
@Data
public class GitlabBranch {
    private String name;
    private GitlabCommit commit;
    private String web_url;
}
