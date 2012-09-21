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

import com.sonyericsson.hudson.plugins.metadata.Messages;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.views.ViewJobFilter;
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.LinkedList;
import java.util.List;

/**
 * Filters out projects using the Metadata search functionality.
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
@Extension
public class MetadataViewJobFilter extends ViewJobFilter {
    private String searchString;

    /**
     * Standard DataBoundConstructor.
     * @param searchString the searchString.
     */
    @DataBoundConstructor
    public MetadataViewJobFilter(String searchString) {
        this.searchString = searchString;
    }

    /**
     * Empty constructor.
     */
    public MetadataViewJobFilter() {
    }

    /**
     * Standard getter.
     * @return the searchString.
     */
    public String getSearchString() {
        return searchString;
    }

    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
        List<TopLevelItem> returnList = new LinkedList<TopLevelItem>();
        try {
            returnList = doSearchMetadata(searchString, all);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnList;
    }

    /**
     * Searches for the queryString.
     * @param queryString the search query.
     * @param all the TopLevelItems to search amongst.
     * @return a list of TopLevelItems, the result of the search.
     * @throws Exception if an error occur.
     */
    private List<TopLevelItem> doSearchMetadata(String queryString, List<TopLevelItem> all) throws Exception {
        MetadataQuerySearch metadataQuerySearch = MetadataQuerySearch.parseQuery(queryString);
        return metadataQuerySearch.searchQuery(all);
    }

    /**
     * Standard Descriptor.
     */
    @Extension
    public static class MetadataViewJobFilterDescriptor extends Descriptor<ViewJobFilter> {
        @Override
        public String getDisplayName() {
            return Messages.ViewFilter_DisplayName();
        }
    }
}
