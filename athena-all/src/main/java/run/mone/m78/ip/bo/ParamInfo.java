package run.mone.m78.ip.bo;

import com.intellij.psi.PsiType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/14 09:24
 */
@Data
@Builder
public class ParamInfo implements Serializable {

    private String name;

    private String type;

    private PsiType psiType;

}
