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

import hudson.model.Descriptor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for handling merge operation inside
 * {@link com.sonyericsson.hudson.plugins.metadata.model.values.MetaDataValueParent}s.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public final class ParentUtil {

    /**
     * Utility constructor.
     */
    private ParentUtil() {

    }

    /**
     * Adds the value as a child to the parent. Help utility for those who implement {@link
     * MetaDataValueParent#addChildValue(com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetaDataValue)}
     *
     * @param parent   the parent
     * @param children the direct list of the parents children.
     * @param value    the value to add.
     * @return the value(s) that failed to be added.
     */
    public static AbstractMetaDataValue addChildValue(MetaDataValueParent parent, List<AbstractMetaDataValue> children,
                                                      AbstractMetaDataValue value) {
        AbstractMetaDataValue my = parent.getChildValue(value.getName());
        if (my != null) {
            if (my instanceof MetaDataValueParent && value instanceof MetaDataValueParent) {
                //they are both a path, let's try to merge as much as possible.
                Collection<AbstractMetaDataValue> subValues = ((MetaDataValueParent)value).getChildren();
                Collection<AbstractMetaDataValue> leftOvers = ((MetaDataValueParent)my).addChildValues(subValues);
                if (leftOvers != null && !leftOvers.isEmpty()) {
                    //some of the children failed to be merged, return them to sender.
                    return new LeftOverMetaDataValues(value.getName(),
                            value.getDescription(), leftOvers);
                } else {
                    return null;
                }
            } else {
                //one or both of them is not a parent, so we fail.
                return value;
            }
        } else {
            children.add(value);
            value.setParent(parent);
            return null;
        }
    }

    /**
     * Adds the values as a child to the parent. Help utility for those who implement {@link
     * MetaDataValueParent#addChildValue(com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetaDataValue)}
     *
     * @param parent   the parent to add the values to
     * @param children the direct list of the parents children.
     * @param values   the values to add.
     * @return the values that failed to be added.
     */
    public static Collection<AbstractMetaDataValue> addChildValues(MetaDataValueParent parent,
                                                                   List<AbstractMetaDataValue> children,
                                                                   Collection<AbstractMetaDataValue> values) {
        List<AbstractMetaDataValue> leftovers = new LinkedList<AbstractMetaDataValue>();
        for (AbstractMetaDataValue value : values) {
            AbstractMetaDataValue returned = addChildValue(parent, children, value);
            if (returned != null) {
                leftovers.add(returned);
            }
        }
        if (leftovers.isEmpty()) {
            return null;
        } else {
            return leftovers;
        }
    }

    /**
     * Utility method for {@link MetaDataValueParent#getChildValue(String)}.
     *
     * @param values the list of children.
     * @param name   the name to search.
     * @return the child if found or null if not.
     */
    public static AbstractMetaDataValue getChildValue(Collection<AbstractMetaDataValue> values, String name) {
        for (AbstractMetaDataValue value : values) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    /**
     * A Dummy parent for use when informing of children failing to be added.
     */
    private static class LeftOverMetaDataValues extends AbstractMetaDataValue implements MetaDataValueParent {
        private Collection<AbstractMetaDataValue> children;

        /**
         * Standard Constructor.
         * @param name the copied name
         * @param description the copied description
         * @param children the leftovers.
         */
        LeftOverMetaDataValues(String name, String description, Collection<AbstractMetaDataValue> children) {
            super(name, description);
            this.children = children;
        }

        @Override
        public Object getValue() {
            return children;
        }

        @Override
        public boolean isGenerated() {
            return true;
        }

        @Override
        public synchronized AbstractMetaDataValue getChildValue(String name) {
            return ParentUtil.getChildValue(children, name);
        }

        @Override
        public AbstractMetaDataValue addChildValue(AbstractMetaDataValue value) {
            return null;
        }

        @Override
        public Collection<AbstractMetaDataValue> addChildValues(Collection<AbstractMetaDataValue> values) {
            return null;
        }

        @Override
        public synchronized Collection<AbstractMetaDataValue> getChildren() {
            return children;
        }

        @Override
        public Descriptor<AbstractMetaDataValue> getDescriptor() {
            return null;
        }
    }
}
