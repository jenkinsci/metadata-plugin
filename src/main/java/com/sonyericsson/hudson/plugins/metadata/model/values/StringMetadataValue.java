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

import static com.sonyericsson.hudson.plugins.metadata.Constants.SERIALIZATION_ALIAS_STRING;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.VALUE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.GENERATED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.checkRequiredJsonAttribute;

/**
 * A Meta Data value of the type String.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@XStreamAlias(SERIALIZATION_ALIAS_STRING)
public class StringMetadataValue extends AbstractMetadataValue {

    /**
     * Constant for hashcode.
     */
    private static final int HASH_CONST = 7;

    private String value;

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
    public StringMetadataValue(String name, String description, String value, boolean exposedToEnvironment) {
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
    public StringMetadataValue(String name, String description, String value) {
        super(name, description);
        this.value = value;
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name.
     * @param value the value.
     */
    public StringMetadataValue(String name, String value) {
        super(name);
        this.value = value;
    }
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Descriptor<AbstractMetadataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(StringMetaDataValueDescriptor.class);
    }

    @Override
    public JSONObject toJson() {
        JSONObject obj = toAbstractJson();
        obj.put(VALUE, value);
        return obj;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (this.value != null) {
            hash = this.value.hashCode();
        }
        hash = HASH_CONST + hash;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringMetadataValue other = (StringMetadataValue)obj;
        if (!this.value.equalsIgnoreCase(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object userValue) {
        if (userValue instanceof MetadataValue) {
            return value.compareTo(((MetadataValue)userValue).getValue().toString());
        } else {
            return value.compareTo(userValue.toString());
        }
    }

    /**
     * The Descriptor.
     */
    @Extension
    public static class StringMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StringMetadataValue_DisplayName();
        }

        @Override
        public String getJsonType() {
            return SERIALIZATION_ALIAS_STRING;
        }

        @Override
        public MetadataValue fromJson(JSONObject json, MetadataContainer<MetadataValue> container)
                throws JsonUtils.ParseException {
            JsonUtils.checkRequiredJsonAttribute(json, NAME);
            checkRequiredJsonAttribute(json, VALUE);

            StringMetadataValue value = new StringMetadataValue(
                    json.getString(NAME), json.optString(DESCRIPTION),
                    json.getString(VALUE));
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
