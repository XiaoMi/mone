/** Taken from "The Definitive ANTLR 4 Reference" by Terence Parr */

// Derived from http://json.org
grammar JSON;

json
   : value
   ;

obj
   : '{' pair (',' pair)* '}' # AnObject
   | '{' '}'                  # EmptyObject
   ;

pair
   : STRING ':' value
   ;

array
   : '[' value (',' value)* ']'  # ArrayOfValues
   | '[' ']'                     # EmptyArray
   ;

value
   : STRING        # String
   | NUMBER        # Number
   | VAR           # Var
   | obj           # ObjectValue
   | array         # ArrayValue
   | 'true'        # Atom
   | 'false'       # Atom
   | 'null'        # Atom
   ;


STRING
   : '"' (ESC | ~ ["\\])* '"'
   ;

VAR
    : '$' ID
    ;

ID : ('a'..'z' |'A'..'Z')+ ;

fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;
fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;
fragment HEX
   : [0-9a-fA-F]
   ;
NUMBER
   : '-'? INT '.' [0-9] + EXP? | '-'? INT EXP | '-'? INT
   ;

//NUMBER : DIGIT+ '.' DIGIT* EXPONET?
//      | '.' DIGIT+ EXPONET?
//      | DIGIT+ EXPONET?
//      | DIGIT+
//      ;

fragment DIGIT : '0'..'9' ;
fragment EXPONET : ('e'|'E') ('+'|'-')? DIGIT+ ;

fragment INT
   : '0' | [1-9] [0-9]*
   ;
// no leading zeros
fragment EXP
   : [Ee] [+\-]? INT
   ;
// \- since - means "range" inside [...]
WS
   : [ \t\n\r] + -> skip
   ;