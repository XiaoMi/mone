package run.mone.m78.ip.dialog;

import com.intellij.openapi.module.Module;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/15 16:01
 */
@Data
@Builder
public class DialogReq implements Serializable {

    private String cmd;

    private Module module;

    private String name;

    private String type;

    @Builder.Default
    private boolean createMethod = true;

}
