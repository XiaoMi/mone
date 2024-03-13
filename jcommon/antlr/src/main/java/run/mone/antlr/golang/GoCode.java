package run.mone.antlr.golang;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/1/29 16:56
 */
public class GoCode {


    public static List<GoMethod> methods(String code) {
        GoLexer lexer = new GoLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GoParser parser = new GoParser(tokens);
        ParseTree tree = parser.sourceFile();
        ParseTreeWalker walker = new ParseTreeWalker();
        List<GoMethod> list = new ArrayList<>();
        GoParserListener listener = new GoParserBaseListener() {
            @Override
            public void enterFunctionDecl(GoParser.FunctionDeclContext ctx) {
                GoParser.SignatureContext signature = ctx.signature();
                List<GoParam> params = new ArrayList<>();

                //获取参数信息
                if (signature != null) {
                    GoParser.ParametersContext parameters = signature.parameters();
                    if (parameters != null) {
                        for (GoParser.ParameterDeclContext paramCtx : parameters.parameterDecl()) {
                            // 获取参数类型
                            String paramType = paramCtx.type_().getText();
                            // 获取参数名称
                            List<TerminalNode> paramNames = paramCtx.identifierList().IDENTIFIER();
                            for (TerminalNode paramName : paramNames) {
                                params.add(GoParam.builder().name(paramName.getText()).type(paramType).build());
                            }
                        }
                    }
                }

                Token startToken = ctx.getStart();
                Token stopToken = ctx.getStop();
                // 获取原始的、格式化的函数文本
                int startIndex = startToken.getTokenIndex();
                int stopIndex = stopToken.getTokenIndex();
                List<Token> t = tokens.getTokens(startIndex, stopIndex);
                StringBuilder functionText = new StringBuilder();
                for (Token token : t) {
                    functionText.append(token.getText());
                }
                list.add(GoMethod.builder().name(ctx.IDENTIFIER().getText()).code(functionText.toString()).paramList(params).build());
            }
        };
        walker.walk(listener, tree);
        return list;
    }

}
