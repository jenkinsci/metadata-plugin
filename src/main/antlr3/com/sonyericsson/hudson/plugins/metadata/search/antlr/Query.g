grammar Query;

options {
    language = Java;
    output=AST;
    ASTLabelType=CommonTree;
}
@header {
  package com.sonyericsson.hudson.plugins.metadata.search.antlr;
}

@lexer::header {
  package com.sonyericsson.hudson.plugins.metadata.search.antlr;
}

query
    :
     NAME ('='^) VALUE
    ;

// START:tokens

NAME
    :
   ('a'..'z'|'A'..'Z'|'0'..'9'|'-'|'.'|' '|':')+
    ;
fragment NON_WORD
    :
     ( '\u0000'..' '
     | '!'
     | '"'
     | '#'
     | '$'
     | '%'
     | '&'
     | '\''
     | '(' | ')'
     | ':'
     | ';'
     | '?'
     | '[' | ']'
     | '{' | '}'
     | '~'
     )
    ;
VALUE
    :
    (NAME|NON_WORD)+
    ;