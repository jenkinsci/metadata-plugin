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
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.ParentUtil;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.Run;
import hudson.security.ACL;
import net.sf.json.JSON;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Holds the meta data for a run.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class MetadataBuildAction implements Action, MetadataContainer<MetadataValue> {

    private Run run;
    private List<MetadataValue> values;

    /**
     * Standard constructor.
     *
     * @param run    The run that this action is added to.
     * @param values the meta data for this run.
     */
    public MetadataBuildAction(Run run, List<MetadataValue> values) {
        this.run = run;
        this.values = values;
    }

    /**
     * Standard constructor.
     *
     * @param run The run that this action is added to.
     */
    public MetadataBuildAction(Run run) {
        this.run = run;
    }

    /**
     * Default constructor.
     */
    public MetadataBuildAction() {
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
     * The run that this action is added to.
     *
     * @return the run.
     */
    public Run getRun() {
        return run;
    }

    /**
     * The meta data in this action.
     *
     * @return the meta data.
     *
     * @see #getChildren()
     */
    public List<MetadataValue> getValues() {
        if (values == null) {
            values = new LinkedList<MetadataValue>();
        }
        return values;
    }

    @Override
    public MetadataValue getChild(String name) {
        return ParentUtil.getChildValue(getValues(), name);
    }

    @Override
    public Collection<MetadataValue> addChild(MetadataValue value) {
        return ParentUtil.addChildValue(this, getValues(), value);
    }

    @Override
    public Collection<MetadataValue> addChildren(Collection<MetadataValue> childValues) {
        return ParentUtil.addChildValues(this, getValues(), childValues);
    }

    @Override
    public Collection<MetadataValue> getChildren() {
        return getValues();
    }

    @Override
    public String getFullName() {
        return "";
    }

    @Override
    public JSON toJson() {
        return ParentUtil.toJson(this);
    }

    @Override
    public void save() throws IOException {
        if (this.run != null) {
            this.run.save();
        } else {
            throw new IOException("This container is not attached to any build.");
        }
    }

    @Override
    public ACL getACL() {
        if (run != null) {
            return run.getACL();
        } else {
            return Hudson.getInstance().getACL();
        }
    }
}
