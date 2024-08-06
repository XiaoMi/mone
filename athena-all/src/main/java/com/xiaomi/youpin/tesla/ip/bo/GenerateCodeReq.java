package com.xiaomi.youpin.tesla.ip.bo;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author caobaoyu
 * @author zhangzhiyong
 * @description:
 * @date 2023-05-17 16:53
 * <p>
 * 主要用来生成代码
 */
@Data
@Builder
public class GenerateCodeReq {

    private String projectName;

    private String model;

    private String promptName;

    private String fileName;

    private String meta;

    private Project project;

    private Editor editor;

    private Module module;

    private String moduleName;

    //所有module的列表
    private List<String> moduleNameList;

    private String type;

    private PromptType promptType;

    private PromptInfo promptInfo;

    private String showDialog;

    @Builder.Default
    private Map<String, String> param = new HashMap<>();

    @Builder.Default
    private Map<String, Object> objMap = new HashMap<>();

    /**
     * 格式化代码
     */
    @Builder.Default
    private boolean format = true;

    private String comment;

    private String chatComment;

    /**
     * method
     * class
     * module
     * project
     */
    private String scope;

    private String context;

    private CountDownLatch latch;

    //这个文件的内容
    private String virtualFileText;

    //class相关代码
    private String classCode;

    //class代码，且带着占位标记($$这里需要补全代码$$)
    private String classCodeWithMark;

    private String qualifiedName;

    private String className;

    private String classPackage;


    private String classCode2;

    //选中的方法code(鼠标停留的位置)
    private String methodCode;

    //带行号的method
    private String lineNumberedMethod;

    private PsiMethod psiMethod;

    //选中的内容
    private String selectText;

    //当前行内容
    private String currentLine;

    //操作系统名称
    private String systemName;

    private String ideaVersion;

    private String pluginVersion;

    @Builder.Default
    private String taskType = "edit";

    //编辑器中的偏移量
    private int offset;

    private List<String> inheritedMethods;

    //用户设定的scope
    private String userSettingScope;

    //debug的错误信息
    private String debugErrorMessage;

    //用来阻塞用的
    private CountDownLatch countDownLatch;


}
