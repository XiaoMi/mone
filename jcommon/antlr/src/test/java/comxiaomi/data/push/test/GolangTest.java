package comxiaomi.data.push.test;

import com.xiaomi.data.push.antlr.expr.ExprLexer;
import com.xiaomi.data.push.antlr.expr.ExprListenerImpl;
import com.xiaomi.data.push.antlr.expr.ExprParser;
import com.xiaomi.data.push.antlr.java.Java8Expr;
import lombok.SneakyThrows;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import run.mone.antlr.golang.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 16:28
 */
public class GolangTest {

    @SneakyThrows
    @Test
    public void test1() {
        String code = new String(Files.readAllBytes(Paths.get("/tmp/test.go")));
        GoLexer lexer = new GoLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GoParser parser = new GoParser(tokens);
        ParseTree tree = parser.sourceFile();
        ParseTreeWalker walker = new ParseTreeWalker();
        GoParserListener listener = new GoParserBaseListener(){


            @Override
            public void enterFunctionDecl(GoParser.FunctionDeclContext ctx) {
                // 获取函数体的起始和结束token
                Token startToken = ctx.getStart();

                // 获取方法体的起始token
                Token openBraceToken = ctx.block().getStart();

                // 获取原始的、格式化的函数文本
                int startIndex = startToken.getTokenIndex();
                int stopIndex = openBraceToken.getTokenIndex() - 1;
                List<Token> t = tokens.getTokens(startIndex, stopIndex);
                StringBuilder functionText = new StringBuilder();
                for (Token token : t) {
                    functionText.append(token.getText());
                }
                // 打印格式化的函数体
                System.out.println("Function content with formatting: " + functionText.toString());
            }


            @Override
            public void enterMethodDecl(GoParser.MethodDeclContext ctx) {
                // 检查是否是结构体的方法
                if (ctx.receiver() != null) {
                    // 获取方法签名的起始token
                    Token startToken = ctx.getStart();
                    // 获取方法体的起始token
                    Token openBraceToken = ctx.block().getStart();

                    // 获取原始的、格式化的方法签名文本
                    int startIndex = startToken.getTokenIndex();
                    int stopIndex = openBraceToken.getTokenIndex() - 1; // 方法体之前的最后一个token
                    List<Token> tokens2 = tokens.getTokens(startIndex, stopIndex);
                    StringBuilder methodSignature = new StringBuilder();
                    for (Token token : tokens2) {
                        methodSignature.append(token.getText());
                    }

                    // 打印格式化的方法签名
                    System.out.println("Formatted Method Signature: " + methodSignature.toString().trim());
                }

            }
        };
        walker.walk(listener, tree);
    }


    @SneakyThrows
    @Test
    public void test2(){
        String code = new String(Files.readAllBytes(Paths.get("/tmp/test.go")));
        System.out.println(GoCode.methods(code));
    }
}
