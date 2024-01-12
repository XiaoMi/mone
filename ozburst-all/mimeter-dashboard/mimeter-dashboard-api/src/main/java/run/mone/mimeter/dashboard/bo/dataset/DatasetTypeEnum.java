package run.mone.mimeter.dashboard.bo.dataset;

import java.util.Arrays;
import java.util.List;

public enum DatasetTypeEnum {

    FileUpload(1, "FileUpload"),
    TrafficRecord(2, "TrafficRecord"),
    GlobalHeader(3, "GlobalHeader"),
    DataOutput(4, "DataOutput"),
    Interface(5, "Interface");



    public int typeCode;
    public String typeName;

    DatasetTypeEnum(int typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

    public static List<Integer> getDatasetTypes() {
        return Arrays.asList(
                FileUpload.typeCode,
                TrafficRecord.typeCode,
                GlobalHeader.typeCode,
                DataOutput.typeCode,
                Interface.typeCode
        );
    }
}
