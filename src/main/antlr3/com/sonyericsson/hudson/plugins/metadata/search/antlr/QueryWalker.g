tree grammar QueryWalker;

options {
language = Java;
tokenVocab=Query;
ASTLabelType=CommonTree;
}

// START:members
@header {
 package com.sonyericsson.hudson.plugins.metadata.search.antlr;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
}

evaluvate
    :
    ^('=' left=NAME right=VALUE)
    ;

