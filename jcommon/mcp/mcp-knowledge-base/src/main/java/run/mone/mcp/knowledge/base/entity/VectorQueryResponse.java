package run.mone.mcp.knowledge.base.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VectorQueryResponse implements Serializable {

    private String id;

    private String content;

    private List<Float> vector;

    private Double similarity;

    private String fileId;

}