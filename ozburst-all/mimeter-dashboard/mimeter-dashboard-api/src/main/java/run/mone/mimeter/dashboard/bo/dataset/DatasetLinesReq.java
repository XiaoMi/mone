package run.mone.mimeter.dashboard.bo.dataset;

import lombok.Data;

import java.io.Serializable;

@Data
public class DatasetLinesReq implements Serializable {
    private Integer datasetId;
    private String fileUrl;
    private String fileKsKey;
    private String defaultParamName;
    //读对应文件的起始行数
    private Integer from;
    //读对应文件的最后一行
    private Integer to;
}
