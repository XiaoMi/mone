package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.m78.api.bo.table.M78ColumnInfo;

/**
 *  实体类。
 *
 * @author hoho
 * @since 2024-03-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_bot_db_table")
public class M78BotDbTable implements Serializable {

    private static final long serialVersionUID = 4502537140249020535L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long workspaceId;

    private Long botId;

    private String tableName;

    private String demo;

    @Column("table_desc")
    private String tableDesc;

    private String creator;

    private LocalDateTime createTime;

    @Column(value = "column_info", typeHandler = Fastjson2TypeHandler.class)
    private List<M78ColumnInfo> columnInfoList;

    private Integer type;

    private Long connectionId;
}
