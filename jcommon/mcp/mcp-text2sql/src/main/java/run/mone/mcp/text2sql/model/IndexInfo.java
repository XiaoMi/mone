package run.mone.mcp.text2sql.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexInfo {
    String indexName;
    boolean isUnique;
    List<String> columns = new ArrayList<>();
}
