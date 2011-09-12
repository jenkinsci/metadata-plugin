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

import com.sonyericsson.hudson.plugins.metadata.Messages;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import org.kohsuke.stapler.QueryParameter;

/**
 * Meta data containing a non-decimal number.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class NumberMetaDataValue extends AbstractMetaDataValue {

    private long value;

    /**
     * Standard Constructor.
     *
     * @param name        the name
     * @param description the description.
     * @param value       the value
     */
    public NumberMetaDataValue(String name, String description, long value) {
        super(name, description);
        this.value = value;
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name
     * @param value the value
     */
    public NumberMetaDataValue(String name, long value) {
        super(name);
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public Descriptor<AbstractMetaDataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(NumberMetaDataValueDescriptor.class);
    }

    /**
     * Descriptor for {@link NumberMetaDataValue}s.
     */
    @Extension
    public static class NumberMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.NumberMetaDataValue_DisplayName();
        }

        /**
         * Form validation for the value. It will try to parse the value to a long.
         *
         * @param value the value.
         * @return {@link hudson.util.FormValidation#ok()} if the value can be parsed to a long.
         */
        public FormValidation doCheckValue(@QueryParameter("value") final String value) {
            try {
                Long.parseLong(value);
                return FormValidation.ok();
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }
    }
}
