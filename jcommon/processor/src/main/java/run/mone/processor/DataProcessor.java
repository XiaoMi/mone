package run.mone.processor;

import com.google.common.base.CaseFormat;
import com.google.common.base.Throwables;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import lombok.SneakyThrows;
import run.mone.processor.anno.Data;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/2/19 20:44
 */
@SupportedAnnotationTypes("run.mone.processor.anno.Data")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DataProcessor extends AbstractProcessor {

    private Filer filer;

    private JavacTrees trees;

    private TreeMaker treeMaker;

    private Names names;

    private Messager messager;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        messager = processingEnv.getMessager();
        this.names = Names.instance(context);
        filer = processingEnv.getFiler();
    }

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotation = roundEnv.getElementsAnnotatedWith(Data.class);
        annotation.stream().map(element -> trees.getTree(element)).forEach(tree -> tree.accept(new TreeTranslator() {

            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClass) {
                Map<Name, JCTree.JCVariableDecl> treeMap =
                        jcClass.defs.stream().filter(k -> k.getKind().equals(Tree.Kind.VARIABLE))
                                .map(tree -> (JCTree.JCVariableDecl) tree)
                                .collect(Collectors.toMap(JCTree.JCVariableDecl::getName, Function.identity()));
                treeMap.forEach((k, jcVariable) -> {
                    messager.printMessage(Diagnostic.Kind.NOTE, String.format("fields:%s", k));
                    try {
                        //增加get方法
                        jcClass.defs = jcClass.defs.prepend(generateGetterMethod(jcVariable));
                        //增加set方法
                        jcClass.defs = jcClass.defs.prepend(generateSetterMethod(jcVariable));
                    } catch (Exception e) {
                        messager.printMessage(Diagnostic.Kind.ERROR, Throwables.getStackTraceAsString(e));
                    }
                });
                super.visitClassDef(jcClass);
            }
        }));
        return false;
    }

    private Name handleMethodSignature(Name name, String prefix) {
        return names.fromString(prefix + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name.toString()));
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }

    private JCTree.JCMethodDecl generateGetterMethod(JCTree.JCVariableDecl jcVariable) {

        //修改方法级别
        JCTree.JCModifiers jcModifiers = treeMaker.Modifiers(Flags.PUBLIC);

        //添加方法名称
        Name methodName = handleMethodSignature(jcVariable.getName(), "get");

        //添加方法内容
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        jcStatements.append(
                treeMaker.Return(treeMaker.Select(treeMaker.Ident(getNameFromString("this")), jcVariable.getName())));
        JCTree.JCBlock jcBlock = treeMaker.Block(0, jcStatements.toList());

        //添加返回值类型
        JCTree.JCExpression returnType = jcVariable.vartype;

        //参数类型
        List<JCTree.JCTypeParameter> typeParameters = List.nil();

        //参数变量
        List<JCTree.JCVariableDecl> parameters = List.nil();

        //声明异常
        List<JCTree.JCExpression> throwsClauses = List.nil();
        //构建方法
        return treeMaker
                .MethodDef(jcModifiers, methodName, returnType, typeParameters, parameters, throwsClauses, jcBlock, null);
    }

    private JCTree.JCMethodDecl generateSetterMethod(JCTree.JCVariableDecl jcVariable) throws ReflectiveOperationException {

        //修改方法级别
        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC);

        //添加方法名称
        Name variableName = jcVariable.getName();
        Name methodName = handleMethodSignature(variableName, "set");

        //设置方法体
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        jcStatements.append(treeMaker.Exec(treeMaker
                .Assign(treeMaker.Select(treeMaker.Ident(getNameFromString("this")), variableName),
                        treeMaker.Ident(variableName))));
        //定义方法体
        JCTree.JCBlock jcBlock = treeMaker.Block(0, jcStatements.toList());

        //添加返回值类型
        JCTree.JCExpression returnType =
                treeMaker.Type((Type) Class.forName("com.sun.tools.javac.code.Type$JCVoidType").newInstance());

        List<JCTree.JCTypeParameter> typeParameters = List.nil();

        //定义参数
        JCTree.JCVariableDecl variableDecl = treeMaker
                .VarDef(treeMaker.Modifiers(Flags.PARAMETER, List.nil()), jcVariable.name, jcVariable.vartype, null);
        List<JCTree.JCVariableDecl> parameters = List.of(variableDecl);

        //声明异常
        List<JCTree.JCExpression> throwsClauses = List.nil();
        return treeMaker
                .MethodDef(modifiers, methodName, returnType, typeParameters, parameters, throwsClauses, jcBlock, null);

    }


}
