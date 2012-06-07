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
import com.sonyericsson.hudson.plugins.metadata.model.PluginImpl;
import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.RootAction;
import hudson.security.Permission;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import org.kohsuke.stapler.QueryParameter;

/**
 * A Metadata Search page for searching metadata
 * in Jenkins jobs, this page will search and list
 * the jobs having the metadata entered by user.
 *
 * @author Shemeer Sulaiman &lt;shemeer.x.sulaiman@sonymobile.com&gt;
 */
@Extension
public class MetadataSearchPage implements RootAction {

    private static final String URL_NAME = "MetaDataSearch";

   /**
     * Default constructor.
     */
    public MetadataSearchPage() {
    }

    @Override
    public String getIconFileName() {
        if (Hudson.getInstance().hasPermission(PluginImpl.READ_METADATA)) {
            return "search.png";
        } else {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        if (Hudson.getInstance().hasPermission(PluginImpl.READ_METADATA)) {
            return Messages.SearchPage_DisplayName();
        } else {
            return null;
        }
    }

    @Override
    public String getUrlName() {
        return URL_NAME;
    }

    /**
     * Serves the permission required to perform this action.
     * Used by index.jelly
     *
     * @return the permission.
     */
    @SuppressWarnings("unused")
    public Permission getRequiredPermission() {
        return PluginImpl.READ_METADATA;
    }

    /**
     * Check whether the project is enabled or not.
     *
     * @return true if enabled, false if not.
     */
    public boolean isEnabled() {
        if (PluginImpl.getInstance() != null) {
            return true;
        }
        return false;
    }

    /**
     * Search the query entered on the Metadata Search page.
     *
     * @param queryString the search query.
     * @param request  the current HTTP request.
     * @param response the response from this operation.
     * @throws Exception if an error occur.
     */
    public void doSearchMetadata(@QueryParameter("metadata.search.queryString") final String queryString,
            StaplerRequest request, StaplerResponse response)
            throws Exception {
        MetadataQueryParser parser = new MetadataQueryParser();
        parser.parseQuery(queryString);
        request.getSession(true).setAttribute("metadata.search.queryString", queryString);
        response.sendRedirect2(".");
    }

    /**
     * Search and find the matched project using
     * Metadata query walker.
     */
    public void searchMatchedProjects() {
    }
}
