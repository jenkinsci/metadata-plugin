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

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import static com.sonyericsson.hudson.plugins.metadata.Constants.SERIALIZATION_ALIAS_NUMBER;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.VALUE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.GENERATED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.checkRequiredJsonAttribute;

/**
 * Meta data containing a non-decimal number.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@XStreamAlias(SERIALIZATION_ALIAS_NUMBER)
public class NumberMetadataValue extends AbstractMetadataValue {
     /**
     * Constant for hashcode.
     */
    private static final int HASH_CONST = 32;
    private long value;

    /**
     * Standard Constructor.
     *
     * @param name        the name.
     * @param description the description.
     * @param value       the value.
     * @param exposedToEnvironment if this value should be exposed to the build as an
     *                      environment variable.
     */
    @DataBoundConstructor
    public NumberMetadataValue(String name, String description, long value, boolean exposedToEnvironment) {
        super(name, description, exposedToEnvironment);
        this.value = value;

    }

    /**
     * Standard Constructor.
     *
     * @param name  the name.
     * @param description the description.
     * @param value the value.
     */
    public NumberMetadataValue(String name, String description, long value) {
        super(name, description, false);
        this.value = value;
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name.
     * @param value the value.
     */
    public NumberMetadataValue(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = toAbstractJson();
        obj.put(VALUE, value);
        return obj;
    }

    @Override
    public Descriptor<AbstractMetadataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(NumberMetaDataValueDescriptor.class);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NumberMetadataValue other = (NumberMetadataValue)obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        int hashCode = (int)(this.value ^ (this.value >>> HASH_CONST));
        hash = HASH_CONST + hashCode;
        return hash;
    }

    @Override
    public int compareTo(Object t) {
        //implementation pending
        return -1;
    }

    /**
     * Descriptor for {@link NumberMetadataValue}s.
     */
    @Extension
    public static class NumberMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.NumberMetadataValue_DisplayName();
        }

        @Override
        public String getJsonType() {
            return SERIALIZATION_ALIAS_NUMBER;
        }

        @Override
        public MetadataValue fromJson(JSONObject json, MetadataContainer<MetadataValue> container)
                throws JsonUtils.ParseException {
            checkRequiredJsonAttribute(json, NAME);
            checkRequiredJsonAttribute(json, VALUE);

            NumberMetadataValue value = new NumberMetadataValue(
                    json.getString(NAME), json.optString(DESCRIPTION),
                    json.getLong(VALUE));
            if (json.has(EXPOSED)) {
                value.setExposeToEnvironment(json.getBoolean(EXPOSED));
            }
            if (json.has(GENERATED)) {
                value.setGenerated(json.getBoolean(GENERATED));
            } else {
                //TODO Should we do this?
                value.setGenerated(true);
            }
            return value;
        }
    }
}
