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
		INTERPRETED_STRING_LIT=80, WS=81, TERMINATOR=82, NEWLINE=83, COMMENT=84, 
		LINE_COMMENT=85, WS_NLSEMI=86, COMMENT_NLSEMI=87, LINE_COMMENT_NLSEMI=88, 
		EOS=89, OTHER=90;
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
		"WS", "TERMINATOR", "NEWLINE", "COMMENT", "LINE_COMMENT", "WS_NLSEMI", 
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSourceFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSourceFile(this);
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
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << CONST) | (1L << TYPE) | (1L << VAR))) != 0) || ((((_la - 83)) & ~0x3f) == 0 && ((1L << (_la - 83)) & ((1L << (NEWLINE - 83)) | (1L << (COMMENT - 83)) | (1L << (LINE_COMMENT - 83)))) != 0)) {
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterPackageClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitPackageClause(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterImportDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitImportDecl(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterImportSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitImportSpec(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterImportPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitImportPath(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitDeclaration(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterConstDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitConstDecl(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterConstSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitConstSpec(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterIdentifierList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitIdentifierList(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterExpressionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitExpressionList(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterComment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitComment(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeDecl(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeSpec(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterAliasDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitAliasDecl(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeDef(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeParameters(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeParameterDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeParameterDecl(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeElement(this);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeTerm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeTerm(this);
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
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(GoParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(GoParser.NEWLINE, i);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterFunctionDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitFunctionDecl(this);
		}
	}

	public final FunctionDeclContext functionDecl() throws RecognitionException {
		FunctionDeclContext _localctx = new FunctionDeclContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_functionDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT || _la==LINE_COMMENT) {
				{
				{
				setState(362);
				comment();
				}
				}
				setState(367);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(368);
				match(NEWLINE);
				}
				}
				setState(373);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(374);
			match(FUNC);
			setState(375);
			match(IDENTIFIER);
			setState(377);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==L_BRACKET) {
				{
				setState(376);
				typeParameters();
				}
			}

			setState(379);
			signature();
			setState(381);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,24,_ctx) ) {
			case 1:
				{
				setState(380);
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
		public List<CommentContext> comment() {
			return getRuleContexts(CommentContext.class);
		}
		public CommentContext comment(int i) {
			return getRuleContext(CommentContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(GoParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(GoParser.NEWLINE, i);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterMethodDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitMethodDecl(this);
		}
	}

	public final MethodDeclContext methodDecl() throws RecognitionException {
		MethodDeclContext _localctx = new MethodDeclContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_methodDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(386);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT || _la==LINE_COMMENT) {
				{
				{
				setState(383);
				comment();
				}
				}
				setState(388);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(392);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==NEWLINE) {
				{
				{
				setState(389);
				match(NEWLINE);
				}
				}
				setState(394);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(395);
			match(FUNC);
			setState(396);
			receiver();
			setState(397);
			match(IDENTIFIER);
			setState(398);
			signature();
			setState(400);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				{
				setState(399);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterReceiver(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitReceiver(this);
		}
	}

	public final ReceiverContext receiver() throws RecognitionException {
		ReceiverContext _localctx = new ReceiverContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_receiver);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(402);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterVarDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitVarDecl(this);
		}
	}

	public final VarDeclContext varDecl() throws RecognitionException {
		VarDeclContext _localctx = new VarDeclContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_varDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
			match(VAR);
			setState(416);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				setState(405);
				varSpec();
				}
				break;
			case L_PAREN:
				{
				setState(406);
				match(L_PAREN);
				setState(412);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IDENTIFIER) {
					{
					{
					setState(407);
					varSpec();
					setState(408);
					eos();
					}
					}
					setState(414);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(415);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterVarSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitVarSpec(this);
		}
	}

	public final VarSpecContext varSpec() throws RecognitionException {
		VarSpecContext _localctx = new VarSpecContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_varSpec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(418);
			identifierList();
			setState(426);
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
				setState(419);
				type_();
				setState(422);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
				case 1:
					{
					setState(420);
					match(ASSIGN);
					setState(421);
					expressionList();
					}
					break;
				}
				}
				break;
			case ASSIGN:
				{
				setState(424);
				match(ASSIGN);
				setState(425);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(428);
			match(L_CURLY);
			setState(430);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(429);
				statementList();
				}
				break;
			}
			setState(432);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitStatementList(this);
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
			setState(446); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(441);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
					case 1:
						{
						setState(435);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==SEMI) {
							{
							setState(434);
							match(SEMI);
							}
						}

						}
						break;
					case 2:
						{
						setState(438);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==EOS) {
							{
							setState(437);
							match(EOS);
							}
						}

						}
						break;
					case 3:
						{
						setState(440);
						if (!(this.closingBracket())) throw new FailedPredicateException(this, "this.closingBracket()");
						}
						break;
					}
					setState(443);
					statement();
					setState(444);
					eos();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(448); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_statement);
		try {
			setState(465);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(450);
				declaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(451);
				labeledStmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(452);
				simpleStmt();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(453);
				goStmt();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(454);
				returnStmt();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(455);
				breakStmt();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(456);
				continueStmt();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(457);
				gotoStmt();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(458);
				fallthroughStmt();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(459);
				block();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(460);
				ifStmt();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(461);
				switchStmt();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(462);
				selectStmt();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(463);
				forStmt();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(464);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSimpleStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSimpleStmt(this);
		}
	}

	public final SimpleStmtContext simpleStmt() throws RecognitionException {
		SimpleStmtContext _localctx = new SimpleStmtContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_simpleStmt);
		try {
			setState(472);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(467);
				sendStmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(468);
				incDecStmt();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(469);
				assignment();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(470);
				expressionStmt();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(471);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterExpressionStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitExpressionStmt(this);
		}
	}

	public final ExpressionStmtContext expressionStmt() throws RecognitionException {
		ExpressionStmtContext _localctx = new ExpressionStmtContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_expressionStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(474);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSendStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSendStmt(this);
		}
	}

	public final SendStmtContext sendStmt() throws RecognitionException {
		SendStmtContext _localctx = new SendStmtContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_sendStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(476);
			((SendStmtContext)_localctx).channel = expression(0);
			setState(477);
			match(RECEIVE);
			setState(478);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterIncDecStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitIncDecStmt(this);
		}
	}

	public final IncDecStmtContext incDecStmt() throws RecognitionException {
		IncDecStmtContext _localctx = new IncDecStmtContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_incDecStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(480);
			expression(0);
			setState(481);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitAssignment(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(483);
			expressionList();
			setState(484);
			assign_op();
			setState(485);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterAssign_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitAssign_op(this);
		}
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_assign_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(488);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OR) | (1L << DIV) | (1L << MOD) | (1L << LSHIFT) | (1L << RSHIFT) | (1L << BIT_CLEAR) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0)) {
				{
				setState(487);
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

			setState(490);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterShortVarDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitShortVarDecl(this);
		}
	}

	public final ShortVarDeclContext shortVarDecl() throws RecognitionException {
		ShortVarDeclContext _localctx = new ShortVarDeclContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_shortVarDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(492);
			identifierList();
			setState(493);
			match(DECLARE_ASSIGN);
			setState(494);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterLabeledStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitLabeledStmt(this);
		}
	}

	public final LabeledStmtContext labeledStmt() throws RecognitionException {
		LabeledStmtContext _localctx = new LabeledStmtContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_labeledStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(496);
			match(IDENTIFIER);
			setState(497);
			match(COLON);
			setState(499);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
			case 1:
				{
				setState(498);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterReturnStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitReturnStmt(this);
		}
	}

	public final ReturnStmtContext returnStmt() throws RecognitionException {
		ReturnStmtContext _localctx = new ReturnStmtContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_returnStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(501);
			match(RETURN);
			setState(503);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(502);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterBreakStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitBreakStmt(this);
		}
	}

	public final BreakStmtContext breakStmt() throws RecognitionException {
		BreakStmtContext _localctx = new BreakStmtContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_breakStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(505);
			match(BREAK);
			setState(507);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(506);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterContinueStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitContinueStmt(this);
		}
	}

	public final ContinueStmtContext continueStmt() throws RecognitionException {
		ContinueStmtContext _localctx = new ContinueStmtContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_continueStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(509);
			match(CONTINUE);
			setState(511);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,43,_ctx) ) {
			case 1:
				{
				setState(510);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterGotoStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitGotoStmt(this);
		}
	}

	public final GotoStmtContext gotoStmt() throws RecognitionException {
		GotoStmtContext _localctx = new GotoStmtContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_gotoStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(513);
			match(GOTO);
			setState(514);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterFallthroughStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitFallthroughStmt(this);
		}
	}

	public final FallthroughStmtContext fallthroughStmt() throws RecognitionException {
		FallthroughStmtContext _localctx = new FallthroughStmtContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_fallthroughStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(516);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterDeferStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitDeferStmt(this);
		}
	}

	public final DeferStmtContext deferStmt() throws RecognitionException {
		DeferStmtContext _localctx = new DeferStmtContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_deferStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(518);
			match(DEFER);
			setState(519);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterIfStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitIfStmt(this);
		}
	}

	public final IfStmtContext ifStmt() throws RecognitionException {
		IfStmtContext _localctx = new IfStmtContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_ifStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(521);
			match(IF);
			setState(530);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(522);
				expression(0);
				}
				break;
			case 2:
				{
				setState(523);
				eos();
				setState(524);
				expression(0);
				}
				break;
			case 3:
				{
				setState(526);
				simpleStmt();
				setState(527);
				eos();
				setState(528);
				expression(0);
				}
				break;
			}
			setState(532);
			block();
			setState(538);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,46,_ctx) ) {
			case 1:
				{
				setState(533);
				match(ELSE);
				setState(536);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case IF:
					{
					setState(534);
					ifStmt();
					}
					break;
				case L_CURLY:
					{
					setState(535);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSwitchStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSwitchStmt(this);
		}
	}

	public final SwitchStmtContext switchStmt() throws RecognitionException {
		SwitchStmtContext _localctx = new SwitchStmtContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_switchStmt);
		try {
			setState(542);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(540);
				exprSwitchStmt();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(541);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterExprSwitchStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitExprSwitchStmt(this);
		}
	}

	public final ExprSwitchStmtContext exprSwitchStmt() throws RecognitionException {
		ExprSwitchStmtContext _localctx = new ExprSwitchStmtContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_exprSwitchStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(544);
			match(SWITCH);
			setState(555);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
			case 1:
				{
				setState(546);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(545);
					expression(0);
					}
				}

				}
				break;
			case 2:
				{
				setState(549);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
				case 1:
					{
					setState(548);
					simpleStmt();
					}
					break;
				}
				setState(551);
				eos();
				setState(553);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(552);
					expression(0);
					}
				}

				}
				break;
			}
			setState(557);
			match(L_CURLY);
			setState(561);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFAULT || _la==CASE) {
				{
				{
				setState(558);
				exprCaseClause();
				}
				}
				setState(563);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(564);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterExprCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitExprCaseClause(this);
		}
	}

	public final ExprCaseClauseContext exprCaseClause() throws RecognitionException {
		ExprCaseClauseContext _localctx = new ExprCaseClauseContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_exprCaseClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(566);
			exprSwitchCase();
			setState(567);
			match(COLON);
			setState(569);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
			case 1:
				{
				setState(568);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterExprSwitchCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitExprSwitchCase(this);
		}
	}

	public final ExprSwitchCaseContext exprSwitchCase() throws RecognitionException {
		ExprSwitchCaseContext _localctx = new ExprSwitchCaseContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_exprSwitchCase);
		try {
			setState(574);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(571);
				match(CASE);
				setState(572);
				expressionList();
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 2);
				{
				setState(573);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeSwitchStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeSwitchStmt(this);
		}
	}

	public final TypeSwitchStmtContext typeSwitchStmt() throws RecognitionException {
		TypeSwitchStmtContext _localctx = new TypeSwitchStmtContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_typeSwitchStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(576);
			match(SWITCH);
			setState(585);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,55,_ctx) ) {
			case 1:
				{
				setState(577);
				typeSwitchGuard();
				}
				break;
			case 2:
				{
				setState(578);
				eos();
				setState(579);
				typeSwitchGuard();
				}
				break;
			case 3:
				{
				setState(581);
				simpleStmt();
				setState(582);
				eos();
				setState(583);
				typeSwitchGuard();
				}
				break;
			}
			setState(587);
			match(L_CURLY);
			setState(591);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFAULT || _la==CASE) {
				{
				{
				setState(588);
				typeCaseClause();
				}
				}
				setState(593);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(594);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeSwitchGuard(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeSwitchGuard(this);
		}
	}

	public final TypeSwitchGuardContext typeSwitchGuard() throws RecognitionException {
		TypeSwitchGuardContext _localctx = new TypeSwitchGuardContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_typeSwitchGuard);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(598);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,57,_ctx) ) {
			case 1:
				{
				setState(596);
				match(IDENTIFIER);
				setState(597);
				match(DECLARE_ASSIGN);
				}
				break;
			}
			setState(600);
			primaryExpr(0);
			setState(601);
			match(DOT);
			setState(602);
			match(L_PAREN);
			setState(603);
			match(TYPE);
			setState(604);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeCaseClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeCaseClause(this);
		}
	}

	public final TypeCaseClauseContext typeCaseClause() throws RecognitionException {
		TypeCaseClauseContext _localctx = new TypeCaseClauseContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_typeCaseClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(606);
			typeSwitchCase();
			setState(607);
			match(COLON);
			setState(609);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,58,_ctx) ) {
			case 1:
				{
				setState(608);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeSwitchCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeSwitchCase(this);
		}
	}

	public final TypeSwitchCaseContext typeSwitchCase() throws RecognitionException {
		TypeSwitchCaseContext _localctx = new TypeSwitchCaseContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_typeSwitchCase);
		try {
			setState(614);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(611);
				match(CASE);
				setState(612);
				typeList();
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 2);
				{
				setState(613);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeList(this);
		}
	}

	public final TypeListContext typeList() throws RecognitionException {
		TypeListContext _localctx = new TypeListContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_typeList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(618);
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
				setState(616);
				type_();
				}
				break;
			case NIL_LIT:
				{
				setState(617);
				match(NIL_LIT);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(627);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(620);
					match(COMMA);
					setState(623);
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
						setState(621);
						type_();
						}
						break;
					case NIL_LIT:
						{
						setState(622);
						match(NIL_LIT);
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					} 
				}
				setState(629);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,62,_ctx);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSelectStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSelectStmt(this);
		}
	}

	public final SelectStmtContext selectStmt() throws RecognitionException {
		SelectStmtContext _localctx = new SelectStmtContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_selectStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(630);
			match(SELECT);
			setState(631);
			match(L_CURLY);
			setState(635);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFAULT || _la==CASE) {
				{
				{
				setState(632);
				commClause();
				}
				}
				setState(637);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(638);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterCommClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitCommClause(this);
		}
	}

	public final CommClauseContext commClause() throws RecognitionException {
		CommClauseContext _localctx = new CommClauseContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_commClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(640);
			commCase();
			setState(641);
			match(COLON);
			setState(643);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,64,_ctx) ) {
			case 1:
				{
				setState(642);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterCommCase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitCommCase(this);
		}
	}

	public final CommCaseContext commCase() throws RecognitionException {
		CommCaseContext _localctx = new CommCaseContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_commCase);
		try {
			setState(651);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CASE:
				enterOuterAlt(_localctx, 1);
				{
				setState(645);
				match(CASE);
				setState(648);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
				case 1:
					{
					setState(646);
					sendStmt();
					}
					break;
				case 2:
					{
					setState(647);
					recvStmt();
					}
					break;
				}
				}
				break;
			case DEFAULT:
				enterOuterAlt(_localctx, 2);
				{
				setState(650);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterRecvStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitRecvStmt(this);
		}
	}

	public final RecvStmtContext recvStmt() throws RecognitionException {
		RecvStmtContext _localctx = new RecvStmtContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_recvStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(659);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,67,_ctx) ) {
			case 1:
				{
				setState(653);
				expressionList();
				setState(654);
				match(ASSIGN);
				}
				break;
			case 2:
				{
				setState(656);
				identifierList();
				setState(657);
				match(DECLARE_ASSIGN);
				}
				break;
			}
			setState(661);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterForStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitForStmt(this);
		}
	}

	public final ForStmtContext forStmt() throws RecognitionException {
		ForStmtContext _localctx = new ForStmtContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_forStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(663);
			match(FOR);
			setState(671);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,70,_ctx) ) {
			case 1:
				{
				setState(665);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(664);
					expression(0);
					}
				}

				}
				break;
			case 2:
				{
				setState(667);
				forClause();
				}
				break;
			case 3:
				{
				setState(669);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << RANGE) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(668);
					rangeClause();
					}
				}

				}
				break;
			}
			setState(673);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterForClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitForClause(this);
		}
	}

	public final ForClauseContext forClause() throws RecognitionException {
		ForClauseContext _localctx = new ForClauseContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_forClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(676);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,71,_ctx) ) {
			case 1:
				{
				setState(675);
				((ForClauseContext)_localctx).initStmt = simpleStmt();
				}
				break;
			}
			setState(678);
			eos();
			setState(680);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,72,_ctx) ) {
			case 1:
				{
				setState(679);
				expression(0);
				}
				break;
			}
			setState(682);
			eos();
			setState(684);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
				{
				setState(683);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterRangeClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitRangeClause(this);
		}
	}

	public final RangeClauseContext rangeClause() throws RecognitionException {
		RangeClauseContext _localctx = new RangeClauseContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_rangeClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(692);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,74,_ctx) ) {
			case 1:
				{
				setState(686);
				expressionList();
				setState(687);
				match(ASSIGN);
				}
				break;
			case 2:
				{
				setState(689);
				identifierList();
				setState(690);
				match(DECLARE_ASSIGN);
				}
				break;
			}
			setState(694);
			match(RANGE);
			setState(695);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterGoStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitGoStmt(this);
		}
	}

	public final GoStmtContext goStmt() throws RecognitionException {
		GoStmtContext _localctx = new GoStmtContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_goStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(697);
			match(GO);
			setState(698);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterType_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitType_(this);
		}
	}

	public final Type_Context type_() throws RecognitionException {
		Type_Context _localctx = new Type_Context(_ctx, getState());
		enterRule(_localctx, 118, RULE_type_);
		try {
			setState(709);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(700);
				typeName();
				setState(702);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,75,_ctx) ) {
				case 1:
					{
					setState(701);
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
				setState(704);
				typeLit();
				}
				break;
			case L_PAREN:
				enterOuterAlt(_localctx, 3);
				{
				setState(705);
				match(L_PAREN);
				setState(706);
				type_();
				setState(707);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeArgs(this);
		}
	}

	public final TypeArgsContext typeArgs() throws RecognitionException {
		TypeArgsContext _localctx = new TypeArgsContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_typeArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(711);
			match(L_BRACKET);
			setState(712);
			typeList();
			setState(714);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(713);
				match(COMMA);
				}
			}

			setState(716);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeName(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_typeName);
		try {
			setState(720);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,78,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(718);
				qualifiedIdent();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(719);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeLit(this);
		}
	}

	public final TypeLitContext typeLit() throws RecognitionException {
		TypeLitContext _localctx = new TypeLitContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_typeLit);
		try {
			setState(730);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,79,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(722);
				arrayType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(723);
				structType();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(724);
				pointerType();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(725);
				functionType();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(726);
				interfaceType();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(727);
				sliceType();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(728);
				mapType();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(729);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitArrayType(this);
		}
	}

	public final ArrayTypeContext arrayType() throws RecognitionException {
		ArrayTypeContext _localctx = new ArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_arrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(732);
			match(L_BRACKET);
			setState(733);
			arrayLength();
			setState(734);
			match(R_BRACKET);
			setState(735);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterArrayLength(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitArrayLength(this);
		}
	}

	public final ArrayLengthContext arrayLength() throws RecognitionException {
		ArrayLengthContext _localctx = new ArrayLengthContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_arrayLength);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(737);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterElementType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitElementType(this);
		}
	}

	public final ElementTypeContext elementType() throws RecognitionException {
		ElementTypeContext _localctx = new ElementTypeContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_elementType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(739);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterPointerType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitPointerType(this);
		}
	}

	public final PointerTypeContext pointerType() throws RecognitionException {
		PointerTypeContext _localctx = new PointerTypeContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_pointerType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(741);
			match(STAR);
			setState(742);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterInterfaceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitInterfaceType(this);
		}
	}

	public final InterfaceTypeContext interfaceType() throws RecognitionException {
		InterfaceTypeContext _localctx = new InterfaceTypeContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_interfaceType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(744);
			match(INTERFACE);
			setState(745);
			match(L_CURLY);
			setState(754);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (((((_la - 3)) & ~0x3f) == 0 && ((1L << (_la - 3)) & ((1L << (FUNC - 3)) | (1L << (INTERFACE - 3)) | (1L << (MAP - 3)) | (1L << (STRUCT - 3)) | (1L << (CHAN - 3)) | (1L << (IDENTIFIER - 3)) | (1L << (L_PAREN - 3)) | (1L << (L_BRACKET - 3)) | (1L << (UNDERLYING - 3)) | (1L << (STAR - 3)) | (1L << (RECEIVE - 3)))) != 0)) {
				{
				{
				setState(748);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,80,_ctx) ) {
				case 1:
					{
					setState(746);
					methodSpec();
					}
					break;
				case 2:
					{
					setState(747);
					typeElement();
					}
					break;
				}
				setState(750);
				eos();
				}
				}
				setState(756);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(757);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSliceType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSliceType(this);
		}
	}

	public final SliceTypeContext sliceType() throws RecognitionException {
		SliceTypeContext _localctx = new SliceTypeContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_sliceType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(759);
			match(L_BRACKET);
			setState(760);
			match(R_BRACKET);
			setState(761);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterMapType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitMapType(this);
		}
	}

	public final MapTypeContext mapType() throws RecognitionException {
		MapTypeContext _localctx = new MapTypeContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_mapType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(763);
			match(MAP);
			setState(764);
			match(L_BRACKET);
			setState(765);
			type_();
			setState(766);
			match(R_BRACKET);
			setState(767);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterChannelType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitChannelType(this);
		}
	}

	public final ChannelTypeContext channelType() throws RecognitionException {
		ChannelTypeContext _localctx = new ChannelTypeContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_channelType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(774);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,82,_ctx) ) {
			case 1:
				{
				setState(769);
				match(CHAN);
				}
				break;
			case 2:
				{
				setState(770);
				match(CHAN);
				setState(771);
				match(RECEIVE);
				}
				break;
			case 3:
				{
				setState(772);
				match(RECEIVE);
				setState(773);
				match(CHAN);
				}
				break;
			}
			setState(776);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterMethodSpec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitMethodSpec(this);
		}
	}

	public final MethodSpecContext methodSpec() throws RecognitionException {
		MethodSpecContext _localctx = new MethodSpecContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_methodSpec);
		try {
			setState(784);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,83,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(778);
				match(IDENTIFIER);
				setState(779);
				parameters();
				setState(780);
				result();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(782);
				match(IDENTIFIER);
				setState(783);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterFunctionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitFunctionType(this);
		}
	}

	public final FunctionTypeContext functionType() throws RecognitionException {
		FunctionTypeContext _localctx = new FunctionTypeContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_functionType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(786);
			match(FUNC);
			setState(787);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSignature(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSignature(this);
		}
	}

	public final SignatureContext signature() throws RecognitionException {
		SignatureContext _localctx = new SignatureContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_signature);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(789);
			parameters();
			setState(791);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,84,_ctx) ) {
			case 1:
				{
				setState(790);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitResult(this);
		}
	}

	public final ResultContext result() throws RecognitionException {
		ResultContext _localctx = new ResultContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_result);
		try {
			setState(795);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,85,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(793);
				parameters();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(794);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterParameters(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitParameters(this);
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
			setState(797);
			match(L_PAREN);
			setState(809);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (((((_la - 3)) & ~0x3f) == 0 && ((1L << (_la - 3)) & ((1L << (FUNC - 3)) | (1L << (INTERFACE - 3)) | (1L << (MAP - 3)) | (1L << (STRUCT - 3)) | (1L << (CHAN - 3)) | (1L << (IDENTIFIER - 3)) | (1L << (L_PAREN - 3)) | (1L << (L_BRACKET - 3)) | (1L << (ELLIPSIS - 3)) | (1L << (STAR - 3)) | (1L << (RECEIVE - 3)))) != 0)) {
				{
				setState(798);
				parameterDecl();
				setState(803);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(799);
						match(COMMA);
						setState(800);
						parameterDecl();
						}
						} 
					}
					setState(805);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,86,_ctx);
				}
				setState(807);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(806);
					match(COMMA);
					}
				}

				}
			}

			setState(811);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterParameterDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitParameterDecl(this);
		}
	}

	public final ParameterDeclContext parameterDecl() throws RecognitionException {
		ParameterDeclContext _localctx = new ParameterDeclContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_parameterDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(814);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,89,_ctx) ) {
			case 1:
				{
				setState(813);
				identifierList();
				}
				break;
			}
			setState(817);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELLIPSIS) {
				{
				setState(816);
				match(ELLIPSIS);
				}
			}

			setState(819);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitExpression(this);
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
			setState(825);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,91,_ctx) ) {
			case 1:
				{
				setState(822);
				primaryExpr(0);
				}
				break;
			case 2:
				{
				setState(823);
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
				setState(824);
				expression(6);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(844);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(842);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,92,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(827);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(828);
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
						setState(829);
						expression(6);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(830);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(831);
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
						setState(832);
						expression(5);
						}
						break;
					case 3:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(833);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(834);
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
						setState(835);
						expression(4);
						}
						break;
					case 4:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(836);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(837);
						match(LOGICAL_AND);
						setState(838);
						expression(3);
						}
						break;
					case 5:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(839);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(840);
						match(LOGICAL_OR);
						setState(841);
						expression(2);
						}
						break;
					}
					} 
				}
				setState(846);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,93,_ctx);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterPrimaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitPrimaryExpr(this);
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
			setState(851);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,94,_ctx) ) {
			case 1:
				{
				setState(848);
				operand();
				}
				break;
			case 2:
				{
				setState(849);
				conversion();
				}
				break;
			case 3:
				{
				setState(850);
				methodExpr();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(864);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,96,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new PrimaryExprContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_primaryExpr);
					setState(853);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(860);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,95,_ctx) ) {
					case 1:
						{
						setState(854);
						match(DOT);
						setState(855);
						match(IDENTIFIER);
						}
						break;
					case 2:
						{
						setState(856);
						index();
						}
						break;
					case 3:
						{
						setState(857);
						slice_();
						}
						break;
					case 4:
						{
						setState(858);
						typeAssertion();
						}
						break;
					case 5:
						{
						setState(859);
						arguments();
						}
						break;
					}
					}
					} 
				}
				setState(866);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,96,_ctx);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterConversion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitConversion(this);
		}
	}

	public final ConversionContext conversion() throws RecognitionException {
		ConversionContext _localctx = new ConversionContext(_ctx, getState());
		enterRule(_localctx, 158, RULE_conversion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(867);
			type_();
			setState(868);
			match(L_PAREN);
			setState(869);
			expression(0);
			setState(871);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(870);
				match(COMMA);
				}
			}

			setState(873);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterOperand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitOperand(this);
		}
	}

	public final OperandContext operand() throws RecognitionException {
		OperandContext _localctx = new OperandContext(_ctx, getState());
		enterRule(_localctx, 160, RULE_operand);
		try {
			setState(884);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,99,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(875);
				literal();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(876);
				operandName();
				setState(878);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,98,_ctx) ) {
				case 1:
					{
					setState(877);
					typeArgs();
					}
					break;
				}
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(880);
				match(L_PAREN);
				setState(881);
				expression(0);
				setState(882);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 162, RULE_literal);
		try {
			setState(889);
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
				setState(886);
				basicLit();
				}
				break;
			case MAP:
			case STRUCT:
			case IDENTIFIER:
			case L_BRACKET:
				enterOuterAlt(_localctx, 2);
				{
				setState(887);
				compositeLit();
				}
				break;
			case FUNC:
				enterOuterAlt(_localctx, 3);
				{
				setState(888);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterBasicLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitBasicLit(this);
		}
	}

	public final BasicLitContext basicLit() throws RecognitionException {
		BasicLitContext _localctx = new BasicLitContext(_ctx, getState());
		enterRule(_localctx, 164, RULE_basicLit);
		try {
			setState(895);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NIL_LIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(891);
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
				setState(892);
				integer();
				}
				break;
			case RAW_STRING_LIT:
			case INTERPRETED_STRING_LIT:
				enterOuterAlt(_localctx, 3);
				{
				setState(893);
				string_();
				}
				break;
			case FLOAT_LIT:
				enterOuterAlt(_localctx, 4);
				{
				setState(894);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitInteger(this);
		}
	}

	public final IntegerContext integer() throws RecognitionException {
		IntegerContext _localctx = new IntegerContext(_ctx, getState());
		enterRule(_localctx, 166, RULE_integer);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(897);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterOperandName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitOperandName(this);
		}
	}

	public final OperandNameContext operandName() throws RecognitionException {
		OperandNameContext _localctx = new OperandNameContext(_ctx, getState());
		enterRule(_localctx, 168, RULE_operandName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(899);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterQualifiedIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitQualifiedIdent(this);
		}
	}

	public final QualifiedIdentContext qualifiedIdent() throws RecognitionException {
		QualifiedIdentContext _localctx = new QualifiedIdentContext(_ctx, getState());
		enterRule(_localctx, 170, RULE_qualifiedIdent);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(901);
			match(IDENTIFIER);
			setState(902);
			match(DOT);
			setState(903);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterCompositeLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitCompositeLit(this);
		}
	}

	public final CompositeLitContext compositeLit() throws RecognitionException {
		CompositeLitContext _localctx = new CompositeLitContext(_ctx, getState());
		enterRule(_localctx, 172, RULE_compositeLit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(905);
			literalType();
			setState(906);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterLiteralType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitLiteralType(this);
		}
	}

	public final LiteralTypeContext literalType() throws RecognitionException {
		LiteralTypeContext _localctx = new LiteralTypeContext(_ctx, getState());
		enterRule(_localctx, 174, RULE_literalType);
		int _la;
		try {
			setState(920);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,103,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(908);
				structType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(909);
				arrayType();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(910);
				match(L_BRACKET);
				setState(911);
				match(ELLIPSIS);
				setState(912);
				match(R_BRACKET);
				setState(913);
				elementType();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(914);
				sliceType();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(915);
				mapType();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(916);
				typeName();
				setState(918);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==L_BRACKET) {
					{
					setState(917);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterLiteralValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitLiteralValue(this);
		}
	}

	public final LiteralValueContext literalValue() throws RecognitionException {
		LiteralValueContext _localctx = new LiteralValueContext(_ctx, getState());
		enterRule(_localctx, 176, RULE_literalValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(922);
			match(L_CURLY);
			setState(927);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_CURLY) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
				{
				setState(923);
				elementList();
				setState(925);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(924);
					match(COMMA);
					}
				}

				}
			}

			setState(929);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterElementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitElementList(this);
		}
	}

	public final ElementListContext elementList() throws RecognitionException {
		ElementListContext _localctx = new ElementListContext(_ctx, getState());
		enterRule(_localctx, 178, RULE_elementList);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(931);
			keyedElement();
			setState(936);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,106,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(932);
					match(COMMA);
					setState(933);
					keyedElement();
					}
					} 
				}
				setState(938);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,106,_ctx);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterKeyedElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitKeyedElement(this);
		}
	}

	public final KeyedElementContext keyedElement() throws RecognitionException {
		KeyedElementContext _localctx = new KeyedElementContext(_ctx, getState());
		enterRule(_localctx, 180, RULE_keyedElement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(942);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,107,_ctx) ) {
			case 1:
				{
				setState(939);
				key();
				setState(940);
				match(COLON);
				}
				break;
			}
			setState(944);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitKey(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 182, RULE_key);
		try {
			setState(948);
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
				setState(946);
				expression(0);
				}
				break;
			case L_CURLY:
				enterOuterAlt(_localctx, 2);
				{
				setState(947);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitElement(this);
		}
	}

	public final ElementContext element() throws RecognitionException {
		ElementContext _localctx = new ElementContext(_ctx, getState());
		enterRule(_localctx, 184, RULE_element);
		try {
			setState(952);
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
				setState(950);
				expression(0);
				}
				break;
			case L_CURLY:
				enterOuterAlt(_localctx, 2);
				{
				setState(951);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterStructType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitStructType(this);
		}
	}

	public final StructTypeContext structType() throws RecognitionException {
		StructTypeContext _localctx = new StructTypeContext(_ctx, getState());
		enterRule(_localctx, 186, RULE_structType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(954);
			match(STRUCT);
			setState(955);
			match(L_CURLY);
			setState(961);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENTIFIER || _la==STAR) {
				{
				{
				setState(956);
				fieldDecl();
				setState(957);
				eos();
				}
				}
				setState(963);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(964);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterFieldDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitFieldDecl(this);
		}
	}

	public final FieldDeclContext fieldDecl() throws RecognitionException {
		FieldDeclContext _localctx = new FieldDeclContext(_ctx, getState());
		enterRule(_localctx, 188, RULE_fieldDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(970);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,111,_ctx) ) {
			case 1:
				{
				setState(966);
				identifierList();
				setState(967);
				type_();
				}
				break;
			case 2:
				{
				setState(969);
				embeddedField();
				}
				break;
			}
			setState(973);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,112,_ctx) ) {
			case 1:
				{
				setState(972);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterString_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitString_(this);
		}
	}

	public final String_Context string_() throws RecognitionException {
		String_Context _localctx = new String_Context(_ctx, getState());
		enterRule(_localctx, 190, RULE_string_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(975);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterEmbeddedField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitEmbeddedField(this);
		}
	}

	public final EmbeddedFieldContext embeddedField() throws RecognitionException {
		EmbeddedFieldContext _localctx = new EmbeddedFieldContext(_ctx, getState());
		enterRule(_localctx, 192, RULE_embeddedField);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(978);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STAR) {
				{
				setState(977);
				match(STAR);
				}
			}

			setState(980);
			typeName();
			setState(982);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,114,_ctx) ) {
			case 1:
				{
				setState(981);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterFunctionLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitFunctionLit(this);
		}
	}

	public final FunctionLitContext functionLit() throws RecognitionException {
		FunctionLitContext _localctx = new FunctionLitContext(_ctx, getState());
		enterRule(_localctx, 194, RULE_functionLit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(984);
			match(FUNC);
			setState(985);
			signature();
			setState(986);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterIndex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitIndex(this);
		}
	}

	public final IndexContext index() throws RecognitionException {
		IndexContext _localctx = new IndexContext(_ctx, getState());
		enterRule(_localctx, 196, RULE_index);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(988);
			match(L_BRACKET);
			setState(989);
			expression(0);
			setState(990);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterSlice_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitSlice_(this);
		}
	}

	public final Slice_Context slice_() throws RecognitionException {
		Slice_Context _localctx = new Slice_Context(_ctx, getState());
		enterRule(_localctx, 198, RULE_slice_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(992);
			match(L_BRACKET);
			setState(1008);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,118,_ctx) ) {
			case 1:
				{
				setState(994);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(993);
					expression(0);
					}
				}

				setState(996);
				match(COLON);
				setState(998);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(997);
					expression(0);
					}
				}

				}
				break;
			case 2:
				{
				setState(1001);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
					{
					setState(1000);
					expression(0);
					}
				}

				setState(1003);
				match(COLON);
				setState(1004);
				expression(0);
				setState(1005);
				match(COLON);
				setState(1006);
				expression(0);
				}
				break;
			}
			setState(1010);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterTypeAssertion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitTypeAssertion(this);
		}
	}

	public final TypeAssertionContext typeAssertion() throws RecognitionException {
		TypeAssertionContext _localctx = new TypeAssertionContext(_ctx, getState());
		enterRule(_localctx, 200, RULE_typeAssertion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1012);
			match(DOT);
			setState(1013);
			match(L_PAREN);
			setState(1014);
			type_();
			setState(1015);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitArguments(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 202, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1017);
			match(L_PAREN);
			setState(1032);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << FUNC) | (1L << INTERFACE) | (1L << MAP) | (1L << STRUCT) | (1L << CHAN) | (1L << NIL_LIT) | (1L << IDENTIFIER) | (1L << L_PAREN) | (1L << L_BRACKET) | (1L << EXCLAMATION) | (1L << PLUS) | (1L << MINUS) | (1L << CARET) | (1L << STAR) | (1L << AMPERSAND))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (RECEIVE - 64)) | (1L << (DECIMAL_LIT - 64)) | (1L << (BINARY_LIT - 64)) | (1L << (OCTAL_LIT - 64)) | (1L << (HEX_LIT - 64)) | (1L << (FLOAT_LIT - 64)) | (1L << (IMAGINARY_LIT - 64)) | (1L << (RUNE_LIT - 64)) | (1L << (RAW_STRING_LIT - 64)) | (1L << (INTERPRETED_STRING_LIT - 64)))) != 0)) {
				{
				setState(1024);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,120,_ctx) ) {
				case 1:
					{
					setState(1018);
					expressionList();
					}
					break;
				case 2:
					{
					setState(1019);
					type_();
					setState(1022);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,119,_ctx) ) {
					case 1:
						{
						setState(1020);
						match(COMMA);
						setState(1021);
						expressionList();
						}
						break;
					}
					}
					break;
				}
				setState(1027);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELLIPSIS) {
					{
					setState(1026);
					match(ELLIPSIS);
					}
				}

				setState(1030);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(1029);
					match(COMMA);
					}
				}

				}
			}

			setState(1034);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterMethodExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitMethodExpr(this);
		}
	}

	public final MethodExprContext methodExpr() throws RecognitionException {
		MethodExprContext _localctx = new MethodExprContext(_ctx, getState());
		enterRule(_localctx, 204, RULE_methodExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(1036);
			type_();
			setState(1037);
			match(DOT);
			setState(1038);
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
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).enterEos(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GoParserListener ) ((GoParserListener)listener).exitEos(this);
		}
	}

	public final EosContext eos() throws RecognitionException {
		EosContext _localctx = new EosContext(_ctx, getState());
		enterRule(_localctx, 206, RULE_eos);
		try {
			setState(1044);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,124,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(1040);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(1041);
				match(EOF);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(1042);
				match(EOS);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(1043);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\\\u0419\4\2\t\2\4"+
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
		"\u0169\n\24\3\24\3\24\3\25\7\25\u016e\n\25\f\25\16\25\u0171\13\25\3\25"+
		"\7\25\u0174\n\25\f\25\16\25\u0177\13\25\3\25\3\25\3\25\5\25\u017c\n\25"+
		"\3\25\3\25\5\25\u0180\n\25\3\26\7\26\u0183\n\26\f\26\16\26\u0186\13\26"+
		"\3\26\7\26\u0189\n\26\f\26\16\26\u018c\13\26\3\26\3\26\3\26\3\26\3\26"+
		"\5\26\u0193\n\26\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\7\30\u019d\n"+
		"\30\f\30\16\30\u01a0\13\30\3\30\5\30\u01a3\n\30\3\31\3\31\3\31\3\31\5"+
		"\31\u01a9\n\31\3\31\3\31\5\31\u01ad\n\31\3\32\3\32\5\32\u01b1\n\32\3\32"+
		"\3\32\3\33\5\33\u01b6\n\33\3\33\5\33\u01b9\n\33\3\33\5\33\u01bc\n\33\3"+
		"\33\3\33\3\33\6\33\u01c1\n\33\r\33\16\33\u01c2\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u01d4\n\34\3\35"+
		"\3\35\3\35\3\35\3\35\5\35\u01db\n\35\3\36\3\36\3\37\3\37\3\37\3\37\3 "+
		"\3 \3 \3!\3!\3!\3!\3\"\5\"\u01eb\n\"\3\"\3\"\3#\3#\3#\3#\3$\3$\3$\5$\u01f6"+
		"\n$\3%\3%\5%\u01fa\n%\3&\3&\5&\u01fe\n&\3\'\3\'\5\'\u0202\n\'\3(\3(\3"+
		"(\3)\3)\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3+\3+\5+\u0215\n+\3+\3+\3+\3+\5"+
		"+\u021b\n+\5+\u021d\n+\3,\3,\5,\u0221\n,\3-\3-\5-\u0225\n-\3-\5-\u0228"+
		"\n-\3-\3-\5-\u022c\n-\5-\u022e\n-\3-\3-\7-\u0232\n-\f-\16-\u0235\13-\3"+
		"-\3-\3.\3.\3.\5.\u023c\n.\3/\3/\3/\5/\u0241\n/\3\60\3\60\3\60\3\60\3\60"+
		"\3\60\3\60\3\60\3\60\5\60\u024c\n\60\3\60\3\60\7\60\u0250\n\60\f\60\16"+
		"\60\u0253\13\60\3\60\3\60\3\61\3\61\5\61\u0259\n\61\3\61\3\61\3\61\3\61"+
		"\3\61\3\61\3\62\3\62\3\62\5\62\u0264\n\62\3\63\3\63\3\63\5\63\u0269\n"+
		"\63\3\64\3\64\5\64\u026d\n\64\3\64\3\64\3\64\5\64\u0272\n\64\7\64\u0274"+
		"\n\64\f\64\16\64\u0277\13\64\3\65\3\65\3\65\7\65\u027c\n\65\f\65\16\65"+
		"\u027f\13\65\3\65\3\65\3\66\3\66\3\66\5\66\u0286\n\66\3\67\3\67\3\67\5"+
		"\67\u028b\n\67\3\67\5\67\u028e\n\67\38\38\38\38\38\38\58\u0296\n8\38\3"+
		"8\39\39\59\u029c\n9\39\39\59\u02a0\n9\59\u02a2\n9\39\39\3:\5:\u02a7\n"+
		":\3:\3:\5:\u02ab\n:\3:\3:\5:\u02af\n:\3;\3;\3;\3;\3;\3;\5;\u02b7\n;\3"+
		";\3;\3;\3<\3<\3<\3=\3=\5=\u02c1\n=\3=\3=\3=\3=\3=\5=\u02c8\n=\3>\3>\3"+
		">\5>\u02cd\n>\3>\3>\3?\3?\5?\u02d3\n?\3@\3@\3@\3@\3@\3@\3@\3@\5@\u02dd"+
		"\n@\3A\3A\3A\3A\3A\3B\3B\3C\3C\3D\3D\3D\3E\3E\3E\3E\5E\u02ef\nE\3E\3E"+
		"\7E\u02f3\nE\fE\16E\u02f6\13E\3E\3E\3F\3F\3F\3F\3G\3G\3G\3G\3G\3G\3H\3"+
		"H\3H\3H\3H\5H\u0309\nH\3H\3H\3I\3I\3I\3I\3I\3I\5I\u0313\nI\3J\3J\3J\3"+
		"K\3K\5K\u031a\nK\3L\3L\5L\u031e\nL\3M\3M\3M\3M\7M\u0324\nM\fM\16M\u0327"+
		"\13M\3M\5M\u032a\nM\5M\u032c\nM\3M\3M\3N\5N\u0331\nN\3N\5N\u0334\nN\3"+
		"N\3N\3O\3O\3O\3O\5O\u033c\nO\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3O\3"+
		"O\3O\7O\u034d\nO\fO\16O\u0350\13O\3P\3P\3P\3P\5P\u0356\nP\3P\3P\3P\3P"+
		"\3P\3P\3P\5P\u035f\nP\7P\u0361\nP\fP\16P\u0364\13P\3Q\3Q\3Q\3Q\5Q\u036a"+
		"\nQ\3Q\3Q\3R\3R\3R\5R\u0371\nR\3R\3R\3R\3R\5R\u0377\nR\3S\3S\3S\5S\u037c"+
		"\nS\3T\3T\3T\3T\5T\u0382\nT\3U\3U\3V\3V\3W\3W\3W\3W\3X\3X\3X\3Y\3Y\3Y"+
		"\3Y\3Y\3Y\3Y\3Y\3Y\3Y\5Y\u0399\nY\5Y\u039b\nY\3Z\3Z\3Z\5Z\u03a0\nZ\5Z"+
		"\u03a2\nZ\3Z\3Z\3[\3[\3[\7[\u03a9\n[\f[\16[\u03ac\13[\3\\\3\\\3\\\5\\"+
		"\u03b1\n\\\3\\\3\\\3]\3]\5]\u03b7\n]\3^\3^\5^\u03bb\n^\3_\3_\3_\3_\3_"+
		"\7_\u03c2\n_\f_\16_\u03c5\13_\3_\3_\3`\3`\3`\3`\5`\u03cd\n`\3`\5`\u03d0"+
		"\n`\3a\3a\3b\5b\u03d5\nb\3b\3b\5b\u03d9\nb\3c\3c\3c\3c\3d\3d\3d\3d\3e"+
		"\3e\5e\u03e5\ne\3e\3e\5e\u03e9\ne\3e\5e\u03ec\ne\3e\3e\3e\3e\3e\5e\u03f3"+
		"\ne\3e\3e\3f\3f\3f\3f\3f\3g\3g\3g\3g\3g\5g\u0401\ng\5g\u0403\ng\3g\5g"+
		"\u0406\ng\3g\5g\u0409\ng\5g\u040b\ng\3g\3g\3h\3h\3h\3h\3i\3i\3i\3i\5i"+
		"\u0417\ni\3i\2\4\u009c\u009ej\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 "+
		"\"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082"+
		"\u0084\u0086\u0088\u008a\u008c\u008e\u0090\u0092\u0094\u0096\u0098\u009a"+
		"\u009c\u009e\u00a0\u00a2\u00a4\u00a6\u00a8\u00aa\u00ac\u00ae\u00b0\u00b2"+
		"\u00b4\u00b6\u00b8\u00ba\u00bc\u00be\u00c0\u00c2\u00c4\u00c6\u00c8\u00ca"+
		"\u00cc\u00ce\u00d0\2\f\4\2\35\35((\3\2VW\3\2)*\4\2\65:=A\3\2<B\4\2\66"+
		":@A\4\2\65\65=?\3\2/\64\4\2CFJK\3\2QR\2\u045e\2\u00d2\3\2\2\2\4\u00ea"+
		"\3\2\2\2\6\u00ed\3\2\2\2\b\u00fc\3\2\2\2\n\u0100\3\2\2\2\f\u0105\3\2\2"+
		"\2\16\u0107\3\2\2\2\20\u0115\3\2\2\2\22\u011d\3\2\2\2\24\u0125\3\2\2\2"+
		"\26\u012d\3\2\2\2\30\u0132\3\2\2\2\32\u0145\3\2\2\2\34\u0147\3\2\2\2\36"+
		"\u014b\3\2\2\2 \u0151\3\2\2\2\"\u015c\3\2\2\2$\u015f\3\2\2\2&\u0168\3"+
		"\2\2\2(\u016f\3\2\2\2*\u0184\3\2\2\2,\u0194\3\2\2\2.\u0196\3\2\2\2\60"+
		"\u01a4\3\2\2\2\62\u01ae\3\2\2\2\64\u01c0\3\2\2\2\66\u01d3\3\2\2\28\u01da"+
		"\3\2\2\2:\u01dc\3\2\2\2<\u01de\3\2\2\2>\u01e2\3\2\2\2@\u01e5\3\2\2\2B"+
		"\u01ea\3\2\2\2D\u01ee\3\2\2\2F\u01f2\3\2\2\2H\u01f7\3\2\2\2J\u01fb\3\2"+
		"\2\2L\u01ff\3\2\2\2N\u0203\3\2\2\2P\u0206\3\2\2\2R\u0208\3\2\2\2T\u020b"+
		"\3\2\2\2V\u0220\3\2\2\2X\u0222\3\2\2\2Z\u0238\3\2\2\2\\\u0240\3\2\2\2"+
		"^\u0242\3\2\2\2`\u0258\3\2\2\2b\u0260\3\2\2\2d\u0268\3\2\2\2f\u026c\3"+
		"\2\2\2h\u0278\3\2\2\2j\u0282\3\2\2\2l\u028d\3\2\2\2n\u0295\3\2\2\2p\u0299"+
		"\3\2\2\2r\u02a6\3\2\2\2t\u02b6\3\2\2\2v\u02bb\3\2\2\2x\u02c7\3\2\2\2z"+
		"\u02c9\3\2\2\2|\u02d2\3\2\2\2~\u02dc\3\2\2\2\u0080\u02de\3\2\2\2\u0082"+
		"\u02e3\3\2\2\2\u0084\u02e5\3\2\2\2\u0086\u02e7\3\2\2\2\u0088\u02ea\3\2"+
		"\2\2\u008a\u02f9\3\2\2\2\u008c\u02fd\3\2\2\2\u008e\u0308\3\2\2\2\u0090"+
		"\u0312\3\2\2\2\u0092\u0314\3\2\2\2\u0094\u0317\3\2\2\2\u0096\u031d\3\2"+
		"\2\2\u0098\u031f\3\2\2\2\u009a\u0330\3\2\2\2\u009c\u033b\3\2\2\2\u009e"+
		"\u0355\3\2\2\2\u00a0\u0365\3\2\2\2\u00a2\u0376\3\2\2\2\u00a4\u037b\3\2"+
		"\2\2\u00a6\u0381\3\2\2\2\u00a8\u0383\3\2\2\2\u00aa\u0385\3\2\2\2\u00ac"+
		"\u0387\3\2\2\2\u00ae\u038b\3\2\2\2\u00b0\u039a\3\2\2\2\u00b2\u039c\3\2"+
		"\2\2\u00b4\u03a5\3\2\2\2\u00b6\u03b0\3\2\2\2\u00b8\u03b6\3\2\2\2\u00ba"+
		"\u03ba\3\2\2\2\u00bc\u03bc\3\2\2\2\u00be\u03cc\3\2\2\2\u00c0\u03d1\3\2"+
		"\2\2\u00c2\u03d4\3\2\2\2\u00c4\u03da\3\2\2\2\u00c6\u03de\3\2\2\2\u00c8"+
		"\u03e2\3\2\2\2\u00ca\u03f6\3\2\2\2\u00cc\u03fb\3\2\2\2\u00ce\u040e\3\2"+
		"\2\2\u00d0\u0416\3\2\2\2\u00d2\u00d3\5\4\3\2\u00d3\u00d9\5\u00d0i\2\u00d4"+
		"\u00d5\5\6\4\2\u00d5\u00d6\5\u00d0i\2\u00d6\u00d8\3\2\2\2\u00d7\u00d4"+
		"\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da"+
		"\u00e5\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00e0\5(\25\2\u00dd\u00e0\5*"+
		"\26\2\u00de\u00e0\5\f\7\2\u00df\u00dc\3\2\2\2\u00df\u00dd\3\2\2\2\u00df"+
		"\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\5\u00d0i\2\u00e2\u00e4"+
		"\3\2\2\2\u00e3\u00df\3\2\2\2\u00e4\u00e7\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5"+
		"\u00e6\3\2\2\2\u00e6\u00e8\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8\u00e9\7\2"+
		"\2\3\u00e9\3\3\2\2\2\u00ea\u00eb\7\20\2\2\u00eb\u00ec\7\35\2\2\u00ec\5"+
		"\3\2\2\2\u00ed\u00f9\7\31\2\2\u00ee\u00fa\5\b\5\2\u00ef\u00f5\7\36\2\2"+
		"\u00f0\u00f1\5\b\5\2\u00f1\u00f2\5\u00d0i\2\u00f2\u00f4\3\2\2\2\u00f3"+
		"\u00f0\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f6\3\2"+
		"\2\2\u00f6\u00f8\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f8\u00fa\7\37\2\2\u00f9"+
		"\u00ee\3\2\2\2\u00f9\u00ef\3\2\2\2\u00fa\7\3\2\2\2\u00fb\u00fd\t\2\2\2"+
		"\u00fc\u00fb\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00ff"+
		"\5\n\6\2\u00ff\t\3\2\2\2\u0100\u0101\5\u00c0a\2\u0101\13\3\2\2\2\u0102"+
		"\u0106\5\16\b\2\u0103\u0106\5\30\r\2\u0104\u0106\5.\30\2\u0105\u0102\3"+
		"\2\2\2\u0105\u0103\3\2\2\2\u0105\u0104\3\2\2\2\u0106\r\3\2\2\2\u0107\u0113"+
		"\7\22\2\2\u0108\u0114\5\20\t\2\u0109\u010f\7\36\2\2\u010a\u010b\5\20\t"+
		"\2\u010b\u010c\5\u00d0i\2\u010c\u010e\3\2\2\2\u010d\u010a\3\2\2\2\u010e"+
		"\u0111\3\2\2\2\u010f\u010d\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0112\3\2"+
		"\2\2\u0111\u010f\3\2\2\2\u0112\u0114\7\37\2\2\u0113\u0108\3\2\2\2\u0113"+
		"\u0109\3\2\2\2\u0114\17\3\2\2\2\u0115\u011b\5\22\n\2\u0116\u0118\5x=\2"+
		"\u0117\u0116\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011a"+
		"\7$\2\2\u011a\u011c\5\24\13\2\u011b\u0117\3\2\2\2\u011b\u011c\3\2\2\2"+
		"\u011c\21\3\2\2\2\u011d\u0122\7\35\2\2\u011e\u011f\7%\2\2\u011f\u0121"+
		"\7\35\2\2\u0120\u011e\3\2\2\2\u0121\u0124\3\2\2\2\u0122\u0120\3\2\2\2"+
		"\u0122\u0123\3\2\2\2\u0123\23\3\2\2\2\u0124\u0122\3\2\2\2\u0125\u012a"+
		"\5\u009cO\2\u0126\u0127\7%\2\2\u0127\u0129\5\u009cO\2\u0128\u0126\3\2"+
		"\2\2\u0129\u012c\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b"+
		"\25\3\2\2\2\u012c\u012a\3\2\2\2\u012d\u012e\t\3\2\2\u012e\27\3\2\2\2\u012f"+
		"\u0131\5\26\f\2\u0130\u012f\3\2\2\2\u0131\u0134\3\2\2\2\u0132\u0130\3"+
		"\2\2\2\u0132\u0133\3\2\2\2\u0133\u0135\3\2\2\2\u0134\u0132\3\2\2\2\u0135"+
		"\u0141\7\26\2\2\u0136\u0142\5\32\16\2\u0137\u013d\7\36\2\2\u0138\u0139"+
		"\5\32\16\2\u0139\u013a\5\u00d0i\2\u013a\u013c\3\2\2\2\u013b\u0138\3\2"+
		"\2\2\u013c\u013f\3\2\2\2\u013d\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u0140\3\2\2\2\u013f\u013d\3\2\2\2\u0140\u0142\7\37\2\2\u0141\u0136\3"+
		"\2\2\2\u0141\u0137\3\2\2\2\u0142\31\3\2\2\2\u0143\u0146\5\34\17\2\u0144"+
		"\u0146\5\36\20\2\u0145\u0143\3\2\2\2\u0145\u0144\3\2\2\2\u0146\33\3\2"+
		"\2\2\u0147\u0148\7\35\2\2\u0148\u0149\7$\2\2\u0149\u014a\5x=\2\u014a\35"+
		"\3\2\2\2\u014b\u014d\7\35\2\2\u014c\u014e\5 \21\2\u014d\u014c\3\2\2\2"+
		"\u014d\u014e\3\2\2\2\u014e\u014f\3\2\2\2\u014f\u0150\5x=\2\u0150\37\3"+
		"\2\2\2\u0151\u0152\7\"\2\2\u0152\u0157\5\"\22\2\u0153\u0154\7%\2\2\u0154"+
		"\u0156\5\"\22\2\u0155\u0153\3\2\2\2\u0156\u0159\3\2\2\2\u0157\u0155\3"+
		"\2\2\2\u0157\u0158\3\2\2\2\u0158\u015a\3\2\2\2\u0159\u0157\3\2\2\2\u015a"+
		"\u015b\7#\2\2\u015b!\3\2\2\2\u015c\u015d\5\22\n\2\u015d\u015e\5$\23\2"+
		"\u015e#\3\2\2\2\u015f\u0164\5&\24\2\u0160\u0161\7\65\2\2\u0161\u0163\5"+
		"&\24\2\u0162\u0160\3\2\2\2\u0163\u0166\3\2\2\2\u0164\u0162\3\2\2\2\u0164"+
		"\u0165\3\2\2\2\u0165%\3\2\2\2\u0166\u0164\3\2\2\2\u0167\u0169\7;\2\2\u0168"+
		"\u0167\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016b\5x"+
		"=\2\u016b\'\3\2\2\2\u016c\u016e\5\26\f\2\u016d\u016c\3\2\2\2\u016e\u0171"+
		"\3\2\2\2\u016f\u016d\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u0175\3\2\2\2\u0171"+
		"\u016f\3\2\2\2\u0172\u0174\7U\2\2\u0173\u0172\3\2\2\2\u0174\u0177\3\2"+
		"\2\2\u0175\u0173\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0178\3\2\2\2\u0177"+
		"\u0175\3\2\2\2\u0178\u0179\7\5\2\2\u0179\u017b\7\35\2\2\u017a\u017c\5"+
		" \21\2\u017b\u017a\3\2\2\2\u017b\u017c\3\2\2\2\u017c\u017d\3\2\2\2\u017d"+
		"\u017f\5\u0094K\2\u017e\u0180\5\62\32\2\u017f\u017e\3\2\2\2\u017f\u0180"+
		"\3\2\2\2\u0180)\3\2\2\2\u0181\u0183\5\26\f\2\u0182\u0181\3\2\2\2\u0183"+
		"\u0186\3\2\2\2\u0184\u0182\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u018a\3\2"+
		"\2\2\u0186\u0184\3\2\2\2\u0187\u0189\7U\2\2\u0188\u0187\3\2\2\2\u0189"+
		"\u018c\3\2\2\2\u018a\u0188\3\2\2\2\u018a\u018b\3\2\2\2\u018b\u018d\3\2"+
		"\2\2\u018c\u018a\3\2\2\2\u018d\u018e\7\5\2\2\u018e\u018f\5,\27\2\u018f"+
		"\u0190\7\35\2\2\u0190\u0192\5\u0094K\2\u0191\u0193\5\62\32\2\u0192\u0191"+
		"\3\2\2\2\u0192\u0193\3\2\2\2\u0193+\3\2\2\2\u0194\u0195\5\u0098M\2\u0195"+
		"-\3\2\2\2\u0196\u01a2\7\33\2\2\u0197\u01a3\5\60\31\2\u0198\u019e\7\36"+
		"\2\2\u0199\u019a\5\60\31\2\u019a\u019b\5\u00d0i\2\u019b\u019d\3\2\2\2"+
		"\u019c\u0199\3\2\2\2\u019d\u01a0\3\2\2\2\u019e\u019c\3\2\2\2\u019e\u019f"+
		"\3\2\2\2\u019f\u01a1\3\2\2\2\u01a0\u019e\3\2\2\2\u01a1\u01a3\7\37\2\2"+
		"\u01a2\u0197\3\2\2\2\u01a2\u0198\3\2\2\2\u01a3/\3\2\2\2\u01a4\u01ac\5"+
		"\22\n\2\u01a5\u01a8\5x=\2\u01a6\u01a7\7$\2\2\u01a7\u01a9\5\24\13\2\u01a8"+
		"\u01a6\3\2\2\2\u01a8\u01a9\3\2\2\2\u01a9\u01ad\3\2\2\2\u01aa\u01ab\7$"+
		"\2\2\u01ab\u01ad\5\24\13\2\u01ac\u01a5\3\2\2\2\u01ac\u01aa\3\2\2\2\u01ad"+
		"\61\3\2\2\2\u01ae\u01b0\7 \2\2\u01af\u01b1\5\64\33\2\u01b0\u01af\3\2\2"+
		"\2\u01b0\u01b1\3\2\2\2\u01b1\u01b2\3\2\2\2\u01b2\u01b3\7!\2\2\u01b3\63"+
		"\3\2\2\2\u01b4\u01b6\7&\2\2\u01b5\u01b4\3\2\2\2\u01b5\u01b6\3\2\2\2\u01b6"+
		"\u01bc\3\2\2\2\u01b7\u01b9\7[\2\2\u01b8\u01b7\3\2\2\2\u01b8\u01b9\3\2"+
		"\2\2\u01b9\u01bc\3\2\2\2\u01ba\u01bc\6\33\2\2\u01bb\u01b5\3\2\2\2\u01bb"+
		"\u01b8\3\2\2\2\u01bb\u01ba\3\2\2\2\u01bc\u01bd\3\2\2\2\u01bd\u01be\5\66"+
		"\34\2\u01be\u01bf\5\u00d0i\2\u01bf\u01c1\3\2\2\2\u01c0\u01bb\3\2\2\2\u01c1"+
		"\u01c2\3\2\2\2\u01c2\u01c0\3\2\2\2\u01c2\u01c3\3\2\2\2\u01c3\65\3\2\2"+
		"\2\u01c4\u01d4\5\f\7\2\u01c5\u01d4\5F$\2\u01c6\u01d4\58\35\2\u01c7\u01d4"+
		"\5v<\2\u01c8\u01d4\5H%\2\u01c9\u01d4\5J&\2\u01ca\u01d4\5L\'\2\u01cb\u01d4"+
		"\5N(\2\u01cc\u01d4\5P)\2\u01cd\u01d4\5\62\32\2\u01ce\u01d4\5T+\2\u01cf"+
		"\u01d4\5V,\2\u01d0\u01d4\5h\65\2\u01d1\u01d4\5p9\2\u01d2\u01d4\5R*\2\u01d3"+
		"\u01c4\3\2\2\2\u01d3\u01c5\3\2\2\2\u01d3\u01c6\3\2\2\2\u01d3\u01c7\3\2"+
		"\2\2\u01d3\u01c8\3\2\2\2\u01d3\u01c9\3\2\2\2\u01d3\u01ca\3\2\2\2\u01d3"+
		"\u01cb\3\2\2\2\u01d3\u01cc\3\2\2\2\u01d3\u01cd\3\2\2\2\u01d3\u01ce\3\2"+
		"\2\2\u01d3\u01cf\3\2\2\2\u01d3\u01d0\3\2\2\2\u01d3\u01d1\3\2\2\2\u01d3"+
		"\u01d2\3\2\2\2\u01d4\67\3\2\2\2\u01d5\u01db\5<\37\2\u01d6\u01db\5> \2"+
		"\u01d7\u01db\5@!\2\u01d8\u01db\5:\36\2\u01d9\u01db\5D#\2\u01da\u01d5\3"+
		"\2\2\2\u01da\u01d6\3\2\2\2\u01da\u01d7\3\2\2\2\u01da\u01d8\3\2\2\2\u01da"+
		"\u01d9\3\2\2\2\u01db9\3\2\2\2\u01dc\u01dd\5\u009cO\2\u01dd;\3\2\2\2\u01de"+
		"\u01df\5\u009cO\2\u01df\u01e0\7B\2\2\u01e0\u01e1\5\u009cO\2\u01e1=\3\2"+
		"\2\2\u01e2\u01e3\5\u009cO\2\u01e3\u01e4\t\4\2\2\u01e4?\3\2\2\2\u01e5\u01e6"+
		"\5\24\13\2\u01e6\u01e7\5B\"\2\u01e7\u01e8\5\24\13\2\u01e8A\3\2\2\2\u01e9"+
		"\u01eb\t\5\2\2\u01ea\u01e9\3\2\2\2\u01ea\u01eb\3\2\2\2\u01eb\u01ec\3\2"+
		"\2\2\u01ec\u01ed\7$\2\2\u01edC\3\2\2\2\u01ee\u01ef\5\22\n\2\u01ef\u01f0"+
		"\7+\2\2\u01f0\u01f1\5\24\13\2\u01f1E\3\2\2\2\u01f2\u01f3\7\35\2\2\u01f3"+
		"\u01f5\7\'\2\2\u01f4\u01f6\5\66\34\2\u01f5\u01f4\3\2\2\2\u01f5\u01f6\3"+
		"\2\2\2\u01f6G\3\2\2\2\u01f7\u01f9\7\32\2\2\u01f8\u01fa\5\24\13\2\u01f9"+
		"\u01f8\3\2\2\2\u01f9\u01fa\3\2\2\2\u01faI\3\2\2\2\u01fb\u01fd\7\3\2\2"+
		"\u01fc\u01fe\7\35\2\2\u01fd\u01fc\3\2\2\2\u01fd\u01fe\3\2\2\2\u01feK\3"+
		"\2\2\2\u01ff\u0201\7\27\2\2\u0200\u0202\7\35\2\2\u0201\u0200\3\2\2\2\u0201"+
		"\u0202\3\2\2\2\u0202M\3\2\2\2\u0203\u0204\7\17\2\2\u0204\u0205\7\35\2"+
		"\2\u0205O\3\2\2\2\u0206\u0207\7\23\2\2\u0207Q\3\2\2\2\u0208\u0209\7\t"+
		"\2\2\u0209\u020a\5\u009cO\2\u020aS\3\2\2\2\u020b\u0214\7\24\2\2\u020c"+
		"\u0215\5\u009cO\2\u020d\u020e\5\u00d0i\2\u020e\u020f\5\u009cO\2\u020f"+
		"\u0215\3\2\2\2\u0210\u0211\58\35\2\u0211\u0212\5\u00d0i\2\u0212\u0213"+
		"\5\u009cO\2\u0213\u0215\3\2\2\2\u0214\u020c\3\2\2\2\u0214\u020d\3\2\2"+
		"\2\u0214\u0210\3\2\2\2\u0215\u0216\3\2\2\2\u0216\u021c\5\62\32\2\u0217"+
		"\u021a\7\16\2\2\u0218\u021b\5T+\2\u0219\u021b\5\62\32\2\u021a\u0218\3"+
		"\2\2\2\u021a\u0219\3\2\2\2\u021b\u021d\3\2\2\2\u021c\u0217\3\2\2\2\u021c"+
		"\u021d\3\2\2\2\u021dU\3\2\2\2\u021e\u0221\5X-\2\u021f\u0221\5^\60\2\u0220"+
		"\u021e\3\2\2\2\u0220\u021f\3\2\2\2\u0221W\3\2\2\2\u0222\u022d\7\21\2\2"+
		"\u0223\u0225\5\u009cO\2\u0224\u0223\3\2\2\2\u0224\u0225\3\2\2\2\u0225"+
		"\u022e\3\2\2\2\u0226\u0228\58\35\2\u0227\u0226\3\2\2\2\u0227\u0228\3\2"+
		"\2\2\u0228\u0229\3\2\2\2\u0229\u022b\5\u00d0i\2\u022a\u022c\5\u009cO\2"+
		"\u022b\u022a\3\2\2\2\u022b\u022c\3\2\2\2\u022c\u022e\3\2\2\2\u022d\u0224"+
		"\3\2\2\2\u022d\u0227\3\2\2\2\u022e\u022f\3\2\2\2\u022f\u0233\7 \2\2\u0230"+
		"\u0232\5Z.\2\u0231\u0230\3\2\2\2\u0232\u0235\3\2\2\2\u0233\u0231\3\2\2"+
		"\2\u0233\u0234\3\2\2\2\u0234\u0236\3\2\2\2\u0235\u0233\3\2\2\2\u0236\u0237"+
		"\7!\2\2\u0237Y\3\2\2\2\u0238\u0239\5\\/\2\u0239\u023b\7\'\2\2\u023a\u023c"+
		"\5\64\33\2\u023b\u023a\3\2\2\2\u023b\u023c\3\2\2\2\u023c[\3\2\2\2\u023d"+
		"\u023e\7\b\2\2\u023e\u0241\5\24\13\2\u023f\u0241\7\4\2\2\u0240\u023d\3"+
		"\2\2\2\u0240\u023f\3\2\2\2\u0241]\3\2\2\2\u0242\u024b\7\21\2\2\u0243\u024c"+
		"\5`\61\2\u0244\u0245\5\u00d0i\2\u0245\u0246\5`\61\2\u0246\u024c\3\2\2"+
		"\2\u0247\u0248\58\35\2\u0248\u0249\5\u00d0i\2\u0249\u024a\5`\61\2\u024a"+
		"\u024c\3\2\2\2\u024b\u0243\3\2\2\2\u024b\u0244\3\2\2\2\u024b\u0247\3\2"+
		"\2\2\u024c\u024d\3\2\2\2\u024d\u0251\7 \2\2\u024e\u0250\5b\62\2\u024f"+
		"\u024e\3\2\2\2\u0250\u0253\3\2\2\2\u0251\u024f\3\2\2\2\u0251\u0252\3\2"+
		"\2\2\u0252\u0254\3\2\2\2\u0253\u0251\3\2\2\2\u0254\u0255\7!\2\2\u0255"+
		"_\3\2\2\2\u0256\u0257\7\35\2\2\u0257\u0259\7+\2\2\u0258\u0256\3\2\2\2"+
		"\u0258\u0259\3\2\2\2\u0259\u025a\3\2\2\2\u025a\u025b\5\u009eP\2\u025b"+
		"\u025c\7(\2\2\u025c\u025d\7\36\2\2\u025d\u025e\7\26\2\2\u025e\u025f\7"+
		"\37\2\2\u025fa\3\2\2\2\u0260\u0261\5d\63\2\u0261\u0263\7\'\2\2\u0262\u0264"+
		"\5\64\33\2\u0263\u0262\3\2\2\2\u0263\u0264\3\2\2\2\u0264c\3\2\2\2\u0265"+
		"\u0266\7\b\2\2\u0266\u0269\5f\64\2\u0267\u0269\7\4\2\2\u0268\u0265\3\2"+
		"\2\2\u0268\u0267\3\2\2\2\u0269e\3\2\2\2\u026a\u026d\5x=\2\u026b\u026d"+
		"\7\34\2\2\u026c\u026a\3\2\2\2\u026c\u026b\3\2\2\2\u026d\u0275\3\2\2\2"+
		"\u026e\u0271\7%\2\2\u026f\u0272\5x=\2\u0270\u0272\7\34\2\2\u0271\u026f"+
		"\3\2\2\2\u0271\u0270\3\2\2\2\u0272\u0274\3\2\2\2\u0273\u026e\3\2\2\2\u0274"+
		"\u0277\3\2\2\2\u0275\u0273\3\2\2\2\u0275\u0276\3\2\2\2\u0276g\3\2\2\2"+
		"\u0277\u0275\3\2\2\2\u0278\u0279\7\7\2\2\u0279\u027d\7 \2\2\u027a\u027c"+
		"\5j\66\2\u027b\u027a\3\2\2\2\u027c\u027f\3\2\2\2\u027d\u027b\3\2\2\2\u027d"+
		"\u027e\3\2\2\2\u027e\u0280\3\2\2\2\u027f\u027d\3\2\2\2\u0280\u0281\7!"+
		"\2\2\u0281i\3\2\2\2\u0282\u0283\5l\67\2\u0283\u0285\7\'\2\2\u0284\u0286"+
		"\5\64\33\2\u0285\u0284\3\2\2\2\u0285\u0286\3\2\2\2\u0286k\3\2\2\2\u0287"+
		"\u028a\7\b\2\2\u0288\u028b\5<\37\2\u0289\u028b\5n8\2\u028a\u0288\3\2\2"+
		"\2\u028a\u0289\3\2\2\2\u028b\u028e\3\2\2\2\u028c\u028e\7\4\2\2\u028d\u0287"+
		"\3\2\2\2\u028d\u028c\3\2\2\2\u028em\3\2\2\2\u028f\u0290\5\24\13\2\u0290"+
		"\u0291\7$\2\2\u0291\u0296\3\2\2\2\u0292\u0293\5\22\n\2\u0293\u0294\7+"+
		"\2\2\u0294\u0296\3\2\2\2\u0295\u028f\3\2\2\2\u0295\u0292\3\2\2\2\u0295"+
		"\u0296\3\2\2\2\u0296\u0297\3\2\2\2\u0297\u0298\5\u009cO\2\u0298o\3\2\2"+
		"\2\u0299\u02a1\7\30\2\2\u029a\u029c\5\u009cO\2\u029b\u029a\3\2\2\2\u029b"+
		"\u029c\3\2\2\2\u029c\u02a2\3\2\2\2\u029d\u02a2\5r:\2\u029e\u02a0\5t;\2"+
		"\u029f\u029e\3\2\2\2\u029f\u02a0\3\2\2\2\u02a0\u02a2\3\2\2\2\u02a1\u029b"+
		"\3\2\2\2\u02a1\u029d\3\2\2\2\u02a1\u029f\3\2\2\2\u02a2\u02a3\3\2\2\2\u02a3"+
		"\u02a4\5\62\32\2\u02a4q\3\2\2\2\u02a5\u02a7\58\35\2\u02a6\u02a5\3\2\2"+
		"\2\u02a6\u02a7\3\2\2\2\u02a7\u02a8\3\2\2\2\u02a8\u02aa\5\u00d0i\2\u02a9"+
		"\u02ab\5\u009cO\2\u02aa\u02a9\3\2\2\2\u02aa\u02ab\3\2\2\2\u02ab\u02ac"+
		"\3\2\2\2\u02ac\u02ae\5\u00d0i\2\u02ad\u02af\58\35\2\u02ae\u02ad\3\2\2"+
		"\2\u02ae\u02af\3\2\2\2\u02afs\3\2\2\2\u02b0\u02b1\5\24\13\2\u02b1\u02b2"+
		"\7$\2\2\u02b2\u02b7\3\2\2\2\u02b3\u02b4\5\22\n\2\u02b4\u02b5\7+\2\2\u02b5"+
		"\u02b7\3\2\2\2\u02b6\u02b0\3\2\2\2\u02b6\u02b3\3\2\2\2\u02b6\u02b7\3\2"+
		"\2\2\u02b7\u02b8\3\2\2\2\u02b8\u02b9\7\25\2\2\u02b9\u02ba\5\u009cO\2\u02ba"+
		"u\3\2\2\2\u02bb\u02bc\7\n\2\2\u02bc\u02bd\5\u009cO\2\u02bdw\3\2\2\2\u02be"+
		"\u02c0\5|?\2\u02bf\u02c1\5z>\2\u02c0\u02bf\3\2\2\2\u02c0\u02c1\3\2\2\2"+
		"\u02c1\u02c8\3\2\2\2\u02c2\u02c8\5~@\2\u02c3\u02c4\7\36\2\2\u02c4\u02c5"+
		"\5x=\2\u02c5\u02c6\7\37\2\2\u02c6\u02c8\3\2\2\2\u02c7\u02be\3\2\2\2\u02c7"+
		"\u02c2\3\2\2\2\u02c7\u02c3\3\2\2\2\u02c8y\3\2\2\2\u02c9\u02ca\7\"\2\2"+
		"\u02ca\u02cc\5f\64\2\u02cb\u02cd\7%\2\2\u02cc\u02cb\3\2\2\2\u02cc\u02cd"+
		"\3\2\2\2\u02cd\u02ce\3\2\2\2\u02ce\u02cf\7#\2\2\u02cf{\3\2\2\2\u02d0\u02d3"+
		"\5\u00acW\2\u02d1\u02d3\7\35\2\2\u02d2\u02d0\3\2\2\2\u02d2\u02d1\3\2\2"+
		"\2\u02d3}\3\2\2\2\u02d4\u02dd\5\u0080A\2\u02d5\u02dd\5\u00bc_\2\u02d6"+
		"\u02dd\5\u0086D\2\u02d7\u02dd\5\u0092J\2\u02d8\u02dd\5\u0088E\2\u02d9"+
		"\u02dd\5\u008aF\2\u02da\u02dd\5\u008cG\2\u02db\u02dd\5\u008eH\2\u02dc"+
		"\u02d4\3\2\2\2\u02dc\u02d5\3\2\2\2\u02dc\u02d6\3\2\2\2\u02dc\u02d7\3\2"+
		"\2\2\u02dc\u02d8\3\2\2\2\u02dc\u02d9\3\2\2\2\u02dc\u02da\3\2\2\2\u02dc"+
		"\u02db\3\2\2\2\u02dd\177\3\2\2\2\u02de\u02df\7\"\2\2\u02df\u02e0\5\u0082"+
		"B\2\u02e0\u02e1\7#\2\2\u02e1\u02e2\5\u0084C\2\u02e2\u0081\3\2\2\2\u02e3"+
		"\u02e4\5\u009cO\2\u02e4\u0083\3\2\2\2\u02e5\u02e6\5x=\2\u02e6\u0085\3"+
		"\2\2\2\u02e7\u02e8\7@\2\2\u02e8\u02e9\5x=\2\u02e9\u0087\3\2\2\2\u02ea"+
		"\u02eb\7\6\2\2\u02eb\u02f4\7 \2\2\u02ec\u02ef\5\u0090I\2\u02ed\u02ef\5"+
		"$\23\2\u02ee\u02ec\3\2\2\2\u02ee\u02ed\3\2\2\2\u02ef\u02f0\3\2\2\2\u02f0"+
		"\u02f1\5\u00d0i\2\u02f1\u02f3\3\2\2\2\u02f2\u02ee\3\2\2\2\u02f3\u02f6"+
		"\3\2\2\2\u02f4\u02f2\3\2\2\2\u02f4\u02f5\3\2\2\2\u02f5\u02f7\3\2\2\2\u02f6"+
		"\u02f4\3\2\2\2\u02f7\u02f8\7!\2\2\u02f8\u0089\3\2\2\2\u02f9\u02fa\7\""+
		"\2\2\u02fa\u02fb\7#\2\2\u02fb\u02fc\5\u0084C\2\u02fc\u008b\3\2\2\2\u02fd"+
		"\u02fe\7\13\2\2\u02fe\u02ff\7\"\2\2\u02ff\u0300\5x=\2\u0300\u0301\7#\2"+
		"\2\u0301\u0302\5\u0084C\2\u0302\u008d\3\2\2\2\u0303\u0309\7\r\2\2\u0304"+
		"\u0305\7\r\2\2\u0305\u0309\7B\2\2\u0306\u0307\7B\2\2\u0307\u0309\7\r\2"+
		"\2\u0308\u0303\3\2\2\2\u0308\u0304\3\2\2\2\u0308\u0306\3\2\2\2\u0309\u030a"+
		"\3\2\2\2\u030a\u030b\5\u0084C\2\u030b\u008f\3\2\2\2\u030c\u030d\7\35\2"+
		"\2\u030d\u030e\5\u0098M\2\u030e\u030f\5\u0096L\2\u030f\u0313\3\2\2\2\u0310"+
		"\u0311\7\35\2\2\u0311\u0313\5\u0098M\2\u0312\u030c\3\2\2\2\u0312\u0310"+
		"\3\2\2\2\u0313\u0091\3\2\2\2\u0314\u0315\7\5\2\2\u0315\u0316\5\u0094K"+
		"\2\u0316\u0093\3\2\2\2\u0317\u0319\5\u0098M\2\u0318\u031a\5\u0096L\2\u0319"+
		"\u0318\3\2\2\2\u0319\u031a\3\2\2\2\u031a\u0095\3\2\2\2\u031b\u031e\5\u0098"+
		"M\2\u031c\u031e\5x=\2\u031d\u031b\3\2\2\2\u031d\u031c\3\2\2\2\u031e\u0097"+
		"\3\2\2\2\u031f\u032b\7\36\2\2\u0320\u0325\5\u009aN\2\u0321\u0322\7%\2"+
		"\2\u0322\u0324\5\u009aN\2\u0323\u0321\3\2\2\2\u0324\u0327\3\2\2\2\u0325"+
		"\u0323\3\2\2\2\u0325\u0326\3\2\2\2\u0326\u0329\3\2\2\2\u0327\u0325\3\2"+
		"\2\2\u0328\u032a\7%\2\2\u0329\u0328\3\2\2\2\u0329\u032a\3\2\2\2\u032a"+
		"\u032c\3\2\2\2\u032b\u0320\3\2\2\2\u032b\u032c\3\2\2\2\u032c\u032d\3\2"+
		"\2\2\u032d\u032e\7\37\2\2\u032e\u0099\3\2\2\2\u032f\u0331\5\22\n\2\u0330"+
		"\u032f\3\2\2\2\u0330\u0331\3\2\2\2\u0331\u0333\3\2\2\2\u0332\u0334\7,"+
		"\2\2\u0333\u0332\3\2\2\2\u0333\u0334\3\2\2\2\u0334\u0335\3\2\2\2\u0335"+
		"\u0336\5x=\2\u0336\u009b\3\2\2\2\u0337\u0338\bO\1\2\u0338\u033c\5\u009e"+
		"P\2\u0339\u033a\t\6\2\2\u033a\u033c\5\u009cO\b\u033b\u0337\3\2\2\2\u033b"+
		"\u0339\3\2\2\2\u033c\u034e\3\2\2\2\u033d\u033e\f\7\2\2\u033e\u033f\t\7"+
		"\2\2\u033f\u034d\5\u009cO\b\u0340\u0341\f\6\2\2\u0341\u0342\t\b\2\2\u0342"+
		"\u034d\5\u009cO\7\u0343\u0344\f\5\2\2\u0344\u0345\t\t\2\2\u0345\u034d"+
		"\5\u009cO\6\u0346\u0347\f\4\2\2\u0347\u0348\7.\2\2\u0348\u034d\5\u009c"+
		"O\5\u0349\u034a\f\3\2\2\u034a\u034b\7-\2\2\u034b\u034d\5\u009cO\4\u034c"+
		"\u033d\3\2\2\2\u034c\u0340\3\2\2\2\u034c\u0343\3\2\2\2\u034c\u0346\3\2"+
		"\2\2\u034c\u0349\3\2\2\2\u034d\u0350\3\2\2\2\u034e\u034c\3\2\2\2\u034e"+
		"\u034f\3\2\2\2\u034f\u009d\3\2\2\2\u0350\u034e\3\2\2\2\u0351\u0352\bP"+
		"\1\2\u0352\u0356\5\u00a2R\2\u0353\u0356\5\u00a0Q\2\u0354\u0356\5\u00ce"+
		"h\2\u0355\u0351\3\2\2\2\u0355\u0353\3\2\2\2\u0355\u0354\3\2\2\2\u0356"+
		"\u0362\3\2\2\2\u0357\u035e\f\3\2\2\u0358\u0359\7(\2\2\u0359\u035f\7\35"+
		"\2\2\u035a\u035f\5\u00c6d\2\u035b\u035f\5\u00c8e\2\u035c\u035f\5\u00ca"+
		"f\2\u035d\u035f\5\u00ccg\2\u035e\u0358\3\2\2\2\u035e\u035a\3\2\2\2\u035e"+
		"\u035b\3\2\2\2\u035e\u035c\3\2\2\2\u035e\u035d\3\2\2\2\u035f\u0361\3\2"+
		"\2\2\u0360\u0357\3\2\2\2\u0361\u0364\3\2\2\2\u0362\u0360\3\2\2\2\u0362"+
		"\u0363\3\2\2\2\u0363\u009f\3\2\2\2\u0364\u0362\3\2\2\2\u0365\u0366\5x"+
		"=\2\u0366\u0367\7\36\2\2\u0367\u0369\5\u009cO\2\u0368\u036a\7%\2\2\u0369"+
		"\u0368\3\2\2\2\u0369\u036a\3\2\2\2\u036a\u036b\3\2\2\2\u036b\u036c\7\37"+
		"\2\2\u036c\u00a1\3\2\2\2\u036d\u0377\5\u00a4S\2\u036e\u0370\5\u00aaV\2"+
		"\u036f\u0371\5z>\2\u0370\u036f\3\2\2\2\u0370\u0371\3\2\2\2\u0371\u0377"+
		"\3\2\2\2\u0372\u0373\7\36\2\2\u0373\u0374\5\u009cO\2\u0374\u0375\7\37"+
		"\2\2\u0375\u0377\3\2\2\2\u0376\u036d\3\2\2\2\u0376\u036e\3\2\2\2\u0376"+
		"\u0372\3\2\2\2\u0377\u00a3\3\2\2\2\u0378\u037c\5\u00a6T\2\u0379\u037c"+
		"\5\u00aeX\2\u037a\u037c\5\u00c4c\2\u037b\u0378\3\2\2\2\u037b\u0379\3\2"+
		"\2\2\u037b\u037a\3\2\2\2\u037c\u00a5\3\2\2\2\u037d\u0382\7\34\2\2\u037e"+
		"\u0382\5\u00a8U\2\u037f\u0382\5\u00c0a\2\u0380\u0382\7G\2\2\u0381\u037d"+
		"\3\2\2\2\u0381\u037e\3\2\2\2\u0381\u037f\3\2\2\2\u0381\u0380\3\2\2\2\u0382"+
		"\u00a7\3\2\2\2\u0383\u0384\t\n\2\2\u0384\u00a9\3\2\2\2\u0385\u0386\7\35"+
		"\2\2\u0386\u00ab\3\2\2\2\u0387\u0388\7\35\2\2\u0388\u0389\7(\2\2\u0389"+
		"\u038a\7\35\2\2\u038a\u00ad\3\2\2\2\u038b\u038c\5\u00b0Y\2\u038c\u038d"+
		"\5\u00b2Z\2\u038d\u00af\3\2\2\2\u038e\u039b\5\u00bc_\2\u038f\u039b\5\u0080"+
		"A\2\u0390\u0391\7\"\2\2\u0391\u0392\7,\2\2\u0392\u0393\7#\2\2\u0393\u039b"+
		"\5\u0084C\2\u0394\u039b\5\u008aF\2\u0395\u039b\5\u008cG\2\u0396\u0398"+
		"\5|?\2\u0397\u0399\5z>\2\u0398\u0397\3\2\2\2\u0398\u0399\3\2\2\2\u0399"+
		"\u039b\3\2\2\2\u039a\u038e\3\2\2\2\u039a\u038f\3\2\2\2\u039a\u0390\3\2"+
		"\2\2\u039a\u0394\3\2\2\2\u039a\u0395\3\2\2\2\u039a\u0396\3\2\2\2\u039b"+
		"\u00b1\3\2\2\2\u039c\u03a1\7 \2\2\u039d\u039f\5\u00b4[\2\u039e\u03a0\7"+
		"%\2\2\u039f\u039e\3\2\2\2\u039f\u03a0\3\2\2\2\u03a0\u03a2\3\2\2\2\u03a1"+
		"\u039d\3\2\2\2\u03a1\u03a2\3\2\2\2\u03a2\u03a3\3\2\2\2\u03a3\u03a4\7!"+
		"\2\2\u03a4\u00b3\3\2\2\2\u03a5\u03aa\5\u00b6\\\2\u03a6\u03a7\7%\2\2\u03a7"+
		"\u03a9\5\u00b6\\\2\u03a8\u03a6\3\2\2\2\u03a9\u03ac\3\2\2\2\u03aa\u03a8"+
		"\3\2\2\2\u03aa\u03ab\3\2\2\2\u03ab\u00b5\3\2\2\2\u03ac\u03aa\3\2\2\2\u03ad"+
		"\u03ae\5\u00b8]\2\u03ae\u03af\7\'\2\2\u03af\u03b1\3\2\2\2\u03b0\u03ad"+
		"\3\2\2\2\u03b0\u03b1\3\2\2\2\u03b1\u03b2\3\2\2\2\u03b2\u03b3\5\u00ba^"+
		"\2\u03b3\u00b7\3\2\2\2\u03b4\u03b7\5\u009cO\2\u03b5\u03b7\5\u00b2Z\2\u03b6"+
		"\u03b4\3\2\2\2\u03b6\u03b5\3\2\2\2\u03b7\u00b9\3\2\2\2\u03b8\u03bb\5\u009c"+
		"O\2\u03b9\u03bb\5\u00b2Z\2\u03ba\u03b8\3\2\2\2\u03ba\u03b9\3\2\2\2\u03bb"+
		"\u00bb\3\2\2\2\u03bc\u03bd\7\f\2\2\u03bd\u03c3\7 \2\2\u03be\u03bf\5\u00be"+
		"`\2\u03bf\u03c0\5\u00d0i\2\u03c0\u03c2\3\2\2\2\u03c1\u03be\3\2\2\2\u03c2"+
		"\u03c5\3\2\2\2\u03c3\u03c1\3\2\2\2\u03c3\u03c4\3\2\2\2\u03c4\u03c6\3\2"+
		"\2\2\u03c5\u03c3\3\2\2\2\u03c6\u03c7\7!\2\2\u03c7\u00bd\3\2\2\2\u03c8"+
		"\u03c9\5\22\n\2\u03c9\u03ca\5x=\2\u03ca\u03cd\3\2\2\2\u03cb\u03cd\5\u00c2"+
		"b\2\u03cc\u03c8\3\2\2\2\u03cc\u03cb\3\2\2\2\u03cd\u03cf\3\2\2\2\u03ce"+
		"\u03d0\5\u00c0a\2\u03cf\u03ce\3\2\2\2\u03cf\u03d0\3\2\2\2\u03d0\u00bf"+
		"\3\2\2\2\u03d1\u03d2\t\13\2\2\u03d2\u00c1\3\2\2\2\u03d3\u03d5\7@\2\2\u03d4"+
		"\u03d3\3\2\2\2\u03d4\u03d5\3\2\2\2\u03d5\u03d6\3\2\2\2\u03d6\u03d8\5|"+
		"?\2\u03d7\u03d9\5z>\2\u03d8\u03d7\3\2\2\2\u03d8\u03d9\3\2\2\2\u03d9\u00c3"+
		"\3\2\2\2\u03da\u03db\7\5\2\2\u03db\u03dc\5\u0094K\2\u03dc\u03dd\5\62\32"+
		"\2\u03dd\u00c5\3\2\2\2\u03de\u03df\7\"\2\2\u03df\u03e0\5\u009cO\2\u03e0"+
		"\u03e1\7#\2\2\u03e1\u00c7\3\2\2\2\u03e2\u03f2\7\"\2\2\u03e3\u03e5\5\u009c"+
		"O\2\u03e4\u03e3\3\2\2\2\u03e4\u03e5\3\2\2\2\u03e5\u03e6\3\2\2\2\u03e6"+
		"\u03e8\7\'\2\2\u03e7\u03e9\5\u009cO\2\u03e8\u03e7\3\2\2\2\u03e8\u03e9"+
		"\3\2\2\2\u03e9\u03f3\3\2\2\2\u03ea\u03ec\5\u009cO\2\u03eb\u03ea\3\2\2"+
		"\2\u03eb\u03ec\3\2\2\2\u03ec\u03ed\3\2\2\2\u03ed\u03ee\7\'\2\2\u03ee\u03ef"+
		"\5\u009cO\2\u03ef\u03f0\7\'\2\2\u03f0\u03f1\5\u009cO\2\u03f1\u03f3\3\2"+
		"\2\2\u03f2\u03e4\3\2\2\2\u03f2\u03eb\3\2\2\2\u03f3\u03f4\3\2\2\2\u03f4"+
		"\u03f5\7#\2\2\u03f5\u00c9\3\2\2\2\u03f6\u03f7\7(\2\2\u03f7\u03f8\7\36"+
		"\2\2\u03f8\u03f9\5x=\2\u03f9\u03fa\7\37\2\2\u03fa\u00cb\3\2\2\2\u03fb"+
		"\u040a\7\36\2\2\u03fc\u0403\5\24\13\2\u03fd\u0400\5x=\2\u03fe\u03ff\7"+
		"%\2\2\u03ff\u0401\5\24\13\2\u0400\u03fe\3\2\2\2\u0400\u0401\3\2\2\2\u0401"+
		"\u0403\3\2\2\2\u0402\u03fc\3\2\2\2\u0402\u03fd\3\2\2\2\u0403\u0405\3\2"+
		"\2\2\u0404\u0406\7,\2\2\u0405\u0404\3\2\2\2\u0405\u0406\3\2\2\2\u0406"+
		"\u0408\3\2\2\2\u0407\u0409\7%\2\2\u0408\u0407\3\2\2\2\u0408\u0409\3\2"+
		"\2\2\u0409\u040b\3\2\2\2\u040a\u0402\3\2\2\2\u040a\u040b\3\2\2\2\u040b"+
		"\u040c\3\2\2\2\u040c\u040d\7\37\2\2\u040d\u00cd\3\2\2\2\u040e\u040f\5"+
		"x=\2\u040f\u0410\7(\2\2\u0410\u0411\7\35\2\2\u0411\u00cf\3\2\2\2\u0412"+
		"\u0417\7&\2\2\u0413\u0417\7\2\2\3\u0414\u0417\7[\2\2\u0415\u0417\6i\t"+
		"\2\u0416\u0412\3\2\2\2\u0416\u0413\3\2\2\2\u0416\u0414\3\2\2\2\u0416\u0415"+
		"\3\2\2\2\u0417\u00d1\3\2\2\2\177\u00d9\u00df\u00e5\u00f5\u00f9\u00fc\u0105"+
		"\u010f\u0113\u0117\u011b\u0122\u012a\u0132\u013d\u0141\u0145\u014d\u0157"+
		"\u0164\u0168\u016f\u0175\u017b\u017f\u0184\u018a\u0192\u019e\u01a2\u01a8"+
		"\u01ac\u01b0\u01b5\u01b8\u01bb\u01c2\u01d3\u01da\u01ea\u01f5\u01f9\u01fd"+
		"\u0201\u0214\u021a\u021c\u0220\u0224\u0227\u022b\u022d\u0233\u023b\u0240"+
		"\u024b\u0251\u0258\u0263\u0268\u026c\u0271\u0275\u027d\u0285\u028a\u028d"+
		"\u0295\u029b\u029f\u02a1\u02a6\u02aa\u02ae\u02b6\u02c0\u02c7\u02cc\u02d2"+
		"\u02dc\u02ee\u02f4\u0308\u0312\u0319\u031d\u0325\u0329\u032b\u0330\u0333"+
		"\u033b\u034c\u034e\u0355\u035e\u0362\u0369\u0370\u0376\u037b\u0381\u0398"+
		"\u039a\u039f\u03a1\u03aa\u03b0\u03b6\u03ba\u03c3\u03cc\u03cf\u03d4\u03d8"+
		"\u03e4\u03e8\u03eb\u03f2\u0400\u0402\u0405\u0408\u040a\u0416";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}