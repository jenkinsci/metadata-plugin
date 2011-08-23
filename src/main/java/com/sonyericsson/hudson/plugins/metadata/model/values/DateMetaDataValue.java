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
import org.kohsuke.stapler.StaplerRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Meta data with the value of a {@link java.util.Date}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class DateMetaDataValue extends AbstractMetaDataValue {

    private Date value;

    /**
     * Standard Constructor.
     *
     * @param name        the name
     * @param description the description
     * @param value       the value
     */
    protected DateMetaDataValue(String name, String description, Date value) {
        super(name, description);
        this.value = value;
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name
     * @param value the value
     */
    protected DateMetaDataValue(String name, Date value) {
        super(name);
        this.value = value;
    }

    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public Descriptor<AbstractMetaDataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(DateMetaDataValueDescriptor.class);
    }

    /**
     * Descriptor for {@link DateMetaDataValue}s.
     */
    @Extension
    public static class DateMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StringMetaDataValue_DisplayName();
        }

        /**
         * Formvalidation for the value.
         * It will try to parse the date according to the user's locale.
         * @param value the value.
         * @param request the http request.
         * @return {@link hudson.util.FormValidation#ok()} if the value can be parsed to a date.
         */
        public FormValidation doCheckValue(@QueryParameter String value, StaplerRequest request) {
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                                                             DateFormat.MEDIUM,
                                                             request.getLocale());
            try {
                if (format.parse(value) != null) {
                    return FormValidation.ok();
                } else {
                    return FormValidation.error(Messages.DateMetaDataValue_BadDate());
                }
            } catch (ParseException e) {
                return FormValidation.error(e.getMessage());
            }
        }
    }
}
