package run.mone.antlr.golang;// Generated from GoParser.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GoParser extends GoParserBase {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BREAK=1, DEFAULT=2, FUNC=3, INTERFACE=4, SELECT=5, CASE=6, DEFER=7, GO=8, 
		MAP=9, STRUCT=10, CHAN=11, ELSE=12, GOTO=13, PACKAGE=14, SWITCH=15, CONST=16, 
		FALLTHROUGH=17, IF=18, RANGE=19, TYPE=20, CONTINUE=21, FOR=22, IMPORT=23, 
		RETURN=24, VAR=25, NIL_LIT=26, IDENTIFIER=27, L_PAREN=28, R_PAREN=29, 
		L_CURLY=30, R_CURLY=31, L_BRACKET=32, R_BRACKET=33, ASSIGN=34, COMMA=35, 
		SEMI=36, COLON=37, DOT=38, PLUS_PLUS=39, MINUS_MINUS=40, DECLARE_ASSIGN=41, 
		ELLIPSIS=42, LOGICAL_OR=43, LOGICAL_AND=44, EQUALS=45, NOT_EQUALS=46, 
		LESS=47, LESS_OR_EQUALS=48, GREATER=49, GREATER_OR_EQUALS=50, OR=51, DIV=52, 
		MOD=53, LSHIFT=54, RSHIFT=55, BIT_CLEAR=56, UNDERLYING=57, EXCLAMATION=58, 
		PLUS=59, MINUS=60, CARET=61, STAR=62, AMPERSAND=63, RECEIVE=64, DECIMAL_LIT=65, 
		BINARY_LIT=66, OCTAL_LIT=67, HEX_LIT=68, FLOAT_LIT=69, DECIMAL_FLOAT_LIT=70, 
		HEX_FLOAT_LIT=71, IMAGINARY_LIT=72, RUNE_LIT=73, BYTE_VALUE=74, OCTAL_BYTE_VALUE=75, 
		HEX_BYTE_VALUE=76, LITTLE_U_VALUE=77, BIG_U_VALUE=78, RAW_STRING_LIT=79, 
		INTERPRETED_STRING_LIT=80, WS=81, COMMENT=82, TERMINATOR=83, LINE_COMMENT=84, 
		NEWLINE=85, WS_NLSEMI=86, COMMENT_NLSEMI=87, LINE_COMMENT_NLSEMI=88, EOS=89, 
		OTHER=90;
	public static final int
		RULE_sourceFile = 0, RULE_packageClause = 1, RULE_importDecl = 2, RULE_importSpec = 3, 
		RULE_importPath = 4, RULE_declaration = 5, RULE_constDecl = 6, RULE_constSpec = 7, 
		RULE_identifierList = 8, RULE_expressionList = 9, RULE_comment = 10, RULE_typeDecl = 11, 
		RULE_typeSpec = 12, RULE_aliasDecl = 13, RULE_typeDef = 14, RULE_typeParameters = 15, 
		RULE_typeParameterDecl = 16, RULE_typeElement = 17, RULE_typeTerm = 18, 
		RULE_functionDecl = 19, RULE_methodDecl = 20, RULE_receiver = 21, RULE_varDecl = 22, 
		RULE_varSpec = 23, RULE_block = 24, RULE_statementList = 25, RULE_statement = 26, 
		RULE_simpleStmt = 27, RULE_expressionStmt = 28, RULE_sendStmt = 29, RULE_incDecStmt = 30, 
		RULE_assignment = 31, RULE_assign_op = 32, RULE_shortVarDecl = 33, RULE_labeledStmt = 34, 
		RULE_returnStmt = 35, RULE_breakStmt = 36, RULE_continueStmt = 37, RULE_gotoStmt = 38, 
		RULE_fallthroughStmt = 39, RULE_deferStmt = 40, RULE_ifStmt = 41, RULE_switchStmt = 42, 
		RULE_exprSwitchStmt = 43, RULE_exprCaseClause = 44, RULE_exprSwitchCase = 45, 
		RULE_typeSwitchStmt = 46, RULE_typeSwitchGuard = 47, RULE_typeCaseClause = 48, 
		RULE_typeSwitchCase = 49, RULE_typeList = 50, RULE_selectStmt = 51, RULE_commClause = 52, 
		RULE_commCase = 53, RULE_recvStmt = 54, RULE_forStmt = 55, RULE_forClause = 56, 
		RULE_rangeClause = 57, RULE_goStmt = 58, RULE_type_ = 59, RULE_typeArgs = 60, 
		RULE_typeName = 61, RULE_typeLit = 62, RULE_arrayType = 63, RULE_arrayLength = 64, 
		RULE_elementType = 65, RULE_pointerType = 66, RULE_interfaceType = 67, 
		RULE_sliceType = 68, RULE_mapType = 69, RULE_channelType = 70, RULE_methodSpec = 71, 
		RULE_functionType = 72, RULE_signature = 73, RULE_result = 74, RULE_parameters = 75, 
		RULE_parameterDecl = 76, RULE_expression = 77, RULE_primaryExpr = 78, 
		RULE_conversion = 79, RULE_operand = 80, RULE_literal = 81, RULE_basicLit = 82, 
		RULE_integer = 83, RULE_operandName = 84, RULE_qualifiedIdent = 85, RULE_compositeLit = 86, 
		RULE_literalType = 87, RULE_literalValue = 88, RULE_elementList = 89, 
		RULE_keyedElement = 90, RULE_key = 91, RULE_element = 92, RULE_structType = 93, 
		RULE_fieldDecl = 94, RULE_string_ = 95, RULE_embeddedField = 96, RULE_functionLit = 97, 
		RULE_index = 98, RULE_slice_ = 99, RULE_typeAssertion = 100, RULE_arguments = 101, 
		RULE_methodExpr = 102, RULE_eos = 103;
	public static final String[] ruleNames = {
		"sourceFile", "packageClause", "importDecl", "importSpec", "importPath", 
		"declaration", "constDecl", "constSpec", "identifierList", "expressionList", 
		"comment", "typeDecl", "typeSpec", "aliasDecl", "typeDef", "typeParameters", 
		"typeParameterDecl", "typeElement", "typeTerm", "functionDecl", "methodDecl", 
		"receiver", "varDecl", "varSpec", "block", "statementList", "statement", 
		"simpleStmt", "expressionStmt", "sendStmt", "incDecStmt", "assignment", 
		"assign_op", "shortVarDecl", "labeledStmt", "returnStmt", "breakStmt", 
		"continueStmt", "gotoStmt", "fallthroughStmt", "deferStmt", "ifStmt", 
		"switchStmt", "exprSwitchStmt", "exprCaseClause", "exprSwitchCase", "typeSwitchStmt", 
		"typeSwitchGuard", "typeCaseClause", "typeSwitchCase", "typeList", "selectStmt", 
		"commClause", "commCase", "recvStmt", "forStmt", "forClause", "rangeClause", 
		"goStmt", "type_", "typeArgs", "typeName", "typeLit", "arrayType", "arrayLength", 
		"elementType", "pointerType", "interfaceType", "sliceType", "mapType", 
		"channelType", "methodSpec", "functionType", "signature", "result", "parameters", 
		"parameterDecl", "expression", "primaryExpr", "conversion", "operand", 
		"literal", "basicLit", "integer", "operandName", "qualifiedIdent", "compositeLit", 
		"literalType", "literalValue", "elementList", "keyedElement", "key", "element", 
		"structType", "fieldDecl", "string_", "embeddedField", "functionLit", 
		"index", "slice_", "typeAssertion", "arguments", "methodExpr", "eos"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'break'", "'default'", "'func'", "'interface'", "'select'", "'case'", 
		"'defer'", "'go'", "'map'", "'struct'", "'chan'", "'else'", "'goto'", 
		"'package'", "'switch'", "'const'", "'fallthrough'", "'if'", "'range'", 
		"'type'", "'continue'", "'for'", "'import'", "'return'", "'var'", "'nil'", 
		null, "'('", "')'", "'{'", "'}'", "'['", "']'", "'='", "','", "';'", "':'", 
		"'.'", "'++'", "'--'", "':='", "'...'", "'||'", "'&&'", "'=='", "'!='", 
		"'<'", "'<='", "'>'", "'>='", "'|'", "'/'", "'%'", "'<<'", "'>>'", "'&^'", 
		"'~'", "'!'", "'+'", "'-'", "'^'", "'*'", "'&'", "'<-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "BREAK", "DEFAULT", "FUNC", "INTERFACE", "SELECT", "CASE", "DEFER", 
		"GO", "MAP", "STRUCT", "CHAN", "ELSE", "GOTO", "PACKAGE", "SWITCH", "CONST", 
		"FALLTHROUGH", "IF", "RANGE", "TYPE", "CONTINUE", "FOR", "IMPORT", "RETURN", 
		"VAR", "NIL_LIT", "IDENTIFIER", "L_PAREN", "R_PAREN", "L_CURLY", "R_CURLY", 
		"L_BRACKET", "R_BRACKET", "ASSIGN", "COMMA", "SEMI", "COLON", "DOT", "PLUS_PLUS", 
		"MINUS_MINUS", "DECLARE_ASSIGN", "ELLIPSIS", "LOGICAL_OR", "LOGICAL_AND", 
		"EQUALS", "NOT_EQUALS", "LESS", "LESS_OR_EQUALS", "GREATER", "GREATER_OR_EQUALS", 
		"OR", "DIV", "MOD", "LSHIFT", "RSHIFT", "BIT_CLEAR", "UNDERLYING", "EXCLAMATION", 
		"PLUS", "MINUS", "CARET", "STAR", "AMPERSAND", "RECEIVE", "DECIMAL_LIT", 
		"BINARY_LIT", "OCTAL_LIT", "HEX_LIT", "FLOAT_LIT", "DECIMAL_FLOAT_LIT", 
		"HEX_FLOAT_LIT", "IMAGINARY_LIT", "RUNE_LIT", "BYTE_VALUE", "OCTAL_BYTE_VALUE", 
		"HEX_BYTE_VALUE", "LITTLE_U_VALUE", "BIG_U_VALUE", "RAW_STRING_LIT", "INTERPRETED_STRING_LIT", 
		"WS", "COMMENT", "TERMINATOR", "LINE_COMMENT", "NEWLINE", "WS_NLSEMI", 
		"COMMENT_NLSEMI", "LINE_COMMENT_NLSEMI", "EOS", "OTHER"
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
	public String getGrammarFileName() { return "GoParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GoParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class SourceFileContext extends ParserRuleContext {
		public PackageClauseContext packageClause() {
			return getRuleContext(PackageClauseContext.class,0);
		}
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public TerminalNode EOF() { return getToken(GoParser.EOF, 0); }
		public List<ImportDeclContext> importDecl() {
			return getRuleContexts(ImportDeclContext.class);
		}
		public ImportDeclContext importDecl(int i) {
			return getRuleContext(ImportDeclContext.class,i);
		}
		public List<FunctionDeclContext> functionDecl() {
			return getRuleContexts(FunctionDeclContext.class);
		}
		public FunctionDeclContext functionDecl(int i) {
			return getRuleContext(FunctionDeclContext.class,i);
		}
		public List<MethodDeclContext> methodDecl() {
			return getRuleContexts(MethodDeclContext.class);
		}
		public MethodDeclContext methodDecl(int i) {
			return getRuleContext(MethodDeclContext.class,i);
		}
		public List<DeclarationContext> declaration() {
			return getRuleContexts(DeclarationContext.class);
		}
		public DeclarationContext declaration(int i) {
			return getRuleContext(DeclarationContext.class,i);
		}
		public SourceFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sourceFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSourceFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSourceFile(this);
		}
	}

	public final SourceFileContext sourceFile() throws RecognitionException {
		SourceFileContext _localctx = new SourceFileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_sourceFile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			packageClause();
			setState(209);
			eos();
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(210);
				importDecl();
				setState(211);
				eos();
				}
				}
				setState(217);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << CONST) | (1L << TYPE) | (1L << VAR))) != 0) || _la==COMMENT || _la==LINE_COMMENT) {
				{
				{
				setState(221);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(218);
					functionDecl();
					}
					break;
				case 2:
					{
					setState(219);
					methodDecl();
					}
					break;
				case 3:
					{
					setState(220);
					declaration();
					}
					break;
				}
				setState(223);
				eos();
				}
				}
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(230);
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

	public static class PackageClauseContext extends ParserRuleContext {
		public Token packageName;
		public TerminalNode PACKAGE() { return getToken(GoParser.PACKAGE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public PackageClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterPackageClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitPackageClause(this);
		}
	}

	public final PackageClauseContext packageClause() throws RecognitionException {
		PackageClauseContext _localctx = new PackageClauseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_packageClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			match(PACKAGE);
			setState(233);
			((PackageClauseContext)_localctx).packageName = match(IDENTIFIER);
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

	public static class ImportDeclContext extends ParserRuleContext {
		public TerminalNode IMPORT() { return getToken(GoParser.IMPORT, 0); }
		public List<ImportSpecContext> importSpec() {
			return getRuleContexts(ImportSpecContext.class);
		}
		public ImportSpecContext importSpec(int i) {
			return getRuleContext(ImportSpecContext.class,i);
		}
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public ImportDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterImportDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitImportDecl(this);
		}
	}

	public final ImportDeclContext importDecl() throws RecognitionException {
		ImportDeclContext _localctx = new ImportDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_importDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(IMPORT);
			setState(247);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
			case DOT:
			case RAW_STRING_LIT:
			case INTERPRETED_STRING_LIT:
				{
				setState(236);
				importSpec();
				}
				break;
			case L_PAREN:
				{
				setState(237);
				match(L_PAREN);
				setState(243);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 27)) & ~0x3f) == 0 && ((1L << (_la - 27)) & ((1L << (IDENTIFIER - 27)) | (1L << (DOT - 27)) | (1L << (RAW_STRING_LIT - 27)) | (1L << (INTERPRETED_STRING_LIT - 27)))) != 0)) {
					{
					{
					setState(238);
					importSpec();
					setState(239);
					eos();
					}
					}
					setState(245);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(246);
				match(R_PAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
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

	public static class ImportSpecContext extends ParserRuleContext {
		public Token alias;
		public ImportPathContext importPath() {
			return getRuleContext(ImportPathContext.class,0);
		}
		public TerminalNode DOT() { return getToken(GoParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public ImportSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterImportSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitImportSpec(this);
		}
	}

	public final ImportSpecContext importSpec() throws RecognitionException {
		ImportSpecContext _localctx = new ImportSpecContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_importSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER || _la==DOT) {
				{
				setState(249);
				((ImportSpecContext)_localctx).alias = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==IDENTIFIER || _la==DOT) ) {
					((ImportSpecContext)_localctx).alias = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(252);
			importPath();
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

	public static class ImportPathContext extends ParserRuleContext {
		public String_Context string_() {
			return getRuleContext(String_Context.class,0);
		}
		public ImportPathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_importPath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterImportPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitImportPath(this);
		}
	}

	public final ImportPathContext importPath() throws RecognitionException {
		ImportPathContext _localctx = new ImportPathContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_importPath);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
			string_();
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

	public static class DeclarationContext extends ParserRuleContext {
		public ConstDeclContext constDecl() {
			return getRuleContext(ConstDeclContext.class,0);
		}
		public TypeDeclContext typeDecl() {
			return getRuleContext(TypeDeclContext.class,0);
		}
		public VarDeclContext varDecl() {
			return getRuleContext(VarDeclContext.class,0);
		}
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitDeclaration(this);
		}
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_declaration);
		try {
			setState(259);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CONST:
				enterOuterAlt(_localctx, 1);
				{
				setState(256);
				constDecl();
				}
				break;
			case TYPE:
			case COMMENT:
			case LINE_COMMENT:
				enterOuterAlt(_localctx, 2);
				{
				setState(257);
				typeDecl();
				}
				break;
			case VAR:
				enterOuterAlt(_localctx, 3);
				{
				setState(258);
				varDecl();
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

	public static class ConstDeclContext extends ParserRuleContext {
		public TerminalNode CONST() { return getToken(GoParser.CONST, 0); }
		public List<ConstSpecContext> constSpec() {
			return getRuleContexts(ConstSpecContext.class);
		}
		public ConstSpecContext constSpec(int i) {
			return getRuleContext(ConstSpecContext.class,i);
		}
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public ConstDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterConstDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitConstDecl(this);
		}
	}

	public final ConstDeclContext constDecl() throws RecognitionException {
		ConstDeclContext _localctx = new ConstDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_constDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(CONST);
			setState(273);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(262);
				constSpec();
				}
				break;
			case L_PAREN:
				{
				setState(263);
				match(L_PAREN);
				setState(269);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IDENTIFIER) {
					{
					{
					setState(264);
					constSpec();
					setState(265);
					eos();
					}
					}
					setState(271);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(272);
				match(R_PAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
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

	public static class ConstSpecContext extends ParserRuleContext {
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(GoParser.ASSIGN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public ConstSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterConstSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitConstSpec(this);
		}
	}

	public final ConstSpecContext constSpec() throws RecognitionException {
		ConstSpecContext _localctx = new ConstSpecContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_constSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			identifierList();
			setState(281);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(277);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (((((_la - 3)) & ~0x3f) == 0 && ((1L << (_la - 3)) & ((1L << (FUNC - 3)) | (1L << (INTERFACE - 3)) | (1L << (MAP - 3)) | (1L << (STRUCT - 3)) | (1L << (CHAN - 3)) | (1L << (IDENTIFIER - 3)) | (1L << (L_PAREN - 3)) | (1L << (L_BRACKET - 3)) | (1L << (STAR - 3)) | (1L << (RECEIVE - 3)))) != 0)) {
					{
					setState(276);
					type_();
					}
				}

				setState(279);
				match(ASSIGN);
				setState(280);
				expressionList();
				}
				break;
			}
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

	public static class IdentifierListContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(GoParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(GoParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GoParser.COMMA, i);
		}
		public IdentifierListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifierList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitIdentifierList(this);
		}
	}

	public final IdentifierListContext identifierList() throws RecognitionException {
		IdentifierListContext _localctx = new IdentifierListContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_identifierList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			match(IDENTIFIER);
			setState(288);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(284);
					match(COMMA);
					setState(285);
					match(IDENTIFIER);
					}
					} 
				}
				setState(290);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
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

	public static class ExpressionListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GoParser.COMMA, i);
		}
		public ExpressionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitExpressionList(this);
		}
	}

	public final ExpressionListContext expressionList() throws RecognitionException {
		ExpressionListContext _localctx = new ExpressionListContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_expressionList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			expression(0);
			setState(296);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(292);
					match(COMMA);
					setState(293);
					expression(0);
					}
					} 
				}
				setState(298);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
			}
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

	public static class CommentContext extends ParserRuleContext {
		public TerminalNode COMMENT() { return getToken(GoParser.COMMENT, 0); }
		public TerminalNode LINE_COMMENT() { return getToken(GoParser.LINE_COMMENT, 0); }
		public CommentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitComment(this);
		}
	}

	public final CommentContext comment() throws RecognitionException {
		CommentContext _localctx = new CommentContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_comment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(299);
			_la = _input.LA(1);
			if ( !(_la==COMMENT || _la==LINE_COMMENT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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

	public static class TypeDeclContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(GoParser.TYPE, 0); }
		public List<TypeSpecContext> typeSpec() {
			return getRuleContexts(TypeSpecContext.class);
		}
		public TypeSpecContext typeSpec(int i) {
			return getRuleContext(TypeSpecContext.class,i);
		}
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public TypeDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeDecl(this);
		}
	}

	public final TypeDeclContext typeDecl() throws RecognitionException {
		TypeDeclContext _localctx = new TypeDeclContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_typeDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT || _la==LINE_COMMENT) {
				{
				{
				setState(301);
				comment();
				}
				}
				setState(306);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(307);
			match(TYPE);
			setState(319);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(308);
				typeSpec();
				}
				break;
			case L_PAREN:
				{
				setState(309);
				match(L_PAREN);
				setState(315);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IDENTIFIER) {
					{
					{
					setState(310);
					typeSpec();
					setState(311);
					eos();
					}
					}
					setState(317);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(318);
				match(R_PAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
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

	public static class TypeSpecContext extends ParserRuleContext {
		public AliasDeclContext aliasDecl() {
			return getRuleContext(AliasDeclContext.class,0);
		}
		public TypeDefContext typeDef() {
			return getRuleContext(TypeDefContext.class,0);
		}
		public TypeSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeSpec(this);
		}
	}

	public final TypeSpecContext typeSpec() throws RecognitionException {
		TypeSpecContext _localctx = new TypeSpecContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_typeSpec);
		try {
			setState(323);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(321);
				aliasDecl();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(322);
				typeDef();
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

	public static class AliasDeclContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public TerminalNode ASSIGN() { return getToken(GoParser.ASSIGN, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public AliasDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aliasDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterAliasDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitAliasDecl(this);
		}
	}

	public final AliasDeclContext aliasDecl() throws RecognitionException {
		AliasDeclContext _localctx = new AliasDeclContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_aliasDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(325);
			match(IDENTIFIER);
			setState(326);
			match(ASSIGN);
			setState(327);
			type_();
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

	public static class TypeDefContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public TypeDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeDef(this);
		}
	}

	public final TypeDefContext typeDef() throws RecognitionException {
		TypeDefContext _localctx = new TypeDefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_typeDef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(329);
			match(IDENTIFIER);
			setState(331);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(330);
				typeParameters();
				}
				break;
			}
			setState(333);
			type_();
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

	public static class TypeParametersContext extends ParserRuleContext {
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public List<TypeParameterDeclContext> typeParameterDecl() {
			return getRuleContexts(TypeParameterDeclContext.class);
		}
		public TypeParameterDeclContext typeParameterDecl(int i) {
			return getRuleContext(TypeParameterDeclContext.class,i);
		}
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public List<TerminalNode> COMMA() { return getTokens(GoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GoParser.COMMA, i);
		}
		public TypeParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeParameters(this);
		}
	}

	public final TypeParametersContext typeParameters() throws RecognitionException {
		TypeParametersContext _localctx = new TypeParametersContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_typeParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(L_BRACKET);
			setState(336);
			typeParameterDecl();
			setState(341);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(337);
				match(COMMA);
				setState(338);
				typeParameterDecl();
				}
				}
				setState(343);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(344);
			match(R_BRACKET);
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

	public static class TypeParameterDeclContext extends ParserRuleContext {
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TypeElementContext typeElement() {
			return getRuleContext(TypeElementContext.class,0);
		}
		public TypeParameterDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeParameterDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeParameterDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeParameterDecl(this);
		}
	}

	public final TypeParameterDeclContext typeParameterDecl() throws RecognitionException {
		TypeParameterDeclContext _localctx = new TypeParameterDeclContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_typeParameterDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			identifierList();
			setState(347);
			typeElement();
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

	public static class TypeElementContext extends ParserRuleContext {
		public List<TypeTermContext> typeTerm() {
			return getRuleContexts(TypeTermContext.class);
		}
		public TypeTermContext typeTerm(int i) {
			return getRuleContext(TypeTermContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(GoParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(GoParser.OR, i);
		}
		public TypeElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeElement(this);
		}
	}

	public final TypeElementContext typeElement() throws RecognitionException {
		TypeElementContext _localctx = new TypeElementContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_typeElement);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			typeTerm();
			setState(354);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(350);
					match(OR);
					setState(351);
					typeTerm();
					}
					} 
				}
				setState(356);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
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

	public static class TypeTermContext extends ParserRuleContext {
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode UNDERLYING() { return getToken(GoParser.UNDERLYING, 0); }
		public TypeTermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeTerm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeTerm(this);
		}
	}

	public final TypeTermContext typeTerm() throws RecognitionException {
		TypeTermContext _localctx = new TypeTermContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_typeTerm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(358);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==UNDERLYING) {
				{
				setState(357);
				match(UNDERLYING);
				}
			}

			setState(360);
			type_();
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

	public static class FunctionDeclContext extends ParserRuleContext {
		public TerminalNode FUNC() { return getToken(GoParser.FUNC, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public SignatureContext signature() {
			return getRuleContext(SignatureContext.class,0);
		}
		public TypeParametersContext typeParameters() {
			return getRuleContext(TypeParametersContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FunctionDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterFunctionDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitFunctionDecl(this);
		}
	}

	public final FunctionDeclContext functionDecl() throws RecognitionException {
		FunctionDeclContext _localctx = new FunctionDeclContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_functionDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(362);
			match(FUNC);
			setState(363);
			match(IDENTIFIER);
			setState(365);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==L_BRACKET) {
				{
				setState(364);
				typeParameters();
				}
			}

			setState(367);
			signature();
			setState(369);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(368);
				block();
				}
				break;
			}
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

	public static class MethodDeclContext extends ParserRuleContext {
		public TerminalNode FUNC() { return getToken(GoParser.FUNC, 0); }
		public ReceiverContext receiver() {
			return getRuleContext(ReceiverContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public SignatureContext signature() {
			return getRuleContext(SignatureContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public MethodDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterMethodDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitMethodDecl(this);
		}
	}

	public final MethodDeclContext methodDecl() throws RecognitionException {
		MethodDeclContext _localctx = new MethodDeclContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_methodDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			match(FUNC);
			setState(372);
			receiver();
			setState(373);
			match(IDENTIFIER);
			setState(374);
			signature();
			setState(376);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(375);
				block();
				}
				break;
			}
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

	public static class ReceiverContext extends ParserRuleContext {
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ReceiverContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_receiver; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterReceiver(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitReceiver(this);
		}
	}

	public final ReceiverContext receiver() throws RecognitionException {
		ReceiverContext _localctx = new ReceiverContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_receiver);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(378);
			parameters();
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

	public static class VarDeclContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(GoParser.VAR, 0); }
		public List<VarSpecContext> varSpec() {
			return getRuleContexts(VarSpecContext.class);
		}
		public VarSpecContext varSpec(int i) {
			return getRuleContext(VarSpecContext.class,i);
		}
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public VarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterVarDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitVarDecl(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(380);
			match(VAR);
			setState(392);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(381);
				varSpec();
				}
				break;
			case L_PAREN:
				{
				setState(382);
				match(L_PAREN);
				setState(388);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IDENTIFIER) {
					{
					{
					setState(383);
					varSpec();
					setState(384);
					eos();
					}
					}
					setState(390);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(391);
				match(R_PAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
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

	public static class VarSpecContext extends ParserRuleContext {
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(GoParser.ASSIGN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public VarSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterVarSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitVarSpec(this);
		}
	}

	public final VarSpecContext varSpec() throws RecognitionException {
		VarSpecContext _localctx = new VarSpecContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_varSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(394);
			identifierList();
			setState(402);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FUNC:
			case INTERFACE:
			case MAP:
			case STRUCT:
			case CHAN:
			case IDENTIFIER:
			case L_PAREN:
			case L_BRACKET:
			case STAR:
			case RECEIVE:
				{
				setState(395);
				type_();
				setState(398);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
				case 1:
					{
					setState(396);
					match(ASSIGN);
					setState(397);
					expressionList();
					}
					break;
				}
				}
				break;
			case ASSIGN:
				{
				setState(400);
				match(ASSIGN);
				setState(401);
				expressionList();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
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

	public static class BlockContext extends ParserRuleContext {
		public TerminalNode L_CURLY() { return getToken(GoParser.L_CURLY, 0); }
		public TerminalNode R_CURLY() { return getToken(GoParser.R_CURLY, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
			match(L_CURLY);
			setState(406);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(405);
				statementList();
				}
				break;
			}
			setState(408);
			match(R_CURLY);
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

	public static class StatementListContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(GoParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(GoParser.SEMI, i);
		}
		public List<TerminalNode> EOS() { return getTokens(GoParser.EOS); }
		public TerminalNode EOS(int i) {
			return getToken(GoParser.EOS, i);
		}
		public StatementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitStatementList(this);
		}
	}

	public final StatementListContext statementList() throws RecognitionException {
		StatementListContext _localctx = new StatementListContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_statementList);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(422); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(417);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
					case 1:
						{
						setState(411);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==SEMI) {
							{
							setState(410);
							match(SEMI);
							}
						}

						}
						break;
					case 2:
						{
						setState(414);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==EOS) {
							{
							setState(413);
							match(EOS);
							}
						}

						}
						break;
					case 3:
						{
						setState(416);
						if (!(this.closingBracket())) throw new FailedPredicateException(this, "this.closingBracket()");
						}
						break;
					}
					setState(419);
					statement();
					setState(420);
					eos();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(424); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			} while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER );
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

	public static class StatementContext extends ParserRuleContext {
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public LabeledStmtContext labeledStmt() {
			return getRuleContext(LabeledStmtContext.class,0);
		}
		public SimpleStmtContext simpleStmt() {
			return getRuleContext(SimpleStmtContext.class,0);
		}
		public GoStmtContext goStmt() {
			return getRuleContext(GoStmtContext.class,0);
		}
		public ReturnStmtContext returnStmt() {
			return getRuleContext(ReturnStmtContext.class,0);
		}
		public BreakStmtContext breakStmt() {
			return getRuleContext(BreakStmtContext.class,0);
		}
		public ContinueStmtContext continueStmt() {
			return getRuleContext(ContinueStmtContext.class,0);
		}
		public GotoStmtContext gotoStmt() {
			return getRuleContext(GotoStmtContext.class,0);
		}
		public FallthroughStmtContext fallthroughStmt() {
			return getRuleContext(FallthroughStmtContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public IfStmtContext ifStmt() {
			return getRuleContext(IfStmtContext.class,0);
		}
		public SwitchStmtContext switchStmt() {
			return getRuleContext(SwitchStmtContext.class,0);
		}
		public SelectStmtContext selectStmt() {
			return getRuleContext(SelectStmtContext.class,0);
		}
		public ForStmtContext forStmt() {
			return getRuleContext(ForStmtContext.class,0);
		}
		public DeferStmtContext deferStmt() {
			return getRuleContext(DeferStmtContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_statement);
		try {
			setState(441);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(426);
				declaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(427);
				labeledStmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(428);
				simpleStmt();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(429);
				goStmt();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(430);
				returnStmt();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(431);
				breakStmt();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(432);
				continueStmt();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(433);
				gotoStmt();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(434);
				fallthroughStmt();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(435);
				block();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(436);
				ifStmt();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(437);
				switchStmt();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(438);
				selectStmt();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(439);
				forStmt();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(440);
				deferStmt();
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

	public static class SimpleStmtContext extends ParserRuleContext {
		public SendStmtContext sendStmt() {
			return getRuleContext(SendStmtContext.class,0);
		}
		public IncDecStmtContext incDecStmt() {
			return getRuleContext(IncDecStmtContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public ExpressionStmtContext expressionStmt() {
			return getRuleContext(ExpressionStmtContext.class,0);
		}
		public ShortVarDeclContext shortVarDecl() {
			return getRuleContext(ShortVarDeclContext.class,0);
		}
		public SimpleStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSimpleStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSimpleStmt(this);
		}
	}

	public final SimpleStmtContext simpleStmt() throws RecognitionException {
		SimpleStmtContext _localctx = new SimpleStmtContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_simpleStmt);
		try {
			setState(448);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(443);
				sendStmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(444);
				incDecStmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(445);
				assignment();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(446);
				expressionStmt();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(447);
				shortVarDecl();
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

	public static class ExpressionStmtContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expressionStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterExpressionStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitExpressionStmt(this);
		}
	}

	public final ExpressionStmtContext expressionStmt() throws RecognitionException {
		ExpressionStmtContext _localctx = new ExpressionStmtContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_expressionStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(450);
			expression(0);
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

	public static class SendStmtContext extends ParserRuleContext {
		public ExpressionContext channel;
		public TerminalNode RECEIVE() { return getToken(GoParser.RECEIVE, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public SendStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sendStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSendStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSendStmt(this);
		}
	}

	public final SendStmtContext sendStmt() throws RecognitionException {
		SendStmtContext _localctx = new SendStmtContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_sendStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(452);
			((SendStmtContext)_localctx).channel = expression(0);
			setState(453);
			match(RECEIVE);
			setState(454);
			expression(0);
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

	public static class IncDecStmtContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode PLUS_PLUS() { return getToken(GoParser.PLUS_PLUS, 0); }
		public TerminalNode MINUS_MINUS() { return getToken(GoParser.MINUS_MINUS, 0); }
		public IncDecStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_incDecStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterIncDecStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitIncDecStmt(this);
		}
	}

	public final IncDecStmtContext incDecStmt() throws RecognitionException {
		IncDecStmtContext _localctx = new IncDecStmtContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_incDecStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(456);
			expression(0);
			setState(457);
			_la = _input.LA(1);
			if ( !(_la==PLUS_PLUS || _la==MINUS_MINUS) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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

	public static class AssignmentContext extends ParserRuleContext {
		public List<ExpressionListContext> expressionList() {
			return getRuleContexts(ExpressionListContext.class);
		}
		public ExpressionListContext expressionList(int i) {
			return getRuleContext(ExpressionListContext.class,i);
		}
		public Assign_opContext assign_op() {
			return getRuleContext(Assign_opContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitAssignment(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(459);
			expressionList();
			setState(460);
			assign_op();
			setState(461);
			expressionList();
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

	public static class Assign_opContext extends ParserRuleContext {
		public TerminalNode ASSIGN() { return getToken(GoParser.ASSIGN, 0); }
		public TerminalNode PLUS() { return getToken(GoParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(GoParser.MINUS, 0); }
		public TerminalNode OR() { return getToken(GoParser.OR, 0); }
		public TerminalNode CARET() { return getToken(GoParser.CARET, 0); }
		public TerminalNode STAR() { return getToken(GoParser.STAR, 0); }
		public TerminalNode DIV() { return getToken(GoParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(GoParser.MOD, 0); }
		public TerminalNode LSHIFT() { return getToken(GoParser.LSHIFT, 0); }
		public TerminalNode RSHIFT() { return getToken(GoParser.RSHIFT, 0); }
		public TerminalNode AMPERSAND() { return getToken(GoParser.AMPERSAND, 0); }
		public TerminalNode BIT_CLEAR() { return getToken(GoParser.BIT_CLEAR, 0); }
		public Assign_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterAssign_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitAssign_op(this);
		}
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_assign_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(464);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OR) | (1L << DIV) | (1L << MOD) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << BIT_CLEAR) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0)) {
				{
				setState(463);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OR) | (1L << DIV) | (1L << MOD) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << BIT_CLEAR) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(466);
			match(ASSIGN);
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

	public static class ShortVarDeclContext extends ParserRuleContext {
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TerminalNode DECLARE_ASSIGN() { return getToken(GoParser.DECLARE_ASSIGN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ShortVarDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shortVarDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterShortVarDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitShortVarDecl(this);
		}
	}

	public final ShortVarDeclContext shortVarDecl() throws RecognitionException {
		ShortVarDeclContext _localctx = new ShortVarDeclContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_shortVarDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468);
			identifierList();
			setState(469);
			match(DECLARE_ASSIGN);
			setState(470);
			expressionList();
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

	public static class LabeledStmtContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(GoParser.COLON, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public LabeledStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labeledStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterLabeledStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitLabeledStmt(this);
		}
	}

	public final LabeledStmtContext labeledStmt() throws RecognitionException {
		LabeledStmtContext _localctx = new LabeledStmtContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_labeledStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(472);
			match(IDENTIFIER);
			setState(473);
			match(COLON);
			setState(475);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(474);
				statement();
				}
				break;
			}
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

	public static class ReturnStmtContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(GoParser.RETURN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public ReturnStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterReturnStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitReturnStmt(this);
		}
	}

	public final ReturnStmtContext returnStmt() throws RecognitionException {
		ReturnStmtContext _localctx = new ReturnStmtContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_returnStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(477);
			match(RETURN);
			setState(479);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				{
				setState(478);
				expressionList();
				}
				break;
			}
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

	public static class BreakStmtContext extends ParserRuleContext {
		public TerminalNode BREAK() { return getToken(GoParser.BREAK, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public BreakStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_breakStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterBreakStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitBreakStmt(this);
		}
	}

	public final BreakStmtContext breakStmt() throws RecognitionException {
		BreakStmtContext _localctx = new BreakStmtContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_breakStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(481);
			match(BREAK);
			setState(483);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(482);
				match(IDENTIFIER);
				}
				break;
			}
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

	public static class ContinueStmtContext extends ParserRuleContext {
		public TerminalNode CONTINUE() { return getToken(GoParser.CONTINUE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public ContinueStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_continueStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterContinueStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitContinueStmt(this);
		}
	}

	public final ContinueStmtContext continueStmt() throws RecognitionException {
		ContinueStmtContext _localctx = new ContinueStmtContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_continueStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(485);
			match(CONTINUE);
			setState(487);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(486);
				match(IDENTIFIER);
				}
				break;
			}
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

	public static class GotoStmtContext extends ParserRuleContext {
		public TerminalNode GOTO() { return getToken(GoParser.GOTO, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public GotoStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gotoStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterGotoStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitGotoStmt(this);
		}
	}

	public final GotoStmtContext gotoStmt() throws RecognitionException {
		GotoStmtContext _localctx = new GotoStmtContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_gotoStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(489);
			match(GOTO);
			setState(490);
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

	public static class FallthroughStmtContext extends ParserRuleContext {
		public TerminalNode FALLTHROUGH() { return getToken(GoParser.FALLTHROUGH, 0); }
		public FallthroughStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fallthroughStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterFallthroughStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitFallthroughStmt(this);
		}
	}

	public final FallthroughStmtContext fallthroughStmt() throws RecognitionException {
		FallthroughStmtContext _localctx = new FallthroughStmtContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_fallthroughStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(492);
			match(FALLTHROUGH);
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

	public static class DeferStmtContext extends ParserRuleContext {
		public TerminalNode DEFER() { return getToken(GoParser.DEFER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public DeferStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deferStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterDeferStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitDeferStmt(this);
		}
	}

	public final DeferStmtContext deferStmt() throws RecognitionException {
		DeferStmtContext _localctx = new DeferStmtContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_deferStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(494);
			match(DEFER);
			setState(495);
			expression(0);
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

	public static class IfStmtContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(GoParser.IF, 0); }
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public SimpleStmtContext simpleStmt() {
			return getRuleContext(SimpleStmtContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(GoParser.ELSE, 0); }
		public IfStmtContext ifStmt() {
			return getRuleContext(IfStmtContext.class,0);
		}
		public IfStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterIfStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitIfStmt(this);
		}
	}

	public final IfStmtContext ifStmt() throws RecognitionException {
		IfStmtContext _localctx = new IfStmtContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_ifStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(497);
			match(IF);
			setState(506);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(498);
				expression(0);
				}
				break;
			case 2:
				{
				setState(499);
				eos();
				setState(500);
				expression(0);
				}
				break;
			case 3:
				{
				setState(502);
				simpleStmt();
				setState(503);
				eos();
				setState(504);
				expression(0);
				}
				break;
			}
			setState(508);
			block();
			setState(514);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(509);
				match(ELSE);
				setState(512);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case IF:
					{
					setState(510);
					ifStmt();
					}
					break;
				case L_CURLY:
					{
					setState(511);
					block();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
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

	public static class SwitchStmtContext extends ParserRuleContext {
		public ExprSwitchStmtContext exprSwitchStmt() {
			return getRuleContext(ExprSwitchStmtContext.class,0);
		}
		public TypeSwitchStmtContext typeSwitchStmt() {
			return getRuleContext(TypeSwitchStmtContext.class,0);
		}
		public SwitchStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_switchStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSwitchStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSwitchStmt(this);
		}
	}

	public final SwitchStmtContext switchStmt() throws RecognitionException {
		SwitchStmtContext _localctx = new SwitchStmtContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_switchStmt);
		try {
			setState(518);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(516);
				exprSwitchStmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(517);
				typeSwitchStmt();
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

	public static class ExprSwitchStmtContext extends ParserRuleContext {
		public TerminalNode SWITCH() { return getToken(GoParser.SWITCH, 0); }
		public TerminalNode L_CURLY() { return getToken(GoParser.L_CURLY, 0); }
		public TerminalNode R_CURLY() { return getToken(GoParser.R_CURLY, 0); }
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public List<ExprCaseClauseContext> exprCaseClause() {
			return getRuleContexts(ExprCaseClauseContext.class);
		}
		public ExprCaseClauseContext exprCaseClause(int i) {
			return getRuleContext(ExprCaseClauseContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public SimpleStmtContext simpleStmt() {
			return getRuleContext(SimpleStmtContext.class,0);
		}
		public ExprSwitchStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprSwitchStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterExprSwitchStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitExprSwitchStmt(this);
		}
	}

	public final ExprSwitchStmtContext exprSwitchStmt() throws RecognitionException {
		ExprSwitchStmtContext _localctx = new ExprSwitchStmtContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_exprSwitchStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(520);
			match(SWITCH);
			setState(531);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				{
				setState(522);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(521);
					expression(0);
					}
				}

				}
				break;
			case 2:
				{
				setState(525);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,45,_ctx) ) {
				case 1:
					{
					setState(524);
					simpleStmt();
					}
					break;
				}
				setState(527);
				eos();
				setState(529);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(528);
					expression(0);
					}
				}

				}
				break;
			}
			setState(533);
			match(L_CURLY);
			setState(537);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFAULT || _la==CASE) {
				{
				{
				setState(534);
				exprCaseClause();
				}
				}
				setState(539);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(540);
			match(R_CURLY);
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

	public static class ExprCaseClauseContext extends ParserRuleContext {
		public ExprSwitchCaseContext exprSwitchCase() {
			return getRuleContext(ExprSwitchCaseContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GoParser.COLON, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public ExprCaseClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprCaseClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterExprCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitExprCaseClause(this);
		}
	}

	public final ExprCaseClauseContext exprCaseClause() throws RecognitionException {
		ExprCaseClauseContext _localctx = new ExprCaseClauseContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_exprCaseClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(542);
			exprSwitchCase();
			setState(543);
			match(COLON);
			setState(545);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(544);
				statementList();
				}
				break;
			}
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

	public static class ExprSwitchCaseContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(GoParser.CASE, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(GoParser.DEFAULT, 0); }
		public ExprSwitchCaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprSwitchCase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterExprSwitchCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitExprSwitchCase(this);
		}
	}

	public final ExprSwitchCaseContext exprSwitchCase() throws RecognitionException {
		ExprSwitchCaseContext _localctx = new ExprSwitchCaseContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_exprSwitchCase);
		try {
			setState(550);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(547);
				match(CASE);
				setState(548);
				expressionList();
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 2);
				{
				setState(549);
				match(DEFAULT);
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

	public static class TypeSwitchStmtContext extends ParserRuleContext {
		public TerminalNode SWITCH() { return getToken(GoParser.SWITCH, 0); }
		public TerminalNode L_CURLY() { return getToken(GoParser.L_CURLY, 0); }
		public TerminalNode R_CURLY() { return getToken(GoParser.R_CURLY, 0); }
		public TypeSwitchGuardContext typeSwitchGuard() {
			return getRuleContext(TypeSwitchGuardContext.class,0);
		}
		public EosContext eos() {
			return getRuleContext(EosContext.class,0);
		}
		public SimpleStmtContext simpleStmt() {
			return getRuleContext(SimpleStmtContext.class,0);
		}
		public List<TypeCaseClauseContext> typeCaseClause() {
			return getRuleContexts(TypeCaseClauseContext.class);
		}
		public TypeCaseClauseContext typeCaseClause(int i) {
			return getRuleContext(TypeCaseClauseContext.class,i);
		}
		public TypeSwitchStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeSwitchStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeSwitchStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeSwitchStmt(this);
		}
	}

	public final TypeSwitchStmtContext typeSwitchStmt() throws RecognitionException {
		TypeSwitchStmtContext _localctx = new TypeSwitchStmtContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_typeSwitchStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(552);
			match(SWITCH);
			setState(561);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				setState(553);
				typeSwitchGuard();
				}
				break;
			case 2:
				{
				setState(554);
				eos();
				setState(555);
				typeSwitchGuard();
				}
				break;
			case 3:
				{
				setState(557);
				simpleStmt();
				setState(558);
				eos();
				setState(559);
				typeSwitchGuard();
				}
				break;
			}
			setState(563);
			match(L_CURLY);
			setState(567);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFAULT || _la==CASE) {
				{
				{
				setState(564);
				typeCaseClause();
				}
				}
				setState(569);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(570);
			match(R_CURLY);
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

	public static class TypeSwitchGuardContext extends ParserRuleContext {
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public TerminalNode DOT() { return getToken(GoParser.DOT, 0); }
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public TerminalNode TYPE() { return getToken(GoParser.TYPE, 0); }
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public TerminalNode DECLARE_ASSIGN() { return getToken(GoParser.DECLARE_ASSIGN, 0); }
		public TypeSwitchGuardContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeSwitchGuard; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeSwitchGuard(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeSwitchGuard(this);
		}
	}

	public final TypeSwitchGuardContext typeSwitchGuard() throws RecognitionException {
		TypeSwitchGuardContext _localctx = new TypeSwitchGuardContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_typeSwitchGuard);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(574);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				{
				setState(572);
				match(IDENTIFIER);
				setState(573);
				match(DECLARE_ASSIGN);
				}
				break;
			}
			setState(576);
			primaryExpr(0);
			setState(577);
			match(DOT);
			setState(578);
			match(L_PAREN);
			setState(579);
			match(TYPE);
			setState(580);
			match(R_PAREN);
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

	public static class TypeCaseClauseContext extends ParserRuleContext {
		public TypeSwitchCaseContext typeSwitchCase() {
			return getRuleContext(TypeSwitchCaseContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GoParser.COLON, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public TypeCaseClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeCaseClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeCaseClause(this);
		}
	}

	public final TypeCaseClauseContext typeCaseClause() throws RecognitionException {
		TypeCaseClauseContext _localctx = new TypeCaseClauseContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_typeCaseClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(582);
			typeSwitchCase();
			setState(583);
			match(COLON);
			setState(585);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				{
				setState(584);
				statementList();
				}
				break;
			}
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

	public static class TypeSwitchCaseContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(GoParser.CASE, 0); }
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(GoParser.DEFAULT, 0); }
		public TypeSwitchCaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeSwitchCase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeSwitchCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeSwitchCase(this);
		}
	}

	public final TypeSwitchCaseContext typeSwitchCase() throws RecognitionException {
		TypeSwitchCaseContext _localctx = new TypeSwitchCaseContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_typeSwitchCase);
		try {
			setState(590);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(587);
				match(CASE);
				setState(588);
				typeList();
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 2);
				{
				setState(589);
				match(DEFAULT);
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

	public static class TypeListContext extends ParserRuleContext {
		public List<Type_Context> type_() {
			return getRuleContexts(Type_Context.class);
		}
		public Type_Context type_(int i) {
			return getRuleContext(Type_Context.class,i);
		}
		public List<TerminalNode> NIL_LIT() { return getTokens(GoParser.NIL_LIT); }
		public TerminalNode NIL_LIT(int i) {
			return getToken(GoParser.NIL_LIT, i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GoParser.COMMA, i);
		}
		public TypeListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeList(this);
		}
	}

	public final TypeListContext typeList() throws RecognitionException {
		TypeListContext _localctx = new TypeListContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_typeList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(594);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FUNC:
			case INTERFACE:
			case MAP:
			case STRUCT:
			case CHAN:
			case IDENTIFIER:
			case L_PAREN:
			case L_BRACKET:
			case STAR:
			case RECEIVE:
				{
				setState(592);
				type_();
				}
				break;
			case NIL_LIT:
				{
				setState(593);
				match(NIL_LIT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(603);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(596);
					match(COMMA);
					setState(599);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case FUNC:
					case INTERFACE:
					case MAP:
					case STRUCT:
					case CHAN:
					case IDENTIFIER:
					case L_PAREN:
					case L_BRACKET:
					case STAR:
					case RECEIVE:
						{
						setState(597);
						type_();
						}
						break;
					case NIL_LIT:
						{
						setState(598);
						match(NIL_LIT);
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					} 
				}
				setState(605);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,58,_ctx);
			}
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

	public static class SelectStmtContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(GoParser.SELECT, 0); }
		public TerminalNode L_CURLY() { return getToken(GoParser.L_CURLY, 0); }
		public TerminalNode R_CURLY() { return getToken(GoParser.R_CURLY, 0); }
		public List<CommClauseContext> commClause() {
			return getRuleContexts(CommClauseContext.class);
		}
		public CommClauseContext commClause(int i) {
			return getRuleContext(CommClauseContext.class,i);
		}
		public SelectStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSelectStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSelectStmt(this);
		}
	}

	public final SelectStmtContext selectStmt() throws RecognitionException {
		SelectStmtContext _localctx = new SelectStmtContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_selectStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(606);
			match(SELECT);
			setState(607);
			match(L_CURLY);
			setState(611);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFAULT || _la==CASE) {
				{
				{
				setState(608);
				commClause();
				}
				}
				setState(613);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(614);
			match(R_CURLY);
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

	public static class CommClauseContext extends ParserRuleContext {
		public CommCaseContext commCase() {
			return getRuleContext(CommCaseContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GoParser.COLON, 0); }
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public CommClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterCommClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitCommClause(this);
		}
	}

	public final CommClauseContext commClause() throws RecognitionException {
		CommClauseContext _localctx = new CommClauseContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_commClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(616);
			commCase();
			setState(617);
			match(COLON);
			setState(619);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,60,_ctx) ) {
			case 1:
				{
				setState(618);
				statementList();
				}
				break;
			}
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

	public static class CommCaseContext extends ParserRuleContext {
		public TerminalNode CASE() { return getToken(GoParser.CASE, 0); }
		public SendStmtContext sendStmt() {
			return getRuleContext(SendStmtContext.class,0);
		}
		public RecvStmtContext recvStmt() {
			return getRuleContext(RecvStmtContext.class,0);
		}
		public TerminalNode DEFAULT() { return getToken(GoParser.DEFAULT, 0); }
		public CommCaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commCase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterCommCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitCommCase(this);
		}
	}

	public final CommCaseContext commCase() throws RecognitionException {
		CommCaseContext _localctx = new CommCaseContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_commCase);
		try {
			setState(627);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(621);
				match(CASE);
				setState(624);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
				case 1:
					{
					setState(622);
					sendStmt();
					}
					break;
				case 2:
					{
					setState(623);
					recvStmt();
					}
					break;
				}
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 2);
				{
				setState(626);
				match(DEFAULT);
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

	public static class RecvStmtContext extends ParserRuleContext {
		public ExpressionContext recvExpr;
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(GoParser.ASSIGN, 0); }
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TerminalNode DECLARE_ASSIGN() { return getToken(GoParser.DECLARE_ASSIGN, 0); }
		public RecvStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recvStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterRecvStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitRecvStmt(this);
		}
	}

	public final RecvStmtContext recvStmt() throws RecognitionException {
		RecvStmtContext _localctx = new RecvStmtContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_recvStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(635);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,63,_ctx) ) {
			case 1:
				{
				setState(629);
				expressionList();
				setState(630);
				match(ASSIGN);
				}
				break;
			case 2:
				{
				setState(632);
				identifierList();
				setState(633);
				match(DECLARE_ASSIGN);
				}
				break;
			}
			setState(637);
			((RecvStmtContext)_localctx).recvExpr = expression(0);
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

	public static class ForStmtContext extends ParserRuleContext {
		public TerminalNode FOR() { return getToken(GoParser.FOR, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ForClauseContext forClause() {
			return getRuleContext(ForClauseContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public RangeClauseContext rangeClause() {
			return getRuleContext(RangeClauseContext.class,0);
		}
		public ForStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterForStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitForStmt(this);
		}
	}

	public final ForStmtContext forStmt() throws RecognitionException {
		ForStmtContext _localctx = new ForStmtContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_forStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(639);
			match(FOR);
			setState(647);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,66,_ctx) ) {
			case 1:
				{
				setState(641);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(640);
					expression(0);
					}
				}

				}
				break;
			case 2:
				{
				setState(643);
				forClause();
				}
				break;
			case 3:
				{
				setState(645);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << RANGE) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(644);
					rangeClause();
					}
				}

				}
				break;
			}
			setState(649);
			block();
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

	public static class ForClauseContext extends ParserRuleContext {
		public SimpleStmtContext initStmt;
		public SimpleStmtContext postStmt;
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public List<SimpleStmtContext> simpleStmt() {
			return getRuleContexts(SimpleStmtContext.class);
		}
		public SimpleStmtContext simpleStmt(int i) {
			return getRuleContext(SimpleStmtContext.class,i);
		}
		public ForClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterForClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitForClause(this);
		}
	}

	public final ForClauseContext forClause() throws RecognitionException {
		ForClauseContext _localctx = new ForClauseContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_forClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(652);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				{
				setState(651);
				((ForClauseContext)_localctx).initStmt = simpleStmt();
				}
				break;
			}
			setState(654);
			eos();
			setState(656);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,68,_ctx) ) {
			case 1:
				{
				setState(655);
				expression(0);
				}
				break;
			}
			setState(658);
			eos();
			setState(660);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
				{
				setState(659);
				((ForClauseContext)_localctx).postStmt = simpleStmt();
				}
			}

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

	public static class RangeClauseContext extends ParserRuleContext {
		public TerminalNode RANGE() { return getToken(GoParser.RANGE, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(GoParser.ASSIGN, 0); }
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TerminalNode DECLARE_ASSIGN() { return getToken(GoParser.DECLARE_ASSIGN, 0); }
		public RangeClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rangeClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterRangeClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitRangeClause(this);
		}
	}

	public final RangeClauseContext rangeClause() throws RecognitionException {
		RangeClauseContext _localctx = new RangeClauseContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_rangeClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(668);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				{
				setState(662);
				expressionList();
				setState(663);
				match(ASSIGN);
				}
				break;
			case 2:
				{
				setState(665);
				identifierList();
				setState(666);
				match(DECLARE_ASSIGN);
				}
				break;
			}
			setState(670);
			match(RANGE);
			setState(671);
			expression(0);
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

	public static class GoStmtContext extends ParserRuleContext {
		public TerminalNode GO() { return getToken(GoParser.GO, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public GoStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterGoStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitGoStmt(this);
		}
	}

	public final GoStmtContext goStmt() throws RecognitionException {
		GoStmtContext _localctx = new GoStmtContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_goStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(673);
			match(GO);
			setState(674);
			expression(0);
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

	public static class Type_Context extends ParserRuleContext {
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TypeArgsContext typeArgs() {
			return getRuleContext(TypeArgsContext.class,0);
		}
		public TypeLitContext typeLit() {
			return getRuleContext(TypeLitContext.class,0);
		}
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public Type_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterType_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitType_(this);
		}
	}

	public final Type_Context type_() throws RecognitionException {
		Type_Context _localctx = new Type_Context(_ctx, getState());
		enterRule(_localctx, 118, RULE_type_);
		try {
			setState(685);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(676);
				typeName();
				setState(678);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
				case 1:
					{
					setState(677);
					typeArgs();
					}
					break;
				}
				}
				break;
			case FUNC:
			case INTERFACE:
			case MAP:
			case STRUCT:
			case CHAN:
			case L_BRACKET:
			case STAR:
			case RECEIVE:
				enterOuterAlt(_localctx, 2);
				{
				setState(680);
				typeLit();
				}
				break;
			case L_PAREN:
				enterOuterAlt(_localctx, 3);
				{
				setState(681);
				match(L_PAREN);
				setState(682);
				type_();
				setState(683);
				match(R_PAREN);
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

	public static class TypeArgsContext extends ParserRuleContext {
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public TypeListContext typeList() {
			return getRuleContext(TypeListContext.class,0);
		}
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public TerminalNode COMMA() { return getToken(GoParser.COMMA, 0); }
		public TypeArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeArgs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeArgs(this);
		}
	}

	public final TypeArgsContext typeArgs() throws RecognitionException {
		TypeArgsContext _localctx = new TypeArgsContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_typeArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(687);
			match(L_BRACKET);
			setState(688);
			typeList();
			setState(690);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(689);
				match(COMMA);
				}
			}

			setState(692);
			match(R_BRACKET);
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

	public static class TypeNameContext extends ParserRuleContext {
		public QualifiedIdentContext qualifiedIdent() {
			return getRuleContext(QualifiedIdentContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public TypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeName(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_typeName);
		try {
			setState(696);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(694);
				qualifiedIdent();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(695);
				match(IDENTIFIER);
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

	public static class TypeLitContext extends ParserRuleContext {
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public PointerTypeContext pointerType() {
			return getRuleContext(PointerTypeContext.class,0);
		}
		public FunctionTypeContext functionType() {
			return getRuleContext(FunctionTypeContext.class,0);
		}
		public InterfaceTypeContext interfaceType() {
			return getRuleContext(InterfaceTypeContext.class,0);
		}
		public SliceTypeContext sliceType() {
			return getRuleContext(SliceTypeContext.class,0);
		}
		public MapTypeContext mapType() {
			return getRuleContext(MapTypeContext.class,0);
		}
		public ChannelTypeContext channelType() {
			return getRuleContext(ChannelTypeContext.class,0);
		}
		public TypeLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeLit(this);
		}
	}

	public final TypeLitContext typeLit() throws RecognitionException {
		TypeLitContext _localctx = new TypeLitContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_typeLit);
		try {
			setState(706);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(698);
				arrayType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(699);
				structType();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(700);
				pointerType();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(701);
				functionType();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(702);
				interfaceType();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(703);
				sliceType();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(704);
				mapType();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(705);
				channelType();
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

	public static class ArrayTypeContext extends ParserRuleContext {
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public ArrayLengthContext arrayLength() {
			return getRuleContext(ArrayLengthContext.class,0);
		}
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public ElementTypeContext elementType() {
			return getRuleContext(ElementTypeContext.class,0);
		}
		public ArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitArrayType(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(708);
			match(L_BRACKET);
			setState(709);
			arrayLength();
			setState(710);
			match(R_BRACKET);
			setState(711);
			elementType();
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

	public static class ArrayLengthContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArrayLengthContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLength; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterArrayLength(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitArrayLength(this);
		}
	}

	public final ArrayLengthContext arrayLength() throws RecognitionException {
		ArrayLengthContext _localctx = new ArrayLengthContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_arrayLength);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(713);
			expression(0);
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

	public static class ElementTypeContext extends ParserRuleContext {
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public ElementTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterElementType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitElementType(this);
		}
	}

	public final ElementTypeContext elementType() throws RecognitionException {
		ElementTypeContext _localctx = new ElementTypeContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_elementType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(715);
			type_();
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

	public static class PointerTypeContext extends ParserRuleContext {
		public TerminalNode STAR() { return getToken(GoParser.STAR, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public PointerTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pointerType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterPointerType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitPointerType(this);
		}
	}

	public final PointerTypeContext pointerType() throws RecognitionException {
		PointerTypeContext _localctx = new PointerTypeContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_pointerType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(717);
			match(STAR);
			setState(718);
			type_();
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

	public static class InterfaceTypeContext extends ParserRuleContext {
		public TerminalNode INTERFACE() { return getToken(GoParser.INTERFACE, 0); }
		public TerminalNode L_CURLY() { return getToken(GoParser.L_CURLY, 0); }
		public TerminalNode R_CURLY() { return getToken(GoParser.R_CURLY, 0); }
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public List<MethodSpecContext> methodSpec() {
			return getRuleContexts(MethodSpecContext.class);
		}
		public MethodSpecContext methodSpec(int i) {
			return getRuleContext(MethodSpecContext.class,i);
		}
		public List<TypeElementContext> typeElement() {
			return getRuleContexts(TypeElementContext.class);
		}
		public TypeElementContext typeElement(int i) {
			return getRuleContext(TypeElementContext.class,i);
		}
		public InterfaceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interfaceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitInterfaceType(this);
		}
	}

	public final InterfaceTypeContext interfaceType() throws RecognitionException {
		InterfaceTypeContext _localctx = new InterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_interfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(720);
			match(INTERFACE);
			setState(721);
			match(L_CURLY);
			setState(730);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 3)) & ~0x3f) == 0 && ((1L << (_la - 3)) & ((1L << (FUNC - 3)) | (1L << (INTERFACE - 3)) | (1L << (MAP - 3)) | (1L << (STRUCT - 3)) | (1L << (CHAN - 3)) | (1L << (IDENTIFIER - 3)) | (1L << (L_PAREN - 3)) | (1L << (L_BRACKET - 3)) | (1L << (UNDERLYING - 3)) | (1L << (STAR - 3)) | (1L << (RECEIVE - 3)))) != 0)) {
				{
				{
				setState(724);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,76,_ctx) ) {
				case 1:
					{
					setState(722);
					methodSpec();
					}
					break;
				case 2:
					{
					setState(723);
					typeElement();
					}
					break;
				}
				setState(726);
				eos();
				}
				}
				setState(732);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(733);
			match(R_CURLY);
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

	public static class SliceTypeContext extends ParserRuleContext {
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public ElementTypeContext elementType() {
			return getRuleContext(ElementTypeContext.class,0);
		}
		public SliceTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sliceType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSliceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSliceType(this);
		}
	}

	public final SliceTypeContext sliceType() throws RecognitionException {
		SliceTypeContext _localctx = new SliceTypeContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_sliceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(735);
			match(L_BRACKET);
			setState(736);
			match(R_BRACKET);
			setState(737);
			elementType();
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

	public static class MapTypeContext extends ParserRuleContext {
		public TerminalNode MAP() { return getToken(GoParser.MAP, 0); }
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public ElementTypeContext elementType() {
			return getRuleContext(ElementTypeContext.class,0);
		}
		public MapTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mapType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterMapType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitMapType(this);
		}
	}

	public final MapTypeContext mapType() throws RecognitionException {
		MapTypeContext _localctx = new MapTypeContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_mapType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(739);
			match(MAP);
			setState(740);
			match(L_BRACKET);
			setState(741);
			type_();
			setState(742);
			match(R_BRACKET);
			setState(743);
			elementType();
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

	public static class ChannelTypeContext extends ParserRuleContext {
		public ElementTypeContext elementType() {
			return getRuleContext(ElementTypeContext.class,0);
		}
		public TerminalNode CHAN() { return getToken(GoParser.CHAN, 0); }
		public TerminalNode RECEIVE() { return getToken(GoParser.RECEIVE, 0); }
		public ChannelTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_channelType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterChannelType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitChannelType(this);
		}
	}

	public final ChannelTypeContext channelType() throws RecognitionException {
		ChannelTypeContext _localctx = new ChannelTypeContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_channelType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(750);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				{
				setState(745);
				match(CHAN);
				}
				break;
			case 2:
				{
				setState(746);
				match(CHAN);
				setState(747);
				match(RECEIVE);
				}
				break;
			case 3:
				{
				setState(748);
				match(RECEIVE);
				setState(749);
				match(CHAN);
				}
				break;
			}
			setState(752);
			elementType();
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

	public static class MethodSpecContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ResultContext result() {
			return getRuleContext(ResultContext.class,0);
		}
		public MethodSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodSpec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterMethodSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitMethodSpec(this);
		}
	}

	public final MethodSpecContext methodSpec() throws RecognitionException {
		MethodSpecContext _localctx = new MethodSpecContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_methodSpec);
		try {
			setState(760);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(754);
				match(IDENTIFIER);
				setState(755);
				parameters();
				setState(756);
				result();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(758);
				match(IDENTIFIER);
				setState(759);
				parameters();
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

	public static class FunctionTypeContext extends ParserRuleContext {
		public TerminalNode FUNC() { return getToken(GoParser.FUNC, 0); }
		public SignatureContext signature() {
			return getRuleContext(SignatureContext.class,0);
		}
		public FunctionTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterFunctionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitFunctionType(this);
		}
	}

	public final FunctionTypeContext functionType() throws RecognitionException {
		FunctionTypeContext _localctx = new FunctionTypeContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_functionType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(762);
			match(FUNC);
			setState(763);
			signature();
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

	public static class SignatureContext extends ParserRuleContext {
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public ResultContext result() {
			return getRuleContext(ResultContext.class,0);
		}
		public SignatureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signature; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSignature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSignature(this);
		}
	}

	public final SignatureContext signature() throws RecognitionException {
		SignatureContext _localctx = new SignatureContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_signature);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(765);
			parameters();
			setState(767);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
			case 1:
				{
				setState(766);
				result();
				}
				break;
			}
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

	public static class ResultContext extends ParserRuleContext {
		public ParametersContext parameters() {
			return getRuleContext(ParametersContext.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public ResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_result; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitResult(this);
		}
	}

	public final ResultContext result() throws RecognitionException {
		ResultContext _localctx = new ResultContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_result);
		try {
			setState(771);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,81,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(769);
				parameters();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(770);
				type_();
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

	public static class ParametersContext extends ParserRuleContext {
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public List<ParameterDeclContext> parameterDecl() {
			return getRuleContexts(ParameterDeclContext.class);
		}
		public ParameterDeclContext parameterDecl(int i) {
			return getRuleContext(ParameterDeclContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GoParser.COMMA, i);
		}
		public ParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameters; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitParameters(this);
		}
	}

	public final ParametersContext parameters() throws RecognitionException {
		ParametersContext _localctx = new ParametersContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_parameters);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(773);
			match(L_PAREN);
			setState(785);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 3)) & ~0x3f) == 0 && ((1L << (_la - 3)) & ((1L << (FUNC - 3)) | (1L << (INTERFACE - 3)) | (1L << (MAP - 3)) | (1L << (STRUCT - 3)) | (1L << (CHAN - 3)) | (1L << (IDENTIFIER - 3)) | (1L << (L_PAREN - 3)) | (1L << (L_BRACKET - 3)) | (1L << (ELLIPSIS - 3)) | (1L << (STAR - 3)) | (1L << (RECEIVE - 3)))) != 0)) {
				{
				setState(774);
				parameterDecl();
				setState(779);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(775);
						match(COMMA);
						setState(776);
						parameterDecl();
						}
						} 
					}
					setState(781);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,82,_ctx);
				}
				setState(783);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(782);
					match(COMMA);
					}
				}

				}
			}

			setState(787);
			match(R_PAREN);
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

	public static class ParameterDeclContext extends ParserRuleContext {
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public TerminalNode ELLIPSIS() { return getToken(GoParser.ELLIPSIS, 0); }
		public ParameterDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parameterDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterParameterDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitParameterDecl(this);
		}
	}

	public final ParameterDeclContext parameterDecl() throws RecognitionException {
		ParameterDeclContext _localctx = new ParameterDeclContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_parameterDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(790);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
			case 1:
				{
				setState(789);
				identifierList();
				}
				break;
			}
			setState(793);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELLIPSIS) {
				{
				setState(792);
				match(ELLIPSIS);
				}
			}

			setState(795);
			type_();
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

	public static class ExpressionContext extends ParserRuleContext {
		public Token unary_op;
		public Token mul_op;
		public Token add_op;
		public Token rel_op;
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(GoParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(GoParser.MINUS, 0); }
		public TerminalNode EXCLAMATION() { return getToken(GoParser.EXCLAMATION, 0); }
		public TerminalNode CARET() { return getToken(GoParser.CARET, 0); }
		public TerminalNode STAR() { return getToken(GoParser.STAR, 0); }
		public TerminalNode AMPERSAND() { return getToken(GoParser.AMPERSAND, 0); }
		public TerminalNode RECEIVE() { return getToken(GoParser.RECEIVE, 0); }
		public TerminalNode DIV() { return getToken(GoParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(GoParser.MOD, 0); }
		public TerminalNode LSHIFT() { return getToken(GoParser.LSHIFT, 0); }
		public TerminalNode RSHIFT() { return getToken(GoParser.RSHIFT, 0); }
		public TerminalNode BIT_CLEAR() { return getToken(GoParser.BIT_CLEAR, 0); }
		public TerminalNode OR() { return getToken(GoParser.OR, 0); }
		public TerminalNode EQUALS() { return getToken(GoParser.EQUALS, 0); }
		public TerminalNode NOT_EQUALS() { return getToken(GoParser.NOT_EQUALS, 0); }
		public TerminalNode LESS() { return getToken(GoParser.LESS, 0); }
		public TerminalNode LESS_OR_EQUALS() { return getToken(GoParser.LESS_OR_EQUALS, 0); }
		public TerminalNode GREATER() { return getToken(GoParser.GREATER, 0); }
		public TerminalNode GREATER_OR_EQUALS() { return getToken(GoParser.GREATER_OR_EQUALS, 0); }
		public TerminalNode LOGICAL_AND() { return getToken(GoParser.LOGICAL_AND, 0); }
		public TerminalNode LOGICAL_OR() { return getToken(GoParser.LOGICAL_OR, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitExpression(this);
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
		int _startState = 154;
		enterRecursionRule(_localctx, 154, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(801);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,87,_ctx) ) {
			case 1:
				{
				setState(798);
				primaryExpr(0);
				}
				break;
			case 2:
				{
				setState(799);
				((ExpressionContext)_localctx).unary_op = _input.LT(1);
				_la = _input.LA(1);
				if ( !(((((_la - 58)) & ~0x3f) == 0 && ((1L << (_la - 58)) & ((1L << (EXCLAMATION - 58)) | (1L << (PLUS - 58)) | (1L << (MINUS - 58)) | (1L << (CARET - 58)) | (1L << (STAR - 58)) | (1L << (AMPERSAND - 58)) | (1L << (RECEIVE - 58)))) != 0)) ) {
					((ExpressionContext)_localctx).unary_op = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(800);
				expression(6);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(820);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(818);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,88,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(803);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(804);
						((ExpressionContext)_localctx).mul_op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DIV) | (1L << MOD) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << BIT_CLEAR) | (1L << STAR) | (1L << AMPERSAND))) != 0)) ) {
							((ExpressionContext)_localctx).mul_op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(805);
						expression(6);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(806);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(807);
						((ExpressionContext)_localctx).add_op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OR) | (1L << PLUS) | (1L << MINUS) | (1L << CARET))) != 0)) ) {
							((ExpressionContext)_localctx).add_op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(808);
						expression(5);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(809);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(810);
						((ExpressionContext)_localctx).rel_op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQUALS) | (1L << NOT_EQUALS) | (1L << LESS) | (1L << LESS_OR_EQUALS) | (1L << GREATER) | (1L << GREATER_OR_EQUALS))) != 0)) ) {
							((ExpressionContext)_localctx).rel_op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(811);
						expression(4);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(812);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(813);
						match(LOGICAL_AND);
						setState(814);
						expression(3);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(815);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(816);
						match(LOGICAL_OR);
						setState(817);
						expression(2);
						}
						break;
					}
					} 
				}
				setState(822);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,89,_ctx);
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

	public static class PrimaryExprContext extends ParserRuleContext {
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public ConversionContext conversion() {
			return getRuleContext(ConversionContext.class,0);
		}
		public MethodExprContext methodExpr() {
			return getRuleContext(MethodExprContext.class,0);
		}
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public TerminalNode DOT() { return getToken(GoParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public IndexContext index() {
			return getRuleContext(IndexContext.class,0);
		}
		public Slice_Context slice_() {
			return getRuleContext(Slice_Context.class,0);
		}
		public TypeAssertionContext typeAssertion() {
			return getRuleContext(TypeAssertionContext.class,0);
		}
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public PrimaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterPrimaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitPrimaryExpr(this);
		}
	}

	public final PrimaryExprContext primaryExpr() throws RecognitionException {
		return primaryExpr(0);
	}

	private PrimaryExprContext primaryExpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		PrimaryExprContext _localctx = new PrimaryExprContext(_ctx, _parentState);
		PrimaryExprContext _prevctx = _localctx;
		int _startState = 156;
		enterRecursionRule(_localctx, 156, RULE_primaryExpr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(827);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,90,_ctx) ) {
			case 1:
				{
				setState(824);
				operand();
				}
				break;
			case 2:
				{
				setState(825);
				conversion();
				}
				break;
			case 3:
				{
				setState(826);
				methodExpr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(840);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PrimaryExprContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_primaryExpr);
					setState(829);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(836);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
					case 1:
						{
						setState(830);
						match(DOT);
						setState(831);
						match(IDENTIFIER);
						}
						break;
					case 2:
						{
						setState(832);
						index();
						}
						break;
					case 3:
						{
						setState(833);
						slice_();
						}
						break;
					case 4:
						{
						setState(834);
						typeAssertion();
						}
						break;
					case 5:
						{
						setState(835);
						arguments();
						}
						break;
					}
					}
					} 
				}
				setState(842);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,92,_ctx);
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

	public static class ConversionContext extends ParserRuleContext {
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public TerminalNode COMMA() { return getToken(GoParser.COMMA, 0); }
		public ConversionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conversion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterConversion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitConversion(this);
		}
	}

	public final ConversionContext conversion() throws RecognitionException {
		ConversionContext _localctx = new ConversionContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_conversion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(843);
			type_();
			setState(844);
			match(L_PAREN);
			setState(845);
			expression(0);
			setState(847);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(846);
				match(COMMA);
				}
			}

			setState(849);
			match(R_PAREN);
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

	public static class OperandContext extends ParserRuleContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public OperandNameContext operandName() {
			return getRuleContext(OperandNameContext.class,0);
		}
		public TypeArgsContext typeArgs() {
			return getRuleContext(TypeArgsContext.class,0);
		}
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public OperandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterOperand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitOperand(this);
		}
	}

	public final OperandContext operand() throws RecognitionException {
		OperandContext _localctx = new OperandContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_operand);
		try {
			setState(860);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(851);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(852);
				operandName();
				setState(854);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
				case 1:
					{
					setState(853);
					typeArgs();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(856);
				match(L_PAREN);
				setState(857);
				expression(0);
				setState(858);
				match(R_PAREN);
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

	public static class LiteralContext extends ParserRuleContext {
		public BasicLitContext basicLit() {
			return getRuleContext(BasicLitContext.class,0);
		}
		public CompositeLitContext compositeLit() {
			return getRuleContext(CompositeLitContext.class,0);
		}
		public FunctionLitContext functionLit() {
			return getRuleContext(FunctionLitContext.class,0);
		}
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_literal);
		try {
			setState(865);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NIL_LIT:
			case DECIMAL_LIT:
			case BINARY_LIT:
			case OCTAL_LIT:
			case HEX_LIT:
			case FLOAT_LIT:
			case IMAGINARY_LIT:
			case RUNE_LIT:
			case RAW_STRING_LIT:
			case INTERPRETED_STRING_LIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(862);
				basicLit();
				}
				break;
			case MAP:
			case STRUCT:
			case IDENTIFIER:
			case L_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(863);
				compositeLit();
				}
				break;
			case FUNC:
				enterOuterAlt(_localctx, 3);
				{
				setState(864);
				functionLit();
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

	public static class BasicLitContext extends ParserRuleContext {
		public TerminalNode NIL_LIT() { return getToken(GoParser.NIL_LIT, 0); }
		public IntegerContext integer() {
			return getRuleContext(IntegerContext.class,0);
		}
		public String_Context string_() {
			return getRuleContext(String_Context.class,0);
		}
		public TerminalNode FLOAT_LIT() { return getToken(GoParser.FLOAT_LIT, 0); }
		public BasicLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basicLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterBasicLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitBasicLit(this);
		}
	}

	public final BasicLitContext basicLit() throws RecognitionException {
		BasicLitContext _localctx = new BasicLitContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_basicLit);
		try {
			setState(871);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NIL_LIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(867);
				match(NIL_LIT);
				}
				break;
			case DECIMAL_LIT:
			case BINARY_LIT:
			case OCTAL_LIT:
			case HEX_LIT:
			case IMAGINARY_LIT:
			case RUNE_LIT:
				enterOuterAlt(_localctx, 2);
				{
				setState(868);
				integer();
				}
				break;
			case RAW_STRING_LIT:
			case INTERPRETED_STRING_LIT:
				enterOuterAlt(_localctx, 3);
				{
				setState(869);
				string_();
				}
				break;
			case FLOAT_LIT:
				enterOuterAlt(_localctx, 4);
				{
				setState(870);
				match(FLOAT_LIT);
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

	public static class IntegerContext extends ParserRuleContext {
		public TerminalNode DECIMAL_LIT() { return getToken(GoParser.DECIMAL_LIT, 0); }
		public TerminalNode BINARY_LIT() { return getToken(GoParser.BINARY_LIT, 0); }
		public TerminalNode OCTAL_LIT() { return getToken(GoParser.OCTAL_LIT, 0); }
		public TerminalNode HEX_LIT() { return getToken(GoParser.HEX_LIT, 0); }
		public TerminalNode IMAGINARY_LIT() { return getToken(GoParser.IMAGINARY_LIT, 0); }
		public TerminalNode RUNE_LIT() { return getToken(GoParser.RUNE_LIT, 0); }
		public IntegerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integer; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitInteger(this);
		}
	}

	public final IntegerContext integer() throws RecognitionException {
		IntegerContext _localctx = new IntegerContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_integer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(873);
			_la = _input.LA(1);
			if ( !(((((_la - 65)) & ~0x3f) == 0 && ((1L << (_la - 65)) & ((1L << (DECIMAL_LIT - 65)) | (1L << (BINARY_LIT - 65)) | (1L << (OCTAL_LIT - 65)) | (1L << (HEX_LIT - 65)) | (1L << (IMAGINARY_LIT - 65)) | (1L << (RUNE_LIT - 65)))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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

	public static class OperandNameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public OperandNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operandName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterOperandName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitOperandName(this);
		}
	}

	public final OperandNameContext operandName() throws RecognitionException {
		OperandNameContext _localctx = new OperandNameContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_operandName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(875);
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

	public static class QualifiedIdentContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(GoParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(GoParser.IDENTIFIER, i);
		}
		public TerminalNode DOT() { return getToken(GoParser.DOT, 0); }
		public QualifiedIdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedIdent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterQualifiedIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitQualifiedIdent(this);
		}
	}

	public final QualifiedIdentContext qualifiedIdent() throws RecognitionException {
		QualifiedIdentContext _localctx = new QualifiedIdentContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_qualifiedIdent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(877);
			match(IDENTIFIER);
			setState(878);
			match(DOT);
			setState(879);
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

	public static class CompositeLitContext extends ParserRuleContext {
		public LiteralTypeContext literalType() {
			return getRuleContext(LiteralTypeContext.class,0);
		}
		public LiteralValueContext literalValue() {
			return getRuleContext(LiteralValueContext.class,0);
		}
		public CompositeLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compositeLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterCompositeLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitCompositeLit(this);
		}
	}

	public final CompositeLitContext compositeLit() throws RecognitionException {
		CompositeLitContext _localctx = new CompositeLitContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_compositeLit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(881);
			literalType();
			setState(882);
			literalValue();
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

	public static class LiteralTypeContext extends ParserRuleContext {
		public StructTypeContext structType() {
			return getRuleContext(StructTypeContext.class,0);
		}
		public ArrayTypeContext arrayType() {
			return getRuleContext(ArrayTypeContext.class,0);
		}
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public TerminalNode ELLIPSIS() { return getToken(GoParser.ELLIPSIS, 0); }
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public ElementTypeContext elementType() {
			return getRuleContext(ElementTypeContext.class,0);
		}
		public SliceTypeContext sliceType() {
			return getRuleContext(SliceTypeContext.class,0);
		}
		public MapTypeContext mapType() {
			return getRuleContext(MapTypeContext.class,0);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TypeArgsContext typeArgs() {
			return getRuleContext(TypeArgsContext.class,0);
		}
		public LiteralTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literalType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterLiteralType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitLiteralType(this);
		}
	}

	public final LiteralTypeContext literalType() throws RecognitionException {
		LiteralTypeContext _localctx = new LiteralTypeContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_literalType);
		int _la;
		try {
			setState(896);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(884);
				structType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(885);
				arrayType();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(886);
				match(L_BRACKET);
				setState(887);
				match(ELLIPSIS);
				setState(888);
				match(R_BRACKET);
				setState(889);
				elementType();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(890);
				sliceType();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(891);
				mapType();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(892);
				typeName();
				setState(894);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==L_BRACKET) {
					{
					setState(893);
					typeArgs();
					}
				}

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

	public static class LiteralValueContext extends ParserRuleContext {
		public TerminalNode L_CURLY() { return getToken(GoParser.L_CURLY, 0); }
		public TerminalNode R_CURLY() { return getToken(GoParser.R_CURLY, 0); }
		public ElementListContext elementList() {
			return getRuleContext(ElementListContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(GoParser.COMMA, 0); }
		public LiteralValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literalValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterLiteralValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitLiteralValue(this);
		}
	}

	public final LiteralValueContext literalValue() throws RecognitionException {
		LiteralValueContext _localctx = new LiteralValueContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_literalValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(898);
			match(L_CURLY);
			setState(903);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_CURLY) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
				{
				setState(899);
				elementList();
				setState(901);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(900);
					match(COMMA);
					}
				}

				}
			}

			setState(905);
			match(R_CURLY);
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

	public static class ElementListContext extends ParserRuleContext {
		public List<KeyedElementContext> keyedElement() {
			return getRuleContexts(KeyedElementContext.class);
		}
		public KeyedElementContext keyedElement(int i) {
			return getRuleContext(KeyedElementContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GoParser.COMMA, i);
		}
		public ElementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitElementList(this);
		}
	}

	public final ElementListContext elementList() throws RecognitionException {
		ElementListContext _localctx = new ElementListContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_elementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(907);
			keyedElement();
			setState(912);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,102,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(908);
					match(COMMA);
					setState(909);
					keyedElement();
					}
					} 
				}
				setState(914);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,102,_ctx);
			}
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

	public static class KeyedElementContext extends ParserRuleContext {
		public ElementContext element() {
			return getRuleContext(ElementContext.class,0);
		}
		public KeyContext key() {
			return getRuleContext(KeyContext.class,0);
		}
		public TerminalNode COLON() { return getToken(GoParser.COLON, 0); }
		public KeyedElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyedElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterKeyedElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitKeyedElement(this);
		}
	}

	public final KeyedElementContext keyedElement() throws RecognitionException {
		KeyedElementContext _localctx = new KeyedElementContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_keyedElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(918);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				{
				setState(915);
				key();
				setState(916);
				match(COLON);
				}
				break;
			}
			setState(920);
			element();
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

	public static class KeyContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LiteralValueContext literalValue() {
			return getRuleContext(LiteralValueContext.class,0);
		}
		public KeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitKey(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_key);
		try {
			setState(924);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FUNC:
			case INTERFACE:
			case MAP:
			case STRUCT:
			case CHAN:
			case NIL_LIT:
			case IDENTIFIER:
			case L_PAREN:
			case L_BRACKET:
			case EXCLAMATION:
			case PLUS:
			case MINUS:
			case CARET:
			case STAR:
			case AMPERSAND:
			case RECEIVE:
			case DECIMAL_LIT:
			case BINARY_LIT:
			case OCTAL_LIT:
			case HEX_LIT:
			case FLOAT_LIT:
			case IMAGINARY_LIT:
			case RUNE_LIT:
			case RAW_STRING_LIT:
			case INTERPRETED_STRING_LIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(922);
				expression(0);
				}
				break;
			case L_CURLY:
				enterOuterAlt(_localctx, 2);
				{
				setState(923);
				literalValue();
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

	public static class ElementContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public LiteralValueContext literalValue() {
			return getRuleContext(LiteralValueContext.class,0);
		}
		public ElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_element; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitElement(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_element);
		try {
			setState(928);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case FUNC:
			case INTERFACE:
			case MAP:
			case STRUCT:
			case CHAN:
			case NIL_LIT:
			case IDENTIFIER:
			case L_PAREN:
			case L_BRACKET:
			case EXCLAMATION:
			case PLUS:
			case MINUS:
			case CARET:
			case STAR:
			case AMPERSAND:
			case RECEIVE:
			case DECIMAL_LIT:
			case BINARY_LIT:
			case OCTAL_LIT:
			case HEX_LIT:
			case FLOAT_LIT:
			case IMAGINARY_LIT:
			case RUNE_LIT:
			case RAW_STRING_LIT:
			case INTERPRETED_STRING_LIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(926);
				expression(0);
				}
				break;
			case L_CURLY:
				enterOuterAlt(_localctx, 2);
				{
				setState(927);
				literalValue();
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

	public static class StructTypeContext extends ParserRuleContext {
		public TerminalNode STRUCT() { return getToken(GoParser.STRUCT, 0); }
		public TerminalNode L_CURLY() { return getToken(GoParser.L_CURLY, 0); }
		public TerminalNode R_CURLY() { return getToken(GoParser.R_CURLY, 0); }
		public List<FieldDeclContext> fieldDecl() {
			return getRuleContexts(FieldDeclContext.class);
		}
		public FieldDeclContext fieldDecl(int i) {
			return getRuleContext(FieldDeclContext.class,i);
		}
		public List<EosContext> eos() {
			return getRuleContexts(EosContext.class);
		}
		public EosContext eos(int i) {
			return getRuleContext(EosContext.class,i);
		}
		public StructTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterStructType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitStructType(this);
		}
	}

	public final StructTypeContext structType() throws RecognitionException {
		StructTypeContext _localctx = new StructTypeContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_structType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(930);
			match(STRUCT);
			setState(931);
			match(L_CURLY);
			setState(937);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENTIFIER || _la==STAR) {
				{
				{
				setState(932);
				fieldDecl();
				setState(933);
				eos();
				}
				}
				setState(939);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(940);
			match(R_CURLY);
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

	public static class FieldDeclContext extends ParserRuleContext {
		public String_Context tag;
		public IdentifierListContext identifierList() {
			return getRuleContext(IdentifierListContext.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public EmbeddedFieldContext embeddedField() {
			return getRuleContext(EmbeddedFieldContext.class,0);
		}
		public String_Context string_() {
			return getRuleContext(String_Context.class,0);
		}
		public FieldDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterFieldDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitFieldDecl(this);
		}
	}

	public final FieldDeclContext fieldDecl() throws RecognitionException {
		FieldDeclContext _localctx = new FieldDeclContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_fieldDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(946);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
			case 1:
				{
				setState(942);
				identifierList();
				setState(943);
				type_();
				}
				break;
			case 2:
				{
				setState(945);
				embeddedField();
				}
				break;
			}
			setState(949);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,108,_ctx) ) {
			case 1:
				{
				setState(948);
				((FieldDeclContext)_localctx).tag = string_();
				}
				break;
			}
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

	public static class String_Context extends ParserRuleContext {
		public TerminalNode RAW_STRING_LIT() { return getToken(GoParser.RAW_STRING_LIT, 0); }
		public TerminalNode INTERPRETED_STRING_LIT() { return getToken(GoParser.INTERPRETED_STRING_LIT, 0); }
		public String_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterString_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitString_(this);
		}
	}

	public final String_Context string_() throws RecognitionException {
		String_Context _localctx = new String_Context(_ctx, getState());
		enterRule(_localctx, 190, RULE_string_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(951);
			_la = _input.LA(1);
			if ( !(_la==RAW_STRING_LIT || _la==INTERPRETED_STRING_LIT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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

	public static class EmbeddedFieldContext extends ParserRuleContext {
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode STAR() { return getToken(GoParser.STAR, 0); }
		public TypeArgsContext typeArgs() {
			return getRuleContext(TypeArgsContext.class,0);
		}
		public EmbeddedFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_embeddedField; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterEmbeddedField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitEmbeddedField(this);
		}
	}

	public final EmbeddedFieldContext embeddedField() throws RecognitionException {
		EmbeddedFieldContext _localctx = new EmbeddedFieldContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_embeddedField);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(954);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STAR) {
				{
				setState(953);
				match(STAR);
				}
			}

			setState(956);
			typeName();
			setState(958);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,110,_ctx) ) {
			case 1:
				{
				setState(957);
				typeArgs();
				}
				break;
			}
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

	public static class FunctionLitContext extends ParserRuleContext {
		public TerminalNode FUNC() { return getToken(GoParser.FUNC, 0); }
		public SignatureContext signature() {
			return getRuleContext(SignatureContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FunctionLitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionLit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterFunctionLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitFunctionLit(this);
		}
	}

	public final FunctionLitContext functionLit() throws RecognitionException {
		FunctionLitContext _localctx = new FunctionLitContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_functionLit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(960);
			match(FUNC);
			setState(961);
			signature();
			setState(962);
			block();
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

	public static class IndexContext extends ParserRuleContext {
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public IndexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_index; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitIndex(this);
		}
	}

	public final IndexContext index() throws RecognitionException {
		IndexContext _localctx = new IndexContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(964);
			match(L_BRACKET);
			setState(965);
			expression(0);
			setState(966);
			match(R_BRACKET);
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

	public static class Slice_Context extends ParserRuleContext {
		public TerminalNode L_BRACKET() { return getToken(GoParser.L_BRACKET, 0); }
		public TerminalNode R_BRACKET() { return getToken(GoParser.R_BRACKET, 0); }
		public List<TerminalNode> COLON() { return getTokens(GoParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(GoParser.COLON, i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public Slice_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slice_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterSlice_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitSlice_(this);
		}
	}

	public final Slice_Context slice_() throws RecognitionException {
		Slice_Context _localctx = new Slice_Context(_ctx, getState());
		enterRule(_localctx, 198, RULE_slice_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(968);
			match(L_BRACKET);
			setState(984);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				{
				setState(970);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(969);
					expression(0);
					}
				}

				setState(972);
				match(COLON);
				setState(974);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(973);
					expression(0);
					}
				}

				}
				break;
			case 2:
				{
				setState(977);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(976);
					expression(0);
					}
				}

				setState(979);
				match(COLON);
				setState(980);
				expression(0);
				setState(981);
				match(COLON);
				setState(982);
				expression(0);
				}
				break;
			}
			setState(986);
			match(R_BRACKET);
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

	public static class TypeAssertionContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(GoParser.DOT, 0); }
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public TypeAssertionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeAssertion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterTypeAssertion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitTypeAssertion(this);
		}
	}

	public final TypeAssertionContext typeAssertion() throws RecognitionException {
		TypeAssertionContext _localctx = new TypeAssertionContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_typeAssertion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(988);
			match(DOT);
			setState(989);
			match(L_PAREN);
			setState(990);
			type_();
			setState(991);
			match(R_PAREN);
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

	public static class ArgumentsContext extends ParserRuleContext {
		public TerminalNode L_PAREN() { return getToken(GoParser.L_PAREN, 0); }
		public TerminalNode R_PAREN() { return getToken(GoParser.R_PAREN, 0); }
		public ExpressionListContext expressionList() {
			return getRuleContext(ExpressionListContext.class,0);
		}
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode ELLIPSIS() { return getToken(GoParser.ELLIPSIS, 0); }
		public List<TerminalNode> COMMA() { return getTokens(GoParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GoParser.COMMA, i);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(993);
			match(L_PAREN);
			setState(1008);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
				{
				setState(1000);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,116,_ctx) ) {
				case 1:
					{
					setState(994);
					expressionList();
					}
					break;
				case 2:
					{
					setState(995);
					type_();
					setState(998);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,115,_ctx) ) {
					case 1:
						{
						setState(996);
						match(COMMA);
						setState(997);
						expressionList();
						}
						break;
					}
					}
					break;
				}
				setState(1003);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELLIPSIS) {
					{
					setState(1002);
					match(ELLIPSIS);
					}
				}

				setState(1006);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(1005);
					match(COMMA);
					}
				}

				}
			}

			setState(1010);
			match(R_PAREN);
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

	public static class MethodExprContext extends ParserRuleContext {
		public Type_Context type_() {
			return getRuleContext(Type_Context.class,0);
		}
		public TerminalNode DOT() { return getToken(GoParser.DOT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GoParser.IDENTIFIER, 0); }
		public MethodExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_methodExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterMethodExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitMethodExpr(this);
		}
	}

	public final MethodExprContext methodExpr() throws RecognitionException {
		MethodExprContext _localctx = new MethodExprContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_methodExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1012);
			type_();
			setState(1013);
			match(DOT);
			setState(1014);
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

	public static class EosContext extends ParserRuleContext {
		public TerminalNode SEMI() { return getToken(GoParser.SEMI, 0); }
		public TerminalNode EOF() { return getToken(GoParser.EOF, 0); }
		public TerminalNode EOS() { return getToken(GoParser.EOS, 0); }
		public EosContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eos; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).enterEos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener) ((GoParserListener)listener).exitEos(this);
		}
	}

	public final EosContext eos() throws RecognitionException {
		EosContext _localctx = new EosContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_eos);
		try {
			setState(1020);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1016);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1017);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1018);
				match(EOS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1019);
				if (!(this.closingBracket())) throw new FailedPredicateException(this, "this.closingBracket()");
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 25:
			return statementList_sempred((StatementListContext)_localctx, predIndex);
		case 77:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 78:
			return primaryExpr_sempred((PrimaryExprContext)_localctx, predIndex);
		case 103:
			return eos_sempred((EosContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean statementList_sempred(StatementListContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return this.closingBracket();
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 5);
		case 2:
			return precpred(_ctx, 4);
		case 3:
			return precpred(_ctx, 3);
		case 4:
			return precpred(_ctx, 2);
		case 5:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean primaryExpr_sempred(PrimaryExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 6:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean eos_sempred(EosContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return this.closingBracket();
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\\\u0401\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\3\2\3\2\3\2"+
		"\3\2\3\2\7\2\u00d8\n\2\f\2\16\2\u00db\13\2\3\2\3\2\3\2\5\2\u00e0\n\2\3"+
		"\2\3\2\7\2\u00e4\n\2\f\2\16\2\u00e7\13\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\7\4\u00f4\n\4\f\4\16\4\u00f7\13\4\3\4\5\4\u00fa\n\4\3\5"+
		"\5\5\u00fd\n\5\3\5\3\5\3\6\3\6\3\7\3\7\3\7\5\7\u0106\n\7\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\7\b\u010e\n\b\f\b\16\b\u0111\13\b\3\b\5\b\u0114\n\b\3\t\3\t"+
		"\5\t\u0118\n\t\3\t\3\t\5\t\u011c\n\t\3\n\3\n\3\n\7\n\u0121\n\n\f\n\16"+
		"\n\u0124\13\n\3\13\3\13\3\13\7\13\u0129\n\13\f\13\16\13\u012c\13\13\3"+
		"\f\3\f\3\r\7\r\u0131\n\r\f\r\16\r\u0134\13\r\3\r\3\r\3\r\3\r\3\r\3\r\7"+
		"\r\u013c\n\r\f\r\16\r\u013f\13\r\3\r\5\r\u0142\n\r\3\16\3\16\5\16\u0146"+
		"\n\16\3\17\3\17\3\17\3\17\3\20\3\20\5\20\u014e\n\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\7\21\u0156\n\21\f\21\16\21\u0159\13\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\23\3\23\3\23\7\23\u0163\n\23\f\23\16\23\u0166\13\23\3\24\5\24"+
		"\u0169\n\24\3\24\3\24\3\25\3\25\3\25\5\25\u0170\n\25\3\25\3\25\5\25\u0174"+
		"\n\25\3\26\3\26\3\26\3\26\3\26\5\26\u017b\n\26\3\27\3\27\3\30\3\30\3\30"+
		"\3\30\3\30\3\30\7\30\u0185\n\30\f\30\16\30\u0188\13\30\3\30\5\30\u018b"+
		"\n\30\3\31\3\31\3\31\3\31\5\31\u0191\n\31\3\31\3\31\5\31\u0195\n\31\3"+
		"\32\3\32\5\32\u0199\n\32\3\32\3\32\3\33\5\33\u019e\n\33\3\33\5\33\u01a1"+
		"\n\33\3\33\5\33\u01a4\n\33\3\33\3\33\3\33\6\33\u01a9\n\33\r\33\16\33\u01aa"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\5\34\u01bc\n\34\3\35\3\35\3\35\3\35\3\35\5\35\u01c3\n\35\3\36\3"+
		"\36\3\37\3\37\3\37\3\37\3 \3 \3 \3!\3!\3!\3!\3\"\5\"\u01d3\n\"\3\"\3\""+
		"\3#\3#\3#\3#\3$\3$\3$\5$\u01de\n$\3%\3%\5%\u01e2\n%\3&\3&\5&\u01e6\n&"+
		"\3\'\3\'\5\'\u01ea\n\'\3(\3(\3(\3)\3)\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3"+
		"+\3+\5+\u01fd\n+\3+\3+\3+\3+\5+\u0203\n+\5+\u0205\n+\3,\3,\5,\u0209\n"+
		",\3-\3-\5-\u020d\n-\3-\5-\u0210\n-\3-\3-\5-\u0214\n-\5-\u0216\n-\3-\3"+
		"-\7-\u021a\n-\f-\16-\u021d\13-\3-\3-\3.\3.\3.\5.\u0224\n.\3/\3/\3/\5/"+
		"\u0229\n/\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\3\60\5\60\u0234\n\60"+
		"\3\60\3\60\7\60\u0238\n\60\f\60\16\60\u023b\13\60\3\60\3\60\3\61\3\61"+
		"\5\61\u0241\n\61\3\61\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62\5\62\u024c"+
		"\n\62\3\63\3\63\3\63\5\63\u0251\n\63\3\64\3\64\5\64\u0255\n\64\3\64\3"+
		"\64\3\64\5\64\u025a\n\64\7\64\u025c\n\64\f\64\16\64\u025f\13\64\3\65\3"+
		"\65\3\65\7\65\u0264\n\65\f\65\16\65\u0267\13\65\3\65\3\65\3\66\3\66\3"+
		"\66\5\66\u026e\n\66\3\67\3\67\3\67\5\67\u0273\n\67\3\67\5\67\u0276\n\67"+
		"\38\38\38\38\38\38\58\u027e\n8\38\38\39\39\59\u0284\n9\39\39\59\u0288"+
		"\n9\59\u028a\n9\39\39\3:\5:\u028f\n:\3:\3:\5:\u0293\n:\3:\3:\5:\u0297"+
		"\n:\3;\3;\3;\3;\3;\3;\5;\u029f\n;\3;\3;\3;\3<\3<\3<\3=\3=\5=\u02a9\n="+
		"\3=\3=\3=\3=\3=\5=\u02b0\n=\3>\3>\3>\5>\u02b5\n>\3>\3>\3?\3?\5?\u02bb"+
		"\n?\3@\3@\3@\3@\3@\3@\3@\3@\5@\u02c5\n@\3A\3A\3A\3A\3A\3B\3B\3C\3C\3D"+
		"\3D\3D\3E\3E\3E\3E\5E\u02d7\nE\3E\3E\7E\u02db\nE\fE\16E\u02de\13E\3E\3"+
		"E\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\3H\3H\3H\3H\3H\5H\u02f1\nH\3H\3H\3I\3"+
		"I\3I\3I\3I\3I\5I\u02fb\nI\3J\3J\3J\3K\3K\5K\u0302\nK\3L\3L\5L\u0306\n"+
		"L\3M\3M\3M\3M\7M\u030c\nM\fM\16M\u030f\13M\3M\5M\u0312\nM\5M\u0314\nM"+
		"\3M\3M\3N\5N\u0319\nN\3N\5N\u031c\nN\3N\3N\3O\3O\3O\3O\5O\u0324\nO\3O"+
		"\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\7O\u0335\nO\fO\16O\u0338\13"+
		"O\3P\3P\3P\3P\5P\u033e\nP\3P\3P\3P\3P\3P\3P\3P\5P\u0347\nP\7P\u0349\n"+
		"P\fP\16P\u034c\13P\3Q\3Q\3Q\3Q\5Q\u0352\nQ\3Q\3Q\3R\3R\3R\5R\u0359\nR"+
		"\3R\3R\3R\3R\5R\u035f\nR\3S\3S\3S\5S\u0364\nS\3T\3T\3T\3T\5T\u036a\nT"+
		"\3U\3U\3V\3V\3W\3W\3W\3W\3X\3X\3X\3Y\3Y\3Y\3Y\3Y\3Y\3Y\3Y\3Y\3Y\5Y\u0381"+
		"\nY\5Y\u0383\nY\3Z\3Z\3Z\5Z\u0388\nZ\5Z\u038a\nZ\3Z\3Z\3[\3[\3[\7[\u0391"+
		"\n[\f[\16[\u0394\13[\3\\\3\\\3\\\5\\\u0399\n\\\3\\\3\\\3]\3]\5]\u039f"+
		"\n]\3^\3^\5^\u03a3\n^\3_\3_\3_\3_\3_\7_\u03aa\n_\f_\16_\u03ad\13_\3_\3"+
		"_\3`\3`\3`\3`\5`\u03b5\n`\3`\5`\u03b8\n`\3a\3a\3b\5b\u03bd\nb\3b\3b\5"+
		"b\u03c1\nb\3c\3c\3c\3c\3d\3d\3d\3d\3e\3e\5e\u03cd\ne\3e\3e\5e\u03d1\n"+
		"e\3e\5e\u03d4\ne\3e\3e\3e\3e\3e\5e\u03db\ne\3e\3e\3f\3f\3f\3f\3f\3g\3"+
		"g\3g\3g\3g\5g\u03e9\ng\5g\u03eb\ng\3g\5g\u03ee\ng\3g\5g\u03f1\ng\5g\u03f3"+
		"\ng\3g\3g\3h\3h\3h\3h\3i\3i\3i\3i\5i\u03ff\ni\3i\2\4\u009c\u009ej\2\4"+
		"\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNP"+
		"RTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e"+
		"\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u009e\u00a0\u00a2\u00a4\u00a6"+
		"\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2\u00b4\u00b6\u00b8\u00ba\u00bc\u00be"+
		"\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca\u00cc\u00ce\u00d0\2\f\4\2\35\35("+
		"(\4\2TTVV\3\2)*\4\2\65:=A\3\2<B\4\2\66:@A\4\2\65\65=?\3\2/\64\4\2CFJK"+
		"\3\2QR\2\u0442\2\u00d2\3\2\2\2\4\u00ea\3\2\2\2\6\u00ed\3\2\2\2\b\u00fc"+
		"\3\2\2\2\n\u0100\3\2\2\2\f\u0105\3\2\2\2\16\u0107\3\2\2\2\20\u0115\3\2"+
		"\2\2\22\u011d\3\2\2\2\24\u0125\3\2\2\2\26\u012d\3\2\2\2\30\u0132\3\2\2"+
		"\2\32\u0145\3\2\2\2\34\u0147\3\2\2\2\36\u014b\3\2\2\2 \u0151\3\2\2\2\""+
		"\u015c\3\2\2\2$\u015f\3\2\2\2&\u0168\3\2\2\2(\u016c\3\2\2\2*\u0175\3\2"+
		"\2\2,\u017c\3\2\2\2.\u017e\3\2\2\2\60\u018c\3\2\2\2\62\u0196\3\2\2\2\64"+
		"\u01a8\3\2\2\2\66\u01bb\3\2\2\28\u01c2\3\2\2\2:\u01c4\3\2\2\2<\u01c6\3"+
		"\2\2\2>\u01ca\3\2\2\2@\u01cd\3\2\2\2B\u01d2\3\2\2\2D\u01d6\3\2\2\2F\u01da"+
		"\3\2\2\2H\u01df\3\2\2\2J\u01e3\3\2\2\2L\u01e7\3\2\2\2N\u01eb\3\2\2\2P"+
		"\u01ee\3\2\2\2R\u01f0\3\2\2\2T\u01f3\3\2\2\2V\u0208\3\2\2\2X\u020a\3\2"+
		"\2\2Z\u0220\3\2\2\2\\\u0228\3\2\2\2^\u022a\3\2\2\2`\u0240\3\2\2\2b\u0248"+
		"\3\2\2\2d\u0250\3\2\2\2f\u0254\3\2\2\2h\u0260\3\2\2\2j\u026a\3\2\2\2l"+
		"\u0275\3\2\2\2n\u027d\3\2\2\2p\u0281\3\2\2\2r\u028e\3\2\2\2t\u029e\3\2"+
		"\2\2v\u02a3\3\2\2\2x\u02af\3\2\2\2z\u02b1\3\2\2\2|\u02ba\3\2\2\2~\u02c4"+
		"\3\2\2\2\u0080\u02c6\3\2\2\2\u0082\u02cb\3\2\2\2\u0084\u02cd\3\2\2\2\u0086"+
		"\u02cf\3\2\2\2\u0088\u02d2\3\2\2\2\u008a\u02e1\3\2\2\2\u008c\u02e5\3\2"+
		"\2\2\u008e\u02f0\3\2\2\2\u0090\u02fa\3\2\2\2\u0092\u02fc\3\2\2\2\u0094"+
		"\u02ff\3\2\2\2\u0096\u0305\3\2\2\2\u0098\u0307\3\2\2\2\u009a\u0318\3\2"+
		"\2\2\u009c\u0323\3\2\2\2\u009e\u033d\3\2\2\2\u00a0\u034d\3\2\2\2\u00a2"+
		"\u035e\3\2\2\2\u00a4\u0363\3\2\2\2\u00a6\u0369\3\2\2\2\u00a8\u036b\3\2"+
		"\2\2\u00aa\u036d\3\2\2\2\u00ac\u036f\3\2\2\2\u00ae\u0373\3\2\2\2\u00b0"+
		"\u0382\3\2\2\2\u00b2\u0384\3\2\2\2\u00b4\u038d\3\2\2\2\u00b6\u0398\3\2"+
		"\2\2\u00b8\u039e\3\2\2\2\u00ba\u03a2\3\2\2\2\u00bc\u03a4\3\2\2\2\u00be"+
		"\u03b4\3\2\2\2\u00c0\u03b9\3\2\2\2\u00c2\u03bc\3\2\2\2\u00c4\u03c2\3\2"+
		"\2\2\u00c6\u03c6\3\2\2\2\u00c8\u03ca\3\2\2\2\u00ca\u03de\3\2\2\2\u00cc"+
		"\u03e3\3\2\2\2\u00ce\u03f6\3\2\2\2\u00d0\u03fe\3\2\2\2\u00d2\u00d3\5\4"+
		"\3\2\u00d3\u00d9\5\u00d0i\2\u00d4\u00d5\5\6\4\2\u00d5\u00d6\5\u00d0i\2"+
		"\u00d6\u00d8\3\2\2\2\u00d7\u00d4\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7"+
		"\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00e5\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc"+
		"\u00e0\5(\25\2\u00dd\u00e0\5*\26\2\u00de\u00e0\5\f\7\2\u00df\u00dc\3\2"+
		"\2\2\u00df\u00dd\3\2\2\2\u00df\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00e2\5\u00d0i\2\u00e2\u00e4\3\2\2\2\u00e3\u00df\3\2\2\2\u00e4\u00e7"+
		"\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e8\3\2\2\2\u00e7"+
		"\u00e5\3\2\2\2\u00e8\u00e9\7\2\2\3\u00e9\3\3\2\2\2\u00ea\u00eb\7\20\2"+
		"\2\u00eb\u00ec\7\35\2\2\u00ec\5\3\2\2\2\u00ed\u00f9\7\31\2\2\u00ee\u00fa"+
		"\5\b\5\2\u00ef\u00f5\7\36\2\2\u00f0\u00f1\5\b\5\2\u00f1\u00f2\5\u00d0"+
		"i\2\u00f2\u00f4\3\2\2\2\u00f3\u00f0\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5"+
		"\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f8\3\2\2\2\u00f7\u00f5\3\2"+
		"\2\2\u00f8\u00fa\7\37\2\2\u00f9\u00ee\3\2\2\2\u00f9\u00ef\3\2\2\2\u00fa"+
		"\7\3\2\2\2\u00fb\u00fd\t\2\2\2\u00fc\u00fb\3\2\2\2\u00fc\u00fd\3\2\2\2"+
		"\u00fd\u00fe\3\2\2\2\u00fe\u00ff\5\n\6\2\u00ff\t\3\2\2\2\u0100\u0101\5"+
		"\u00c0a\2\u0101\13\3\2\2\2\u0102\u0106\5\16\b\2\u0103\u0106\5\30\r\2\u0104"+
		"\u0106\5.\30\2\u0105\u0102\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0104\3\2"+
		"\2\2\u0106\r\3\2\2\2\u0107\u0113\7\22\2\2\u0108\u0114\5\20\t\2\u0109\u010f"+
		"\7\36\2\2\u010a\u010b\5\20\t\2\u010b\u010c\5\u00d0i\2\u010c\u010e\3\2"+
		"\2\2\u010d\u010a\3\2\2\2\u010e\u0111\3\2\2\2\u010f\u010d\3\2\2\2\u010f"+
		"\u0110\3\2\2\2\u0110\u0112\3\2\2\2\u0111\u010f\3\2\2\2\u0112\u0114\7\37"+
		"\2\2\u0113\u0108\3\2\2\2\u0113\u0109\3\2\2\2\u0114\17\3\2\2\2\u0115\u011b"+
		"\5\22\n\2\u0116\u0118\5x=\2\u0117\u0116\3\2\2\2\u0117\u0118\3\2\2\2\u0118"+
		"\u0119\3\2\2\2\u0119\u011a\7$\2\2\u011a\u011c\5\24\13\2\u011b\u0117\3"+
		"\2\2\2\u011b\u011c\3\2\2\2\u011c\21\3\2\2\2\u011d\u0122\7\35\2\2\u011e"+
		"\u011f\7%\2\2\u011f\u0121\7\35\2\2\u0120\u011e\3\2\2\2\u0121\u0124\3\2"+
		"\2\2\u0122\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123\23\3\2\2\2\u0124\u0122"+
		"\3\2\2\2\u0125\u012a\5\u009cO\2\u0126\u0127\7%\2\2\u0127\u0129\5\u009c"+
		"O\2\u0128\u0126\3\2\2\2\u0129\u012c\3\2\2\2\u012a\u0128\3\2\2\2\u012a"+
		"\u012b\3\2\2\2\u012b\25\3\2\2\2\u012c\u012a\3\2\2\2\u012d\u012e\t\3\2"+
		"\2\u012e\27\3\2\2\2\u012f\u0131\5\26\f\2\u0130\u012f\3\2\2\2\u0131\u0134"+
		"\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2\2\2\u0134"+
		"\u0132\3\2\2\2\u0135\u0141\7\26\2\2\u0136\u0142\5\32\16\2\u0137\u013d"+
		"\7\36\2\2\u0138\u0139\5\32\16\2\u0139\u013a\5\u00d0i\2\u013a\u013c\3\2"+
		"\2\2\u013b\u0138\3\2\2\2\u013c\u013f\3\2\2\2\u013d\u013b\3\2\2\2\u013d"+
		"\u013e\3\2\2\2\u013e\u0140\3\2\2\2\u013f\u013d\3\2\2\2\u0140\u0142\7\37"+
		"\2\2\u0141\u0136\3\2\2\2\u0141\u0137\3\2\2\2\u0142\31\3\2\2\2\u0143\u0146"+
		"\5\34\17\2\u0144\u0146\5\36\20\2\u0145\u0143\3\2\2\2\u0145\u0144\3\2\2"+
		"\2\u0146\33\3\2\2\2\u0147\u0148\7\35\2\2\u0148\u0149\7$\2\2\u0149\u014a"+
		"\5x=\2\u014a\35\3\2\2\2\u014b\u014d\7\35\2\2\u014c\u014e\5 \21\2\u014d"+
		"\u014c\3\2\2\2\u014d\u014e\3\2\2\2\u014e\u014f\3\2\2\2\u014f\u0150\5x"+
		"=\2\u0150\37\3\2\2\2\u0151\u0152\7\"\2\2\u0152\u0157\5\"\22\2\u0153\u0154"+
		"\7%\2\2\u0154\u0156\5\"\22\2\u0155\u0153\3\2\2\2\u0156\u0159\3\2\2\2\u0157"+
		"\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u015a\3\2\2\2\u0159\u0157\3\2"+
		"\2\2\u015a\u015b\7#\2\2\u015b!\3\2\2\2\u015c\u015d\5\22\n\2\u015d\u015e"+
		"\5$\23\2\u015e#\3\2\2\2\u015f\u0164\5&\24\2\u0160\u0161\7\65\2\2\u0161"+
		"\u0163\5&\24\2\u0162\u0160\3\2\2\2\u0163\u0166\3\2\2\2\u0164\u0162\3\2"+
		"\2\2\u0164\u0165\3\2\2\2\u0165%\3\2\2\2\u0166\u0164\3\2\2\2\u0167\u0169"+
		"\7;\2\2\u0168\u0167\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u016a\3\2\2\2\u016a"+
		"\u016b\5x=\2\u016b\'\3\2\2\2\u016c\u016d\7\5\2\2\u016d\u016f\7\35\2\2"+
		"\u016e\u0170\5 \21\2\u016f\u016e\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u0171"+
		"\3\2\2\2\u0171\u0173\5\u0094K\2\u0172\u0174\5\62\32\2\u0173\u0172\3\2"+
		"\2\2\u0173\u0174\3\2\2\2\u0174)\3\2\2\2\u0175\u0176\7\5\2\2\u0176\u0177"+
		"\5,\27\2\u0177\u0178\7\35\2\2\u0178\u017a\5\u0094K\2\u0179\u017b\5\62"+
		"\32\2\u017a\u0179\3\2\2\2\u017a\u017b\3\2\2\2\u017b+\3\2\2\2\u017c\u017d"+
		"\5\u0098M\2\u017d-\3\2\2\2\u017e\u018a\7\33\2\2\u017f\u018b\5\60\31\2"+
		"\u0180\u0186\7\36\2\2\u0181\u0182\5\60\31\2\u0182\u0183\5\u00d0i\2\u0183"+
		"\u0185\3\2\2\2\u0184\u0181\3\2\2\2\u0185\u0188\3\2\2\2\u0186\u0184\3\2"+
		"\2\2\u0186\u0187\3\2\2\2\u0187\u0189\3\2\2\2\u0188\u0186\3\2\2\2\u0189"+
		"\u018b\7\37\2\2\u018a\u017f\3\2\2\2\u018a\u0180\3\2\2\2\u018b/\3\2\2\2"+
		"\u018c\u0194\5\22\n\2\u018d\u0190\5x=\2\u018e\u018f\7$\2\2\u018f\u0191"+
		"\5\24\13\2\u0190\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191\u0195\3\2\2\2"+
		"\u0192\u0193\7$\2\2\u0193\u0195\5\24\13\2\u0194\u018d\3\2\2\2\u0194\u0192"+
		"\3\2\2\2\u0195\61\3\2\2\2\u0196\u0198\7 \2\2\u0197\u0199\5\64\33\2\u0198"+
		"\u0197\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019a\3\2\2\2\u019a\u019b\7!"+
		"\2\2\u019b\63\3\2\2\2\u019c\u019e\7&\2\2\u019d\u019c\3\2\2\2\u019d\u019e"+
		"\3\2\2\2\u019e\u01a4\3\2\2\2\u019f\u01a1\7[\2\2\u01a0\u019f\3\2\2\2\u01a0"+
		"\u01a1\3\2\2\2\u01a1\u01a4\3\2\2\2\u01a2\u01a4\6\33\2\2\u01a3\u019d\3"+
		"\2\2\2\u01a3\u01a0\3\2\2\2\u01a3\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5"+
		"\u01a6\5\66\34\2\u01a6\u01a7\5\u00d0i\2\u01a7\u01a9\3\2\2\2\u01a8\u01a3"+
		"\3\2\2\2\u01a9\u01aa\3\2\2\2\u01aa\u01a8\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab"+
		"\65\3\2\2\2\u01ac\u01bc\5\f\7\2\u01ad\u01bc\5F$\2\u01ae\u01bc\58\35\2"+
		"\u01af\u01bc\5v<\2\u01b0\u01bc\5H%\2\u01b1\u01bc\5J&\2\u01b2\u01bc\5L"+
		"\'\2\u01b3\u01bc\5N(\2\u01b4\u01bc\5P)\2\u01b5\u01bc\5\62\32\2\u01b6\u01bc"+
		"\5T+\2\u01b7\u01bc\5V,\2\u01b8\u01bc\5h\65\2\u01b9\u01bc\5p9\2\u01ba\u01bc"+
		"\5R*\2\u01bb\u01ac\3\2\2\2\u01bb\u01ad\3\2\2\2\u01bb\u01ae\3\2\2\2\u01bb"+
		"\u01af\3\2\2\2\u01bb\u01b0\3\2\2\2\u01bb\u01b1\3\2\2\2\u01bb\u01b2\3\2"+
		"\2\2\u01bb\u01b3\3\2\2\2\u01bb\u01b4\3\2\2\2\u01bb\u01b5\3\2\2\2\u01bb"+
		"\u01b6\3\2\2\2\u01bb\u01b7\3\2\2\2\u01bb\u01b8\3\2\2\2\u01bb\u01b9\3\2"+
		"\2\2\u01bb\u01ba\3\2\2\2\u01bc\67\3\2\2\2\u01bd\u01c3\5<\37\2\u01be\u01c3"+
		"\5> \2\u01bf\u01c3\5@!\2\u01c0\u01c3\5:\36\2\u01c1\u01c3\5D#\2\u01c2\u01bd"+
		"\3\2\2\2\u01c2\u01be\3\2\2\2\u01c2\u01bf\3\2\2\2\u01c2\u01c0\3\2\2\2\u01c2"+
		"\u01c1\3\2\2\2\u01c39\3\2\2\2\u01c4\u01c5\5\u009cO\2\u01c5;\3\2\2\2\u01c6"+
		"\u01c7\5\u009cO\2\u01c7\u01c8\7B\2\2\u01c8\u01c9\5\u009cO\2\u01c9=\3\2"+
		"\2\2\u01ca\u01cb\5\u009cO\2\u01cb\u01cc\t\4\2\2\u01cc?\3\2\2\2\u01cd\u01ce"+
		"\5\24\13\2\u01ce\u01cf\5B\"\2\u01cf\u01d0\5\24\13\2\u01d0A\3\2\2\2\u01d1"+
		"\u01d3\t\5\2\2\u01d2\u01d1\3\2\2\2\u01d2\u01d3\3\2\2\2\u01d3\u01d4\3\2"+
		"\2\2\u01d4\u01d5\7$\2\2\u01d5C\3\2\2\2\u01d6\u01d7\5\22\n\2\u01d7\u01d8"+
		"\7+\2\2\u01d8\u01d9\5\24\13\2\u01d9E\3\2\2\2\u01da\u01db\7\35\2\2\u01db"+
		"\u01dd\7\'\2\2\u01dc\u01de\5\66\34\2\u01dd\u01dc\3\2\2\2\u01dd\u01de\3"+
		"\2\2\2\u01deG\3\2\2\2\u01df\u01e1\7\32\2\2\u01e0\u01e2\5\24\13\2\u01e1"+
		"\u01e0\3\2\2\2\u01e1\u01e2\3\2\2\2\u01e2I\3\2\2\2\u01e3\u01e5\7\3\2\2"+
		"\u01e4\u01e6\7\35\2\2\u01e5\u01e4\3\2\2\2\u01e5\u01e6\3\2\2\2\u01e6K\3"+
		"\2\2\2\u01e7\u01e9\7\27\2\2\u01e8\u01ea\7\35\2\2\u01e9\u01e8\3\2\2\2\u01e9"+
		"\u01ea\3\2\2\2\u01eaM\3\2\2\2\u01eb\u01ec\7\17\2\2\u01ec\u01ed\7\35\2"+
		"\2\u01edO\3\2\2\2\u01ee\u01ef\7\23\2\2\u01efQ\3\2\2\2\u01f0\u01f1\7\t"+
		"\2\2\u01f1\u01f2\5\u009cO\2\u01f2S\3\2\2\2\u01f3\u01fc\7\24\2\2\u01f4"+
		"\u01fd\5\u009cO\2\u01f5\u01f6\5\u00d0i\2\u01f6\u01f7\5\u009cO\2\u01f7"+
		"\u01fd\3\2\2\2\u01f8\u01f9\58\35\2\u01f9\u01fa\5\u00d0i\2\u01fa\u01fb"+
		"\5\u009cO\2\u01fb\u01fd\3\2\2\2\u01fc\u01f4\3\2\2\2\u01fc\u01f5\3\2\2"+
		"\2\u01fc\u01f8\3\2\2\2\u01fd\u01fe\3\2\2\2\u01fe\u0204\5\62\32\2\u01ff"+
		"\u0202\7\16\2\2\u0200\u0203\5T+\2\u0201\u0203\5\62\32\2\u0202\u0200\3"+
		"\2\2\2\u0202\u0201\3\2\2\2\u0203\u0205\3\2\2\2\u0204\u01ff\3\2\2\2\u0204"+
		"\u0205\3\2\2\2\u0205U\3\2\2\2\u0206\u0209\5X-\2\u0207\u0209\5^\60\2\u0208"+
		"\u0206\3\2\2\2\u0208\u0207\3\2\2\2\u0209W\3\2\2\2\u020a\u0215\7\21\2\2"+
		"\u020b\u020d\5\u009cO\2\u020c\u020b\3\2\2\2\u020c\u020d\3\2\2\2\u020d"+
		"\u0216\3\2\2\2\u020e\u0210\58\35\2\u020f\u020e\3\2\2\2\u020f\u0210\3\2"+
		"\2\2\u0210\u0211\3\2\2\2\u0211\u0213\5\u00d0i\2\u0212\u0214\5\u009cO\2"+
		"\u0213\u0212\3\2\2\2\u0213\u0214\3\2\2\2\u0214\u0216\3\2\2\2\u0215\u020c"+
		"\3\2\2\2\u0215\u020f\3\2\2\2\u0216\u0217\3\2\2\2\u0217\u021b\7 \2\2\u0218"+
		"\u021a\5Z.\2\u0219\u0218\3\2\2\2\u021a\u021d\3\2\2\2\u021b\u0219\3\2\2"+
		"\2\u021b\u021c\3\2\2\2\u021c\u021e\3\2\2\2\u021d\u021b\3\2\2\2\u021e\u021f"+
		"\7!\2\2\u021fY\3\2\2\2\u0220\u0221\5\\/\2\u0221\u0223\7\'\2\2\u0222\u0224"+
		"\5\64\33\2\u0223\u0222\3\2\2\2\u0223\u0224\3\2\2\2\u0224[\3\2\2\2\u0225"+
		"\u0226\7\b\2\2\u0226\u0229\5\24\13\2\u0227\u0229\7\4\2\2\u0228\u0225\3"+
		"\2\2\2\u0228\u0227\3\2\2\2\u0229]\3\2\2\2\u022a\u0233\7\21\2\2\u022b\u0234"+
		"\5`\61\2\u022c\u022d\5\u00d0i\2\u022d\u022e\5`\61\2\u022e\u0234\3\2\2"+
		"\2\u022f\u0230\58\35\2\u0230\u0231\5\u00d0i\2\u0231\u0232\5`\61\2\u0232"+
		"\u0234\3\2\2\2\u0233\u022b\3\2\2\2\u0233\u022c\3\2\2\2\u0233\u022f\3\2"+
		"\2\2\u0234\u0235\3\2\2\2\u0235\u0239\7 \2\2\u0236\u0238\5b\62\2\u0237"+
		"\u0236\3\2\2\2\u0238\u023b\3\2\2\2\u0239\u0237\3\2\2\2\u0239\u023a\3\2"+
		"\2\2\u023a\u023c\3\2\2\2\u023b\u0239\3\2\2\2\u023c\u023d\7!\2\2\u023d"+
		"_\3\2\2\2\u023e\u023f\7\35\2\2\u023f\u0241\7+\2\2\u0240\u023e\3\2\2\2"+
		"\u0240\u0241\3\2\2\2\u0241\u0242\3\2\2\2\u0242\u0243\5\u009eP\2\u0243"+
		"\u0244\7(\2\2\u0244\u0245\7\36\2\2\u0245\u0246\7\26\2\2\u0246\u0247\7"+
		"\37\2\2\u0247a\3\2\2\2\u0248\u0249\5d\63\2\u0249\u024b\7\'\2\2\u024a\u024c"+
		"\5\64\33\2\u024b\u024a\3\2\2\2\u024b\u024c\3\2\2\2\u024cc\3\2\2\2\u024d"+
		"\u024e\7\b\2\2\u024e\u0251\5f\64\2\u024f\u0251\7\4\2\2\u0250\u024d\3\2"+
		"\2\2\u0250\u024f\3\2\2\2\u0251e\3\2\2\2\u0252\u0255\5x=\2\u0253\u0255"+
		"\7\34\2\2\u0254\u0252\3\2\2\2\u0254\u0253\3\2\2\2\u0255\u025d\3\2\2\2"+
		"\u0256\u0259\7%\2\2\u0257\u025a\5x=\2\u0258\u025a\7\34\2\2\u0259\u0257"+
		"\3\2\2\2\u0259\u0258\3\2\2\2\u025a\u025c\3\2\2\2\u025b\u0256\3\2\2\2\u025c"+
		"\u025f\3\2\2\2\u025d\u025b\3\2\2\2\u025d\u025e\3\2\2\2\u025eg\3\2\2\2"+
		"\u025f\u025d\3\2\2\2\u0260\u0261\7\7\2\2\u0261\u0265\7 \2\2\u0262\u0264"+
		"\5j\66\2\u0263\u0262\3\2\2\2\u0264\u0267\3\2\2\2\u0265\u0263\3\2\2\2\u0265"+
		"\u0266\3\2\2\2\u0266\u0268\3\2\2\2\u0267\u0265\3\2\2\2\u0268\u0269\7!"+
		"\2\2\u0269i\3\2\2\2\u026a\u026b\5l\67\2\u026b\u026d\7\'\2\2\u026c\u026e"+
		"\5\64\33\2\u026d\u026c\3\2\2\2\u026d\u026e\3\2\2\2\u026ek\3\2\2\2\u026f"+
		"\u0272\7\b\2\2\u0270\u0273\5<\37\2\u0271\u0273\5n8\2\u0272\u0270\3\2\2"+
		"\2\u0272\u0271\3\2\2\2\u0273\u0276\3\2\2\2\u0274\u0276\7\4\2\2\u0275\u026f"+
		"\3\2\2\2\u0275\u0274\3\2\2\2\u0276m\3\2\2\2\u0277\u0278\5\24\13\2\u0278"+
		"\u0279\7$\2\2\u0279\u027e\3\2\2\2\u027a\u027b\5\22\n\2\u027b\u027c\7+"+
		"\2\2\u027c\u027e\3\2\2\2\u027d\u0277\3\2\2\2\u027d\u027a\3\2\2\2\u027d"+
		"\u027e\3\2\2\2\u027e\u027f\3\2\2\2\u027f\u0280\5\u009cO\2\u0280o\3\2\2"+
		"\2\u0281\u0289\7\30\2\2\u0282\u0284\5\u009cO\2\u0283\u0282\3\2\2\2\u0283"+
		"\u0284\3\2\2\2\u0284\u028a\3\2\2\2\u0285\u028a\5r:\2\u0286\u0288\5t;\2"+
		"\u0287\u0286\3\2\2\2\u0287\u0288\3\2\2\2\u0288\u028a\3\2\2\2\u0289\u0283"+
		"\3\2\2\2\u0289\u0285\3\2\2\2\u0289\u0287\3\2\2\2\u028a\u028b\3\2\2\2\u028b"+
		"\u028c\5\62\32\2\u028cq\3\2\2\2\u028d\u028f\58\35\2\u028e\u028d\3\2\2"+
		"\2\u028e\u028f\3\2\2\2\u028f\u0290\3\2\2\2\u0290\u0292\5\u00d0i\2\u0291"+
		"\u0293\5\u009cO\2\u0292\u0291\3\2\2\2\u0292\u0293\3\2\2\2\u0293\u0294"+
		"\3\2\2\2\u0294\u0296\5\u00d0i\2\u0295\u0297\58\35\2\u0296\u0295\3\2\2"+
		"\2\u0296\u0297\3\2\2\2\u0297s\3\2\2\2\u0298\u0299\5\24\13\2\u0299\u029a"+
		"\7$\2\2\u029a\u029f\3\2\2\2\u029b\u029c\5\22\n\2\u029c\u029d\7+\2\2\u029d"+
		"\u029f\3\2\2\2\u029e\u0298\3\2\2\2\u029e\u029b\3\2\2\2\u029e\u029f\3\2"+
		"\2\2\u029f\u02a0\3\2\2\2\u02a0\u02a1\7\25\2\2\u02a1\u02a2\5\u009cO\2\u02a2"+
		"u\3\2\2\2\u02a3\u02a4\7\n\2\2\u02a4\u02a5\5\u009cO\2\u02a5w\3\2\2\2\u02a6"+
		"\u02a8\5|?\2\u02a7\u02a9\5z>\2\u02a8\u02a7\3\2\2\2\u02a8\u02a9\3\2\2\2"+
		"\u02a9\u02b0\3\2\2\2\u02aa\u02b0\5~@\2\u02ab\u02ac\7\36\2\2\u02ac\u02ad"+
		"\5x=\2\u02ad\u02ae\7\37\2\2\u02ae\u02b0\3\2\2\2\u02af\u02a6\3\2\2\2\u02af"+
		"\u02aa\3\2\2\2\u02af\u02ab\3\2\2\2\u02b0y\3\2\2\2\u02b1\u02b2\7\"\2\2"+
		"\u02b2\u02b4\5f\64\2\u02b3\u02b5\7%\2\2\u02b4\u02b3\3\2\2\2\u02b4\u02b5"+
		"\3\2\2\2\u02b5\u02b6\3\2\2\2\u02b6\u02b7\7#\2\2\u02b7{\3\2\2\2\u02b8\u02bb"+
		"\5\u00acW\2\u02b9\u02bb\7\35\2\2\u02ba\u02b8\3\2\2\2\u02ba\u02b9\3\2\2"+
		"\2\u02bb}\3\2\2\2\u02bc\u02c5\5\u0080A\2\u02bd\u02c5\5\u00bc_\2\u02be"+
		"\u02c5\5\u0086D\2\u02bf\u02c5\5\u0092J\2\u02c0\u02c5\5\u0088E\2\u02c1"+
		"\u02c5\5\u008aF\2\u02c2\u02c5\5\u008cG\2\u02c3\u02c5\5\u008eH\2\u02c4"+
		"\u02bc\3\2\2\2\u02c4\u02bd\3\2\2\2\u02c4\u02be\3\2\2\2\u02c4\u02bf\3\2"+
		"\2\2\u02c4\u02c0\3\2\2\2\u02c4\u02c1\3\2\2\2\u02c4\u02c2\3\2\2\2\u02c4"+
		"\u02c3\3\2\2\2\u02c5\177\3\2\2\2\u02c6\u02c7\7\"\2\2\u02c7\u02c8\5\u0082"+
		"B\2\u02c8\u02c9\7#\2\2\u02c9\u02ca\5\u0084C\2\u02ca\u0081\3\2\2\2\u02cb"+
		"\u02cc\5\u009cO\2\u02cc\u0083\3\2\2\2\u02cd\u02ce\5x=\2\u02ce\u0085\3"+
		"\2\2\2\u02cf\u02d0\7@\2\2\u02d0\u02d1\5x=\2\u02d1\u0087\3\2\2\2\u02d2"+
		"\u02d3\7\6\2\2\u02d3\u02dc\7 \2\2\u02d4\u02d7\5\u0090I\2\u02d5\u02d7\5"+
		"$\23\2\u02d6\u02d4\3\2\2\2\u02d6\u02d5\3\2\2\2\u02d7\u02d8\3\2\2\2\u02d8"+
		"\u02d9\5\u00d0i\2\u02d9\u02db\3\2\2\2\u02da\u02d6\3\2\2\2\u02db\u02de"+
		"\3\2\2\2\u02dc\u02da\3\2\2\2\u02dc\u02dd\3\2\2\2\u02dd\u02df\3\2\2\2\u02de"+
		"\u02dc\3\2\2\2\u02df\u02e0\7!\2\2\u02e0\u0089\3\2\2\2\u02e1\u02e2\7\""+
		"\2\2\u02e2\u02e3\7#\2\2\u02e3\u02e4\5\u0084C\2\u02e4\u008b\3\2\2\2\u02e5"+
		"\u02e6\7\13\2\2\u02e6\u02e7\7\"\2\2\u02e7\u02e8\5x=\2\u02e8\u02e9\7#\2"+
		"\2\u02e9\u02ea\5\u0084C\2\u02ea\u008d\3\2\2\2\u02eb\u02f1\7\r\2\2\u02ec"+
		"\u02ed\7\r\2\2\u02ed\u02f1\7B\2\2\u02ee\u02ef\7B\2\2\u02ef\u02f1\7\r\2"+
		"\2\u02f0\u02eb\3\2\2\2\u02f0\u02ec\3\2\2\2\u02f0\u02ee\3\2\2\2\u02f1\u02f2"+
		"\3\2\2\2\u02f2\u02f3\5\u0084C\2\u02f3\u008f\3\2\2\2\u02f4\u02f5\7\35\2"+
		"\2\u02f5\u02f6\5\u0098M\2\u02f6\u02f7\5\u0096L\2\u02f7\u02fb\3\2\2\2\u02f8"+
		"\u02f9\7\35\2\2\u02f9\u02fb\5\u0098M\2\u02fa\u02f4\3\2\2\2\u02fa\u02f8"+
		"\3\2\2\2\u02fb\u0091\3\2\2\2\u02fc\u02fd\7\5\2\2\u02fd\u02fe\5\u0094K"+
		"\2\u02fe\u0093\3\2\2\2\u02ff\u0301\5\u0098M\2\u0300\u0302\5\u0096L\2\u0301"+
		"\u0300\3\2\2\2\u0301\u0302\3\2\2\2\u0302\u0095\3\2\2\2\u0303\u0306\5\u0098"+
		"M\2\u0304\u0306\5x=\2\u0305\u0303\3\2\2\2\u0305\u0304\3\2\2\2\u0306\u0097"+
		"\3\2\2\2\u0307\u0313\7\36\2\2\u0308\u030d\5\u009aN\2\u0309\u030a\7%\2"+
		"\2\u030a\u030c\5\u009aN\2\u030b\u0309\3\2\2\2\u030c\u030f\3\2\2\2\u030d"+
		"\u030b\3\2\2\2\u030d\u030e\3\2\2\2\u030e\u0311\3\2\2\2\u030f\u030d\3\2"+
		"\2\2\u0310\u0312\7%\2\2\u0311\u0310\3\2\2\2\u0311\u0312\3\2\2\2\u0312"+
		"\u0314\3\2\2\2\u0313\u0308\3\2\2\2\u0313\u0314\3\2\2\2\u0314\u0315\3\2"+
		"\2\2\u0315\u0316\7\37\2\2\u0316\u0099\3\2\2\2\u0317\u0319\5\22\n\2\u0318"+
		"\u0317\3\2\2\2\u0318\u0319\3\2\2\2\u0319\u031b\3\2\2\2\u031a\u031c\7,"+
		"\2\2\u031b\u031a\3\2\2\2\u031b\u031c\3\2\2\2\u031c\u031d\3\2\2\2\u031d"+
		"\u031e\5x=\2\u031e\u009b\3\2\2\2\u031f\u0320\bO\1\2\u0320\u0324\5\u009e"+
		"P\2\u0321\u0322\t\6\2\2\u0322\u0324\5\u009cO\b\u0323\u031f\3\2\2\2\u0323"+
		"\u0321\3\2\2\2\u0324\u0336\3\2\2\2\u0325\u0326\f\7\2\2\u0326\u0327\t\7"+
		"\2\2\u0327\u0335\5\u009cO\b\u0328\u0329\f\6\2\2\u0329\u032a\t\b\2\2\u032a"+
		"\u0335\5\u009cO\7\u032b\u032c\f\5\2\2\u032c\u032d\t\t\2\2\u032d\u0335"+
		"\5\u009cO\6\u032e\u032f\f\4\2\2\u032f\u0330\7.\2\2\u0330\u0335\5\u009c"+
		"O\5\u0331\u0332\f\3\2\2\u0332\u0333\7-\2\2\u0333\u0335\5\u009cO\4\u0334"+
		"\u0325\3\2\2\2\u0334\u0328\3\2\2\2\u0334\u032b\3\2\2\2\u0334\u032e\3\2"+
		"\2\2\u0334\u0331\3\2\2\2\u0335\u0338\3\2\2\2\u0336\u0334\3\2\2\2\u0336"+
		"\u0337\3\2\2\2\u0337\u009d\3\2\2\2\u0338\u0336\3\2\2\2\u0339\u033a\bP"+
		"\1\2\u033a\u033e\5\u00a2R\2\u033b\u033e\5\u00a0Q\2\u033c\u033e\5\u00ce"+
		"h\2\u033d\u0339\3\2\2\2\u033d\u033b\3\2\2\2\u033d\u033c\3\2\2\2\u033e"+
		"\u034a\3\2\2\2\u033f\u0346\f\3\2\2\u0340\u0341\7(\2\2\u0341\u0347\7\35"+
		"\2\2\u0342\u0347\5\u00c6d\2\u0343\u0347\5\u00c8e\2\u0344\u0347\5\u00ca"+
		"f\2\u0345\u0347\5\u00ccg\2\u0346\u0340\3\2\2\2\u0346\u0342\3\2\2\2\u0346"+
		"\u0343\3\2\2\2\u0346\u0344\3\2\2\2\u0346\u0345\3\2\2\2\u0347\u0349\3\2"+
		"\2\2\u0348\u033f\3\2\2\2\u0349\u034c\3\2\2\2\u034a\u0348\3\2\2\2\u034a"+
		"\u034b\3\2\2\2\u034b\u009f\3\2\2\2\u034c\u034a\3\2\2\2\u034d\u034e\5x"+
		"=\2\u034e\u034f\7\36\2\2\u034f\u0351\5\u009cO\2\u0350\u0352\7%\2\2\u0351"+
		"\u0350\3\2\2\2\u0351\u0352\3\2\2\2\u0352\u0353\3\2\2\2\u0353\u0354\7\37"+
		"\2\2\u0354\u00a1\3\2\2\2\u0355\u035f\5\u00a4S\2\u0356\u0358\5\u00aaV\2"+
		"\u0357\u0359\5z>\2\u0358\u0357\3\2\2\2\u0358\u0359\3\2\2\2\u0359\u035f"+
		"\3\2\2\2\u035a\u035b\7\36\2\2\u035b\u035c\5\u009cO\2\u035c\u035d\7\37"+
		"\2\2\u035d\u035f\3\2\2\2\u035e\u0355\3\2\2\2\u035e\u0356\3\2\2\2\u035e"+
		"\u035a\3\2\2\2\u035f\u00a3\3\2\2\2\u0360\u0364\5\u00a6T\2\u0361\u0364"+
		"\5\u00aeX\2\u0362\u0364\5\u00c4c\2\u0363\u0360\3\2\2\2\u0363\u0361\3\2"+
		"\2\2\u0363\u0362\3\2\2\2\u0364\u00a5\3\2\2\2\u0365\u036a\7\34\2\2\u0366"+
		"\u036a\5\u00a8U\2\u0367\u036a\5\u00c0a\2\u0368\u036a\7G\2\2\u0369\u0365"+
		"\3\2\2\2\u0369\u0366\3\2\2\2\u0369\u0367\3\2\2\2\u0369\u0368\3\2\2\2\u036a"+
		"\u00a7\3\2\2\2\u036b\u036c\t\n\2\2\u036c\u00a9\3\2\2\2\u036d\u036e\7\35"+
		"\2\2\u036e\u00ab\3\2\2\2\u036f\u0370\7\35\2\2\u0370\u0371\7(\2\2\u0371"+
		"\u0372\7\35\2\2\u0372\u00ad\3\2\2\2\u0373\u0374\5\u00b0Y\2\u0374\u0375"+
		"\5\u00b2Z\2\u0375\u00af\3\2\2\2\u0376\u0383\5\u00bc_\2\u0377\u0383\5\u0080"+
		"A\2\u0378\u0379\7\"\2\2\u0379\u037a\7,\2\2\u037a\u037b\7#\2\2\u037b\u0383"+
		"\5\u0084C\2\u037c\u0383\5\u008aF\2\u037d\u0383\5\u008cG\2\u037e\u0380"+
		"\5|?\2\u037f\u0381\5z>\2\u0380\u037f\3\2\2\2\u0380\u0381\3\2\2\2\u0381"+
		"\u0383\3\2\2\2\u0382\u0376\3\2\2\2\u0382\u0377\3\2\2\2\u0382\u0378\3\2"+
		"\2\2\u0382\u037c\3\2\2\2\u0382\u037d\3\2\2\2\u0382\u037e\3\2\2\2\u0383"+
		"\u00b1\3\2\2\2\u0384\u0389\7 \2\2\u0385\u0387\5\u00b4[\2\u0386\u0388\7"+
		"%\2\2\u0387\u0386\3\2\2\2\u0387\u0388\3\2\2\2\u0388\u038a\3\2\2\2\u0389"+
		"\u0385\3\2\2\2\u0389\u038a\3\2\2\2\u038a\u038b\3\2\2\2\u038b\u038c\7!"+
		"\2\2\u038c\u00b3\3\2\2\2\u038d\u0392\5\u00b6\\\2\u038e\u038f\7%\2\2\u038f"+
		"\u0391\5\u00b6\\\2\u0390\u038e\3\2\2\2\u0391\u0394\3\2\2\2\u0392\u0390"+
		"\3\2\2\2\u0392\u0393\3\2\2\2\u0393\u00b5\3\2\2\2\u0394\u0392\3\2\2\2\u0395"+
		"\u0396\5\u00b8]\2\u0396\u0397\7\'\2\2\u0397\u0399\3\2\2\2\u0398\u0395"+
		"\3\2\2\2\u0398\u0399\3\2\2\2\u0399\u039a\3\2\2\2\u039a\u039b\5\u00ba^"+
		"\2\u039b\u00b7\3\2\2\2\u039c\u039f\5\u009cO\2\u039d\u039f\5\u00b2Z\2\u039e"+
		"\u039c\3\2\2\2\u039e\u039d\3\2\2\2\u039f\u00b9\3\2\2\2\u03a0\u03a3\5\u009c"+
		"O\2\u03a1\u03a3\5\u00b2Z\2\u03a2\u03a0\3\2\2\2\u03a2\u03a1\3\2\2\2\u03a3"+
		"\u00bb\3\2\2\2\u03a4\u03a5\7\f\2\2\u03a5\u03ab\7 \2\2\u03a6\u03a7\5\u00be"+
		"`\2\u03a7\u03a8\5\u00d0i\2\u03a8\u03aa\3\2\2\2\u03a9\u03a6\3\2\2\2\u03aa"+
		"\u03ad\3\2\2\2\u03ab\u03a9\3\2\2\2\u03ab\u03ac\3\2\2\2\u03ac\u03ae\3\2"+
		"\2\2\u03ad\u03ab\3\2\2\2\u03ae\u03af\7!\2\2\u03af\u00bd\3\2\2\2\u03b0"+
		"\u03b1\5\22\n\2\u03b1\u03b2\5x=\2\u03b2\u03b5\3\2\2\2\u03b3\u03b5\5\u00c2"+
		"b\2\u03b4\u03b0\3\2\2\2\u03b4\u03b3\3\2\2\2\u03b5\u03b7\3\2\2\2\u03b6"+
		"\u03b8\5\u00c0a\2\u03b7\u03b6\3\2\2\2\u03b7\u03b8\3\2\2\2\u03b8\u00bf"+
		"\3\2\2\2\u03b9\u03ba\t\13\2\2\u03ba\u00c1\3\2\2\2\u03bb\u03bd\7@\2\2\u03bc"+
		"\u03bb\3\2\2\2\u03bc\u03bd\3\2\2\2\u03bd\u03be\3\2\2\2\u03be\u03c0\5|"+
		"?\2\u03bf\u03c1\5z>\2\u03c0\u03bf\3\2\2\2\u03c0\u03c1\3\2\2\2\u03c1\u00c3"+
		"\3\2\2\2\u03c2\u03c3\7\5\2\2\u03c3\u03c4\5\u0094K\2\u03c4\u03c5\5\62\32"+
		"\2\u03c5\u00c5\3\2\2\2\u03c6\u03c7\7\"\2\2\u03c7\u03c8\5\u009cO\2\u03c8"+
		"\u03c9\7#\2\2\u03c9\u00c7\3\2\2\2\u03ca\u03da\7\"\2\2\u03cb\u03cd\5\u009c"+
		"O\2\u03cc\u03cb\3\2\2\2\u03cc\u03cd\3\2\2\2\u03cd\u03ce\3\2\2\2\u03ce"+
		"\u03d0\7\'\2\2\u03cf\u03d1\5\u009cO\2\u03d0\u03cf\3\2\2\2\u03d0\u03d1"+
		"\3\2\2\2\u03d1\u03db\3\2\2\2\u03d2\u03d4\5\u009cO\2\u03d3\u03d2\3\2\2"+
		"\2\u03d3\u03d4\3\2\2\2\u03d4\u03d5\3\2\2\2\u03d5\u03d6\7\'\2\2\u03d6\u03d7"+
		"\5\u009cO\2\u03d7\u03d8\7\'\2\2\u03d8\u03d9\5\u009cO\2\u03d9\u03db\3\2"+
		"\2\2\u03da\u03cc\3\2\2\2\u03da\u03d3\3\2\2\2\u03db\u03dc\3\2\2\2\u03dc"+
		"\u03dd\7#\2\2\u03dd\u00c9\3\2\2\2\u03de\u03df\7(\2\2\u03df\u03e0\7\36"+
		"\2\2\u03e0\u03e1\5x=\2\u03e1\u03e2\7\37\2\2\u03e2\u00cb\3\2\2\2\u03e3"+
		"\u03f2\7\36\2\2\u03e4\u03eb\5\24\13\2\u03e5\u03e8\5x=\2\u03e6\u03e7\7"+
		"%\2\2\u03e7\u03e9\5\24\13\2\u03e8\u03e6\3\2\2\2\u03e8\u03e9\3\2\2\2\u03e9"+
		"\u03eb\3\2\2\2\u03ea\u03e4\3\2\2\2\u03ea\u03e5\3\2\2\2\u03eb\u03ed\3\2"+
		"\2\2\u03ec\u03ee\7,\2\2\u03ed\u03ec\3\2\2\2\u03ed\u03ee\3\2\2\2\u03ee"+
		"\u03f0\3\2\2\2\u03ef\u03f1\7%\2\2\u03f0\u03ef\3\2\2\2\u03f0\u03f1\3\2"+
		"\2\2\u03f1\u03f3\3\2\2\2\u03f2\u03ea\3\2\2\2\u03f2\u03f3\3\2\2\2\u03f3"+
		"\u03f4\3\2\2\2\u03f4\u03f5\7\37\2\2\u03f5\u00cd\3\2\2\2\u03f6\u03f7\5"+
		"x=\2\u03f7\u03f8\7(\2\2\u03f8\u03f9\7\35\2\2\u03f9\u00cf\3\2\2\2\u03fa"+
		"\u03ff\7&\2\2\u03fb\u03ff\7\2\2\3\u03fc\u03ff\7[\2\2\u03fd\u03ff\6i\t"+
		"\2\u03fe\u03fa\3\2\2\2\u03fe\u03fb\3\2\2\2\u03fe\u03fc\3\2\2\2\u03fe\u03fd"+
		"\3\2\2\2\u03ff\u00d1\3\2\2\2{\u00d9\u00df\u00e5\u00f5\u00f9\u00fc\u0105"+
		"\u010f\u0113\u0117\u011b\u0122\u012a\u0132\u013d\u0141\u0145\u014d\u0157"+
		"\u0164\u0168\u016f\u0173\u017a\u0186\u018a\u0190\u0194\u0198\u019d\u01a0"+
		"\u01a3\u01aa\u01bb\u01c2\u01d2\u01dd\u01e1\u01e5\u01e9\u01fc\u0202\u0204"+
		"\u0208\u020c\u020f\u0213\u0215\u021b\u0223\u0228\u0233\u0239\u0240\u024b"+
		"\u0250\u0254\u0259\u025d\u0265\u026d\u0272\u0275\u027d\u0283\u0287\u0289"+
		"\u028e\u0292\u0296\u029e\u02a8\u02af\u02b4\u02ba\u02c4\u02d6\u02dc\u02f0"+
		"\u02fa\u0301\u0305\u030d\u0311\u0313\u0318\u031b\u0323\u0334\u0336\u033d"+
		"\u0346\u034a\u0351\u0358\u035e\u0363\u0369\u0380\u0382\u0387\u0389\u0392"+
		"\u0398\u039e\u03a2\u03ab\u03b4\u03b7\u03bc\u03c0\u03cc\u03d0\u03d3\u03da"+
		"\u03e8\u03ea\u03ed\u03f0\u03f2\u03fe";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}