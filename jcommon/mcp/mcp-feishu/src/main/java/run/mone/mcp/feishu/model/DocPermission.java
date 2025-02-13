package run.mone.mcp.feishu.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DocPermission {
    private String documentId;
    private String userId;
    private String userType; // user, group, department
    private String perm; // view, edit, full_access
} 