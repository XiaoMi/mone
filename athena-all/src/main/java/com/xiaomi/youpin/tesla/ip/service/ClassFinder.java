package com.xiaomi.youpin.tesla.ip.service;

import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.tesla.ip.bo.ClassInfo;
import com.xiaomi.youpin.tesla.ip.util.AnnoUtils;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/5/23 13:36
 */
public class ClassFinder {


    public static List<ClassInfo> findClassList(Project project, String type, String name, String moduleName) {
        if (type.equals("anno")) {
            return AnnoUtils.findClassWithAnno(project, name, moduleName);
        }
        if (type.equals("endWith")) {
            return CodeService.getClassList(project, name, moduleName);
        }
        return Lists.newArrayList();
    }


}
