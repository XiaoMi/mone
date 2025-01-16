package run.mone.m78.gateway.bo;

import lombok.Data;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.util.List;

@Data
public class RelationBO {

    private String source;

    private String docName;

    private List<PartBO> parts;
}
