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

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.AbstractMetaDataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetaDataValueParent;
import com.sonyericsson.hudson.plugins.metadata.model.values.ParentUtil;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import java.util.ArrayList;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Gives support for meta data on Projects and their builds.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class MetaDataJobProperty extends JobProperty<AbstractProject<?, ?>> implements MetaDataValueParent {

    private List<AbstractMetaDataValue> values;

    /**
     * Standard DataBound Constructor.
     *
     * @param values the meta data.
     */
    @DataBoundConstructor
    public MetaDataJobProperty(List<AbstractMetaDataValue> values) {
        this.values = values;
    }

    /**
     * Default constructor. <strong>Do not use unless you are a serializer.</strong>
     */
    public MetaDataJobProperty() {
    }

    /**
     * The meta data.
     *
     * @return the values.
     */
    public synchronized List<AbstractMetaDataValue> getValues() {
        if (values == null) {
            values = new LinkedList<AbstractMetaDataValue>();
        }
        return values;
    }

    /**
     * All the non generated values. I.e. the values that the user has put in.
     *
     * @return all user values.
     */
    public synchronized List<AbstractMetaDataValue> getUserValues() {
        List<AbstractMetaDataValue> allValues = getValues();
        List<AbstractMetaDataValue> userValues = new LinkedList<AbstractMetaDataValue>();
        for (AbstractMetaDataValue value : allValues) {
            if (!value.isGenerated()) {
                userValues.add(value);
            }
        }
        return userValues;
    }

    public AbstractProject<?, ?> getOwner() {
        return owner;
    }

    @Override
    public Collection<? extends Action> getJobActions(AbstractProject<?, ?> job) {
        final MetaDataJobAction newAction = new MetaDataJobAction(job.getProperty(this.getClass()));
        if(newAction==null) return Collections.emptyList();
        return Collections.singletonList(newAction);
    }

    @Override
    public synchronized AbstractMetaDataValue getChildValue(String name) {
        return ParentUtil.getChildValue(getValues(), name);
    }

    @Override
    public synchronized AbstractMetaDataValue addChildValue(AbstractMetaDataValue value) {
        return ParentUtil.addChildValue(this, getValues(), value);
    }

    @Override
    public synchronized Collection<AbstractMetaDataValue> addChildValues(Collection<AbstractMetaDataValue> childValues) {
        return ParentUtil.addChildValues(this, getValues(), childValues);
    }

    @Override
    public Collection<AbstractMetaDataValue> getChildren() {
        return values;
    }

    /**
     * All registered meta data descriptors. To be used by a hetero-list.
     *
     * @param request the current http request.
     * @return a list.
     */
    public List<AbstractMetaDataDefinition> getDefinitions(StaplerRequest request) {
        return PluginImpl.getInstance().getDefinitions();
    }

    /**
     * Descriptor for the {@link MetaDataJobProperty}.
     */
    @Extension
    public static class MetaDataJobPropertyDescriptor extends JobPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.MetaDataJobProperty_DisplayName();
        }

        /**
         * All registered meta data descriptors. To be used by a hetero-list.
         *
         * @param request the current http request.
         * @return a list.
         */
        public List<AbstractMetaDataValue.AbstractMetaDataValueDescriptor> getValueDescriptors(StaplerRequest request) {
            List<AbstractMetaDataValue.AbstractMetaDataValueDescriptor> list =
                    new LinkedList<AbstractMetaDataValue.AbstractMetaDataValueDescriptor>();
            ExtensionList<AbstractMetaDataValue.AbstractMetaDataValueDescriptor> extensionList =
                    Hudson.getInstance().getExtensionList(AbstractMetaDataValue.AbstractMetaDataValueDescriptor.class);
            for (AbstractMetaDataValue.AbstractMetaDataValueDescriptor d : extensionList) {
                list.add(d);
            }
            return list;
        }


    }
}
