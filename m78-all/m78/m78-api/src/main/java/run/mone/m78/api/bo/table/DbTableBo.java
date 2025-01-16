package run.mone.m78.api.bo.table;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-15 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DbTableBo {

    @HttpApiDocClassDefine(value = "id", description = "id")
    private Long id;

    @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id")
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "tableName", description = "表名")
    private String tableName;

    @HttpApiDocClassDefine(value = "creator", description = "创建人")
    private String creator;

    @HttpApiDocClassDefine(value = "gmtCreate", description = "创建时间")
    private LocalDateTime createTime;
}
