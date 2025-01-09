package run.mone.ai.mcp;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class ServerConfig implements Serializable {
    String command;
    List<String> args;
    String url;
    Map<String, String> env;
}
