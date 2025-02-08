package run.mone.m78.api.bo.code.editor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScriptReq {
    private Long scriptId;
    private String content;
    private String param;
}
