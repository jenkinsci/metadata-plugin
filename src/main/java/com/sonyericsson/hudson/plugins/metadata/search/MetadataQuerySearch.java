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
package com.sonyericsson.hudson.plugins.metadata.search;

import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.search.antlr.QueryLexer;
import com.sonyericsson.hudson.plugins.metadata.search.antlr.QueryParser;
import com.sonyericsson.hudson.plugins.metadata.search.antlr.QueryWalker;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import java.util.LinkedList;
import java.util.List;

/**
 * This class will parse the query and search for the matched projects.
 *
 * @author Shemeer Sulaiman &lt;shemeer.x.sulaiman@sonymobile.com&gt;
 */
public class MetadataQuerySearch {

    private CommonTree queryTree;

    /**
     * MetadataQuerySearch Constructor.
     *
     * @param queryTree CommomTree.
     */
    protected MetadataQuerySearch(CommonTree queryTree) {
        this.queryTree = queryTree;
    }

    /**
     * Method will perform the search using QueryWalker and returns the list of matched projects.
     *
     * @param all List.
     * @return ArrayList of matched TopLevelItems.
     *
     * @throws Exception exception.
     */
    public List<TopLevelItem> searchQuery(List<TopLevelItem> all) throws Exception {
        List<TopLevelItem> matchedItems = new LinkedList<TopLevelItem>();
        for (TopLevelItem item : all) {
            if (item instanceof AbstractProject) {
                AbstractProject project = (AbstractProject)item;
                MetadataJobProperty property =
                        (MetadataJobProperty)project.getProperty(MetadataJobProperty.class);
                if (property != null) {
                    CommonTreeNodeStream nodes = new CommonTreeNodeStream(queryTree);
                    QueryWalker walker = new QueryWalker(nodes);
                    boolean matchStatus = walker.evaluate(property);
                    if (matchStatus) {
                        matchedItems.add(item);
                    }
                }
            }
        }
        return matchedItems;
    }

    /**
     * The method will parse the query using QueryParser and construct the CommonTree and return the instance of
     * MetadataQuerySearch.
     *
     * @param queryString the search query.
     * @return query tree.
     *
     * @throws Exception if an error occur.
     */
    public static MetadataQuerySearch parseQuery(String queryString) throws Exception {
        CharStream charStream = new ANTLRStringStream(queryString);
        QueryLexer exprLexer = new QueryLexer(charStream);
        TokenStream tokenStream = new CommonTokenStream(exprLexer);
        QueryParser parser = new QueryParser(tokenStream);
        QueryParser.expression_return tree = parser.expression();
        return new MetadataQuerySearch((CommonTree)tree.getTree());
    }
}
