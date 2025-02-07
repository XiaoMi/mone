package run.mone.m78.api.bo.multiModal.image;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/8/1
 */
@Data
public class WorkChartReq extends BaseReq implements Serializable {

    /**
     * @see run.mone.m78.api.enums.WorkChartTypeEnum
     */
    private String chartType;

    private String input;

}
