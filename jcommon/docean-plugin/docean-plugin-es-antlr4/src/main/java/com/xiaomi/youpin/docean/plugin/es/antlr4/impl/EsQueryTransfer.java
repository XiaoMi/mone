package com.xiaomi.youpin.docean.plugin.es.antlr4.impl;

import com.xiaomi.youpin.docean.plugin.es.antlr4.common.context.ValueContext;
import com.xiaomi.youpin.docean.plugin.es.antlr4.common.enums.ValueTypeEnum;
import com.xiaomi.youpin.docean.plugin.es.antlr4.common.util.MergeUtils;
import com.xiaomi.youpin.docean.plugin.es.antlr4.query.EsQueryListener;
import com.xiaomi.youpin.docean.plugin.es.antlr4.query.EsQueryParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/3 17:25
 */
public class EsQueryTransfer implements EsQueryListener {

    public static final String DOUBLE_QUOTATION_MARK_SEPARATOR = "~";

    private ParseTreeProperty<SearchSourceBuilder> treeProperty = new ParseTreeProperty<>();

    private ParseTreeProperty<ValueContext> valueProperty = new ParseTreeProperty<>();

    @Override
    public void enterParse(EsQueryParser.ParseContext ctx) {

    }

    @Override
    public void exitParse(EsQueryParser.ParseContext ctx) {

    }

    @Override
    public void enterOrExpression(EsQueryParser.OrExpressionContext ctx) {

    }

    @Override
    public void exitOrExpression(EsQueryParser.OrExpressionContext ctx) {
        ParseTree expression1 = ctx.children.get(0);
        ParseTree expression2 = ctx.children.get(2);
        SearchSourceBuilder sourceBuilder = MergeUtils.MergeOr(treeProperty.get(expression1), treeProperty.get(expression2));
        treeProperty.put(ctx, sourceBuilder);
    }

    @Override
    public void enterAndExpression(EsQueryParser.AndExpressionContext ctx) {

    }

    @Override
    public void exitAndExpression(EsQueryParser.AndExpressionContext ctx) {
        ParseTree expression1 = ctx.children.get(0);
        ParseTree expression2 = ctx.children.get(2);
        SearchSourceBuilder sourceBuilder = MergeUtils.MergeAnd(treeProperty.get(expression1), treeProperty.get(expression2));
        treeProperty.put(ctx, sourceBuilder);
    }

    @Override
    public void enterAggreExpression(EsQueryParser.AggreExpressionContext ctx) {

    }

    @Override
    public void exitAggreExpression(EsQueryParser.AggreExpressionContext ctx) {
        treeProperty.put(ctx, treeProperty.get(ctx.getChild(0)));
    }

    @Override
    public void enterNotExpression(EsQueryParser.NotExpressionContext ctx) {

    }

    @Override
    public void exitNotExpression(EsQueryParser.NotExpressionContext ctx) {
        //获取到括号内得表达式
        //注：“非”逻辑不论包含多少参数都需加上括号，NOT(a:1)、 NOT(a : 1 AND b : 2)
        if ("not".equals(ctx.children.get(0).getText()) || "NOT".equals(ctx.children.get(0).getText())) {
            ParseTree tree = ctx.children.get(1);
            SearchSourceBuilder sourceBuilder = MergeUtils.MergeNot(treeProperty.get(tree));
            treeProperty.put(ctx, sourceBuilder);
        } else {
            ParseTree tree = ctx.children.get(2);
            SearchSourceBuilder sourceBuilder = MergeUtils.MergeNot(treeProperty.get(tree));
            treeProperty.put(ctx, MergeUtils.MergeAnd(treeProperty.get(ctx.children.get(0)), sourceBuilder));
        }
    }

    @Override
    public void enterParenExpression(EsQueryParser.ParenExpressionContext ctx) {

    }

    @Override
    public void exitParenExpression(EsQueryParser.ParenExpressionContext ctx) {
        ParseTree tree = ctx.children.get(1);
        treeProperty.put(ctx, treeProperty.get(tree));
    }

