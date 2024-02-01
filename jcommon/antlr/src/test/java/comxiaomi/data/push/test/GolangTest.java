package comxiaomi.data.push.test;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import run.mone.antlr.golang.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 16:28
 */
public class GolangTest {

    @Data
    @Builder
    static class MI {
        public String name;
        public List<String> paramList;
    }


    @SneakyThrows
    @Test
    public void test1() {
        String code = new String(Files.readAllBytes(Paths.get("/Users/zhangzhiyong/GolandProjects/zzystudy/common/a.go")));
        GoLexer lexer = new GoLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GoParser parser = new GoParser(tokens);

        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                System.out.println("line:" + line + " msg:" + msg);
            }
        });

        ParseTree tree = parser.sourceFile();
        ParseTreeWalker walker = new ParseTreeWalker();


        List<Field> list = new ArrayList<>();

        Map<String, List<String>> map = new HashMap<>();


        GoParserListener listener = new GoParserBaseListener() {

            @Override
            public void enterCompositeLit(GoParser.CompositeLitContext ctx) {
                // 遍历所有的键值初始化
                for (GoParser.KeyedElementContext keyedElement : ctx.literalValue().elementList().keyedElement()) {
                    String key = keyedElement.key().getText();
                    System.out.println("key:" + key + " value:" + keyedElement.element().getText());
                    list.add(Field.builder().k(key).v(keyedElement.element().getText()).type("CompositeLit").build());
                }
            }

            @Override
            public void enterVarSpec(GoParser.VarSpecContext ctx) {
                // 获取变量名
                String varName = ctx.identifierList().getText();
                // 获取变量的初始化值
                String varValue = ctx.expressionList().getText();
                list.add(Field.builder().k(varName).v(varValue).type("VarSpec").build());
            }


            @Override
            public void enterArguments(GoParser.ArgumentsContext ctx) {
                int count = ctx.getChildCount();
                if (count == 3) {
                    list.add(Field.builder().k(ctx.getParent().getChild(0).getText()).v(ctx.getChild(1).getText()).type("method").build());
                }
            }

            @Override
            public void enterFunctionDecl(GoParser.FunctionDeclContext ctx) {

                //解析方法名和参数名
                List<GoParser.ParameterDeclContext> pList = ctx.signature().parameters().parameterDecl();
                List<String> l2 = pList.stream().map(it -> {
                    return it.identifierList().IDENTIFIER().stream().map(it2 -> it2.getText()).collect(Collectors.joining(","));
                }).collect(Collectors.toList());

                map.put(ctx.getChild(1).getText(),l2);

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

                // 获取函数声明前的隐藏注释
                List<Token> hiddenTokens = tokens.getHiddenTokensToLeft(startIndex);
                StringBuilder comments = new StringBuilder();
                if (hiddenTokens != null) {
                    for (Token hiddenToken : hiddenTokens) {
                        if (hiddenToken.getChannel() == Lexer.HIDDEN) {
                            comments.append(hiddenToken.getText());
                        }
                    }
                }


                // 打印格式化的函数体
                System.out.println("Function content with formatting: \n" + comments + functionText.toString());
            }


            @Override
            public void enterMethodDecl(GoParser.MethodDeclContext ctx) {
                // 检查是否是结构体的方法
                if (ctx.receiver() != null) {

                    //解析方法名和参数名
                    List<GoParser.ParameterDeclContext> pList = ctx.signature().parameters().parameterDecl();
                    List<String> l2 = pList.stream().map(it -> {
                        return it.identifierList().IDENTIFIER().stream().map(it2 -> it2.getText()).collect(Collectors.joining(","));
                    }).collect(Collectors.toList());

                    //0 func 1 receiver 2 methodName
                    map.put(ctx.getChild(2).getText(),l2);


                    // 获取方法签名的起始token
                    Token startToken = ctx.getStart();
                    // 获取方法体的起始token
                    Token openBraceToken = ctx.block().getStart();

                    // 获取原始的、格式化的方法签名文本
                    int startIndex = startToken.getTokenIndex();
                    int stopIndex = openBraceToken.getTokenIndex() - 1; // 方法体之前的最后一个token

                    // 获取函数声明前的隐藏注释
                    List<Token> hiddenTokens = tokens.getHiddenTokensToLeft(startIndex);
                    StringBuilder comments = new StringBuilder();
                    if (hiddenTokens != null) {
                        for (Token hiddenToken : hiddenTokens) {
                            if (hiddenToken.getChannel() == Lexer.HIDDEN) {
                                comments.append(hiddenToken.getText());
                            }
                        }
                    }


                    List<Token> tokens2 = tokens.getTokens(startIndex, stopIndex);
                    StringBuilder methodSignature = new StringBuilder();
                    for (Token token : tokens2) {
                        methodSignature.append(token.getText());
                    }

                    // 打印格式化的方法签名
                    System.out.println("Formatted Method Signature: \n" + comments + methodSignature.toString().trim());
                }

            }
        };
        walker.walk(listener, tree);
        System.out.println(list);

        System.out.println(map);
    }


    @SneakyThrows
    @Test
    public void test2() {
        String code = new String(Files.readAllBytes(Paths.get("/tmp/test.go")));
        System.out.println(GoCode.methods(code));
    }
}
