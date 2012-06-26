/*
 *  The MIT License
 *
 *  Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
tree grammar QueryWalker;

options {
language = Java;
tokenVocab=Query;
ASTLabelType=CommonTree;
}

// START:members
@header {
package com.sonyericsson.hudson.plugins.metadata.search.antlr;

import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
}

@members{
    public boolean checkMatch(String sleftVal, String srightVal,
            MetadataContainer mDataProperty) {
        MetadataValue lvalue = (MetadataValue) TreeStructureUtil.getPath(mDataProperty,
                sleftVal.trim().split("\\."));
        MetadataValue rvalue = (MetadataValue) TreeStructureUtil.getPath(mDataProperty,
                srightVal.trim().split("\\."));
        if (rvalue != null && lvalue != null) {
            if (lvalue.compareTo(rvalue) == 0) {
                return true;
            } else if (lvalue.compareTo(srightVal.trim()) == 0) {
                return true;
            } else if (rvalue.compareTo(sleftVal.trim()) == 0) {
                return true;
            }
        } else if (rvalue == null && lvalue != null) {
            if (lvalue.compareTo(srightVal.trim()) == 0) {
                return true;
            }
        } else if (lvalue == null && rvalue != null) {
            if (rvalue.compareTo(sleftVal.trim()) == 0) {
                return true;
            }
        }
        return false;
    }
}

evaluate[MetadataContainer<MetadataValue> metaDataProperty] returns [boolean result]
    :
    ^(op=(OPERATOR) left=NAME right=NAME)
    {
    result = checkMatch(left.getText(),right.getText(),metaDataProperty);
    }
    ;