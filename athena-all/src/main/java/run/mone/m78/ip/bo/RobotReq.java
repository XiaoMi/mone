package run.mone.m78.ip.bo;

import com.intellij.openapi.project.Project;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/4/28 14:23
 */
@Data
@Builder
public class RobotReq implements Serializable {

    private String param;

    private Project project;

}
