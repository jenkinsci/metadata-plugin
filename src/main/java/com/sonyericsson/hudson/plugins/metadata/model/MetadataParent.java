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
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 * @param <T> the type to work with, either MetadataValue or MetadataDefinition.
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
     * Adds the child to this parent's list of children. If a child with the same name is already present, the
     * children should try to be merged. The returned child is either the child itself if it is a leaf or a
     * clone of itself with the children that failed to be merged if it contains children,
     * null indicates a fully successful merge/add.
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
     * Convert this object into a JSON object.
     * @return the JSON version.
     */
    JSON toJson();
}
