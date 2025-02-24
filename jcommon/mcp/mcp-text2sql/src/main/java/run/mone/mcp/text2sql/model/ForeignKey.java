package run.mone.mcp.text2sql.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForeignKey {
    String constraintName;
    String columnName;
    String foreignTable;
    String foreignColumn;
}
