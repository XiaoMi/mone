grammar Expr;

prog : stat+;

stat : expr NEWLINE
     ;

expr : expr '.' expr #pro
     | INT  #int
     | ID   #id
     | ID'(' params? ')' #met
     | '[' ID']' #mb
     | '{' ID '}' #mp
     ;





//属性调用
property : ID ('.'? (method|ID))+
        ;

//方法
method : ID'(' params? ')'
        ;




params : pair (',' pair)* ;



pair : value ':' value;

value : ID
      | INT
      ;

ID : ('a'..'z' |'A'..'Z'|'0'..'9'|'_')+ ;
INT : [0-9]+ ;
NEWLINE : '\r'? '\n';
WS : [ \t\n\r]+ -> skip;
LINE_COMMENT : '//' .*? '\n' ->skip;
COMMENT : '/*' .*? '*/' -> skip;

MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';
Ju : '.';
LP   : '(';
RP   : ')';



