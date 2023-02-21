package run.mone.processor.test;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.SneakyThrows;
import org.junit.Test;

import javax.lang.model.element.Modifier;

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

        System.out.println(typeSpec.toString());

        JavaFile javaFile = JavaFile.builder("run.mone.test", typeSpec).build();
        javaFile.writeTo(System.out);
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
