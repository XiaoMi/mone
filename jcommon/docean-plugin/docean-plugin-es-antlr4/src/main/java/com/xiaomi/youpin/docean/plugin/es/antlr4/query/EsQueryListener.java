// Generated from D:/code/open_mone/mone/jcommon/docean-plugin/docean-plugin-es-antlr4/src/main/java/com/xiaomi/youpin/docean/plugin/es/antlr4/g4\EsQuery.g4 by ANTLR 4.12.0
package com.xiaomi.youpin.docean.plugin.es.antlr4.query;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EsQueryParser}.
 */
public interface EsQueryListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link EsQueryParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(EsQueryParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsQueryParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(EsQueryParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code orExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterOrExpression(EsQueryParser.OrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code orExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitOrExpression(EsQueryParser.OrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code andExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAndExpression(EsQueryParser.AndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code andExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAndExpression(EsQueryParser.AndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code aggreExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAggreExpression(EsQueryParser.AggreExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code aggreExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAggreExpression(EsQueryParser.AggreExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNotExpression(EsQueryParser.NotExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNotExpression(EsQueryParser.NotExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParenExpression(EsQueryParser.ParenExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parenExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParenExpression(EsQueryParser.ParenExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code commonExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCommonExpression(EsQueryParser.CommonExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code commonExpression}
	 * labeled alternative in {@link EsQueryParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCommonExpression(EsQueryParser.CommonExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LtExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLtExpr(EsQueryParser.LtExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LtExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLtExpr(EsQueryParser.LtExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GtExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterGtExpr(EsQueryParser.GtExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GtExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitGtExpr(EsQueryParser.GtExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterLeExpr(EsQueryParser.LeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitLeExpr(EsQueryParser.LeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterGeExpr(EsQueryParser.GeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitGeExpr(EsQueryParser.GeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNeExpr(EsQueryParser.NeExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NeExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNeExpr(EsQueryParser.NeExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEqExpr(EsQueryParser.EqExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEqExpr(EsQueryParser.EqExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ContainExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterContainExpr(EsQueryParser.ContainExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ContainExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitContainExpr(EsQueryParser.ContainExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotContainExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNotContainExpr(EsQueryParser.NotContainExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotContainExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNotContainExpr(EsQueryParser.NotContainExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInExpr(EsQueryParser.InExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInExpr(EsQueryParser.InExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotInExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNotInExpr(EsQueryParser.NotInExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotInExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNotInExpr(EsQueryParser.NotInExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExistExpr(EsQueryParser.ExistExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExistExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExistExpr(EsQueryParser.ExistExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotExistExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNotExistExpr(EsQueryParser.NotExistExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotExistExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNotExistExpr(EsQueryParser.NotExistExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RegexExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRegexExpr(EsQueryParser.RegexExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RegexExpr}
	 * labeled alternative in {@link EsQueryParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRegexExpr(EsQueryParser.RegexExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsQueryParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(EsQueryParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsQueryParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(EsQueryParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MaxAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void enterMaxAggExpr(EsQueryParser.MaxAggExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MaxAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void exitMaxAggExpr(EsQueryParser.MaxAggExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MinAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void enterMinAggExpr(EsQueryParser.MinAggExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MinAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void exitMinAggExpr(EsQueryParser.MinAggExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AvgAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void enterAvgAggExpr(EsQueryParser.AvgAggExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AvgAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void exitAvgAggExpr(EsQueryParser.AvgAggExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GroupAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void enterGroupAggExpr(EsQueryParser.GroupAggExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GroupAggExpr}
	 * labeled alternative in {@link EsQueryParser#aggexpr}.
	 * @param ctx the parse tree
	 */
	void exitGroupAggExpr(EsQueryParser.GroupAggExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsQueryParser#parenValve}.
	 * @param ctx the parse tree
	 */
	void enterParenValve(EsQueryParser.ParenValveContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsQueryParser#parenValve}.
	 * @param ctx the parse tree
	 */
	void exitParenValve(EsQueryParser.ParenValveContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsQueryParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(EsQueryParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsQueryParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(EsQueryParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IpV4Value}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterIpV4Value(EsQueryParser.IpV4ValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IpV4Value}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitIpV4Value(EsQueryParser.IpV4ValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterStringValue(EsQueryParser.StringValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitStringValue(EsQueryParser.StringValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNumberValue(EsQueryParser.NumberValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNumberValue(EsQueryParser.NumberValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TimeValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterTimeValue(EsQueryParser.TimeValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TimeValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitTimeValue(EsQueryParser.TimeValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TrueValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterTrueValue(EsQueryParser.TrueValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TrueValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitTrueValue(EsQueryParser.TrueValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FalseValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterFalseValue(EsQueryParser.FalseValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FalseValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitFalseValue(EsQueryParser.FalseValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NullValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterNullValue(EsQueryParser.NullValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NullValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitNullValue(EsQueryParser.NullValueContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentifierValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierValue(EsQueryParser.IdentifierValueContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentifierValue}
	 * labeled alternative in {@link EsQueryParser#value}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierValue(EsQueryParser.IdentifierValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link EsQueryParser#regex}.
	 * @param ctx the parse tree
	 */
	void enterRegex(EsQueryParser.RegexContext ctx);
	/**
	 * Exit a parse tree produced by {@link EsQueryParser#regex}.
	 * @param ctx the parse tree
	 */
	void exitRegex(EsQueryParser.RegexContext ctx);
}