package run.mone.m78.api.bo.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * @author HawickMason@xiaomi.com
 * @date 3/27/24 20:16
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DbTableCmtReq implements Serializable {

    private static final long serialVersionUID = -1829537606644445021L;

    @Nonnull
    private Long workspaceId;

    @Nonnull
    private String comment;

    @Nonnull
    private String tableName;

    private String model;

    private Integer type;

    private Long connectionId;
}
