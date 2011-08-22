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

import hudson.model.Describable;
import hudson.model.Descriptor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

/**
 * A metadata value to be set in a job or node.
 */
@ExportedBean
public abstract class AbstractMetaDataValue implements Serializable, Describable<AbstractMetaDataValue> {
    /**
     * The name of this metadata value.
     */
    protected final String name;
    private String description;
    private MetaDataValueParent parent;

    /**
     * Constructor with name and description.
     *
     * @param name        The name of the definitions.
     * @param description The description of the definitions.
     */
    protected AbstractMetaDataValue(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor with only a name.
     *
     * @param name The name of the definitions.
     */
    protected AbstractMetaDataValue(String name) {
        this(name, null);
    }

    /**
     * Get the description of this value.
     *
     * @return the description.
     */
    public synchronized String getDescription() {
        return description;
    }

    /**
     * Set the description of this value.
     *
     * @param description the description.
     */
    public synchronized void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the name of this value.
     *
     * @return the name.
     */
    @Exported
    public final synchronized String getName() {
        return name;
    }

    /**
     * Get the value.
     *
     * @return the value.
     */
    public synchronized Object getValue() {
        return null;
    }

    /**
     * This value's parent.
     * @return the parent.
     */
    public synchronized MetaDataValueParent getParent() {
        return parent;
    }

    /**
     * This value's parent.
     * @param parent the parent.
     */
    public synchronized void setParent(MetaDataValueParent parent) {
        this.parent = parent;
    }

    /**
     * Checks if the two values can be merged or not. It is good to do this check before calling {@link
     * #merge(AbstractMetaDataValue)} since a failed merge could leave this value in a semi merged state.
     *
     * @param other the other value to check for mergeability.
     * @return true if they can be merged.
     */
    public synchronized boolean canMerge(AbstractMetaDataValue other) {
        return false;
    }

    /**
     * Attempts to merge two values with each other. This value could already be created by another contributing plugin
     * or parts of a tree of metadata information is already created. This method tries to merge these values.
     *
     * @param other The other value to merge with
     * @return true if successful, false if not
     */
    public synchronized boolean merge(AbstractMetaDataValue other) {
        return false;
    }

    /**
     * The descriptor for the AbstractMetaDataValue.
     */
    public abstract static class AbstractMetaDataValueDescriptor extends Descriptor<AbstractMetaDataValue> {
    }
}

