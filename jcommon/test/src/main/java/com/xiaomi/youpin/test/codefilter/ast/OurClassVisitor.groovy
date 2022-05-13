package com.xiaomi.youpin.test.codefilter.ast

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.GroovyClassVisitor
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/18 16:14
 */
class OurClassVisitor implements GroovyClassVisitor {

    @Override
    void visitClass(ClassNode classNode) {

    }

    @Override
    void visitConstructor(ConstructorNode constructorNode) {

    }

    @Override
    void visitMethod(MethodNode methodNode) {
        if (methodNode.getName().equals("sum")) {
            println("****" + methodNode.getName())


            Statement code = methodNode.getCode();
            OurCodeVisitor v = new OurCodeVisitor(methodNode)
            code.getStatements().each { it ->
                println(it)
                it.visit(v)
            }
            v.list.each { it ->
                methodNode.code.statements.add(0, it)
            }

//            def codeAsString = 'call()'
//            List<BlockStatement> statements = new AstBuilder().buildFromString(codeAsString)
//            def call = statements[0].statements[0].expression
//            methodNode.code.statements.add(0, new ExpressionStatement(call))
        }
    }

    @Override
    void visitField(FieldNode fieldNode) {

    }

    @Override
    void visitProperty(PropertyNode propertyNode) {

    }
}
