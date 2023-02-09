/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.ip.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PsiElementAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);

        //获取当前事件触发时，光标所在的元素
        PsiElement psiElement = e.getData(LangDataKeys.PSI_ELEMENT);
        //如果光标选择的不是类，弹出对话框提醒
        if (psiElement == null || !(psiElement instanceof PsiClass)) {
            Messages.showMessageDialog(project, "Please focus on a class", "Generate Failed", null);
        } else {
            List<PsiClass> list = getClasses(psiElement);
            String msg = list.stream().map(it -> it.getName()).collect(Collectors.joining(","));
            Messages.showMessageDialog(project, msg, "title", Messages.getInformationIcon());
        }
    }


    public static List<PsiClass> getClasses(PsiElement element) {
        List<PsiClass> elements = new ArrayList<>();
        List<PsiClass> classElements = PsiTreeUtil.getChildrenOfTypeAsList(element, PsiClass.class);
        elements.addAll(classElements);
        for (PsiClass classElement : classElements) {
            //这里用了递归的方式获取内部类
            elements.addAll(getClasses(classElement));
        }
        return elements;
    }
}
