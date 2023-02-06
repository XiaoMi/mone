// Generated from Expr.g4 by ANTLR 4.7.1
package com.xiaomi.data.push.antlr.expr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExprLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, ID=7, INT=8, NEWLINE=9, 
		WS=10, LINE_COMMENT=11, COMMENT=12, MUL=13, DIV=14, ADD=15, SUB=16, Ju=17, 
		LP=18, RP=19;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "ID", "INT", "NEWLINE", 
		"WS", "LINE_COMMENT", "COMMENT", "MUL", "DIV", "ADD", "SUB", "Ju", "LP", 
		"RP"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'['", "']'", "'{'", "'}'", "','", "':'", null, null, null, null, 
		null, null, "'*'", "'/'", "'+'", "'-'", "'.'", "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, "ID", "INT", "NEWLINE", "WS", 
		"LINE_COMMENT", "COMMENT", "MUL", "DIV", "ADD", "SUB", "Ju", "LP", "RP"
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


	public ExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Expr.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\25t\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\6\b\67\n\b\r\b\16\b8\3\t\6\t<\n\t\r\t\16\t=\3\n\5\nA\n\n\3\n\3\n\3"+
		"\13\6\13F\n\13\r\13\16\13G\3\13\3\13\3\f\3\f\3\f\3\f\7\fP\n\f\f\f\16\f"+
		"S\13\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\7\r]\n\r\f\r\16\r`\13\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3"+
		"\23\3\24\3\24\4Q^\2\25\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25\3\2\5\6\2\62;C\\aac|\3\2\62"+
		";\5\2\13\f\17\17\"\"\2y\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\3)\3\2\2\2\5+\3\2\2"+
		"\2\7-\3\2\2\2\t/\3\2\2\2\13\61\3\2\2\2\r\63\3\2\2\2\17\66\3\2\2\2\21;"+
		"\3\2\2\2\23@\3\2\2\2\25E\3\2\2\2\27K\3\2\2\2\31X\3\2\2\2\33f\3\2\2\2\35"+
		"h\3\2\2\2\37j\3\2\2\2!l\3\2\2\2#n\3\2\2\2%p\3\2\2\2\'r\3\2\2\2)*\7]\2"+
		"\2*\4\3\2\2\2+,\7_\2\2,\6\3\2\2\2-.\7}\2\2.\b\3\2\2\2/\60\7\177\2\2\60"+
		"\n\3\2\2\2\61\62\7.\2\2\62\f\3\2\2\2\63\64\7<\2\2\64\16\3\2\2\2\65\67"+
		"\t\2\2\2\66\65\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2\29\20\3\2\2\2:"+
		"<\t\3\2\2;:\3\2\2\2<=\3\2\2\2=;\3\2\2\2=>\3\2\2\2>\22\3\2\2\2?A\7\17\2"+
		"\2@?\3\2\2\2@A\3\2\2\2AB\3\2\2\2BC\7\f\2\2C\24\3\2\2\2DF\t\4\2\2ED\3\2"+
		"\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2\2HI\3\2\2\2IJ\b\13\2\2J\26\3\2\2\2KL"+
		"\7\61\2\2LM\7\61\2\2MQ\3\2\2\2NP\13\2\2\2ON\3\2\2\2PS\3\2\2\2QR\3\2\2"+
		"\2QO\3\2\2\2RT\3\2\2\2SQ\3\2\2\2TU\7\f\2\2UV\3\2\2\2VW\b\f\2\2W\30\3\2"+
		"\2\2XY\7\61\2\2YZ\7,\2\2Z^\3\2\2\2[]\13\2\2\2\\[\3\2\2\2]`\3\2\2\2^_\3"+
		"\2\2\2^\\\3\2\2\2_a\3\2\2\2`^\3\2\2\2ab\7,\2\2bc\7\61\2\2cd\3\2\2\2de"+
		"\b\r\2\2e\32\3\2\2\2fg\7,\2\2g\34\3\2\2\2hi\7\61\2\2i\36\3\2\2\2jk\7-"+
		"\2\2k \3\2\2\2lm\7/\2\2m\"\3\2\2\2no\7\60\2\2o$\3\2\2\2pq\7*\2\2q&\3\2"+
		"\2\2rs\7+\2\2s(\3\2\2\2\t\28=@GQ^\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}