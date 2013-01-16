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

package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataChecks;

/**
 * A metadata definition of the type Number..
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class NumberMetadataDefinition extends AbstractMetadataDefinition {

    private long defaultValue;

    /**
     * Standard Constructor.
     *
     * @param name         the name.
     * @param description  the description.
     * @param defaultValue the default value for this definition
     */
    @DataBoundConstructor
    public NumberMetadataDefinition(String name, String description, long defaultValue) {
        super(name, description);
        this.defaultValue = defaultValue;
    }

    /**
     * Standard Constructor.
     *
     * @param name the name.
     * @param defaultValue the default value.
     */
    public NumberMetadataDefinition(String name, long defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    @Override
    public NumberMetadataValue createValue(Object o) throws Descriptor.FormException {
        MetadataChecks checks = new MetadataChecks();
        long value;
        if (o instanceof String) {
            FormValidation formValidation = checks.doCheckNumberValue((String)o);
            if (!formValidation.equals(FormValidation.ok())) {
                throw new Descriptor.FormException(formValidation.getMessage(), "");
            }
            value = Long.parseLong((String)o);
        } else {
            throw new Descriptor.FormException("Wrong number format", "");
        }
        NumberMetadataValue metadataValue =
                new NumberMetadataValue(getName(), getDescription(), value, isExposedToEnvironment());
        return metadataValue;
    }

    /**
     * Returns default metadata values for this definition.
     *
     * @return default metadata values or null if no defaults are available
     */
    @Override
    public synchronized Long getDefaultValue() {
        return defaultValue;
    }

    /**
     * The Descriptor.
     */
    @Extension
    public static class NumberMetaDataDefinitionDescriptor extends
            AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.NumberMetadataDefinition_DisplayName();
        }
          /**
         * Form validation for the value. It will try to parse the value to a long.
         *
         * @param value   the value.
         * @return {@link hudson.util.FormValidation#ok()} if the value can be parsed to a long.
         */
        public FormValidation doCheckNumberValue(@QueryParameter ("value") final String value) {
            try {
                Long.parseLong(value);
                return FormValidation.ok();
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }
    }
}
