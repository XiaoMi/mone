// Generated from D:/code/open_mone/mone/jcommon/docean-plugin/docean-plugin-es-antlr4/src/main/java/com/xiaomi/youpin/docean/plugin/es/antlr4/g4\EsQuery.g4 by ANTLR 4.12.0
package com.xiaomi.youpin.docean.plugin.es.antlr4.query;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link EsQueryParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface EsQueryVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link EsQueryParser#parse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParse(EsQueryParser.ParseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExpression(EsQueryParser.OrExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpression(EsQueryParser.AndExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code aggreExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggreExpression(EsQueryParser.AggreExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpression(EsQueryParser.NotExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpression(EsQueryParser.ParenExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code commonExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommonExpression(EsQueryParser.CommonExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LtExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtExpr(EsQueryParser.LtExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GtExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGtExpr(EsQueryParser.GtExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLeExpr(EsQueryParser.LeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGeExpr(EsQueryParser.GeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNeExpr(EsQueryParser.NeExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EqExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExpr(EsQueryParser.EqExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContainExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContainExpr(EsQueryParser.ContainExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotContainExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotContainExpr(EsQueryParser.NotContainExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInExpr(EsQueryParser.InExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotInExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotInExpr(EsQueryParser.NotInExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExistExpr(EsQueryParser.ExistExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotExistExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExistExpr(EsQueryParser.NotExistExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RegexExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegexExpr(EsQueryParser.RegexExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link EsQueryParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(EsQueryParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MaxAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaxAggExpr(EsQueryParser.MaxAggExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MinAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinAggExpr(EsQueryParser.MinAggExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AvgAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAvgAggExpr(EsQueryParser.AvgAggExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GroupAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupAggExpr(EsQueryParser.GroupAggExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link EsQueryParser#parenValve}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenValve(EsQueryParser.ParenValveContext ctx);
	/**
	 * Visit a parse tree produced by {@link EsQueryParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(EsQueryParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IpV4Value}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIpV4Value(EsQueryParser.IpV4ValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringValue(EsQueryParser.StringValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberValue(EsQueryParser.NumberValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TimeValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeValue(EsQueryParser.TimeValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueValue(EsQueryParser.TrueValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseValue(EsQueryParser.FalseValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullValue(EsQueryParser.NullValueContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdentifierValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifierValue(EsQueryParser.IdentifierValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link EsQueryParser#regex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRegex(EsQueryParser.RegexContext ctx);
}