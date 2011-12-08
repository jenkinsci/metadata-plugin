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
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.EnvVars;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.sonyericsson.hudson.plugins.metadata.Constants.REQUEST_ATTR_METADATA_CONTAINER;
import static com.sonyericsson.hudson.plugins.metadata.Constants.SERIALIZATION_ALIAS_TREE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.CHILDREN;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.GENERATED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.checkRequiredJsonAttribute;

/**
 * Meta data containing other meta data values. Used to create tree structures of data.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(
        value = "UG_SYNC_SET_UNSYNC_GET",
        justification = "It is synchronized")
@XStreamAlias(SERIALIZATION_ALIAS_TREE)
public class TreeNodeMetadataValue extends AbstractMetadataValue implements MetadataParent<MetadataValue>, Cloneable {

    private List<MetadataValue> children;

    /**
     * Standard Constructor.
     *
     * @param name        the name.
     * @param description the description
     * @param children    its children.
     * @param exposedToEnvironment if this value should be exposed to the build as an
     *                      environment variable.
     */
    @DataBoundConstructor
    public TreeNodeMetadataValue(String name, String description, List<MetadataValue> children,
                                 boolean exposedToEnvironment) {
        super(name, description, exposedToEnvironment);
        setChildren(children);
    }

    /**
     * Standard Constructor.
     *
     * @param name        the name
     * @param description the description.
     * @param children    its children.
     */
    public TreeNodeMetadataValue(String name, String description, List<MetadataValue> children) {
        super(name, description, false);
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
     * Standard Constructor.
     *
     * @param name the name.
     */
    public TreeNodeMetadataValue(String name) {
        super(name);
        this.children = new LinkedList<MetadataValue>();
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
        } else {
            this.children = new LinkedList<MetadataValue>();
        }
    }

    @Override
    public synchronized List<MetadataValue> getValue() {
        if (children == null) {
            children = new LinkedList<MetadataValue>();
        }
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
    public synchronized int indexOf(String name) {
        return ParentUtil.getChildIndex(children, name);
    }

    @Override
    public synchronized MetadataValue setChild(int index, MetadataValue value) {
        return children.set(index, value);
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
        return getValue();
    }

    @Override
    public synchronized void replacementOf(MetadataValue old) {
        if (old instanceof MetadataParent) {
            MetadataParent<MetadataValue> oldParent = (MetadataParent<MetadataValue>)old;
            for (MetadataValue child : oldParent.getChildren()) {
                if (getChild(child.getName()) == null) {
                    addChild(child);
                }
            }
        }
    }

    @Override
    public Descriptor<AbstractMetadataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(TreeNodeMetaDataValueDescriptor.class);
    }

    @Override
    public synchronized JSONObject toJson() {
        JSONObject obj = toAbstractJson();
        JSONArray array = new JSONArray();
        for (MetadataValue child : children) {
            array.add(child.toJson());
        }
        obj.put(CHILDREN, array);
        return obj;
    }

    @Override
    public boolean requiresReplacement() {
        return false;
    }

    @Override
    public void addEnvironmentVariables(EnvVars variables, boolean exposeAll) {
        for (MetadataValue v : getChildren()) {
            if (isExposedToEnvironment()) {
                v.addEnvironmentVariables(variables, true);
            } else {
                v.addEnvironmentVariables(variables, exposeAll);
            }
        }
    }

    @Override
        public TreeNodeMetadataValue clone() throws CloneNotSupportedException {
            TreeNodeMetadataValue tree = (TreeNodeMetadataValue)super.clone();
            List<MetadataValue> clonedChildren = new LinkedList<MetadataValue>();
            for (MetadataValue child : children) {
                MetadataValue clonedChild = child.clone();
                clonedChild.setParent(tree);
                clonedChildren.add(clonedChild);
            }

            tree.setChildren(clonedChildren);
            return tree;
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

        @Override
        public String getJsonType() {
            return SERIALIZATION_ALIAS_TREE;
        }

        @Override
        public MetadataValue fromJson(JSONObject json, MetadataContainer<MetadataValue> container)
                throws JsonUtils.ParseException {
            checkRequiredJsonAttribute(json, NAME);
            List<MetadataValue> children = new LinkedList<MetadataValue>();
            if (json.has(CHILDREN)) {
                JSONArray array = json.getJSONArray(CHILDREN);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    children.add(JsonUtils.toValue(obj, container));
                }
            }
            TreeNodeMetadataValue value = new TreeNodeMetadataValue(
                    json.getString(NAME), json.optString(DESCRIPTION), children);
            if (json.has(EXPOSED)) {
                value.setExposeToEnvironment(json.getBoolean(EXPOSED));
            }
            if (json.has(GENERATED)) {
                value.setGenerated(json.getBoolean(GENERATED));
            } else {
                //TODO Decide if this is really what should be done.
                value.setGenerated(true);
            }
            return value;
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
                if (d.appliesTo(container)) {
                    list.add(d);
                }
            }
            return list;
        }
    }
}
