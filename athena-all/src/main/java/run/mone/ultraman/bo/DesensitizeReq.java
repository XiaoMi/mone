package run.mone.ultraman.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/2/4 16:22
 */
@Data
@Builder
public class DesensitizeReq implements Serializable {

    private String text;

    private String langType;

    private boolean aiDesensitizeFlag;

    private String zzToken;


}
