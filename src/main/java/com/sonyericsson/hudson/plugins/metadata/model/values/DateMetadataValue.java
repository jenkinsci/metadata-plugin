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
import com.sonyericsson.hudson.plugins.metadata.model.TimeDetails;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.sonyericsson.hudson.plugins.metadata.Constants.DEFAULT_MONTH_ADJUSTMENT;
import static com.sonyericsson.hudson.plugins.metadata.Constants.DEFAULT_TIME_DETAILS;
import static com.sonyericsson.hudson.plugins.metadata.Constants.SERIALIZATION_ALIAS_DATE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.GENERATED;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.VALUE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.checkRequiredJsonAttribute;

/**
 * Meta data with the value of a {@link java.util.Date}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@XStreamAlias(SERIALIZATION_ALIAS_DATE)
public class DateMetadataValue extends AbstractMetadataValue {
    /**
     * Constant for hashcode.
     */
    private static final int HASH_CONST = 93;
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
     * @param name                 the name
     * @param year                 the default year.
     * @param month                the default month of the year.
     * @param day                  the default day of the month.
     * @param description          the description.
     * @param details              the optional time details, hour/minute/second.
     * @param exposedToEnvironment if this value should be exposed to the build as an
     *                             environment variable.
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
     * @param name                 the name.
     * @param description          the description.
     * @param value                the value.
     * @param checked               the checked boolean value, true if TimeDetails exist for this value.
     * @param exposedToEnvironment if this value should be exposed to the build as an
     *                             environment variable.
     */
    public DateMetadataValue(String name, String description, Calendar value,
                             boolean checked, boolean exposedToEnvironment) {
        super(name, description, exposedToEnvironment);
        this.checked = checked;
        setValue(value);
    }

    /**
     * Standard Constructor.
     *
     * @param name        the name.
     * @param description the description.
     * @param value       the value.
     */
    public DateMetadataValue(String name, String description, Calendar value) {
        super(name, description);
        setValue(value);
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name.
     * @param value the value.
     */
    public DateMetadataValue(String name, Calendar value) {
        super(name);
        setValue(value);
    }

    @Override
    public Calendar getValue() {
        return value;
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
    private synchronized void setValue(Calendar dateValue) {
        this.value = dateValue;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DateMetadataValue other = (DateMetadataValue)obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
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

    //CS IGNORE EmptyBlock FOR NEXT 50 LINES. REASON: Trying different inputs and moving on if Exceptions are found.
    @Override
    public int compareTo(Object userValue) {
        if (userValue == null) {
            return -1;
        }
        Date date = value.getTime();
        //if it is being compared to a DateMetadataValue, just compare the values.
        if (userValue instanceof DateMetadataValue) {
            DateMetadataValue dateMetadataValue = (DateMetadataValue)userValue;
            Date userDate = dateMetadataValue.getValue().getTime();
            return date.compareTo(userDate);
        }
        try {
            return compareAsStandardDateTime(userValue.toString());
        } catch (ParseException e) {
            //didn't work, move on
        }
        try {
            return compareAsStandardDate(userValue.toString());
        } catch (ParseException e) {
            //didn't work, move on
        }
        Locale locale;
        StaplerRequest currentRequest = Stapler.getCurrentRequest();
        if (currentRequest != null) {
            locale = currentRequest.getLocale();
        //If no locale could be found, try to use the System locale.
        } else {
            String property = System.getProperty("user.language");
            locale = new Locale(property);
        }

        try {
            return compareAsLocalDateTime(locale, userValue.toString());
        } catch (ParseException e) {
            //didn't work, move on
        }
        try {
            return compareAsLocalDate(locale, userValue.toString());
        } catch (ParseException e) {
            //didn't work, move on
        }
        return -1;
    }

    /**
     * Tries to compare the value as the ISO-8601 standard for only date.
     * @param userValue the String to compare.
     * @return 0 if userValue is equal, -1 if value less than userValue, 1 if larger.
     * @throws ParseException if userValue can't be parsed
     */
    private int compareAsStandardDate(String userValue) throws ParseException {
        Calendar clonedValue = (Calendar)value.clone();
        DateFormat dateInstance = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = dateInstance.parse(userValue);
        clonedValue.set(Calendar.HOUR_OF_DAY, 0);
        clonedValue.set(Calendar.MINUTE, 0);
        clonedValue.set(Calendar.SECOND, 0);
        clonedValue.set(Calendar.MILLISECOND, 0);
        Date clonedDate = clonedValue.getTime();
        return clonedDate.compareTo(parse);
    }

    /**
     * Tries to compare the value as the ISO-8601 standard for date and time.
     * @param userValue the String to compare.
     * @return 0 if userValue is equal, -1 if value less than userValue, 1 if larger.
     * @throws ParseException if userValue can't be parsed
     */
    private int compareAsStandardDateTime(String userValue) throws ParseException {
        Calendar clonedValue = (Calendar)value.clone();
        DateFormat dateInstance = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Date parse = dateInstance.parse(userValue);
        clonedValue.set(Calendar.MILLISECOND, 0);
        Date clonedDate = clonedValue.getTime();
        return clonedDate.compareTo(parse);
    }

    /**
     * Tries to compare the userValue using the locale, parsing only the date.
     * @param locale the locale to use for parsing the date String.
     * @param userValue the String to compare.
     * @return 0 if userValue is equal, -1 if value less than userValue, 1 if larger.
     * @throws ParseException if userValue can't be parsed
     */
    private int compareAsLocalDate(Locale locale, String userValue) throws ParseException {
        Calendar clonedValue = (Calendar)value.clone();
        DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        Date parse = dateInstance.parse(userValue);
        clonedValue.set(Calendar.HOUR_OF_DAY, 0);
        clonedValue.set(Calendar.MINUTE, 0);
        clonedValue.set(Calendar.SECOND, 0);
        clonedValue.set(Calendar.MILLISECOND, 0);
        return clonedValue.getTime().compareTo(parse);
    }

     /**
     * Tries to compare the userValue using the locale, parsing the date and time.
     * @param locale the locale to use for parsing the date String.
     * @param userValue the String to compare.
     * @return 0 if userValue is equal, -1 if value less than userValue, 1 if larger.
     * @throws ParseException if userValue can't be parsed
     */
    private int compareAsLocalDateTime(Locale locale, String userValue) throws ParseException {
        Calendar clonedValue = (Calendar)value.clone();
        DateFormat dateInstance = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
        Date parse = dateInstance.parse(userValue);
        clonedValue.set(Calendar.MILLISECOND, 0);
        return clonedValue.getTime().compareTo(parse);
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
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(json.getLong(VALUE));
            DateMetadataValue value = new DateMetadataValue(
                    json.getString(NAME), json.optString(DESCRIPTION),
                    cal);
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
