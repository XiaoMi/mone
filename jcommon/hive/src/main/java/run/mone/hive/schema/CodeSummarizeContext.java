package run.mone.hive.schema;

import lombok.Data;

import java.util.List;

@Data
public class CodeSummarizeContext {
    private String designFilename;
    private String taskFilename;
    private List<String> codesFilenames;
}