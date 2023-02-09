package com.xiaomi.youpin.test.codefilter.ast

import com.google.common.collect.Lists
import org.codehaus.groovy.ast.GroovyCodeVisitor
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ArrayExpression
import org.codehaus.groovy.ast.expr.AttributeExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ClosureListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.FieldExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.MethodPointerExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.PostfixExpression
import org.codehaus.groovy.ast.expr.PrefixExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.RangeExpression
import org.codehaus.groovy.ast.expr.SpreadExpression
import org.codehaus.groovy.ast.expr.SpreadMapExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.UnaryPlusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.BreakStatement
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ContinueStatement
import org.codehaus.groovy.ast.stmt.DoWhileStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codehaus.groovy.classgen.BytecodeExpression

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/18 16:15
 */
class OurCodeVisitor implements GroovyCodeVisitor {


    private MethodNode methodNode;

    public List<ExpressionStatement> list = Lists.newArrayList();

    OurCodeVisitor(MethodNode methodNode) {
        this.methodNode = methodNode
    }

    @Override
    void visitBlockStatement(BlockStatement blockStatement) {
        println(blockStatement)
    }

    @Override
    void visitForLoop(ForStatement forStatement) {

    }

    @Override
    void visitWhileLoop(WhileStatement whileStatement) {

    }

    @Override
    void visitDoWhileLoop(DoWhileStatement doWhileStatement) {

    }

    @Override
    void visitIfElse(IfStatement ifStatement) {

    }

    @Override
    void visitExpressionStatement(ExpressionStatement expressionStatement) {
        if (expressionStatement.expression instanceof DeclarationExpression) {
            DeclarationExpression de = expressionStatement.expression;
            ConstantExpression ce = de.getRightExpression()
            println("###"+ce.getValue())

            def codeAsString = 'call("map")'
            List<BlockStatement> statements = new AstBuilder().buildFromString(codeAsString)
            def call = statements[0].statements[0].expression
            list.add(new ExpressionStatement(call))
        }
    }

    @Override
    void visitReturnStatement(ReturnStatement returnStatement) {
        println("return gogogo:"+returnStatement.toString())
    }

    @Override
    void visitAssertStatement(AssertStatement assertStatement) {

    }

    @Override
    void visitTryCatchFinally(TryCatchStatement tryCatchStatement) {

    }

    @Override
    void visitSwitch(SwitchStatement switchStatement) {

    }

    @Override
    void visitCaseStatement(CaseStatement caseStatement) {

    }

    @Override
    void visitBreakStatement(BreakStatement breakStatement) {

    }

    @Override
    void visitContinueStatement(ContinueStatement continueStatement) {

    }

    @Override
    void visitThrowStatement(ThrowStatement throwStatement) {

    }

    @Override
    void visitSynchronizedStatement(SynchronizedStatement synchronizedStatement) {

    }

    @Override
    void visitCatchStatement(CatchStatement catchStatement) {

    }

    @Override
    void visitMethodCallExpression(MethodCallExpression methodCallExpression) {

    }

    @Override
    void visitStaticMethodCallExpression(StaticMethodCallExpression staticMethodCallExpression) {

    }

    @Override
    void visitConstructorCallExpression(ConstructorCallExpression constructorCallExpression) {

    }

    @Override
    void visitTernaryExpression(TernaryExpression ternaryExpression) {

    }

    @Override
    void visitShortTernaryExpression(ElvisOperatorExpression elvisOperatorExpression) {

    }

    @Override
    void visitBinaryExpression(BinaryExpression binaryExpression) {

    }

    @Override
    void visitPrefixExpression(PrefixExpression prefixExpression) {

    }

    @Override
    void visitPostfixExpression(PostfixExpression postfixExpression) {

    }

    @Override
    void visitBooleanExpression(BooleanExpression booleanExpression) {

    }

    @Override
    void visitClosureExpression(ClosureExpression closureExpression) {

    }

    @Override
    void visitTupleExpression(TupleExpression tupleExpression) {

    }

    @Override
    void visitMapExpression(MapExpression mapExpression) {

    }

    @Override
    void visitMapEntryExpression(MapEntryExpression mapEntryExpression) {

    }

    @Override
    void visitListExpression(ListExpression listExpression) {

    }

    @Override
    void visitRangeExpression(RangeExpression rangeExpression) {

    }

    @Override
    void visitPropertyExpression(PropertyExpression propertyExpression) {

    }

    @Override
    void visitAttributeExpression(AttributeExpression attributeExpression) {

    }

    @Override
    void visitFieldExpression(FieldExpression fieldExpression) {

    }

    @Override
    void visitMethodPointerExpression(MethodPointerExpression methodPointerExpression) {

    }

    @Override
    void visitConstantExpression(ConstantExpression constantExpression) {

    }

    @Override
    void visitClassExpression(ClassExpression classExpression) {

    }

    @Override
    void visitVariableExpression(VariableExpression variableExpression) {

    }

    @Override
    void visitDeclarationExpression(DeclarationExpression declarationExpression) {
        println("---->"+declarationExpression.toString())
    }

    @Override
    void visitGStringExpression(GStringExpression gStringExpression) {
        println(gStringExpression)
    }

    @Override
    void visitArrayExpression(ArrayExpression arrayExpression) {

    }

    @Override
    void visitSpreadExpression(SpreadExpression spreadExpression) {

    }

    @Override
    void visitSpreadMapExpression(SpreadMapExpression spreadMapExpression) {

    }

    @Override
    void visitNotExpression(NotExpression notExpression) {

    }

    @Override
    void visitUnaryMinusExpression(UnaryMinusExpression unaryMinusExpression) {

    }

    @Override
    void visitUnaryPlusExpression(UnaryPlusExpression unaryPlusExpression) {

    }

    @Override
    void visitBitwiseNegationExpression(BitwiseNegationExpression bitwiseNegationExpression) {

    }

    @Override
    void visitCastExpression(CastExpression castExpression) {

    }

    @Override
    void visitArgumentlistExpression(ArgumentListExpression argumentListExpression) {

    }

    @Override
    void visitClosureListExpression(ClosureListExpression closureListExpression) {

    }

    @Override
    void visitBytecodeExpression(BytecodeExpression bytecodeExpression) {

    }
}
