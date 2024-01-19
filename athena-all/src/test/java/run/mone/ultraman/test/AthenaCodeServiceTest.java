package run.mone.ultraman.test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.ultraman.bo.AthenaClassInfo;
import run.mone.ultraman.bo.AthenaFieldInfo;
import run.mone.ultraman.bo.AthenaMethodInfo;
import run.mone.ultraman.service.AthenaCodeService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/11/6 22:46
 */
public class AthenaCodeServiceTest {

    @Test
    public void testParseMethod() {
        List<AthenaMethodInfo> list = AthenaCodeService.parseMethodCode("public class A { public void t(){} public int sum(int a, int b) {return a+b;}}");
        System.out.println(list);
    }


    @SneakyThrows
    @Test
    public void testClassInfo() {
        String code = Files.readString(Paths.get("/tmp/o.java"));
        AthenaClassInfo info = AthenaCodeService.classInfo(code);
        System.out.println(info);
        System.out.println(info.getClassCode().length());
    }


    @SneakyThrows
    @Test
    public void testRemove() {
        String code = Files.readString(Paths.get("/tmp/T.java"));
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> pr = javaParser.parse(code);
        if (!pr.isSuccessful()) {
            System.out.println(pr.getProblems());
        }
        CompilationUnit cu = pr.getResult().get();
        cu.findAll(MethodDeclaration.class).stream().filter(it -> it.isPrivate()).forEach(it -> it.removeForced());
        cu.findAll(ConstructorDeclaration.class).stream().forEach(it->it.getBody().removeForced());
        cu.findAll(InitializerDeclaration.class, init -> init.isStatic()).forEach(it -> it.removeForced());

        cu.findAll(FieldDeclaration.class).stream().forEach(it->it.removeForced());
        System.out.println(cu.toString());
    }


    @SneakyThrows
    @Test
    public void testParseField() {
        String code = Files.readString(Paths.get("/tmp/AthenaClassInfo.java"));
        List<AthenaFieldInfo> fieldList = AthenaCodeService.parseFieldCode(code);
        System.out.println(fieldList);
    }
}
