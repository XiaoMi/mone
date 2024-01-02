package run.mone.mimeter.dashboard.bo.dataset;

import lombok.Data;

import java.io.Serializable;

@Data
public class DatasetLineNum implements Serializable {
    Integer datasetId;
    String fileName;
    String fileUrl;
    String fileKsKey;
    Long fileRaw;
    String defaultParamName;
    Boolean ignoreFirstLine;
}
