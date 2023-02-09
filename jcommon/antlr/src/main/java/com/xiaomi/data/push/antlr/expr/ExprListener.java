// Generated from Expr.g4 by ANTLR 4.7.1
package com.xiaomi.data.push.antlr.expr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(ExprParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(ExprParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(ExprParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(ExprParser.StatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mp}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMp(ExprParser.MpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mp}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMp(ExprParser.MpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mb}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMb(ExprParser.MbContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mb}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMb(ExprParser.MbContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterId(ExprParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitId(ExprParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code pro}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterPro(ExprParser.ProContext ctx);
	/**
	 * Exit a parse tree produced by the {@code pro}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitPro(ExprParser.ProContext ctx);
	/**
	 * Enter a parse tree produced by the {@code met}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMet(ExprParser.MetContext ctx);
	/**
	 * Exit a parse tree produced by the {@code met}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMet(ExprParser.MetContext ctx);
	/**
	 * Enter a parse tree produced by the {@code int}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInt(ExprParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code int}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInt(ExprParser.IntContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#property}.
	 * @param ctx the parse tree
	 */
	void enterProperty(ExprParser.PropertyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#property}.
	 * @param ctx the parse tree
	 */
	void exitProperty(ExprParser.PropertyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#method}.
	 * @param ctx the parse tree
	 */
	void enterMethod(ExprParser.MethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#method}.
	 * @param ctx the parse tree
	 */
	void exitMethod(ExprParser.MethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#params}.
	 * @param ctx the parse tree
	 */
	void enterParams(ExprParser.ParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#params}.
	 * @param ctx the parse tree
	 */
	void exitParams(ExprParser.ParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(ExprParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(ExprParser.PairContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(ExprParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(ExprParser.ValueContext ctx);
}