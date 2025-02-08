package run.mone.m78.gateway.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KBSRequestBO implements Serializable {

    private String query;
    private String region;
    private String requestId;
    private String userId;
    private List<RecordBO> history;
}
