package com.xiaomi.youpin.tesla.ip.bo.prompt;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.common.Const;
import com.xiaomi.youpin.tesla.ip.util.LabelUtils;
import com.xiaomi.youpin.tesla.ip.util.PackageUtils;
import com.xiaomi.youpin.tesla.ip.util.ProjectUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/7/16 00:22
 */
@Data
public class PromptParam implements Serializable {

    private String type;

    private String subType;

    private String name;

    private String value;

    private List<String> list;

    public void init(String key, Project project, PromptInfo promptInfo) {
        if (key.equals("reqClass")) {
            String reqPackage = LabelUtils.getLabelValue(project, promptInfo, Const.REQ_PACKAGE, Const.DEFAULT_REQ_PACKAGE);
            List<String> list = PackageUtils.getClassList(project, reqPackage);
            list.add("Select");
            setList(list);
            setType("comboBox");
            setSubType("class");
        }
        if (key.equals("module")) {
            List<String> moduleList = ProjectUtils.listAllModules(project);
            setList(moduleList);
            setType("comboBox");
        }
        if (key.equals("package")) {
            String selectType = LabelUtils.open(Const.TREE_SELECT) ? "Select" : "Select2";
            List<String> list = Lists.newArrayList(selectType);
            setList(list);
            setType("comboBox");
        }
    }

    @Override
    public String toString() {
        return this.value;
    }
}