    @Override
    public void enterCommonExpression(EsQueryParser.CommonExpressionContext ctx) {

    }

    @Override
    public void exitCommonExpression(EsQueryParser.CommonExpressionContext ctx) {
        ParseTree tree = ctx.getChild(0);
        treeProperty.put(ctx, treeProperty.get(tree));
    }

    @Override
    public void enterLtExpr(EsQueryParser.LtExprContext ctx) {

    }

    @Override
    public void exitLtExpr(EsQueryParser.LtExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ParseTree valueNode = ctx.getChild(2);
        ValueContext value = valueProperty.get(valueNode);
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().must(new RangeQueryBuilder(param).lt(value.getValue())));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterGtExpr(EsQueryParser.GtExprContext ctx) {

    }

    @Override
    public void exitGtExpr(EsQueryParser.GtExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ValueContext value = valueProperty.get(ctx.getChild(2));
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().must(new RangeQueryBuilder(param).gt(value.getValue())));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterLeExpr(EsQueryParser.LeExprContext ctx) {

    }

    @Override
    public void exitLeExpr(EsQueryParser.LeExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ValueContext value = valueProperty.get(ctx.getChild(2));
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().must(new RangeQueryBuilder(param).lte(value.getValue())));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterGeExpr(EsQueryParser.GeExprContext ctx) {

    }

    @Override
    public void exitGeExpr(EsQueryParser.GeExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ValueContext value = valueProperty.get(ctx.getChild(2));
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().must(new RangeQueryBuilder(param).gte(value.getValue())));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterNeExpr(EsQueryParser.NeExprContext ctx) {

    }

    @Override
    public void exitNeExpr(EsQueryParser.NeExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ValueContext value = valueProperty.get(ctx.getChild(2));
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().mustNot(QueryBuilders.termQuery(param, value.getValue())));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterEqExpr(EsQueryParser.EqExprContext ctx) {

    }

    @Override
    public void exitEqExpr(EsQueryParser.EqExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ValueContext value = valueProperty.get(ctx.getChild(2));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (null == value) {
            //不区分field
            boolQueryBuilder.filter(QueryBuilders.queryStringQuery(param));
        } else {
            switch (value.getType()) {
                case IPV4:
                case IPV6: {
                    boolQueryBuilder.must(QueryBuilders.prefixQuery(param, value.getValue().toString()));
                    break;
                }
                default: {
                    if (value.getValue().toString().startsWith(DOUBLE_QUOTATION_MARK_SEPARATOR)) {
                        boolQueryBuilder.must(QueryBuilders.matchQuery(param, value.getValue().toString().substring(1)));
                    } else {
                        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(param, value.getValue()));
                    }
                }
            }
        }
        SearchSourceBuilder builder = new SearchSourceBuilder().query(boolQueryBuilder);
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterContainExpr(EsQueryParser.ContainExprContext ctx) {

    }

    @Override
    public void exitContainExpr(EsQueryParser.ContainExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ValueContext value = valueProperty.get(ctx.getChild(2));
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().must(
                        QueryBuilders.wildcardQuery(param, "*" + value.getValue().toString() + "*")
                ));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterNotContainExpr(EsQueryParser.NotContainExprContext ctx) {

    }

    @Override
    public void exitNotContainExpr(EsQueryParser.NotContainExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ValueContext value = valueProperty.get(ctx.getChild(2));
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().mustNot(
                        QueryBuilders.wildcardQuery(param, "*" + value.getValue().toString() + "*")
                ));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterInExpr(EsQueryParser.InExprContext ctx) {

    }

    @Override
    public void exitInExpr(EsQueryParser.InExprContext ctx) {
        String param = ctx.getChild(0).getText();
        ArrayList value = (ArrayList) valueProperty.get(ctx.getChild(2)).getValue();
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().must(QueryBuilders.termsQuery(param, value)));
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterNotInExpr(EsQueryParser.NotInExprContext ctx) {

    }

