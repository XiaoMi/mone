package run.mone.hera.operator.bo;


import lombok.Data;

/**
 * @author shanwb
 */
@Data
public class HeraStatus {

    public static final int STATUS_FAILED = -1;
    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_SUCCESS = 1;

    /**
     * -1：失败
     *  0：默认
     *  1：成功
     */
    private int status;

    private String msg;
}
