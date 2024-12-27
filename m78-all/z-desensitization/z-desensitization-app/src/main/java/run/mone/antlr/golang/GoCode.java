package run.mone.antlr.golang;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 16:56
 */
@Slf4j
public class GoCode {


    public static ParseResult parse(String code) {
        GoLexer lexer = new GoLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GoParser parser = new GoParser(tokens);

        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                log.error("line:" + line + " msg:" + msg);
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
                }

            }
        };
        walker.walk(listener, tree);
        return ParseResult.builder().fieldList(list).methodMap(map).build();
    }



}
