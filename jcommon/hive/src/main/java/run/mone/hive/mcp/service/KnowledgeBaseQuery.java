package run.mone.hive.mcp.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KnowledgeBaseQuery {

    private boolean autoQuery;
    private String apiUrl;
    private String apiKey;
    private String knowledgeBaseId;

}