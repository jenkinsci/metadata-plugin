/*
 *  The MIT License
 *
 *  Copyright 2011 Sony Ericsson Mobile Communications. All rights reserved.
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
package com.sonyericsson.hudson.plugins.metadata.model;

import com.sonyericsson.hudson.plugins.metadata.Constants;
import com.sonyericsson.hudson.plugins.metadata.Messages;
import hudson.model.AbstractProject;
import hudson.model.Action;

/**
 * Holds the meta data for a job.
 *
 * @author Shemeer S;
 */
public class MetadataJobAction implements Action {

    /** The project. */
    private final transient AbstractProject<?, ?> project;
    /** The MetadataJobProperty. */
    private MetadataJobProperty jobProperty;

    /**
     * Standard constructor.
     * @param jobProperty the current jobProperty.
     */
    public MetadataJobAction(MetadataJobProperty jobProperty) {
        this.jobProperty = jobProperty;
        this.project = jobProperty.getOwner();
    }

    @Override
    public String getIconFileName() {
        return Constants.COMMON_ICON;
    }

    @Override
    public String getDisplayName() {
        return Messages.Actions_DisplayName();
    }

    @Override
    public String getUrlName() {
        return Constants.COMMON_URL_NAME;
    }

    /**
     * Returns the project.
     *
     * @return project
     */
    public final AbstractProject<?, ?> getProject() {
        return project;
    }

    /**
     * Returns the metaDataJobProperty.
     *
     * @return property
     */
    public final MetadataJobProperty getJobProperty() {
        return jobProperty;
    }
}
