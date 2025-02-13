package run.mone.mcp.feishu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author caobaoyu
 * @description:
 * @date 2025-02-13 15:02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Files{

    private String name;

    private String url;

    private String type;

    private String token;
}
