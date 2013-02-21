/*
 *  The MIT License
 *
 *  Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *  Copyright 2013 Sony Mobile Communications AB. All rights reserved.
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
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
}

@members{
  /**
   * Enumerator describing Logical Operators in the
   * search string and its corresponding evaluation.
   */
 enum Operators {
    /**
     * AND operator and its corresponding evaluation.
     */
    AND("&&") {

        @Override
        public boolean evaluateValues(boolean op1, boolean op2) {
            return op1 && op2;
        }
    },
    /**
     * OR operator and its corresponding evaluation.
     */
    OR("||") {

        @Override
        public boolean evaluateValues(boolean op1, boolean op2) {
            return op1 || op2;
        }
    };
    private String operator;

    /**
     * Operator enum Constructor.
     * @param operator operator string.
     */
    private Operators(String operator) {
        this.operator = operator;
    }
    /**
     * operatorLook for mapping operator and its name.
     *
     */
    private static final Map<String, Operators> operatorLookup = new HashMap<String, Operators>();

    /**
     * This function will return the current operator.
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Static operation to map operator and its
     * corresponding name in operatorLookup map.
     */
    static {
        for (Operators operator : EnumSet.allOf(Operators.class)) {
            operatorLookup.put(operator.getOperator(), operator);
        }
    }
    /**
     * Method will return the operator corresponding to
     * given name.
     * @param operString operator name.
     */
    public static Operators find(String operString) {
        return operatorLookup.get(operString);
    }
   public abstract boolean evaluateValues(boolean op1, boolean op2);
}

 /**
   * Enumerator describing comparitive Operators in the
   * search string and its corresponding compare and
   * metadata search.
   */
enum Compare {
    /**
     * Equal to operator.
     */
    EQLS("=") {},
    /**
     * Greater than operator.
     */
    GTR(">") {},
    /**
     * Lesser than operator.
     */
    LSR("<") {},
    /**
     * Greater than or equalto operator.
     */
   GREQ(">=") {},
    /**
     * Lesser than and Equal tooperator.
     */
    LSEQ("<=") {};
    private String compareOperator;

    /**
     * Compare enum Constructor.
     * @param operator operator string.
     */
    private Compare(String compareOperator) {
        this.compareOperator = compareOperator;
    }
    private static final Map<String, Compare> operatorLookup = new HashMap<String, Compare>();
    /**
     * Method will return the operator corresponding to
     * given name.
     * @param compareOperator name.
     */
    public String getCompareOperator() {
        return compareOperator;
    }
    /**
     * Static operation to map compareOperator and its
     * corresponding name in operatorLookup map.
     */
    static {
        for (Compare compare : EnumSet.allOf(Compare.class)) {
            operatorLookup.put(compare.getCompareOperator(), compare);
        }
    }

    /**
     * Method will return the operator corresponding to
     * given name.
     * @param operString operator name.
     */
    public static Compare find(String operatorString) {
        return operatorLookup.get(operatorString);
    }

    /**
     * Method will compare the String values in the metadata.
     * @param sleftVal left String.
     * @param srightVal right string.
     * @param mDataProperty metadata.
     */
    public boolean searchMeatadata(String sleftVal, String srightVal,
                MetadataContainer mDataProperty) {
    MetadataValue lvalue = (MetadataValue) TreeStructureUtil.getPath(mDataProperty,
            sleftVal.trim().split("\\."));
    MetadataValue rvalue = (MetadataValue) TreeStructureUtil.getPath(mDataProperty,
            srightVal.trim().split("\\."));
    if (rvalue != null && lvalue != null) {
       if (compareValues(lvalue, rvalue)) {
          return true;
        } else if (compareValues(lvalue, srightVal.trim())) {
            return true;
        } else if (compareValues(rvalue, sleftVal.trim())) {
            return true;
        }
    } else if (rvalue == null && lvalue != null) {
        if ( compareValues(lvalue, srightVal.trim())) {
            return true;
        }
    } else if (lvalue == null && rvalue != null) {
        if (compareValues(rvalue, sleftVal.trim())) {
            return true;
        }
    }
    return false;
    }

    /**
     * Method will compare the argument metadata values values.
     * @param lv left metadata.
     * @param rv right metadata.
     */
    public boolean compareValues(MetadataValue lv,MetadataValue rv){
        if(this.equals(Compare.EQLS)){
            return (lv.compareTo(rv)==0);
        }else if(this.equals(Compare.GTR)){
            return (lv.compareTo(rv)>0);
        }else if(this.equals(Compare.LSR)){
            return (lv.compareTo(rv)<0);
        }else if(this.equals(Compare.GREQ)){
            return (lv.compareTo(rv)>=0);
        }else if(this.equals(Compare.LSEQ)){
            return (lv.compareTo(rv)<=0);
        }
        return false;
        }
    /**
     * Method will compare the argument metadata values and String.
     * @param lv left metadata.
     * @param rv right String.
     */
    public boolean compareValues(MetadataValue lv,String rv){
        if(this.equals(Compare.EQLS)){
            return (lv.compareTo(rv)==0);
        }else if(this.equals(Compare.GTR)){
            return (lv.compareTo(rv)>0);
        }else if(this.equals(Compare.LSR)){
            return (lv.compareTo(rv)<0);
        }else if(this.equals(Compare.GREQ)){
            return (lv.compareTo(rv)>=0);
        }else if(this.equals(Compare.LSEQ)){
            return (lv.compareTo(rv)<=0);
        }
        return false;
    }
}
}

evaluate[MetadataContainer<MetadataValue> metaDataProperty] returns [boolean result]
    :
    ^(operator=(AND|OR) lefteval=evaluate[metaDataProperty] righteval=evaluate[metaDataProperty])
    {
    result = Operators.find(operator.getText()).evaluateValues(lefteval, righteval);
    }
    |
    ^(operator=(EQLS|GTR|GREQ|LSR|LSEQ) left=NAME right=NAME)
    {
    result = Compare.find(operator.getText()).searchMeatadata(left.getText(),right.getText(),metaDataProperty);
    }
   ;