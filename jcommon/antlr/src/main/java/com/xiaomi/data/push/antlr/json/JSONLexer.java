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

// Generated from /Users/zhangzhiyong/IdeaProjects/jcommon/common/src/main/resources/antlr/JSON.g4 by ANTLR 4.7.1
package com.xiaomi.data.push.antlr.json;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JSONLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		STRING=10, VAR=11, ID=12, NUMBER=13, WS=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"STRING", "VAR", "ID", "ESC", "UNICODE", "HEX", "NUMBER", "DIGIT", "EXPONET", 
		"INT", "EXP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "','", "'}'", "':'", "'['", "']'", "'true'", "'false'", "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, "STRING", 
		"VAR", "ID", "NUMBER", "WS"
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


	public JSONLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "JSON.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\20\u00a3\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\7\13M\n\13\f\13\16\13P\13\13\3\13\3"+
		"\13\3\f\3\f\3\f\3\r\6\rX\n\r\r\r\16\rY\3\16\3\16\3\16\5\16_\n\16\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\21\5\21j\n\21\3\21\3\21\3\21\6\21"+
		"o\n\21\r\21\16\21p\3\21\5\21t\n\21\3\21\5\21w\n\21\3\21\3\21\3\21\3\21"+
		"\5\21}\n\21\3\21\5\21\u0080\n\21\3\22\3\22\3\23\3\23\5\23\u0086\n\23\3"+
		"\23\6\23\u0089\n\23\r\23\16\23\u008a\3\24\3\24\3\24\7\24\u0090\n\24\f"+
		"\24\16\24\u0093\13\24\5\24\u0095\n\24\3\25\3\25\5\25\u0099\n\25\3\25\3"+
		"\25\3\26\6\26\u009e\n\26\r\26\16\26\u009f\3\26\3\26\2\2\27\3\3\5\4\7\5"+
		"\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\2\35\2\37\2!\17#\2%\2"+
		"\'\2)\2+\20\3\2\13\4\2$$^^\4\2C\\c|\n\2$$\61\61^^ddhhppttvv\5\2\62;CH"+
		"ch\3\2\62;\4\2GGgg\4\2--//\3\2\63;\5\2\13\f\17\17\"\"\2\u00ac\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2!\3\2\2\2\2+\3\2\2\2\3-\3\2\2\2\5/\3\2\2\2\7\61\3\2\2\2\t\63\3\2"+
		"\2\2\13\65\3\2\2\2\r\67\3\2\2\2\179\3\2\2\2\21>\3\2\2\2\23D\3\2\2\2\25"+
		"I\3\2\2\2\27S\3\2\2\2\31W\3\2\2\2\33[\3\2\2\2\35`\3\2\2\2\37f\3\2\2\2"+
		"!\177\3\2\2\2#\u0081\3\2\2\2%\u0083\3\2\2\2\'\u0094\3\2\2\2)\u0096\3\2"+
		"\2\2+\u009d\3\2\2\2-.\7}\2\2.\4\3\2\2\2/\60\7.\2\2\60\6\3\2\2\2\61\62"+
		"\7\177\2\2\62\b\3\2\2\2\63\64\7<\2\2\64\n\3\2\2\2\65\66\7]\2\2\66\f\3"+
		"\2\2\2\678\7_\2\28\16\3\2\2\29:\7v\2\2:;\7t\2\2;<\7w\2\2<=\7g\2\2=\20"+
		"\3\2\2\2>?\7h\2\2?@\7c\2\2@A\7n\2\2AB\7u\2\2BC\7g\2\2C\22\3\2\2\2DE\7"+
		"p\2\2EF\7w\2\2FG\7n\2\2GH\7n\2\2H\24\3\2\2\2IN\7$\2\2JM\5\33\16\2KM\n"+
		"\2\2\2LJ\3\2\2\2LK\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OQ\3\2\2\2PN\3"+
		"\2\2\2QR\7$\2\2R\26\3\2\2\2ST\7&\2\2TU\5\31\r\2U\30\3\2\2\2VX\t\3\2\2"+
		"WV\3\2\2\2XY\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\32\3\2\2\2[^\7^\2\2\\_\t\4\2"+
		"\2]_\5\35\17\2^\\\3\2\2\2^]\3\2\2\2_\34\3\2\2\2`a\7w\2\2ab\5\37\20\2b"+
		"c\5\37\20\2cd\5\37\20\2de\5\37\20\2e\36\3\2\2\2fg\t\5\2\2g \3\2\2\2hj"+
		"\7/\2\2ih\3\2\2\2ij\3\2\2\2jk\3\2\2\2kl\5\'\24\2ln\7\60\2\2mo\t\6\2\2"+
		"nm\3\2\2\2op\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rt\5)\25\2sr\3\2\2\2"+
		"st\3\2\2\2t\u0080\3\2\2\2uw\7/\2\2vu\3\2\2\2vw\3\2\2\2wx\3\2\2\2xy\5\'"+
		"\24\2yz\5)\25\2z\u0080\3\2\2\2{}\7/\2\2|{\3\2\2\2|}\3\2\2\2}~\3\2\2\2"+
		"~\u0080\5\'\24\2\177i\3\2\2\2\177v\3\2\2\2\177|\3\2\2\2\u0080\"\3\2\2"+
		"\2\u0081\u0082\4\62;\2\u0082$\3\2\2\2\u0083\u0085\t\7\2\2\u0084\u0086"+
		"\t\b\2\2\u0085\u0084\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0088\3\2\2\2\u0087"+
		"\u0089\5#\22\2\u0088\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u0088\3\2"+
		"\2\2\u008a\u008b\3\2\2\2\u008b&\3\2\2\2\u008c\u0095\7\62\2\2\u008d\u0091"+
		"\t\t\2\2\u008e\u0090\t\6\2\2\u008f\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091"+
		"\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2"+
		"\2\2\u0094\u008c\3\2\2\2\u0094\u008d\3\2\2\2\u0095(\3\2\2\2\u0096\u0098"+
		"\t\7\2\2\u0097\u0099\t\b\2\2\u0098\u0097\3\2\2\2\u0098\u0099\3\2\2\2\u0099"+
		"\u009a\3\2\2\2\u009a\u009b\5\'\24\2\u009b*\3\2\2\2\u009c\u009e\t\n\2\2"+
		"\u009d\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0"+
		"\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\b\26\2\2\u00a2,\3\2\2\2\23\2"+
		"LNY^ipsv|\177\u0085\u008a\u0091\u0094\u0098\u009f\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}