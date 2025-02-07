package run.mone.m78.api.bo.multiModal.image;

import lombok.Data;
import run.mone.m78.api.enums.MultiModalCmdTypeEnum;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/7/30
 */
@Data
public class BaseReq implements Serializable {

    private String model;

    private String userName;

    /**
     * @see MultiModalCmdTypeEnum
     */
    private Integer cmdType;

    private String callbackUrl;

    private boolean generateInnerImageUrl;

    private boolean asyncCall = true;

}
