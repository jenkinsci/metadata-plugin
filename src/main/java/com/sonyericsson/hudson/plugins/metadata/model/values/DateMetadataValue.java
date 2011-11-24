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
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.sonyericsson.hudson.plugins.metadata.Constants.SERIALIZATION_ALIAS_DATE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.VALUE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.GENERATED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.checkRequiredJsonAttribute;

/**
 * Meta data with the value of a {@link java.util.Date}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@XStreamAlias(SERIALIZATION_ALIAS_DATE)
public class DateMetadataValue extends AbstractMetadataValue {

    private Calendar value;

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
    public DateMetadataValue(String name, String description, Date value, boolean exposedToEnvironment) {
        super(name, description, exposedToEnvironment);
        setValue(value);
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name.
     * @param description the description.
     * @param value the value.
     */
    public DateMetadataValue(String name, String description, Date value) {
        super(name, description);
        setValue(value);
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name.
     * @param value the value.
     */
    public DateMetadataValue(String name, Date value) {
        super(name);
        setValue(value);
    }

    @Override
    public Date getValue() {
        return value.getTime();
    }

    @Override
    public Descriptor<AbstractMetadataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(DateMetaDataValueDescriptor.class);
    }

    /**
     * Sets the internal Calendar value based on the provided Date.
     *
     * @param dateValue the value.
     */
    private synchronized void setValue(Date dateValue) {
        this.value = Calendar.getInstance();
        this.value.setTime(dateValue);
    }

    /**
     * Sets the internal Calendar value.
     *
     * @param calendar the value.
     */
    private synchronized void setCalendar(Calendar calendar) {
        this.value = calendar;
    }

    @Override
    public synchronized JSONObject toJson() {
        JSONObject obj = toAbstractJson();
        //TODO Serialize timezone info?
        obj.put(VALUE, value.getTimeInMillis());
        return obj;
    }

    @Override
    public DateMetadataValue clone() throws CloneNotSupportedException {
        DateMetadataValue date = (DateMetadataValue)super.clone();
        Calendar calendar = (Calendar)value.clone();
        date.setCalendar(calendar);
        return date;
    }

    /**
     * Descriptor for {@link DateMetadataValue}s.
     */
    @Extension
    public static class DateMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.DateMetadataValue_DisplayName();
        }

        @Override
        public String getJsonType() {
            return SERIALIZATION_ALIAS_DATE;
        }

        @Override
        public MetadataValue fromJson(JSONObject json) throws JsonUtils.ParseException {
            checkRequiredJsonAttribute(json, NAME);
            checkRequiredJsonAttribute(json, VALUE);

            //TODO Deserialize timezone info?
            DateMetadataValue value = new DateMetadataValue(
                    json.getString(NAME), json.optString(DESCRIPTION),
                    new Date(json.getLong(VALUE)));
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

        /**
         * Form validation for the value. It will try to parse the date according to the user's locale.
         *
         * @param value   the value.
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
                    return FormValidation.error(Messages.DateMetadataValue_BadDate());
                }
            } catch (java.text.ParseException e) {
                return FormValidation.error(e.getMessage());
            }
        }
    }
}
