package run.mone.mcp.custommodel.model;

import lombok.Data;
import java.util.List;

@Data
public class PredictRequest {
    private String model_type;
    private String version;
    private List<String> texts;
    private Integer top_k;
} 