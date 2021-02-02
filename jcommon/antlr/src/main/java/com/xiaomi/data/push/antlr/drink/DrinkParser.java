/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

// Generated from Drink.g4 by ANTLR 4.7.1
package com.xiaomi.data.push.antlr.drink;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DrinkParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ARTICLE=1, OF=2, DRINKING_VESSEL=3, TEXT=4, WHITESPACE=5;
	public static final int
		RULE_drinkSentence = 0, RULE_drink = 1;
	public static final String[] ruleNames = {
		"drinkSentence", "drink"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, "'of'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "ARTICLE", "OF", "DRINKING_VESSEL", "TEXT", "WHITESPACE"
	};
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
	public String getGrammarFileName() { return "Drink.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DrinkParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DrinkSentenceContext extends ParserRuleContext {
		public TerminalNode DRINKING_VESSEL() { return getToken(DrinkParser.DRINKING_VESSEL, 0); }
		public TerminalNode OF() { return getToken(DrinkParser.OF, 0); }
		public DrinkContext drink() {
			return getRuleContext(DrinkContext.class,0);
		}
		public TerminalNode ARTICLE() { return getToken(DrinkParser.ARTICLE, 0); }
		public DrinkSentenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drinkSentence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DrinkListener) ((DrinkListener)listener).enterDrinkSentence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DrinkListener) ((DrinkListener)listener).exitDrinkSentence(this);
		}
	}

	public final DrinkSentenceContext drinkSentence() throws RecognitionException {
		DrinkSentenceContext _localctx = new DrinkSentenceContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_drinkSentence);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(5);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ARTICLE) {
				{
				setState(4);
				match(ARTICLE);
				}
			}

			setState(7);
			match(DRINKING_VESSEL);
			setState(8);
			match(OF);
			setState(9);
			drink();
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

	public static class DrinkContext extends ParserRuleContext {
		public TerminalNode TEXT() { return getToken(DrinkParser.TEXT, 0); }
		public DrinkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_drink; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DrinkListener) ((DrinkListener)listener).enterDrink(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DrinkListener) ((DrinkListener)listener).exitDrink(this);
		}
	}

	public final DrinkContext drink() throws RecognitionException {
		DrinkContext _localctx = new DrinkContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_drink);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(11);
			match(TEXT);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\7\20\4\2\t\2\4\3"+
		"\t\3\3\2\5\2\b\n\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\2\2\4\2\4\2\2\2\16\2\7"+
		"\3\2\2\2\4\r\3\2\2\2\6\b\7\3\2\2\7\6\3\2\2\2\7\b\3\2\2\2\b\t\3\2\2\2\t"+
		"\n\7\5\2\2\n\13\7\4\2\2\13\f\5\4\3\2\f\3\3\2\2\2\r\16\7\6\2\2\16\5\3\2"+
		"\2\2\3\7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}