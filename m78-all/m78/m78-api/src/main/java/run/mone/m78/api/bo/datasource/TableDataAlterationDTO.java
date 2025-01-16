package run.mone.m78.api.bo.datasource;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class TableDataAlterationDTO implements Serializable {

    private Integer datasourceId;

    private String  tableName;

    private String operationType;

    private Map<String, String> updateData;

    private Map<String, String> newData;

    private int id;

}
