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
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.ParentUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Computer;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.security.ACL;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import net.sf.json.JSON;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.sonyericsson.hudson.plugins.metadata.Constants.REQUEST_ATTR_METADATA_CONTAINER;

/**
 * Stores metadata about Nodes.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@XStreamAlias("node-metadata")
@ExportedBean
public class MetadataNodeProperty extends NodeProperty<Node> implements MetadataContainer<MetadataValue> {

    private List<MetadataValue> values;

    /**
     * Standard Constructor.
     *
     * @param values the metadata for a Node.
     */
    @DataBoundConstructor
    public MetadataNodeProperty(List<MetadataValue> values) {
        this.values = values;
        for (MetadataValue value : this.values) {
            value.setParent(this);
        }
    }

    /**
     * Default constructor. <strong>Do not use this unless you are a serializer.</strong>
     */
    public MetadataNodeProperty() {
    }

    /**
     * The list of metadata values.
     *
     * @return the values.
     *
     * @see #getChildren()
     */
    public synchronized List<MetadataValue> getValues() {
        if (values == null) {
            values = new LinkedList<MetadataValue>();
        }
        return values;
    }

    @Override
    public synchronized MetadataValue getChild(String name) {
        return ParentUtil.getChildValue(values, name);
    }

    @Override
    public synchronized Collection<MetadataValue> addChild(MetadataValue value) {
        return ParentUtil.addChildValue(this, values, value);
    }

    @Override
    public synchronized Collection<MetadataValue> addChildren(Collection<MetadataValue> children) {
        return ParentUtil.addChildValues(this, this.values, children);
    }

    @Override
    @Exported
    public synchronized Collection<MetadataValue> getChildren() {
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

    /**
     * Jenkins likes to display both the master's properties summary.jelly and the node's summary.jelly on the computer
     * page. This method determines if the request really represents the Node that this property is tied to.
     *
     * @param request the request.
     * @return true if the request is for this Node and the summary should be displayed.
     */
    public boolean shouldDisplaySummary(StaplerRequest request) {
        List<Ancestor> ancestors = request.getAncestors();
        Ancestor ancestor = ancestors.get(ancestors.size() - 1);
        if (ancestor.getObject() instanceof Computer) {
            Computer computer = (Computer)ancestor.getObject();
            return computer.getNode().getNodeProperties().get(MetadataNodeProperty.class) == this;
        }
        return false;
    }

    @Override
    public void save() throws IOException {
        Hudson.getInstance().save();
    }

    @Override
    public ACL getACL() {
        if (this.node != null) {
            return this.node.getACL();
        } else {
            return Hudson.getInstance().getACL();
        }
    }

    /**
     * Descriptor for {@link MetadataNodeProperty}.
     */
    @Extension
    public static class MetadataNodePropertyDescriptor extends NodePropertyDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.MetadataNodeProperty_DisplayName();
        }

        /**
         * All registered meta data descriptors that applies to jobs. To be used by a hetero-list.
         *
         * @param request the current http request.
         * @return a list.
         */
        public List<AbstractMetadataValue.AbstractMetaDataValueDescriptor> getValueDescriptors(StaplerRequest request) {
            request.setAttribute(REQUEST_ATTR_METADATA_CONTAINER, this);
            List<AbstractMetadataValue.AbstractMetaDataValueDescriptor> list =
                    new LinkedList<AbstractMetadataValue.AbstractMetaDataValueDescriptor>();
            ExtensionList<AbstractMetadataValue.AbstractMetaDataValueDescriptor> extensionList =
                    Hudson.getInstance().getExtensionList(AbstractMetadataValue.AbstractMetaDataValueDescriptor.class);
            for (AbstractMetadataValue.AbstractMetaDataValueDescriptor d : extensionList) {
                if (d.appliesTo(this)) {
                    list.add(d);
                }
            }
            return list;
        }

        /**
         * Gives the {@link MetadataNodeProperty} for the given Node. If no metadata is available on the node the
         * property will be created and added to the node.
         *
         * @param node the node.
         * @return the property created or found.
         *
         * @throws IOException if an error occurs when adding the property to the node.
         */
        public static MetadataNodeProperty instanceFor(Node node) throws IOException {
            if (node.getNodeProperties().get(MetadataNodeProperty.class) != null) {
                return node.getNodeProperties().get(MetadataNodeProperty.class);
            } else {
                MetadataNodeProperty property = new MetadataNodeProperty();
                property.setNode(node);
                node.getNodeProperties().add(property);
                return property;
            }
        }
    }
}
