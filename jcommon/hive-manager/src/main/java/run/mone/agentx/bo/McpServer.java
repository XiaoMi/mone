package run.mone.agentx.bo;

import lombok.Data;

@Data
public class McpServer {
    private String type;
    private Env env;

    @Data
    public static class Env {
        private String host;
        private String port;
        private String clientId;
        private String token;
    }
}
