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
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.TimeDetails;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.Calendar;
import java.util.Date;

import static com.sonyericsson.hudson.plugins.metadata.Constants.DEFAULT_MONTH_ADJUSTMENT;
import static com.sonyericsson.hudson.plugins.metadata.Constants.SERIALIZATION_ALIAS_DATE;
import static com.sonyericsson.hudson.plugins.metadata.Constants.DEFAULT_TIME_DETAILS;
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
    private boolean checked = false;
    /**
     * Getter for the defaultYear.
     *
     * @return the default year.
     */
    public int getYear() {
        return value.get(Calendar.YEAR);
    }

    /**
     * Getter for the defaultMonth.
     *
     * @return the default month of the year.
     */
    public int getMonth() {
        return value.get(Calendar.MONTH) + DEFAULT_MONTH_ADJUSTMENT;
    }

    /**
     * Getter for the defaultDay.
     *
     * @return the default day of the month.
     */
    public int getDay() {
        return value.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Getter for the default hour.
     *
     * @return the default hour of the day..
     */
    public int getHour() {
        return value.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Getter for the default minute.
     *
     * @return the default minute of the hour.
     */
    public int getMinute() {
        return value.get(Calendar.MINUTE);
    }

    /**
     * Getter for the default second.
     *
     * @return the default second.
     */
    public int getSecond() {
        return value.get(Calendar.SECOND);
    }

    /**
     * Returns the checked value, used to decide if the time details should be visible.
     *
     * @return the checked value.
     */
    public boolean isChecked() {
        return checked;
    }

/**
     * Standard Constructor.
     *
     * @param name         the name
     * @param year  the default year.
     * @param month the default month of the year.
     * @param day   the default day of the month.
     * @param description  the description.
     * @param details      the optional time details, hour/minute/second.
     * @param exposedToEnvironment if this value should be exposed to the build as an
//     *                      environment variable.
     */
    @DataBoundConstructor
    public DateMetadataValue(String name, String description, int year,
                                  int month, int day, TimeDetails details, boolean exposedToEnvironment) {
        super(name, description, exposedToEnvironment);
        value = Calendar.getInstance();


        if (details != null) {
            value.set(year, month - DEFAULT_MONTH_ADJUSTMENT, day,
                    details.getHour(), details.getMinute(), details.getSecond());
            checked = details.isChecked();
        } else {
            value.set(year, month - DEFAULT_MONTH_ADJUSTMENT, day,
                    DEFAULT_TIME_DETAILS, DEFAULT_TIME_DETAILS, DEFAULT_TIME_DETAILS);
        }
    }



    /**
     * Standard Constructor.
     *
     * @param name        the name.
     * @param description the description.
     * @param value       the value.
     * @param exposedToEnvironment if this value should be exposed to the build as an
     *                      environment variable.
     */
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
        public MetadataValue fromJson(JSONObject json, MetadataContainer<MetadataValue> container)
                throws JsonUtils.ParseException {
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
    }
}
