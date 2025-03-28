package run.mone.mcp.text2sql.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnInfo {
    String columnName;
    String dataType;
    int columnSize;
    boolean isNullable;
    String columnComment;
}
