// Generated from D:/code/open_mone/mone/jcommon/docean-plugin/docean-plugin-es-antlr4/src/main/java/com/xiaomi/youpin/docean/plugin/es/antlr4/g4\EsQuery.g4 by ANTLR 4.12.0
package com.xiaomi.youpin.docean.plugin.es.antlr4.query;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class EsQueryParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.12.0", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, IPv4=9, 
		STRING=10, REGEX=11, AND=12, OR=13, EQ=14, NE=15, LT=16, GT=17, LE=18, 
		GE=19, REG=20, LIKE=21, IN=22, NOT_IN=23, EXIST=24, NOT_EXIST=25, NOT=26, 
		DOT=27, CONTAIN=28, NOTCONTAIN=29, MAX=30, MIN=31, SUM=32, AVG=33, GROUP=34, 
		IDENTIFIER=35, TIME=36, NUMBER=37, SEGMENT=38, WS=39;
	public static final int
		RULE_parse = 0, RULE_expression = 1, RULE_expr = 2, RULE_array = 3, RULE_aggexpr = 4, 
		RULE_parenValve = 5, RULE_param = 6, RULE_value = 7, RULE_regex = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "expression", "expr", "array", "aggexpr", "parenValve", "param", 
			"value", "regex"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'['", "']'", "','", "'true'", "'false'", "'null'", 
			null, null, null, null, null, "':'", "'!='", "'<'", "'>'", "'<='", "'>='", 
			"':~'", null, null, null, null, null, null, "'.'", null, null, "'max'", 
			"'min'", "'sum'", "'avg'", "'group'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "IPv4", "STRING", 
			"REGEX", "AND", "OR", "EQ", "NE", "LT", "GT", "LE", "GE", "REG", "LIKE", 
			"IN", "NOT_IN", "EXIST", "NOT_EXIST", "NOT", "DOT", "CONTAIN", "NOTCONTAIN", 
			"MAX", "MIN", "SUM", "AVG", "GROUP", "IDENTIFIER", "TIME", "NUMBER", 
			"SEGMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "EsQuery.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public EsQueryParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParseContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(EsQueryParser.EOF, 0); }
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitParse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			expression(0);
			setState(19);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OrExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode OR() { return getToken(EsQueryParser.OR, 0); }
		public OrExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterOrExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitOrExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitOrExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AndExpressionContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode AND() { return getToken(EsQueryParser.AND, 0); }
		public AndExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterAndExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitAndExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitAndExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AggreExpressionContext extends ExpressionContext {
		public AggexprContext aggexpr() {
			return getRuleContext(AggexprContext.class,0);
		}
		public AggreExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterAggreExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitAggreExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitAggreExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotExpressionContext extends ExpressionContext {
		public TerminalNode NOT() { return getToken(EsQueryParser.NOT, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public NotExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterNotExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitNotExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitNotExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExpressionContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParenExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterParenExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitParenExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitParenExpression(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CommonExpressionContext extends ExpressionContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public CommonExpressionContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterCommonExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitCommonExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitCommonExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_expression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				_localctx = new ParenExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(22);
				match(T__0);
				setState(23);
				expression(0);
				setState(24);
				match(T__1);
				}
				break;
			case 2:
				{
				_localctx = new AggreExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(26);
				aggexpr();
				}
				break;
			case 3:
				{
				_localctx = new NotExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(27);
				match(NOT);
				setState(28);
				expression(3);
				}
				break;
			case 4:
				{
				_localctx = new CommonExpressionContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(29);
				expr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(46);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(44);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new AndExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(32);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(33);
						match(AND);
						setState(34);
						expression(7);
						}
						break;
					case 2:
						{
						_localctx = new AndExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(35);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(36);
						match(AND);
						setState(37);
						expression(6);
						}
						break;
					case 3:
						{
						_localctx = new OrExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(38);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(39);
						match(OR);
						setState(40);
						expression(5);
						}
						break;
					case 4:
						{
						_localctx = new NotExpressionContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(41);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(42);
						match(NOT);
						setState(43);
						expression(3);
						}
						break;
					}
					} 
				}
				setState(48);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LikeExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode LIKE() { return getToken(EsQueryParser.LIKE, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public LikeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterLikeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitLikeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitLikeExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExistExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode EXIST() { return getToken(EsQueryParser.EXIST, 0); }
		public ExistExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterExistExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitExistExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitExistExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RegexExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode REG() { return getToken(EsQueryParser.REG, 0); }
		public RegexContext regex() {
			return getRuleContext(RegexContext.class,0);
		}
		public RegexExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterRegexExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitRegexExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitRegexExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NeExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode NE() { return getToken(EsQueryParser.NE, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public NeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterNeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitNeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitNeExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotExistExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode NOT_EXIST() { return getToken(EsQueryParser.NOT_EXIST, 0); }
		public NotExistExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterNotExistExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitNotExistExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitNotExistExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LtExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode LT() { return getToken(EsQueryParser.LT, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public LtExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterLtExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitLtExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitLtExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GtExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode GT() { return getToken(EsQueryParser.GT, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public GtExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterGtExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitGtExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitGtExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GeExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode GE() { return getToken(EsQueryParser.GE, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public GeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterGeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitGeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitGeExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotContainExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode NOTCONTAIN() { return getToken(EsQueryParser.NOTCONTAIN, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public NotContainExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterNotContainExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitNotContainExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitNotContainExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LeExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode LE() { return getToken(EsQueryParser.LE, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public LeExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterLeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitLeExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitLeExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ContainExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode CONTAIN() { return getToken(EsQueryParser.CONTAIN, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ContainExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterContainExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitContainExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitContainExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EqExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode EQ() { return getToken(EsQueryParser.EQ, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public EqExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterEqExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitEqExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitEqExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode IN() { return getToken(EsQueryParser.IN, 0); }
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public InExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterInExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitInExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitInExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NotInExprContext extends ExprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode NOT_IN() { return getToken(EsQueryParser.NOT_IN, 0); }
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public NotInExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterNotInExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitNotInExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitNotInExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_expr);
		try {
			setState(105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new LtExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(49);
				param();
				setState(50);
				match(LT);
				setState(51);
				value();
				}
				break;
			case 2:
				_localctx = new GtExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(53);
				param();
				setState(54);
				match(GT);
				setState(55);
				value();
				}
				break;
			case 3:
				_localctx = new LeExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(57);
				param();
				setState(58);
				match(LE);
				setState(59);
				value();
				}
				break;
			case 4:
				_localctx = new GeExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(61);
				param();
				setState(62);
				match(GE);
				setState(63);
				value();
				}
				break;
			case 5:
				_localctx = new NeExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(65);
				param();
				setState(66);
				match(NE);
				setState(67);
				value();
				}
				break;
			case 6:
				_localctx = new EqExprContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(69);
				param();
				setState(70);
				match(EQ);
				setState(71);
				value();
				}
				break;
			case 7:
				_localctx = new LikeExprContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(73);
				param();
				setState(74);
				match(LIKE);
				setState(75);
				value();
				}
				break;
			case 8:
				_localctx = new ContainExprContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(77);
				param();
				setState(78);
				match(CONTAIN);
				setState(79);
				value();
				}
				break;
			case 9:
				_localctx = new NotContainExprContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(81);
				param();
				setState(82);
				match(NOTCONTAIN);
				setState(83);
				value();
				}
				break;
			case 10:
				_localctx = new InExprContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(85);
				param();
				setState(86);
				match(IN);
				setState(87);
				array();
				}
				break;
			case 11:
				_localctx = new NotInExprContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(89);
				param();
				setState(90);
				match(NOT_IN);
				setState(91);
				array();
				}
				break;
			case 12:
				_localctx = new ExistExprContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(93);
				param();
				setState(94);
				match(EXIST);
				}
				break;
			case 13:
				_localctx = new NotExistExprContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(96);
				param();
				setState(97);
				match(NOT_EXIST);
				}
				break;
			case 14:
				_localctx = new RegexExprContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(99);
				param();
				setState(100);
				match(REG);
				setState(101);
				regex();
				}
				break;
			case 15:
				_localctx = new EqExprContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(103);
				param();
				}
				break;
			case 16:
				_localctx = new EqExprContext(_localctx);
				enterOuterAlt(_localctx, 16);
				{
				setState(104);
				value();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayContext extends ParserRuleContext {
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_array);
		int _la;
		try {
			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(107);
				match(T__2);
				setState(108);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(109);
				match(T__2);
				setState(110);
				value();
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__4) {
					{
					{
					setState(111);
					match(T__4);
					setState(112);
					value();
					}
					}
					setState(117);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(118);
				match(T__3);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AggexprContext extends ParserRuleContext {
		public AggexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggexpr; }
	 
		public AggexprContext() { }
		public void copyFrom(AggexprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MaxAggExprContext extends AggexprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode DOT() { return getToken(EsQueryParser.DOT, 0); }
		public TerminalNode MAX() { return getToken(EsQueryParser.MAX, 0); }
		public ParenValveContext parenValve() {
			return getRuleContext(ParenValveContext.class,0);
		}
		public MaxAggExprContext(AggexprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterMaxAggExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitMaxAggExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitMaxAggExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class GroupAggExprContext extends AggexprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode DOT() { return getToken(EsQueryParser.DOT, 0); }
		public TerminalNode GROUP() { return getToken(EsQueryParser.GROUP, 0); }
		public ParenValveContext parenValve() {
			return getRuleContext(ParenValveContext.class,0);
		}
		public GroupAggExprContext(AggexprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterGroupAggExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitGroupAggExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitGroupAggExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MinAggExprContext extends AggexprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode DOT() { return getToken(EsQueryParser.DOT, 0); }
		public TerminalNode MIN() { return getToken(EsQueryParser.MIN, 0); }
		public ParenValveContext parenValve() {
			return getRuleContext(ParenValveContext.class,0);
		}
		public MinAggExprContext(AggexprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterMinAggExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitMinAggExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitMinAggExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AvgAggExprContext extends AggexprContext {
		public ParamContext param() {
			return getRuleContext(ParamContext.class,0);
		}
		public TerminalNode DOT() { return getToken(EsQueryParser.DOT, 0); }
		public TerminalNode AVG() { return getToken(EsQueryParser.AVG, 0); }
		public ParenValveContext parenValve() {
			return getRuleContext(ParenValveContext.class,0);
		}
		public AvgAggExprContext(AggexprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterAvgAggExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitAvgAggExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitAvgAggExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggexprContext aggexpr() throws RecognitionException {
		AggexprContext _localctx = new AggexprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_aggexpr);
		try {
			setState(142);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				_localctx = new MaxAggExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(122);
				param();
				setState(123);
				match(DOT);
				setState(124);
				match(MAX);
				setState(125);
				parenValve();
				}
				break;
			case 2:
				_localctx = new MinAggExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(127);
				param();
				setState(128);
				match(DOT);
				setState(129);
				match(MIN);
				setState(130);
				parenValve();
				}
				break;
			case 3:
				_localctx = new AvgAggExprContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(132);
				param();
				setState(133);
				match(DOT);
				setState(134);
				match(AVG);
				setState(135);
				parenValve();
				}
				break;
			case 4:
				_localctx = new GroupAggExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(137);
				param();
				setState(138);
				match(DOT);
				setState(139);
				match(GROUP);
				setState(140);
				parenValve();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParenValveContext extends ParserRuleContext {
		public AggexprContext aggexpr() {
			return getRuleContext(AggexprContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ParenValveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parenValve; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterParenValve(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitParenValve(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitParenValve(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParenValveContext parenValve() throws RecognitionException {
		ParenValveContext _localctx = new ParenValveContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_parenValve);
		try {
			setState(158);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				match(T__0);
				setState(145);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(146);
				match(T__0);
				setState(147);
				aggexpr();
				setState(148);
				match(T__1);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(150);
				match(T__0);
				setState(151);
				expression(0);
				setState(152);
				match(T__1);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(154);
				match(T__0);
				setState(155);
				value();
				setState(156);
				match(T__1);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParamContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(EsQueryParser.IDENTIFIER, 0); }
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ValueContext extends ParserRuleContext {
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
	 
		public ValueContext() { }
		public void copyFrom(ValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NullValueContext extends ValueContext {
		public NullValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterNullValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitNullValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitNullValue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NumberValueContext extends ValueContext {
		public TerminalNode NUMBER() { return getToken(EsQueryParser.NUMBER, 0); }
		public NumberValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterNumberValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitNumberValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitNumberValue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TrueValueContext extends ValueContext {
		public TrueValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterTrueValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitTrueValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitTrueValue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IpV4ValueContext extends ValueContext {
		public TerminalNode IPv4() { return getToken(EsQueryParser.IPv4, 0); }
		public IpV4ValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterIpV4Value(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitIpV4Value(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitIpV4Value(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierValueContext extends ValueContext {
		public TerminalNode IDENTIFIER() { return getToken(EsQueryParser.IDENTIFIER, 0); }
		public IdentifierValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterIdentifierValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitIdentifierValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitIdentifierValue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringValueContext extends ValueContext {
		public TerminalNode STRING() { return getToken(EsQueryParser.STRING, 0); }
		public StringValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterStringValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitStringValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitStringValue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TimeValueContext extends ValueContext {
		public TerminalNode TIME() { return getToken(EsQueryParser.TIME, 0); }
		public TimeValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterTimeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitTimeValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitTimeValue(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FalseValueContext extends ValueContext {
		public FalseValueContext(ValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterFalseValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitFalseValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitFalseValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_value);
		try {
			setState(170);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IPv4:
				_localctx = new IpV4ValueContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(162);
				match(IPv4);
				}
				break;
			case STRING:
				_localctx = new StringValueContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(163);
				match(STRING);
				}
				break;
			case NUMBER:
				_localctx = new NumberValueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(164);
				match(NUMBER);
				}
				break;
			case TIME:
				_localctx = new TimeValueContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(165);
				match(TIME);
				}
				break;
			case T__5:
				_localctx = new TrueValueContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(166);
				match(T__5);
				}
				break;
			case T__6:
				_localctx = new FalseValueContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(167);
				match(T__6);
				}
				break;
			case T__7:
				_localctx = new NullValueContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(168);
				match(T__7);
				}
				break;
			case IDENTIFIER:
				_localctx = new IdentifierValueContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(169);
				match(IDENTIFIER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RegexContext extends ParserRuleContext {
		public TerminalNode REGEX() { return getToken(EsQueryParser.REGEX, 0); }
		public RegexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_regex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).enterRegex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof EsQueryListener ) ((EsQueryListener)listener).exitRegex(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof EsQueryVisitor ) return ((EsQueryVisitor<? extends T>)visitor).visitRegex(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RegexContext regex() throws RecognitionException {
		RegexContext _localctx = new RegexContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_regex);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(REGEX);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 6);
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\'\u00af\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001\u001f\b\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001-\b\u0001\n\u0001\f\u0001"+
		"0\t\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002j\b\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003"+
		"r\b\u0003\n\u0003\f\u0003u\t\u0003\u0001\u0003\u0001\u0003\u0003\u0003"+
		"y\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u008f\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005\u009f\b\u0005\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0003\u0007\u00ab\b\u0007\u0001\b\u0001\b\u0001\b\u0000\u0001"+
		"\u0002\t\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0000\u0000\u00ca\u0000"+
		"\u0012\u0001\u0000\u0000\u0000\u0002\u001e\u0001\u0000\u0000\u0000\u0004"+
		"i\u0001\u0000\u0000\u0000\u0006x\u0001\u0000\u0000\u0000\b\u008e\u0001"+
		"\u0000\u0000\u0000\n\u009e\u0001\u0000\u0000\u0000\f\u00a0\u0001\u0000"+
		"\u0000\u0000\u000e\u00aa\u0001\u0000\u0000\u0000\u0010\u00ac\u0001\u0000"+
		"\u0000\u0000\u0012\u0013\u0003\u0002\u0001\u0000\u0013\u0014\u0005\u0000"+
		"\u0000\u0001\u0014\u0001\u0001\u0000\u0000\u0000\u0015\u0016\u0006\u0001"+
		"\uffff\uffff\u0000\u0016\u0017\u0005\u0001\u0000\u0000\u0017\u0018\u0003"+
		"\u0002\u0001\u0000\u0018\u0019\u0005\u0002\u0000\u0000\u0019\u001f\u0001"+
		"\u0000\u0000\u0000\u001a\u001f\u0003\b\u0004\u0000\u001b\u001c\u0005\u001a"+
		"\u0000\u0000\u001c\u001f\u0003\u0002\u0001\u0003\u001d\u001f\u0003\u0004"+
		"\u0002\u0000\u001e\u0015\u0001\u0000\u0000\u0000\u001e\u001a\u0001\u0000"+
		"\u0000\u0000\u001e\u001b\u0001\u0000\u0000\u0000\u001e\u001d\u0001\u0000"+
		"\u0000\u0000\u001f.\u0001\u0000\u0000\u0000 !\n\u0006\u0000\u0000!\"\u0005"+
		"\f\u0000\u0000\"-\u0003\u0002\u0001\u0007#$\n\u0005\u0000\u0000$%\u0005"+
		"\f\u0000\u0000%-\u0003\u0002\u0001\u0006&\'\n\u0004\u0000\u0000\'(\u0005"+
		"\r\u0000\u0000(-\u0003\u0002\u0001\u0005)*\n\u0002\u0000\u0000*+\u0005"+
		"\u001a\u0000\u0000+-\u0003\u0002\u0001\u0003, \u0001\u0000\u0000\u0000"+
		",#\u0001\u0000\u0000\u0000,&\u0001\u0000\u0000\u0000,)\u0001\u0000\u0000"+
		"\u0000-0\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000./\u0001\u0000"+
		"\u0000\u0000/\u0003\u0001\u0000\u0000\u00000.\u0001\u0000\u0000\u0000"+
		"12\u0003\f\u0006\u000023\u0005\u0010\u0000\u000034\u0003\u000e\u0007\u0000"+
		"4j\u0001\u0000\u0000\u000056\u0003\f\u0006\u000067\u0005\u0011\u0000\u0000"+
		"78\u0003\u000e\u0007\u00008j\u0001\u0000\u0000\u00009:\u0003\f\u0006\u0000"+
		":;\u0005\u0012\u0000\u0000;<\u0003\u000e\u0007\u0000<j\u0001\u0000\u0000"+
		"\u0000=>\u0003\f\u0006\u0000>?\u0005\u0013\u0000\u0000?@\u0003\u000e\u0007"+
		"\u0000@j\u0001\u0000\u0000\u0000AB\u0003\f\u0006\u0000BC\u0005\u000f\u0000"+
		"\u0000CD\u0003\u000e\u0007\u0000Dj\u0001\u0000\u0000\u0000EF\u0003\f\u0006"+
		"\u0000FG\u0005\u000e\u0000\u0000GH\u0003\u000e\u0007\u0000Hj\u0001\u0000"+
		"\u0000\u0000IJ\u0003\f\u0006\u0000JK\u0005\u0015\u0000\u0000KL\u0003\u000e"+
		"\u0007\u0000Lj\u0001\u0000\u0000\u0000MN\u0003\f\u0006\u0000NO\u0005\u001c"+
		"\u0000\u0000OP\u0003\u000e\u0007\u0000Pj\u0001\u0000\u0000\u0000QR\u0003"+
		"\f\u0006\u0000RS\u0005\u001d\u0000\u0000ST\u0003\u000e\u0007\u0000Tj\u0001"+
		"\u0000\u0000\u0000UV\u0003\f\u0006\u0000VW\u0005\u0016\u0000\u0000WX\u0003"+
		"\u0006\u0003\u0000Xj\u0001\u0000\u0000\u0000YZ\u0003\f\u0006\u0000Z[\u0005"+
		"\u0017\u0000\u0000[\\\u0003\u0006\u0003\u0000\\j\u0001\u0000\u0000\u0000"+
		"]^\u0003\f\u0006\u0000^_\u0005\u0018\u0000\u0000_j\u0001\u0000\u0000\u0000"+
		"`a\u0003\f\u0006\u0000ab\u0005\u0019\u0000\u0000bj\u0001\u0000\u0000\u0000"+
		"cd\u0003\f\u0006\u0000de\u0005\u0014\u0000\u0000ef\u0003\u0010\b\u0000"+
		"fj\u0001\u0000\u0000\u0000gj\u0003\f\u0006\u0000hj\u0003\u000e\u0007\u0000"+
		"i1\u0001\u0000\u0000\u0000i5\u0001\u0000\u0000\u0000i9\u0001\u0000\u0000"+
		"\u0000i=\u0001\u0000\u0000\u0000iA\u0001\u0000\u0000\u0000iE\u0001\u0000"+
		"\u0000\u0000iI\u0001\u0000\u0000\u0000iM\u0001\u0000\u0000\u0000iQ\u0001"+
		"\u0000\u0000\u0000iU\u0001\u0000\u0000\u0000iY\u0001\u0000\u0000\u0000"+
		"i]\u0001\u0000\u0000\u0000i`\u0001\u0000\u0000\u0000ic\u0001\u0000\u0000"+
		"\u0000ig\u0001\u0000\u0000\u0000ih\u0001\u0000\u0000\u0000j\u0005\u0001"+
		"\u0000\u0000\u0000kl\u0005\u0003\u0000\u0000ly\u0005\u0004\u0000\u0000"+
		"mn\u0005\u0003\u0000\u0000ns\u0003\u000e\u0007\u0000op\u0005\u0005\u0000"+
		"\u0000pr\u0003\u000e\u0007\u0000qo\u0001\u0000\u0000\u0000ru\u0001\u0000"+
		"\u0000\u0000sq\u0001\u0000\u0000\u0000st\u0001\u0000\u0000\u0000tv\u0001"+
		"\u0000\u0000\u0000us\u0001\u0000\u0000\u0000vw\u0005\u0004\u0000\u0000"+
		"wy\u0001\u0000\u0000\u0000xk\u0001\u0000\u0000\u0000xm\u0001\u0000\u0000"+
		"\u0000y\u0007\u0001\u0000\u0000\u0000z{\u0003\f\u0006\u0000{|\u0005\u001b"+
		"\u0000\u0000|}\u0005\u001e\u0000\u0000}~\u0003\n\u0005\u0000~\u008f\u0001"+
		"\u0000\u0000\u0000\u007f\u0080\u0003\f\u0006\u0000\u0080\u0081\u0005\u001b"+
		"\u0000\u0000\u0081\u0082\u0005\u001f\u0000\u0000\u0082\u0083\u0003\n\u0005"+
		"\u0000\u0083\u008f\u0001\u0000\u0000\u0000\u0084\u0085\u0003\f\u0006\u0000"+
		"\u0085\u0086\u0005\u001b\u0000\u0000\u0086\u0087\u0005!\u0000\u0000\u0087"+
		"\u0088\u0003\n\u0005\u0000\u0088\u008f\u0001\u0000\u0000\u0000\u0089\u008a"+
		"\u0003\f\u0006\u0000\u008a\u008b\u0005\u001b\u0000\u0000\u008b\u008c\u0005"+
		"\"\u0000\u0000\u008c\u008d\u0003\n\u0005\u0000\u008d\u008f\u0001\u0000"+
		"\u0000\u0000\u008ez\u0001\u0000\u0000\u0000\u008e\u007f\u0001\u0000\u0000"+
		"\u0000\u008e\u0084\u0001\u0000\u0000\u0000\u008e\u0089\u0001\u0000\u0000"+
		"\u0000\u008f\t\u0001\u0000\u0000\u0000\u0090\u0091\u0005\u0001\u0000\u0000"+
		"\u0091\u009f\u0005\u0002\u0000\u0000\u0092\u0093\u0005\u0001\u0000\u0000"+
		"\u0093\u0094\u0003\b\u0004\u0000\u0094\u0095\u0005\u0002\u0000\u0000\u0095"+
		"\u009f\u0001\u0000\u0000\u0000\u0096\u0097\u0005\u0001\u0000\u0000\u0097"+
		"\u0098\u0003\u0002\u0001\u0000\u0098\u0099\u0005\u0002\u0000\u0000\u0099"+
		"\u009f\u0001\u0000\u0000\u0000\u009a\u009b\u0005\u0001\u0000\u0000\u009b"+
		"\u009c\u0003\u000e\u0007\u0000\u009c\u009d\u0005\u0002\u0000\u0000\u009d"+
		"\u009f\u0001\u0000\u0000\u0000\u009e\u0090\u0001\u0000\u0000\u0000\u009e"+
		"\u0092\u0001\u0000\u0000\u0000\u009e\u0096\u0001\u0000\u0000\u0000\u009e"+
		"\u009a\u0001\u0000\u0000\u0000\u009f\u000b\u0001\u0000\u0000\u0000\u00a0"+
		"\u00a1\u0005#\u0000\u0000\u00a1\r\u0001\u0000\u0000\u0000\u00a2\u00ab"+
		"\u0005\t\u0000\u0000\u00a3\u00ab\u0005\n\u0000\u0000\u00a4\u00ab\u0005"+
		"%\u0000\u0000\u00a5\u00ab\u0005$\u0000\u0000\u00a6\u00ab\u0005\u0006\u0000"+
		"\u0000\u00a7\u00ab\u0005\u0007\u0000\u0000\u00a8\u00ab\u0005\b\u0000\u0000"+
		"\u00a9\u00ab\u0005#\u0000\u0000\u00aa\u00a2\u0001\u0000\u0000\u0000\u00aa"+
		"\u00a3\u0001\u0000\u0000\u0000\u00aa\u00a4\u0001\u0000\u0000\u0000\u00aa"+
		"\u00a5\u0001\u0000\u0000\u0000\u00aa\u00a6\u0001\u0000\u0000\u0000\u00aa"+
		"\u00a7\u0001\u0000\u0000\u0000\u00aa\u00a8\u0001\u0000\u0000\u0000\u00aa"+
		"\u00a9\u0001\u0000\u0000\u0000\u00ab\u000f\u0001\u0000\u0000\u0000\u00ac"+
		"\u00ad\u0005\u000b\u0000\u0000\u00ad\u0011\u0001\u0000\u0000\u0000\t\u001e"+
		",.isx\u008e\u009e\u00aa";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}