package run.mone.m78.service.dto;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-06 18:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserWorkSpaceDto implements Serializable {

    @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "username", description = "用户名")
    private String username;

    @HttpApiDocClassDefine(value = "role", description = "权限 0-成员 1-管理员")
    private Integer role;

    @HttpApiDocClassDefine(value = "roleDesc", description = "权限描述")
    private String roleDesc;

}
