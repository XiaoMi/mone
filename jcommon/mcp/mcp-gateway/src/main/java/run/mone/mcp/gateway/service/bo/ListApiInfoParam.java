package run.mone.mcp.gateway.service.bo;

import lombok.Data;

import java.util.List;

@Data
public class ListApiInfoParam {

    private List<String> applications;

    private int pageNo;

    private int pageSize;

    private String name;

    private String url;

    private String path;

}
