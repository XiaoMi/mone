package run.mone.processor.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.google.common.collect.Lists;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.processor.common.CodeUtils;
import run.mone.processor.common.MethodCode;
import run.mone.processor.common.Pair;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2023/2/20 18:29
 */
public class JavaPoetTest {

    @SneakyThrows
    private void out(MethodSpec methodSpec) {
        TypeSpec typeSpec = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder("run.mone.test", typeSpec).build();
        javaFile.writeTo(System.out);
    }


    @Test
    public void test4() {
        String path = "/Users/zhangzhiyong/IdeaProjects/new_opensource/mone/jcommon/processor/src/test/java/run/mone/processor/test/Run.java";
        MethodCode addMethod = MethodCode.builder()
                .name("setEnv")
                .paramList(Lists.newArrayList(Pair.of(String.class, "key"), Pair.of(String.class, "value")))
                .returnType(void.class)
                .code("try {\n" +
                        "            Map<String, String> env = System.getenv();\n" +
                        "            Class<?> cl = env.getClass();\n" +
                        "            Field field = cl.getDeclaredField(\"m\");\n" +
                        "            field.setAccessible(true);\n" +
                        "            Map<String, String> writableEnv = (Map<String, String>) field.get(env);\n" +
                        "            writableEnv.put(key, value);\n" +
                        "        } catch (Exception e) {\n" +
                        "            throw new IllegalStateException(\"Failed to set environment variable\", e);\n" +
                        "        }")
                .build();
        MethodCode modifyMethod = MethodCode.builder().name("main").code("setEnv(\"a\",\"b\")").build();
        String code = CodeUtils.modifyCode(path, "Run", Lists.newArrayList(addMethod), Lists.newArrayList(modifyMethod), Lists.newArrayList("java.util.Map", "java.lang.reflect.Field"));
        System.out.println(code);
    }


    @SneakyThrows
    @Test
    public void testParse() {
        JavaParser javaParser = new JavaParser();
        CompilationUnit cu = javaParser.parse(new File("/tmp/e")).getResult().get();
        PackageDeclaration a = cu.getPackageDeclaration().get();
        System.out.println(a.getName());
        System.out.println(cu.getType(0).getName().getIdentifier());
    }


    @Test
    public void test0() {
        File file = new File("/Users/zhangzhiyong/IdeaProjects/new_opensource/mone/jcommon/processor/src/test/java/run/mone/processor/test/Run.java");
        try {
            JavaParser javaParser = new JavaParser();
            CompilationUnit cu = javaParser.parse(file).getResult().get();
            ClassOrInterfaceDeclaration classOrInterface = cu.getClassByName("Run")
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            MethodSpec newMethod = MethodSpec.methodBuilder("setEnv")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "key")
                    .addParameter(String.class, "value")
                    .returns(void.class)
                    .addCode("try {\n" +
                            "            Map<String, String> env = System.getenv();\n" +
                            "            Class<?> cl = env.getClass();\n" +
                            "            Field field = cl.getDeclaredField(\"m\");\n" +
                            "            field.setAccessible(true);\n" +
                            "            Map<String, String> writableEnv = (Map<String, String>) field.get(env);\n" +
                            "            writableEnv.put(key, value);\n" +
                            "        } catch (Exception e) {\n" +
                            "            throw new IllegalStateException(\"Failed to set environment variable\", e);\n" +
                            "        }")
                    .build();
            classOrInterface.addMember(javaParser.parseBodyDeclaration(newMethod.toString()).getResult().get());

            cu.addImport("java.util.Map");
            cu.addImport("java.lang.reflect.Field");

            Optional<MethodDeclaration> methodOptional = classOrInterface.getMethodsByName("main").stream().findFirst();
            if (methodOptional.isPresent()) {
                MethodDeclaration method = methodOptional.get();

                CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                codeBlockBuilder.addStatement("setEnv(\"a\",\"b\")");
                CodeBlock newCode = codeBlockBuilder.build();
                NodeList<Statement> newStatements = javaParser.parseBlock("{" + newCode.toString() + "}").getResult().get().getStatements();

                // 在方法的开头插入新代码
                method.getBody().ifPresent(body -> {
                    NodeList<Statement> statements = body.getStatements();
                    statements.addAll(0, newStatements);
                });
            } else {
                throw new RuntimeException("Method not found");
            }
            System.out.println(cu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SneakyThrows
    @Test
    public void test1() {
        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.FINAL, Modifier.STATIC, Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello World!")
                .build();

        out(methodSpec);
    }


    @Test
    public void test2() {
        MethodSpec methodSpec = MethodSpec.methodBuilder("sum")
                .returns(int.class)
                .addModifiers(Modifier.PUBLIC)
                .addCode("int total = 0;\n" +
                        "for (int i=0;i<10;i++;){\n" +
                        "    total+=i;\n" + "" +
                        "}\n" +
                        "return total;").build();

        out(methodSpec);
    }

    @Test
    public void test3() {
        MethodSpec methodSpec = MethodSpec.methodBuilder("sum")
                .returns(int.class)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("int total = 0")
                .beginControlFlow("for (int i=0;i<10;i++)")
                .addStatement("total+=i")
                .endControlFlow()
                .build();

        System.out.println(methodSpec.toString());

        out(methodSpec);
    }

}
