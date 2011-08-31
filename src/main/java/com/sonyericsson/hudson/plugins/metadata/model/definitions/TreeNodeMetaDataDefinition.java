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
package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetaDataValue;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * A metadata definition containing other metadata definitions as children.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class TreeNodeMetaDataDefinition extends AbstractMetaDataDefinition {

    private List<AbstractMetaDataDefinition> children;

    /**
     * Standard constructor.
     * @param name the name.
     * @param description the description.
     * @param children the children of this node.
     */
    @DataBoundConstructor
    public TreeNodeMetaDataDefinition(String name, String description, List<AbstractMetaDataDefinition> children) {
        super(name, description);
        this.children = children;
    }

    /**
     * Standard constructor.
     *
     * @param name the name.
     */
    public TreeNodeMetaDataDefinition(String name) {
        super(name);
        this.children = new LinkedList<AbstractMetaDataDefinition>();

    }

    /**
     * Standard constructor.
     *
     * @param name     the name.
     * @param children the children of this node.
     */
    public TreeNodeMetaDataDefinition(String name, List<AbstractMetaDataDefinition> children) {
        super(name);
        this.children = children;
    }

    @Override
    public AbstractMetaDataValue createValue(StaplerRequest req, JSONObject jo) {
        TreeNodeMetaDataValue value = req.bindJSON(TreeNodeMetaDataValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

    @Override
    public AbstractMetaDataValue createValue(StaplerRequest req) {
        return null;

    }

    /**
     * The Descriptor.
     */
    @Extension
    public static class TreeNodeMetaDataDefinitionDescriptor extends
            AbstractMetaDataDefinition.AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.TreeNodeMetaDataDefinition_DisplayName();
        }

        /**
         * Returns all the registered meta data definition descriptors. For use in a hetero-list.
         *
         * @param request the current request.
         * @return the descriptors.
         */
        public List<AbstractMetaDataDefinitionDescriptor> getDefinitionDescriptors(StaplerRequest request) {
            List<AbstractMetaDataDefinitionDescriptor> list = new LinkedList<AbstractMetaDataDefinitionDescriptor>();
            ExtensionList<AbstractMetaDataDefinitionDescriptor> extensionList =
                    Hudson.getInstance().getExtensionList(AbstractMetaDataDefinitionDescriptor.class);
            for (AbstractMetaDataDefinitionDescriptor d : extensionList) {
                //TODO fix the problem  with limitless loop, for now don't return anything nested.
                if (!(d instanceof TreeNodeMetaDataDefinitionDescriptor)) {
                    list.add(d);
                }
            }
            return list;
        }
    }
}