    @Override
    public void exitNotInExpr(EsQueryParser.NotInExprContext ctx) {

    }

    @Override
    public void enterExistExpr(EsQueryParser.ExistExprContext ctx) {

    }

    @Override
    public void exitExistExpr(EsQueryParser.ExistExprContext ctx) {
        String param = ctx.getChild(0).getText();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        QueryBuilder existsQuery = QueryBuilders.existsQuery(param);
        builder.query(existsQuery);
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterNotExistExpr(EsQueryParser.NotExistExprContext ctx) {

    }

    @Override
    public void exitNotExistExpr(EsQueryParser.NotExistExprContext ctx) {
        String param = ctx.getChild(0).getText();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        QueryBuilder existsQuery = QueryBuilders.boolQuery()
                .mustNot(QueryBuilders.existsQuery(param));
        builder.query(existsQuery);
        treeProperty.put(ctx, builder);
    }

    @Override
    public void enterRegexExpr(EsQueryParser.RegexExprContext ctx) {

    }

    @Override
    public void exitRegexExpr(EsQueryParser.RegexExprContext ctx) {
        String param = ctx.getChild(0).getText();
        String value = valueProperty.get(ctx.getChild(2)).getValue().toString();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(new BoolQueryBuilder().must(QueryBuilders.regexpQuery(param, value)));
        treeProperty.put(ctx, sourceBuilder);
    }

    @Override
    public void enterArray(EsQueryParser.ArrayContext ctx) {

    }

    @Override
    public void exitArray(EsQueryParser.ArrayContext ctx) {
        List<ParseTree> children = ctx.children;
        //ArrayList<Object> arr = new ArrayList<>();
        List<Object> list = children.stream().filter(x -> x.getChildCount() >= 1).map(x -> valueProperty.get(x).getValue()).collect(Collectors.toList());
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.ARRAY, list));
    }

    @Override
    public void enterMaxAggExpr(EsQueryParser.MaxAggExprContext ctx) {

    }

    @Override
    public void exitMaxAggExpr(EsQueryParser.MaxAggExprContext ctx) {

    }

    @Override
    public void enterMinAggExpr(EsQueryParser.MinAggExprContext ctx) {

    }

    @Override
    public void exitMinAggExpr(EsQueryParser.MinAggExprContext ctx) {

    }

    @Override
    public void enterAvgAggExpr(EsQueryParser.AvgAggExprContext ctx) {

    }

    @Override
    public void exitAvgAggExpr(EsQueryParser.AvgAggExprContext ctx) {

    }

    @Override
    public void enterGroupAggExpr(EsQueryParser.GroupAggExprContext ctx) {

    }

    @Override
    public void exitGroupAggExpr(EsQueryParser.GroupAggExprContext ctx) {
        String param = ctx.getChild(0).getText();
        if (Objects.isNull(treeProperty.get(ctx.getChild(3)))) {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .aggregation(AggregationBuilders.terms(param));
            treeProperty.put(ctx, sourceBuilder);
        } else {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                    .aggregation(AggregationBuilders.terms(param));
            sourceBuilder.query(treeProperty.get(ctx.getChild(3)).query());
            treeProperty.put(ctx, sourceBuilder);
        }
    }

    @Override
    public void enterParenValve(EsQueryParser.ParenValveContext ctx) {

    }

    @Override
    public void exitParenValve(EsQueryParser.ParenValveContext ctx) {
        if (ctx.getChildCount() < 2) {
            treeProperty.put(ctx, null);
        }
        valueProperty.put(ctx, valueProperty.get(ctx.getChild(1)));
    }

    @Override
    public void enterParam(EsQueryParser.ParamContext ctx) {

    }

    @Override
    public void exitParam(EsQueryParser.ParamContext ctx) {

    }

    @Override
    public void enterIpV4Value(EsQueryParser.IpV4ValueContext ctx) {

    }

    @Override
    public void exitIpV4Value(EsQueryParser.IpV4ValueContext ctx) {
        String ip = ctx.getChild(0).getText();
        //目前不支持子网掩码形式，去除掩码部分
        if (ip.contains("/")) {
            ip = ip.substring(0, ip.indexOf("/"));
        }
        int index = ip.length();
        //通配符位置获取
        if (ip.contains("*")) {
            index = Math.min(ip.indexOf("*"), index);
        }
        ip = ip.substring(0, index);
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.IPV4, ip));
    }

    @Override
    public void enterStringValue(EsQueryParser.StringValueContext ctx) {

    }

    @Override
    public void exitStringValue(EsQueryParser.StringValueContext ctx) {
        String str = ctx.getChild(0).getText();
        str = str.substring(1, str.length() - 1);
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.STRING, str));
    }

    @Override
    public void enterNumberValue(EsQueryParser.NumberValueContext ctx) {

    }

    @Override
    public void exitNumberValue(EsQueryParser.NumberValueContext ctx) {
        long number = Long.parseLong(ctx.getChild(0).getText());
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.NUMBER, number));
    }

    @Override
    public void enterTimeValue(EsQueryParser.TimeValueContext ctx) {

    }

    @Override
    public void exitTimeValue(EsQueryParser.TimeValueContext ctx) {
        String time = ctx.getChild(0).getText();
        StringBuilder res = new StringBuilder();
        if (time.length() < 20) {
            for (char c : time.toCharArray()) {
                res.append(c);
            }
            res.append(".000");
        }
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.TIME, res.toString()));
    }

    @Override
    public void enterTrueValue(EsQueryParser.TrueValueContext ctx) {

    }

    @Override
    public void exitTrueValue(EsQueryParser.TrueValueContext ctx) {
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.TRUE, Boolean.TRUE));
    }

    @Override
    public void enterFalseValue(EsQueryParser.FalseValueContext ctx) {

    }

    @Override
    public void exitFalseValue(EsQueryParser.FalseValueContext ctx) {
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.FALSE, Boolean.FALSE));
    }

    @Override
    public void enterNullValue(EsQueryParser.NullValueContext ctx) {

    }

    @Override
    public void exitNullValue(EsQueryParser.NullValueContext ctx) {
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.NULL, null));
    }

    @Override
    public void enterIdentifierValue(EsQueryParser.IdentifierValueContext ctx) {

    }

    @Override
    public void exitIdentifierValue(EsQueryParser.IdentifierValueContext ctx) {
        if (ctx.getParent() instanceof EsQueryParser.EqExprContext) {
            //属于等于下的值
            valueProperty.put(ctx, new ValueContext(ValueTypeEnum.EQUAL, ctx.getChild(0).getText()));
        } else if (ctx.getParent() instanceof EsQueryParser.NeExprContext) {
            valueProperty.put(ctx, new ValueContext(ValueTypeEnum.IDENTIFY, ctx.getChild(0).getText()));
        } else if (ctx.getParent() instanceof EsQueryParser.ArrayContext) {
            valueProperty.put(ctx, new ValueContext(ValueTypeEnum.STRING, ctx.getChild(0).getText()));
        } else {
            String value = ctx.getChild(0).getText();
            if (null == value) {
                value = valueProperty.get(ctx.children.get(1)).getValue().toString();
            }
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.queryStringQuery(value));
            treeProperty.put(ctx, sourceBuilder);
        }
    }

    @Override
    public void enterRegex(EsQueryParser.RegexContext ctx) {

    }

    @Override
    public void exitRegex(EsQueryParser.RegexContext ctx) {
        String value = ctx.getChild(0).getText();
        valueProperty.put(ctx, new ValueContext(ValueTypeEnum.REGEX, value));
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }

    public SearchSourceBuilder getBuilder(ParseTree tree) {
        return treeProperty.get(tree);
    }
}
