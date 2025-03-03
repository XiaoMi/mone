package run.mone.mcp.miapi.bo;

import lombok.Data;

@Data
public class ReqParamsBo {
    private Integer protocol;
    private String keyword;
    private String path;
}
