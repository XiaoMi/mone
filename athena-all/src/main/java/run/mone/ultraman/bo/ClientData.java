package run.mone.ultraman.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/12/26 10:50
 */
@Data
@Builder
public class ClientData implements Serializable {

    private String projectName;

    private String module;

    private String scope;

}
