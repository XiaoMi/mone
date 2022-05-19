package com.xiaomi.youpin.test.codefilter.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * @author goodjava@qq.com
 * @date 3/15/21
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
//@GroovyASTTransformation
class MyASTTransformation implements ASTTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        println("MyASTTransformation visit()  $nodes")
        source.getAST()?.getClasses()?.each { classNode ->
            //Class node is a class that is contained in the file being compiled
            println("className:->" + classNode.getName())
//            classNode.getMethods().each {it->
//                println(it.getName())
//            }
            classNode.addProperty("abc", ClassNode.ACC_PUBLIC, new ClassNode(Class.forName("java.lang.String")), null, null, null)


            classNode.visitContents(new OurClassVisitor())

//            classNode.getDeclaredMethods("sum")?.each {
//                BlockStatement code = it.getCode();
//                code.getStatements().each {println(it.toString())}
//
//            }
//
//            classNode.getDeclaredMethods("sum")?.each {
//                def codeAsString = 'call()'
//                List<BlockStatement> statements = new AstBuilder().buildFromString(codeAsString)
//                def call = statements[0].statements[0].expression
//                it.code.statements.add(0,new ExpressionStatement(call))
//            }

        }
    }


}
