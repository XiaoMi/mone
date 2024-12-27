package com.xiaomi.youpin.tesla.ip.bo;

import com.intellij.psi.PsiType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/14 15:08
 */
@Data
@Builder
public class AddMethodConfig implements Serializable {

    private String name;

    private PsiType returnType;

    private boolean isInterface;


}
