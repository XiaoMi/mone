grammar EsQuery;

parse : expression EOF;

expression
    :   '(' expression ')'    #parenExpression
    |   aggexpr             #aggreExpression
    |   expression AND expression   #andExpression
    |   expression AND expression   #andExpression
    |   expression OR expression    #orExpression
    |   NOT expression      #notExpression
    |   expression NOT expression      #notExpression
    |   expr                #commonExpression
    ;

expr
    :   param LT value  #LtExpr
    |   param GT value  #GtExpr
    |   param LE value  #LeExpr
    |   param GE value  #GeExpr
    |   param NE value  #NeExpr
    |   param EQ value  #EqExpr
    |   param LIKE value #LikeExpr
    |   param CONTAIN value  #ContainExpr
    |   param NOTCONTAIN value  #NotContainExpr
    |   param IN array  #InExpr
    |   param NOT_IN array  #NotInExpr
    |   param EXIST #ExistExpr
    |   param NOT_EXIST #NotExistExpr
    |   param REG regex #RegexExpr
    |   param           #EqExpr
    |   value           #EqExpr
    ;

array
    :'['']'
    |'[' value (','value)* ']'
    ;

aggexpr
    :   param DOT MAX parenValve #MaxAggExpr
    |   param DOT MIN parenValve #MinAggExpr
    |   param DOT AVG parenValve #AvgAggExpr
    |   param DOT GROUP parenValve  #GroupAggExpr
    ;

parenValve
    :   '('')'
    |   '(' aggexpr ')'
    |   '(' expression ')'
    |   '(' value ')'
    ;

param
    :   IDENTIFIER
    ;

value
    :   IPv4    #IpV4Value
    |   STRING  #StringValue
    |   NUMBER  #NumberValue
    |   TIME    #TimeValue
    |   'true'  #TrueValue
    |   'false' #FalseValue
    |   'null'  #NullValue
    |  IDENTIFIER #IdentifierValue
    ;

regex
    :REGEX
    ;

IPv4
    :   '"'SEGMENT DOT SEGMENT DOT SEGMENT DOT SEGMENT '"'
    |   SEGMENT DOT SEGMENT DOT SEGMENT DOT SEGMENT
    |   '"'SEGMENT DOT SEGMENT DOT SEGMENT DOT SEGMENT '/' NUMBER '"'
    ;


STRING
    :   '"' (ESC | ~["\\] )*  '"'
    ;
REGEX   :   '/' (ESC | ~["\\] )* '/';

AND :   'AND' | 'and';
OR  :   'OR'  | 'or';
EQ  :   ':';
NE  :   '!=';
LT  :   '<';
GT  :   '>';
LE  :   '<=';
GE  :   '>=';
REG :   ':~';
LIKE:   'LIKE' | 'like';
IN  :   'IN' | 'in';
NOT_IN  :   'NOT_IN' | 'not_in';
EXIST   :   'EXIST' | 'exist';
NOT_EXIST   :   'NOT_EXIST' | 'not_exist';
NOT :   'NOT' | 'not';
DOT :   '.';
CONTAIN :   'CONTAIN' | 'contain';
NOTCONTAIN  :   'NOT_CONTAIN' | 'not_contain';

MAX :   'max';
MIN :   'min';
SUM :   'sum';
AVG :   'avg';
GROUP   :   'group';

IDENTIFIER : [a-zA-Z_] [a-zA-Z_0-9]* | [0-9]+ [a-zA-Z] [a-zA-Z_0-9]*;

TIME
    :   [0-9][0-9][0-9][0-9]'-'[0-9][0-9]'-'[0-9][0-9]' '[0-9][0-9]':'[0-9][0-9]':'[0-9][0-9]'.'[0-9][0-9][0-9]
    |   [0-9][0-9][0-9][0-9]'-'[0-9][0-9]'-'[0-9][0-9]' '[0-9][0-9]':'[0-9][0-9]':'[0-9][0-9]
    ;
fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;


NUMBER
    :   '-'? INT '.' INT EXP?   // 1.35, 1.35E-9, 0.3, -4.5
    |   '-'? INT EXP            // 1e10 -3e4
    |   '-'? INT                // -3, 45
    ;

SEGMENT
    :   ([0-9] | [1-9][0-9] | '1'[0-9][0-9] | '2'[0-4]'5' | '2'[0-4][0-9] | [1-2]'5'[0-4] )
    |   '*'
    ;


fragment INT :   '0' | '1'..'9' '0'..'9'* ; // no leading zeros
fragment EXP :   [Ee] [+\-]? INT ; // \- since - means "range" inside [...]

WS  :   [ \t\n\r]+ -> skip ;
