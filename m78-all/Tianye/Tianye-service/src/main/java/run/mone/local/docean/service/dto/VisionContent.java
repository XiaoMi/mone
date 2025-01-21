package run.mone.local.docean.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisionContent implements Serializable {

    private String type;

    private String text;

    private Map<String, String> source;
}
