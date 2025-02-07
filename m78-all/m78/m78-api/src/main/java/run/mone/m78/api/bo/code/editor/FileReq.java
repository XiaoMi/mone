package run.mone.m78.api.bo.code.editor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileReq {
    private String url;
    private String token;
    private String branch;
    private String path;
    private String content;
    private String commitMsg;
}
