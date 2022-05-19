package com.xiaomi.data.push.antlr.java;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xiaomi.data.push.antlr.java8.Java8BaseListener;
import com.xiaomi.data.push.antlr.java8.Java8Lexer;
import com.xiaomi.data.push.antlr.java8.Java8Parser;
import lombok.Data;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import java.util.List;
import java.util.Set;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/8 10:13
 * <p>
 * 分析源代码,可以分析出(包路径  那些属性  那些方法)
 */
@Data
public class Java8Expr {


    private List<JavaField> fields = Lists.newArrayList();

    private List<JavaMethod> methods = Lists.newArrayList();

    private String classPackage;

    private ClassInfo classInfo;

    private Set<String> modifierSet = Sets.newHashSet("public", "private", "protected");


    public void walk(String script) {
        Java8Lexer lexer = new Java8Lexer(new ANTLRInputStream(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        Java8BaseListener listener = new Java8BaseListener() {

            /**
             * 获取包路径
             * @param ctx
             */
            @Override
            public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
                super.enterPackageDeclaration(ctx);
                classPackage = ctx.getText();
            }

            /**
             * 获取所有field
             * @param ctx
             */
            @Override
            public void enterFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
                super.enterFieldDeclaration(ctx);
                JavaField field = new JavaField();
                List<String> annos = Lists.newArrayList();
                for (int i = 0; i < ctx.getChildCount(); i++) {
                    ParseTree t = ctx.getChild(i);
                    String text = t.getText();

                    if (text.startsWith("@")) {
                        annos.add(text);
                    }

                    if (text.equals("final")) {
                        field.setFfinal(true);
                    }

                    if (text.equals("static")) {
                        field.setFstatic(true);
                    }

                    if (modifierSet.contains(text)) {
                        field.setModifier(text);
                    }

                    if (t.getClass().equals(Java8Parser.VariableDeclaratorListContext.class)) {
                        if (t.getText().contains("=")) {
                            String[] ss = t.getText().split("=");
                            field.setName(ss[0]);
                            field.setData(ss[1]);
                        } else {
                            field.setName(t.getText());
                        }
                        field.setAnnos(annos);
                        fields.add(field);
                    }
                }
            }

            /**
             * 获取方法名称
             * @param ctx
             */
            @Override
            public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
                super.enterMethodDeclarator(ctx);
                ParserRuleContext header = ctx.getParent().getParent();
                JavaMethod method = new JavaMethod();
                for (int i = 0; i < header.getChildCount(); i++) {
                    ParseTree c = header.getChild(i);

                    String text = c.getText();
                    if (text.equals("final")) {
                        method.setFfinal(true);
                    }

                    if (text.equals("static")) {
                        method.setFstatic(true);
                    }

                    if (modifierSet.contains(text)) {
                        method.setModifier(text);
                    }

                }


                String modifier = header.getChild(0).getText();
                method.setModifier(modifier);
                method.setName(ctx.getChild(0).getText());
                methods.add(method);
            }


            @Override
            public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
                super.enterNormalClassDeclaration(ctx);
                List<String> annos = Lists.newArrayList();
                String name = "";
                for (int i = 0; i < ctx.getChildCount(); i++) {
                    String text = ctx.getChild(i).getText();
                    if (ctx.getChild(i).getClass().equals(TerminalNodeImpl.class)) {
                        name = text;
                    }

//                    System.out.println(text + ":" + ctx.getChild(i).getClass());

                    if (text.startsWith("@")) {
                        annos.add(text);
                    }
                }
                classInfo = new ClassInfo();
                classInfo.setName(name);
                classInfo.setAnnos(annos);
            }
        };
        walker.walk(listener, tree);

    }


}
