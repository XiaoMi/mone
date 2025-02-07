package run.mone.m78.service.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/1/19 13:48
 */
@Data
@Builder
public class M78ColumnDef implements Serializable {

    private String columnName;

    private String columnType;

    private String columnComment;


}
