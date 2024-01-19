package run.mone.m78.ip.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import run.mone.m78.ip.bo.ClassInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/4/25 18:01
 */
public class AnnoUtils {


    /**
     * Multiple names can be passed at once, separated by commas.
     *
     * @param project
     * @param name
     * @return
     */
    public static List<ClassInfo> findClassWithAnno(Project project, String name, String moduleName) {
        List<ClassInfo> result = new ArrayList<>();
        // 获取 JavaPsiFacade 实例
        JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        GlobalSearchScope scope = StringUtils.isNotEmpty(moduleName) ? GlobalSearchScope.moduleScope(ProjectUtils.getModuleWithName(project, moduleName)) : GlobalSearchScope.projectScope(project);
        // Find the specified annotation using JavaPsiFacade.

        String[] names = new String[]{name};
        if (name.contains(",")) {
            names = name.split(",");
        }
        @NotNull GlobalSearchScope annoScope = GlobalSearchScope.allScope(project);
        Arrays.stream(names).forEach(it -> {
            PsiClass annotationClass = javaPsiFacade.findClass(it, annoScope);
            if (annotationClass != null && annotationClass.isAnnotationType()) {
                AnnotatedElementsSearch.searchPsiClasses(annotationClass, scope).forEach(psiClass -> {
                    Module module = PsiClassUtils.getModule(project, psiClass);
                    ClassInfo classInfo = ClassInfo.builder().className(psiClass.getQualifiedName()).moduleName(module.getName()).build();
                    result.add(classInfo);
                });
            }
        });
        return result;
    }


}
