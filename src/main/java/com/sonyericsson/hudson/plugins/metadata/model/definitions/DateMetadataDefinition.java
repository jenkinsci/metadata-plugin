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
import com.sonyericsson.hudson.plugins.metadata.model.TimeDetails;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Calendar;
import java.util.Date;

import static com.sonyericsson.hudson.plugins.metadata.Constants.DEFAULT_MONTH_ADJUSTMENT;
import static com.sonyericsson.hudson.plugins.metadata.Constants.DEFAULT_TIME_DETAILS;

/**
 * Meta data with the value of a {@link java.util.Date}.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class DateMetadataDefinition extends AbstractMetadataDefinition {

    private Calendar defaultCal;
    private boolean checked = false;

    /**
     * Getter for the defaultYear.
     *
     * @return the default year.
     */
    public int getDefaultYear() {
        return defaultCal.get(Calendar.YEAR);
    }

    /**
     * Getter for the defaultMonth.
     *
     * @return the default month of the year.
     */
    public int getDefaultMonth() {
        return defaultCal.get(Calendar.MONTH) + DEFAULT_MONTH_ADJUSTMENT;
    }

    /**
     * Getter for the defaultDay.
     *
     * @return the default day of the month.
     */
    public int getDefaultDay() {
        return defaultCal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Getter for the default hour.
     *
     * @return the default hour of the day..
     */
    public int getDefaultHour() {
        return defaultCal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Getter for the default minute.
     *
     * @return the default minute of the hour.
     */
    public int getDefaultMinute() {
        return defaultCal.get(Calendar.MINUTE);
    }

    /**
     * Getter for the default second.
     *
     * @return the default second.
     */
    public int getDefaultSecond() {
        return defaultCal.get(Calendar.SECOND);
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
     * @param defaultYear  the default year.
     * @param defaultMonth the default month of the year.
     * @param defaultDay   the default day of the month.
     * @param description  the description.
     * @param details      the optional time details, hour/minute/second.
     */
    @DataBoundConstructor
    public DateMetadataDefinition(String name, String description, int defaultYear,
                                  int defaultMonth, int defaultDay, TimeDetails details) {
        super(name, description);
        defaultCal = Calendar.getInstance();


        if (details != null) {
            defaultCal.set(defaultYear, defaultMonth - DEFAULT_MONTH_ADJUSTMENT, defaultDay,
                    details.getHour(), details.getMinute(), details.getSecond());
            checked = details.isChecked();
        } else {
            defaultCal.set(defaultYear, defaultMonth - DEFAULT_MONTH_ADJUSTMENT, defaultDay,
                    DEFAULT_TIME_DETAILS, DEFAULT_TIME_DETAILS, DEFAULT_TIME_DETAILS);
        }
        defaultCal.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public synchronized Date getDefaultValue() {
        return defaultCal.getTime();
    }

    //TODO Add support for creating values.
    @Override
    public AbstractMetadataValue createValue(Object o) {
        return null;
    }

    /**
     * Descriptor for {@link DateMetadataDefinition}s.
     */
    @Extension
    public static class DateMetaDataDefinitionDescriptor extends AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.DateMetadataDefinition_DisplayName();
        }
    }
}

