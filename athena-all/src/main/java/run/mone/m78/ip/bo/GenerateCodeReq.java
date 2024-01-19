package run.mone.m78.ip.bo;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import run.mone.m78.ip.common.PromptType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author caobaoyu
 * @author zhangzhiyong
 * @description:
 * @date 2023-05-17 16:53
 *
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

    private Map<String, String> param;

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

    private String qualifiedName;

    private String className;

    private String classPackage;


    private String classCode2;

    //选中的方法code(鼠标停留的位置)
    private String methodCode;

    //选中的内容
    private String selectText;

    //操作系统名称
    private String systemName;

    private String ideaVersion;

    private String pluginVersion;

    @Builder.Default
    private String taskType = "edit";


}
