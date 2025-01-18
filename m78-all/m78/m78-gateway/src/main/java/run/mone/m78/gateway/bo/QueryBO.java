package run.mone.m78.gateway.bo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QueryBO {

    private String action;
    private String requestId;
    private String sessionId;
    private String messageId;
    private String group;
    private String query;
    private String from;
    private List<RecordBO> history;

}
