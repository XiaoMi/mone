package run.mone.ultraman.visitor;

import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;
import com.xiaomi.youpin.tesla.ip.util.ImportUtils;
import com.xiaomi.youpin.tesla.ip.util.PsiClassFixer;
import com.xiaomi.youpin.tesla.ip.util.PsiClassUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/4/21 21:19
 */
public class M78Visitor extends JavaElementVisitor {

    private String packageStr = "";

    private String className = "";

    private PsiJavaFile javaFile;

    private List<String> missedImports; // HINT: 如果想要添加其他的import，须在配置访问者前设置该map

    private List<String> wildCardImportPackages; // HINT: xxx.* 导入


    @Override
    public void visitJavaFile(@NotNull PsiJavaFile file) {
        this.javaFile = file;
        this.packageStr = file.getPackageName();

        @Nullable PsiImportList importList = file.getImportList();
        if (null != importList) {
            this.visitImportList(importList);
        }

        PsiClass[] classes = file.getClasses();
        Arrays.stream(classes).forEach(it -> {
            this.visitClass(it);
        });

    }

    @Override
    public void visitPackageStatement(@NotNull PsiPackageStatement statement) {
        String text = statement.getPackageName();
        System.out.println("packcage:" + text);
        this.packageStr = text;
    }

    //如果缺少相应的字段,则直接添加这些字段
    @Override
    public void visitClass(@NotNull PsiClass psiClass) {
        String className = psiClass.getQualifiedName();
        System.out.println("class:" + className);
        this.className = className;

        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());

        Map<String, PsiAnnotation> annotationMap = Arrays.stream(psiClass.getAnnotations()).collect(Collectors.toMap(PsiAnnotation::getQualifiedName, Function.identity()));
        if (!annotationMap.containsKey("lombok.Data") && !annotationMap.containsKey("Data")) {
            WriteCommandAction.runWriteCommandAction(this.javaFile.getProject(), () -> {
                psiClass.getModifierList().addAnnotation("Data");
            });
        }


        //psiClass.ggetFields 转换成map key = name
        Map<String, PsiField> fieldMap = Arrays.stream(psiClass.getFields())
                .collect(Collectors.toMap(PsiField::getName, Function.identity()));

        System.out.println(fieldMap);
        if (!fieldMap.containsKey("id1")) {
            ApplicationManager.getApplication().invokeLater(() -> {

                PsiField idField = elementFactory.createField("id1", PsiTypes.intType());
                // 将注解添加到字段上
                idField.getModifierList().addAnnotation("Id");
                PsiClassFixer.addFields(psiClass, Lists.newArrayList(idField));
            });
        }
    }

    @Override
    public void visitMethod(PsiMethod method) {
        String methodName = method.getName();
        System.out.println("methodName:" + methodName);
    }


    //处理有没导入的类
    @Override
    public void visitImportList(@NotNull PsiImportList list) {
        Map<String, PsiImportStatement> map = Arrays.stream(list.getImportStatements()).collect(Collectors.toMap(PsiImportStatement::getQualifiedName, Function.identity()));
        if (!map.containsKey("lombok.Data")) {
            //添加进来缺失的lombok.Data
            ApplicationManager.getApplication().invokeLater(() -> {
                WriteCommandAction.runWriteCommandAction(this.javaFile.getProject(), () -> {
                    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(list.getProject());
                    PsiClass psiClass = ImportUtils.createPsiClass(elementFactory, "lombok.Data");
                    PsiImportStatement importStatement = elementFactory.createImportStatement(psiClass);
                    javaFile.getImportList().add(importStatement);
                    if (CollectionUtils.isNotEmpty(missedImports)) {
                        // 添加其他确实的import
                        for (String missingClass : missedImports) {
                            PsiClass other = ImportUtils.createPsiClass(elementFactory, missingClass);
                            PsiImportStatement otherImport = elementFactory.createImportStatement(other);
                            javaFile.getImportList().add(otherImport);
                        }

                    }
                    if (CollectionUtils.isNotEmpty(wildCardImportPackages)) {
                        // 添加 wildcard import
                        for (String packageName : wildCardImportPackages) {
                            PsiImportStatement wildCardImport = elementFactory.createImportStatementOnDemand(packageName);
                            javaFile.getImportList().add(wildCardImport);
                        }
                    }
                });
            });
        }

    }


    //添加注解
    @Override
    public void visitField(@NotNull PsiField field) {
        String name = field.getName();
        if (name.equals("id")) {
            @Nullable PsiAnnotation anno = field.getAnnotation("dev.morphia.annotations.Id");
            System.out.println("field:" + anno);
        }
    }

    public void setMissedImports(List<String> missedImports) {
        this.missedImports = missedImports;
    }

    public void setWildCardImportPackages(List<String> wildCardImportPackages) {
        this.wildCardImportPackages = wildCardImportPackages;
    }
}
