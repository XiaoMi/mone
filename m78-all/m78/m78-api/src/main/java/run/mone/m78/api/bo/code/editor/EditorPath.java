package run.mone.m78.api.bo.code.editor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditorPath {
    private boolean dir;
    private String name;
    private List<EditorPath> dirChild;
}
