package run.mone.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import lombok.SneakyThrows;
import run.mone.processor.anno.CodeCheck;
import run.mone.processor.codecheck.ClassPasswordCheck;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * 用来check代码
 *  1.代码中是否包含明文密码
 * @date 2023/2/21 20:04
 */
@SupportedAnnotationTypes("run.mone.processor.anno.CodeCheck")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CodeCheckProcessor extends AbstractProcessor {

    private Filer filer;

    private JavacTrees trees;

    private TreeMaker treeMaker;

    private Names names;

    private Messager messager;

    private ClassPasswordCheck classPasswordCheck = new ClassPasswordCheck();


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
        Set<? extends Element> annotation = roundEnv.getElementsAnnotatedWith(CodeCheck.class);
        annotation.stream().map(element -> trees.getTree(element)).forEach(tree -> tree.accept(new TreeTranslator() {

            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClass) {
                classPasswordCheck._check(jcClass, logMessage -> {
                    messager.printMessage(Diagnostic.Kind.ERROR, logMessage);
                });
                super.visitClassDef(jcClass);
            }
        }));
        return false;
    }


}
