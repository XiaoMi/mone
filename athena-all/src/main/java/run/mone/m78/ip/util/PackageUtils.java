package run.mone.m78.ip.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/6/15 13:52
 */
public class PackageUtils {


    public static List<String> getClassList(Project project, String packageName) {
        PsiPackage psiPackage = JavaPsiFacade.getInstance(project).findPackage(packageName);
        List<String> result = new ArrayList<>();
        if (psiPackage != null) {
            PsiClass[] psiClasses = psiPackage.getClasses();
            for (PsiClass psiClass : psiClasses) {
                String className = psiClass.getQualifiedName();
                result.add(className);
            }
        }
        return result;
    }

    public static PsiPackage findPackageByName(Project project, String packageName) {
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        PsiPackage psiPackage = javaPsiFacade.findPackage(packageName);
        return psiPackage;
    }

}
