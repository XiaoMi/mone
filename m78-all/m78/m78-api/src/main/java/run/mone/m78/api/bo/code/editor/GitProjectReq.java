package run.mone.m78.api.bo.code.editor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitProjectReq {
    private String url;
    private String token;
    private String branch;
}
