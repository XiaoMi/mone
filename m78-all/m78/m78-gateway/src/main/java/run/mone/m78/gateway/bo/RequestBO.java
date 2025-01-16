package run.mone.m78.gateway.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class RequestBO implements Serializable {

    private String userId;
    private String userName;
    private String type;
    private String query;
    private String requestId;
    private List<RecordBO> history;
}
