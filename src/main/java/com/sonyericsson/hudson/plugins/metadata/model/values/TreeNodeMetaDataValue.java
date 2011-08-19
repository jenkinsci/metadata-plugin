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
package com.sonyericsson.hudson.plugins.metadata.model.values;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * Meta data containing other meta data values. Used to create tree structures of data.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class TreeNodeMetaDataValue extends AbstractMetaDataValue {

    private List<AbstractMetaDataValue> children;

    /**
     * Standard Constructor.
     *
     * @param name        the name.
     * @param description the description
     * @param children    its children.
     */
    @DataBoundConstructor
    public TreeNodeMetaDataValue(String name, String description, List<AbstractMetaDataValue> children) {
        super(name, description);
        this.children = children;
    }

    /**
     * Standard Constructor.
     *
     * @param name        the name
     * @param description the description.
     */
    public TreeNodeMetaDataValue(String name, String description) {
        super(name, description);
        this.children = new LinkedList<AbstractMetaDataValue>();
    }

    /**
     * Standard Constructor.
     *
     * @param name     the name.
     * @param children its children.
     */
    public TreeNodeMetaDataValue(String name, List<AbstractMetaDataValue> children) {
        super(name);
        this.children = children;
    }

    /**
     * Standard Constructor.
     *
     * @param name the name.
     */
    public TreeNodeMetaDataValue(String name) {
        super(name);
        this.children = new LinkedList<AbstractMetaDataValue>();
    }

    @Override
    public List<AbstractMetaDataValue> getValue() {
        return children;
    }

    /**
     * Returns the child with the given name, or null if there is none. comparison is case insensitive.
     *
     * @param name the name to search for.
     * @return the value.
     */
    public AbstractMetaDataValue getChildValue(String name) {
        for (AbstractMetaDataValue value : children) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public boolean canMerge(AbstractMetaDataValue other) {
        if (other instanceof TreeNodeMetaDataValue) {
            TreeNodeMetaDataValue otherTree = (TreeNodeMetaDataValue)other;
            for (AbstractMetaDataValue otherChild : otherTree.getValue()) {
                AbstractMetaDataValue myChild = getChildValue(otherChild.getName());
                if (myChild != null) {
                    if (!myChild.canMerge(otherChild)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean merge(AbstractMetaDataValue other) {
        if (other instanceof TreeNodeMetaDataValue) {
            TreeNodeMetaDataValue otherTree = (TreeNodeMetaDataValue)other;
            for (AbstractMetaDataValue otherChild : otherTree.getValue()) {
                AbstractMetaDataValue myChild = getChildValue(otherChild.getName());
                if (myChild != null) {
                    if (!myChild.merge(otherChild)) {
                        return false;
                    }
                } else {
                    children.add(otherChild);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Descriptor<AbstractMetaDataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(TreeNodeMetaDataValueDescriptor.class);
    }

    /**
     * Descriptor for {@link TreeNodeMetaDataValue}s.
     */
    @Extension
    public static class TreeNodeMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            //TODO Find a better display name.
            return Messages.TreeNodeMetaDataValue_DisplayName();
        }

        /**
         * Returns all the registered meta data descriptors. For use in a hetero-list.
         *
         * @param request the current request.
         * @return the descriptors.
         */
        public List<AbstractMetaDataValueDescriptor> getValueDescriptors(StaplerRequest request) {
            List<AbstractMetaDataValueDescriptor> list = new LinkedList<AbstractMetaDataValueDescriptor>();
            ExtensionList<AbstractMetaDataValueDescriptor> extensionList =
                    Hudson.getInstance().getExtensionList(AbstractMetaDataValueDescriptor.class);
            for (AbstractMetaDataValueDescriptor d : extensionList) {
                //TODO fix the problem  with limitless loop, for now don't return anything nested.
                if (!(d instanceof TreeNodeMetaDataValueDescriptor)) {
                    list.add(d);
                }
            }
            return list;
        }
    }
}
