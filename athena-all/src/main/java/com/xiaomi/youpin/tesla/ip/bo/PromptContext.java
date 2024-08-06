package com.xiaomi.youpin.tesla.ip.bo;

import com.intellij.openapi.editor.Editor;
import lombok.Data;
import run.mone.ultraman.bo.AthenaFieldInfo;
import run.mone.ultraman.bo.AthenaMethodInfo;
import run.mone.ultraman.bo.PoClassInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/5/13 09:36
 */
@Data
public class PromptContext implements Serializable {

    private String project;

    private String module;

    private String className;

    private String methodName;

    private String scope;

    private String comment;

    private List<String> resourceCode;

    private List<PoClassInfo> poClassInfos;

    private List<AthenaMethodInfo> methodCodeList;

    private List<AthenaFieldInfo> fieldCodeList;

    private String clazzName;

    private List<String> resourceBeanList;

    private Editor editor;


}
