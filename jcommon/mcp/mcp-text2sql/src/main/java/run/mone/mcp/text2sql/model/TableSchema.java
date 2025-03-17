package run.mone.mcp.text2sql.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableSchema {
    String tableName;
    String tableComment;
    List<ColumnInfo> columns = new ArrayList<>();
    List<String> primaryKeys = new ArrayList<>();
    List<ForeignKey> foreignKeys = new ArrayList<>();
    List<IndexInfo> indexes = new ArrayList<>();
}
