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

import com.sonyericsson.hudson.plugins.metadata.Constants;
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import hudson.ExtensionList;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

/**
 * A metadata value to be set in a job or node.
 */
@ExportedBean
public abstract class AbstractMetadataValue implements Serializable, Describable<AbstractMetadataValue>, MetadataValue {
    /**
     * The name of this metadata value.
     */
    protected final String name;
    private String description;
    private MetadataParent parent;
    private boolean generated = false;

    /**
     * Constructor with name and description.
     *
     * @param name        The name of the definitions.
     * @param description The description of the definitions.
     */
    protected AbstractMetadataValue(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Constructor with only a name.
     *
     * @param name The name of the definitions.
     */
    protected AbstractMetadataValue(String name) {
        this(name, null);
    }

    /**
     * Get the description of this value.
     *
     * @return the description.
     */

    @Exported
    @Override
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
    @Exported
    @Override
    public synchronized Object getValue() {
        return null;
    }

    /**
     * This value's parent.
     *
     * @return the parent.
     */
    public synchronized MetadataParent getParent() {
        return parent;
    }

    /**
     * This value's parent.
     *
     * @param parent the parent.
     */
    public synchronized void setParent(MetadataParent parent) {
        this.parent = parent;
    }

    /**
     * If this value is generated or user created.
     *
     * @return true if generated.
     */
    @Override
    public synchronized boolean isGenerated() {
        return generated;
    }

    /**
     * Set if this value is generated or user created.
     *
     * @param generated true if generated.
     */
    @Override
    public synchronized void setGenerated(boolean generated) {
        this.generated = generated;
    }

    /**
     * This function will generate the full name.
     *
     * @return the full name.
     */
    @Exported
    public String getFullName() {
        if (getParent() != null) {
            return getParent().getFullName() + Constants.SEPARATOR_DOT + getName();
        }
        return getName();
    }

    @Override
    public void replacementOf(MetadataValue old) {
        //Nothing needs to be done as most types should be replaced directly.
    }

    /**
     * Converts this into a JSON Object <strong>without the value</strong>. Implementing classes can use this as a
     * utility method for the name, type and description. And then just add the value.
     *
     * @return the half finished JSON Object.
     */
    protected synchronized JSONObject toAbstractJson() {
        JSONObject obj = new JSONObject();
        obj.put(JsonUtils.NAME, name);
        obj.put(JsonUtils.DESCRIPTION, description);
        obj.put(JsonUtils.GENERATED, generated);

        AbstractMetaDataValueDescriptor descriptor = (AbstractMetaDataValueDescriptor)getDescriptor();
        obj.put(JsonUtils.METADATA_TYPE, descriptor.getJsonType());
        return obj;
    }

    /**
     * The descriptor for the AbstractMetadataValue.
     */
    public abstract static class AbstractMetaDataValueDescriptor extends Descriptor<AbstractMetadataValue> {

        /**
         * Tells if values of this descriptor can be added to the specified container type or not. Some value types
         * might not apply to be added to a node and vice versa. The default implementation always returns true.
         *
         * @param containerDescriptor the descriptor for the container that the values of this type can be added to. Can
         *                            be null.
         * @return true if it applies.
         */
        public boolean appliesTo(Descriptor containerDescriptor) {
            return true;
        }

        /**
         * Finds the descriptor for the given metadata-type.
         * @param type the type to find.
         * @return the Descriptor or null if non is found.
         */
        public static AbstractMetaDataValueDescriptor findForJsonType(String type) {
            ExtensionList<AbstractMetaDataValueDescriptor> extensionList =
                    Hudson.getInstance().getExtensionList(AbstractMetadataValue.AbstractMetaDataValueDescriptor.class);
            for (AbstractMetadataValue.AbstractMetaDataValueDescriptor d : extensionList) {
                if (d.getJsonType().equals(type)) {
                    return d;
                }
            }
            return null;
        }

        /**
         * Gives the type to put into the JSON conversations.
         *
         * @return the JSON type field.
         */
        public abstract String getJsonType();

        /**
         * Converts a JSON object into a MetadataValue of this descriptors describable.
         *
         * @param json the json data to use.
         * @return the converted value.
         *
         * @throws JsonUtils.ParseException if something is for example missing.
         */
        public abstract MetadataValue fromJson(JSONObject json) throws JsonUtils.ParseException;
    }
}

