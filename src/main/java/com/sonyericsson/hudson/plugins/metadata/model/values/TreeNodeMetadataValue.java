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

import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.sonyericsson.hudson.plugins.metadata.Constants.REQUEST_ATTR_METADATA_CONTAINER;

/**
 * Meta data containing other meta data values. Used to create tree structures of data.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */

@XStreamAlias("metadata-tree")
public class TreeNodeMetadataValue extends AbstractMetadataValue implements MetadataParent<MetadataValue> {

    private List<MetadataValue> children;

    /**
     * Standard Constructor.
     *
     * @param name        the name.
     * @param description the description
     * @param children    its children.
     */
    @DataBoundConstructor
    public TreeNodeMetadataValue(String name, String description, List<MetadataValue> children) {
        super(name, description);
        setChildren(children);
    }

    /**
     * Standard Constructor.
     *
     * @param name        the name
     * @param description the description.
     */
    public TreeNodeMetadataValue(String name, String description) {
        super(name, description);
        this.children = new LinkedList<MetadataValue>();
    }

    /**
     * Standard Constructor.
     *
     * @param name     the name.
     * @param children its children.
     */
    public TreeNodeMetadataValue(String name, List<MetadataValue> children) {
        super(name);
        setChildren(children);
    }

    /**
     * Sets {@link #children} and sets their parent to this.
     *
     * @param children the children.
     */
    private synchronized void setChildren(List<MetadataValue> children) {
        this.children = children;
        if (this.children != null) {
            for (MetadataValue value : this.children) {
                value.setParent(this);
            }
        }
    }

    /**
     * Standard Constructor.
     *
     * @param name the name.
     */
    public TreeNodeMetadataValue(String name) {
        super(name);
        this.children = new LinkedList<MetadataValue>();
    }

    @Override
    public synchronized List<MetadataValue> getValue() {
        return children;
    }

    /**
     * Returns the child with the given name, or null if there is none. comparison is case insensitive.
     *
     * @param name the name to search for.
     * @return the value.
     */
    @Override
    public synchronized MetadataValue getChild(String name) {
        return ParentUtil.getChildValue(children, name);
    }

    @Override
    public synchronized Collection<MetadataValue> addChild(MetadataValue value) {
        return ParentUtil.addChildValue(this, children, value);
    }

    @Override
    public synchronized Collection<MetadataValue> addChildren(Collection<MetadataValue> values) {
        return ParentUtil.addChildValues(this, children, values);
    }

    @Override
    public synchronized Collection<MetadataValue> getChildren() {
        return children;
    }

    @Override
    public Descriptor<AbstractMetadataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(TreeNodeMetaDataValueDescriptor.class);
    }

    /**
     * Descriptor for {@link TreeNodeMetadataValue}s.
     */
    @Extension
    public static class TreeNodeMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            //TODO Find a better display name.
            return Messages.TreeNodeMetadataValue_DisplayName();
        }

        /**
         * Returns all the registered meta data descriptors. For use in a hetero-list.
         *
         * @param request the current request.
         * @return the descriptors.
         */
        public List<AbstractMetaDataValueDescriptor> getValueDescriptors(StaplerRequest request) {
            Object containerObj = request.getAttribute(REQUEST_ATTR_METADATA_CONTAINER);
            Descriptor container = null;
            if ((containerObj != null) && containerObj instanceof Descriptor) {
                container = (Descriptor)containerObj;
            }
            List<AbstractMetaDataValueDescriptor> list = new LinkedList<AbstractMetaDataValueDescriptor>();
            ExtensionList<AbstractMetaDataValueDescriptor> extensionList =
                    Hudson.getInstance().getExtensionList(AbstractMetaDataValueDescriptor.class);
            for (AbstractMetaDataValueDescriptor d : extensionList) {
                //TODO fix the problem  with limitless loop, for now don't return anything nested.
                if (!(d instanceof TreeNodeMetaDataValueDescriptor) && d.appliesTo(container)) {
                    list.add(d);
                }
            }
            return list;
        }
    }
}
