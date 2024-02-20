package run.mone.m78.ip.util;

import com.google.common.base.Splitter;
import com.intellij.codeInsight.actions.OptimizeImportsAction;
import com.intellij.codeInspection.unusedImport.UnusedImportInspection;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/6/22 21:41
 */
@Slf4j
public abstract class ImportUtils {

    public static void removeInvalidImports(Project project, Editor editor) {
    }

    public static void optimizeImports(Project project, Editor editor) {

    }


    public static void addImport(Project project, Editor editor, List<String> importStrList) {

    }

    public static PsiClass createPsiClass(PsiElementFactory factory, String className) {
        return null;
    }


    public static String junitVersion(PsiClass psiClass) {
        return "";
    }


}
