grammar Drink;

// Parser Rules

drinkSentence : ARTICLE? DRINKING_VESSEL OF drink ;

drink : TEXT;

// Lexer Rules

ARTICLE : 'the' | 'an' | 'a' ;

OF : 'of' ;

DRINKING_VESSEL : 'cup' | 'pint' | 'shot' | 'mug' | 'glass' ;

TEXT : ('a'..'z')+ ;

WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ -> skip ;