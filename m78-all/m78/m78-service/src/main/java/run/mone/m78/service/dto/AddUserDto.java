package run.mone.m78.service.dto;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-06 20:50
 */
@Data
public class AddUserDto implements Serializable {

    @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id", required = true)
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "usernameList", description = "用户列表", required = true)
    private List<String> username;

    private int role;

}
