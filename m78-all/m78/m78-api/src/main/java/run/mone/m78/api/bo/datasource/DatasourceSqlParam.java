package run.mone.m78.api.bo.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dp
 * @date 2024/2/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatasourceSqlParam {

    private Integer connectionId;

    private String comment;

    private String customKnowledge;

    private Integer lowerBound;

    private Integer upperBound;

    private List<String> tableNames;
}
