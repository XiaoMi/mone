package run.mone.processor.common;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import lombok.SneakyThrows;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2023/3/29 16:52
 */
public abstract class CodeUtils {

    @SneakyThrows
    public static String getClassName(String code) {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(code).getResult().get();
        PackageDeclaration a = cu.getPackageDeclaration().get();
        return (cu.getType(0).getName().getIdentifier());
    }

    /**
     * 可以根据已有的代码生成新的代码
     *
     * @param filePath         已有代码路径
     * @param className        要修改的类
     * @param addMethodList    添加的代码片段
     * @param modifyMethodList 修改已有的代码
     * @param importList       需要import的类
     * @return
     */
    public static String modifyCode(String filePath, String className, List<MethodCode> addMethodList, List<MethodCode> modifyMethodList, List<String> importList) {
        File file = new File(filePath);
        try {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(file).getResult().get();
            ClassOrInterfaceDeclaration classOrInterface = cu.getClassByName(className)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            //add method
            addMethodList.forEach(it -> {
                MethodSpec.Builder builder = MethodSpec.methodBuilder(it.getName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(it.getReturnType())
                        .addCode(it.getCode());
                it.getParamList().forEach(p -> {
                    builder.addParameter(p.getKey(), p.getValue());
                });
                MethodSpec newMethod = builder.build();
                classOrInterface.addMember(javaParser.parseBodyDeclaration(newMethod.toString()).getResult().get());
            });
            //add import
            importList.forEach(i -> cu.addImport(i));
            //modify method
            modifyMethodList.forEach(mm -> {
                Optional<MethodDeclaration> methodOptional = classOrInterface.getMethodsByName(mm.getName()).stream().findFirst();
                if (methodOptional.isPresent()) {
                    MethodDeclaration method = methodOptional.get();
                    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                    codeBlockBuilder.addStatement(mm.getCode());
                    CodeBlock newCode = codeBlockBuilder.build();
                    NodeList<Statement> newStatements = javaParser.parseBlock("{" + newCode.toString() + "}").getResult().get().getStatements();
                    // 在方法的开头插入新代码
                    method.getBody().ifPresent(body -> {
                        NodeList<Statement> statements = body.getStatements();
                        statements.addAll(0, newStatements);
                    });
                }
            });
            return cu.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
