package run.mone.mimeter.engine.filter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongzhenxing
 */
@Data
public class MimeterFilterInfo implements Serializable {
    private String filterName;
    private int order;

    /**
     * 0 前置
     * 1 后置
     */
    private int type;
}
