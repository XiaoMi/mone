package comxiaomi.data.push.test;

import com.xiaomi.data.push.antlr.java.Java8Expr;
import com.xiaomi.data.push.antlr.java8.Java8BaseListener;
import com.xiaomi.data.push.antlr.java8.Java8Lexer;
import com.xiaomi.data.push.antlr.java8.Java8Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 3/6/21
 */
public class Java8Test {

    @Test
    public void testjava8() throws IOException {
//        String script = new String(Files.readAllBytes(Paths.get("/Users/zzy/IdeaProjects/mione/jcommon/antlr/src/main/java/com/xiaomi/data/push/antlr/example/Example.java")));
        String script = new String(Files.readAllBytes(Paths.get("/tmp/Example.java")));
        Java8Expr java8Expr = new Java8Expr();
        java8Expr.walk(script);
        System.out.println(java8Expr.getClassPackage()  + java8Expr.getClassInfo());
        System.out.println(java8Expr.getFields());
        System.out.println(java8Expr.getMethods());
    }

}
