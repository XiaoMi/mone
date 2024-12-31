package com.xiaomi.youpin.tesla.ip.bo;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/4/28 14:24
 */
@Data
public class RobotContext implements Serializable {

    private Project project;

    private Module module;

}
