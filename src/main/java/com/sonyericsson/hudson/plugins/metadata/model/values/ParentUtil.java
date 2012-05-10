/*
 *  The MIT License
 *
 *  Copyright 2011 Sony Ericsson Mobile Communications. All rights reserved.
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
package com.sonyericsson.hudson.plugins.metadata.model.values;

import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.sonyericsson.hudson.plugins.metadata.model.Metadata;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.MetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.TreeNodeMetadataDefinition;
import net.sf.json.JSON;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for handling merge operation inside
 * {@link com.sonyericsson.hudson.plugins.metadata.model.MetadataParent}s.
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
     * Replaces an existing value amongst the parent's children.
     *
     * @param parent the parent to add/replace the child to
     * @param value  the value to replace
     */
    public static void replaceChild(MetadataParent<MetadataValue> parent, MetadataValue value) {
        if (value == null) {
            throw new IllegalArgumentException("The child value is null");
        }
        MetadataValue my = parent.getChild(value.getName());
        if (my != null) {
            if (my instanceof MetadataParent && value instanceof MetadataParent) {
                MetadataParent<MetadataValue> myParent = (MetadataParent<MetadataValue>)my;
                MetadataParent<MetadataValue> valueParent = (MetadataParent<MetadataValue>)value;
                if (myParent.requiresReplacement() || valueParent.requiresReplacement()) {
                    parent.setChild(parent.indexOf(my.getName()), value);
                    value.setParent(parent);
                    value.replacementOf(my);
                    my.setParent(null); //It should be prepared to be gc'ed
                } else {
                    replaceChildren(myParent, new ArrayList<MetadataValue>(valueParent.getChildren()));
                }
            } else {
                //it exists! then it is time to replace it.
                parent.setChild(parent.indexOf(my.getName()), value);
                value.setParent(parent);
                value.replacementOf(my);
                my.setParent(null); //It should be prepared to be gc'ed
            }
        } else {
            //didn't exist lets just add it.
            parent.getChildren().add(value);
            value.setParent(parent);
        }
    }

    /**
     * Adds the children in the list to the parent, replacing any existing children already present.
     *
     * @param parent   the parent to add to.
     * @param children the children to add/replace
     */
    public static void replaceChildren(MetadataParent<MetadataValue> parent, List<MetadataValue> children) {
        for (MetadataValue child : children) {
            replaceChild(parent, child);
        }
    }

    /**
     * Adds the value as a child to the parent. Help utility for those who implement {@link
     * com.sonyericsson.hudson.plugins.metadata.model.MetadataParent#addChild(Metadata)}
     *
     * @param parent   the parent
     * @param children the direct list of the parents children.
     * @param value    the value to add.
     * @param <T>      the type for parent, children, value and the return value.
     * @return the value(s) that failed to be added.
     */
    public static <T extends Metadata> Collection<T> addChildValue(MetadataParent<T> parent,
                                                                   List<T> children,
                                                                   T value) {

        if (value == null) {
            throw new IllegalArgumentException("The added child value is null");
        }
        T my = parent.getChild(value.getName());
        if (my != null) {
            Collection<T> returnList = null;
            if (my instanceof MetadataParent && value instanceof MetadataParent) {
                //they are both a path, let's try to merge as much as possible.
                Collection<T> subValues = ((MetadataParent)value).getChildren();
                Collection<T> leftOvers = ((MetadataParent)my).addChildren(subValues);
                if (leftOvers != null && !leftOvers.isEmpty()) {
                    //some of the children failed to be merged, return them to sender.
                    Metadata treeNode = null;
                    if (value instanceof MetadataValue) {
                        LinkedList<MetadataValue> list = (LinkedList<MetadataValue>)leftOvers;
                        treeNode = new TreeNodeMetadataValue(value.getName(), value.getDescription(),
                                list, value.isExposedToEnvironment());
                    } else if (value instanceof MetadataDefinition) {
                        LinkedList<MetadataDefinition> list = (LinkedList<MetadataDefinition>)leftOvers;
                        treeNode = new TreeNodeMetadataDefinition(value.getName(), value.getDescription(), list);
                    }
                    returnList = new LinkedList<T>();
                    returnList.add((T)treeNode);
                    return returnList;
                }

            } else {
                //one or both of them is not a parent, so we fail.
                returnList = new LinkedList<T>();
                returnList.add(value);

            }
            return returnList;
        } else {
            children.add(value);
            value.setParent(parent);
            return null;
        }
    }

    /**
     * Adds the values as a child to the parent. Help utility for those who implement {@link
     * com.sonyericsson.hudson.plugins.metadata.model.MetadataParent#
     * addChild(com.sonyericsson.hudson.plugins.metadata.model.Metadata)}
     *
     * @param parent   the parent to add the values to
     * @param children the direct list of the parents children.
     * @param values   the values to add.
     * @param <T>      the type for parent, children, values and the return value.
     * @return the values that failed to be added.
     */
    public static <T extends Metadata> Collection<T> addChildValues(MetadataParent parent,
                                                                    List<T> children,
                                                                    Collection<T> values) {
        List<T> leftovers = new LinkedList<T>();
        for (T value : values) {
            Collection<T> returned = addChildValue(parent, children, value);
            if (returned != null) {
                leftovers.addAll(returned);
            }
        }
        if (leftovers.isEmpty()) {
            return null;
        } else {
            return leftovers;
        }
    }

    /**
     * Utility method for {@link com.sonyericsson.hudson.plugins.metadata.model.MetadataParent#getChild(String)}.
     *
     * @param values the list of children.
     * @param name   the name to search.
     * @param <T>    the type for values, name and the return value.
     * @return the child if found or null if not.
     */
    public static <T extends Metadata> T getChildValue(Collection<T> values, String name) {
        for (T value : values) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    /**
     * Utility method for {@link com.sonyericsson.hudson.plugins.metadata.model.MetadataParent#indexOf(String)} }.
     *
     * @param children the list of children.
     * @param name     the name to search.
     * @param <T>      the type for values, name and the return value.
     * @return the index of the child if found or -1 if not.
     */
    public static <T extends Metadata> int getChildIndex(List<T> children, String name) {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Converts the container into a JSON object. This processing is different from {@link
     * com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue#toJson()} because it will only convert the
     * children not the entire object, since a container (like
     * {@link com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty})
     * in essence doesn't have a name.
     *
     * @param container the container
     * @return the JSON representation.
     */
    public static JSON toJson(MetadataContainer<MetadataValue> container) {
        return JsonUtils.toJson(container.getChildren());
    }

    /**
     * Gets the child names of the given parent.
     * @param parent the parent to get the child names from.
     * @return the child names.
     */
    public static Collection<String> getChildNames(MetadataParent<MetadataValue> parent) {
        Collection<String> childNames = new LinkedList<String>();
        Collection<MetadataValue> children = parent.getChildren();
        if (children != null) {
            for (MetadataValue value : children) {
                childNames.add(value.getName());
            }
        }
        return childNames;
    }
}
