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

import net.sf.json.JSON;

import java.util.Collection;

/**
 * The Parent node of some metadata.
 *
 * @param <T> the type to work with, either MetadataValue or MetadataDefinition.
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public interface MetadataParent<T extends Metadata> {

    /**
     * Returns the child with the given name, or null if there is none. comparison is case insensitive.
     *
     * @param name the name to search for.
     * @return the child.
     */
    T getChild(String name);

    /**
     * Adds the child to the list of children. If a child with the same name is already present, the children
     * should try to be merged. The returned child is either the child itself if it is a leaf or a clone of itself with
     * the children that failed to be merged if it contains children, null indicates a fully successful merge/add.
     *
     * @param child the child to add.
     * @return null if the operation was successful.
     */
    Collection<T> addChild(T child);

    /**
     * Adds the children to this parent's list of children. If a child with the same name is already present, the
     * children should try to be merged.
     *
     * @param children the children to add.
     * @return the children that failed to be added/merged or null if all succeeded.
     */
    Collection<T> addChildren(Collection<T> children);

    /**
     * The children of this parent.
     *
     * @return the children.
     */
    Collection<T> getChildren();

    /**
     * The full name of the element.
     *
     * @return the fullName.
     */
    String getFullName();

    /**
     * The full name of the element, using the chosen separator string.
     * @param separator the separator string.
     * @return the fullName.
     */
    String getFullName(String separator);

    /**
     * Convert this object into a JSON object.
     *
     * @return the JSON version.
     */
    JSON toJson();

    /**
     * If this parent type requires to be replaced or not when a replacement command is issued. I.e. If this is just a
     * holder of children then it is not necessary, but if it contains more complex structures it might want to.
     *
     * @return true if it needs to be replaced by fresher instances or false if it can be reused.
     * @see com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue#replacementOf(
     * com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue)
     */
    boolean requiresReplacement();

    /**
     * The index of the child with the provided name.
     *
     * @param name the name of the child to find.
     * @return the index of the child or -1 if no child with that name was found.
     */
    int indexOf(String name);

    /**
     * Sets the child on <code>index</code> with the provided value, replacing any object currently on that index.
     * @param index the index to set.
     * @param value the child to set.
     * @return the value previous at the specified index.
     */
    T setChild(int index, T value);
}
