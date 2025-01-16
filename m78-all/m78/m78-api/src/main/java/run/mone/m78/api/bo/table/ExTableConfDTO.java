package run.mone.m78.api.bo.table;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 5/9/24 3:24 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ExTableConfDTO implements Serializable {

    private static final long serialVersionUID = 5173642561423292377L;

    @HttpApiDocClassDefine(value = "workspaceId", description = "工作空间id")
    @Nonnull
    private Long workspaceId;

    @HttpApiDocClassDefine(value = "connectionId", description = "连接信息id")
    @Nonnull
    private Long connectionId;

    @HttpApiDocClassDefine(value = "exTables", description = "外部表列表")
    @Nonnull
    private List<ExTable> exTables;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Builder
    public static class ExTable {

        @HttpApiDocClassDefine(value = "tableName", description = "外部表表名")
        private String tableName;

        @HttpApiDocClassDefine(value = "tableDesc", description = "外部表描述")
        private String tableDesc;

    }
}
