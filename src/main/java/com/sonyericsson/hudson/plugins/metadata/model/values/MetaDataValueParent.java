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

import java.util.Collection;

/**
 * The Parent node of some meta-data value.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public interface MetaDataValueParent {

    /**
     * Returns the child with the given name, or null if there is none. comparison is case insensitive.
     *
     * @param name the name to search for.
     * @return the value.
     */
    AbstractMetaDataValue getChildValue(String name);

    /**
     * Adds the value to this parent's list of children. If a value with the same name is already present, the values
     * should try to be merged. The returned value is either the value itself if it is a leaf or a clone of itself with
     * the children that failed to be merged if it contains children,
     * null indicates a fully successful merge/add.
     *
     * @param value the value to add.
     * @return null if the operation was successful.
     */
    AbstractMetaDataValue addChildValue(AbstractMetaDataValue value);

    /**
     * Adds the values to this parent's list of children. If a value with the same name is already present, the values
     * should try to be merged.
     *
     * @param values the value to add.
     * @return the values that failed to be added/merged or null if all succeeded.
     */
    Collection<AbstractMetaDataValue> addChildValues(Collection<AbstractMetaDataValue> values);

    /**
     * The children of this parent.
     *
     * @return the children.
     */
    Collection<AbstractMetaDataValue> getChildren();
}
