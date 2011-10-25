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
package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.TimeDetails;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.DateMetadataValue;
import hudson.Extension;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.Calendar;
import java.util.Date;

/**
 * Meta data with the value of a {@link java.util.Date}.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class DateMetadataDefinition extends AbstractMetadataDefinition {

    private Calendar defaultCal;
    //Default hour,minute,second if no defaults are added.
    private static final int DEFAULT_TIME_DETAILS = 0;
    //Subtracted from month value since it starts on 1 instead of 0.
    private static final int DEFAULT_MONTH_ADJUSTMENT = 1;
    //Min and max values for the date details.
    private static final int MIN_TIME_DETAILS_VALUE = 0;
    private static final int MAX_TIME_DETAILS_MONTH_VALUE = 12;
    private static final int MAX_TIME_DETAILS_DAY_VALUE = 31;
    private static final int MAX_TIME_DETAILS_HOUR_VALUE = 23;
    private static final int MAX_TIME_DETAILS_MINUTE_SECOND_VALUE = 59;
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
    }

    @Override
    public Date getDefaultValue() {
        return defaultCal.getTime();
    }

    //TODO as with the other definitions, creating values isn't tested yet. Add support for this.
    @Override
    public AbstractMetadataValue createValue(StaplerRequest req, JSONObject jo) {
        DateMetadataValue value = req.bindJSON(DateMetadataValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

    @Override
    public AbstractMetadataValue createValue(StaplerRequest req) {
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

        //TODO The below checkers are not in use since I could not get them to work with
        //TODO the current layout of the table. Add support for the checkers.

        /**
         * Form validation for the month value..
         * The check fails if the number can't be parsed or is not between 1 and 12.
         *
         * @param value   the value.
         * @param request the http request.
         * @return {@link hudson.util.FormValidation#ok()} if the value is a number between 1 and 12.
         */
        public FormValidation doCheckMonthValue(@QueryParameter String value, StaplerRequest request) {
            try {
                long longValue = Long.parseLong(value);
                if (longValue <= MIN_TIME_DETAILS_VALUE || longValue > MAX_TIME_DETAILS_MONTH_VALUE) {
                    return FormValidation.error(Messages.DateMetadataDefinition_BadMonth());
                } else {
                    return FormValidation.ok();
                }
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }

        /**
         * Form validation for the day value..
         * The check fails if the number can't be parsed or is not between 1 and 31.
         *
         * @param value   the value.
         * @param request the http request.
         * @return {@link hudson.util.FormValidation#ok()} if the value is a number between 1 and 31.
         */
        public FormValidation doCheckDayValue(@QueryParameter String value, StaplerRequest request) {
            try {
                long longValue = Long.parseLong(value);
                if (longValue <= MIN_TIME_DETAILS_VALUE || longValue > MAX_TIME_DETAILS_DAY_VALUE) {
                    return FormValidation.error(Messages.DateMetadataDefinition_BadDay());
                } else {
                    return FormValidation.ok();
                }
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }

        /**
         * Form validation for the hour value..
         * The check fails if the number can't be parsed or is not between 0 and 23.
         *
         * @param value   the value.
         * @param request the http request.
         * @return {@link hudson.util.FormValidation#ok()} if the value is a number between 0 and 23.
         */
        public FormValidation doCheckHourValue(@QueryParameter String value, StaplerRequest request) {
            try {
                long longValue = Long.parseLong(value);
                if (longValue < MIN_TIME_DETAILS_VALUE || longValue > MAX_TIME_DETAILS_HOUR_VALUE) {
                    return FormValidation.error(Messages.DateMetadataDefinition_BadHour());
                } else {
                    return FormValidation.ok();
                }
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }


        /**
         * Form validation for the minute value..
         * The check fails if the number can't be parsed or is not between 0 and 59.
         *
         * @param value   the value.
         * @param request the http request.
         * @return {@link hudson.util.FormValidation#ok()} if the value is a number between 0 and 59.
         */
        public FormValidation doCheckMinuteValue(@QueryParameter String value, StaplerRequest request) {
            try {
                long longValue = Long.parseLong(value);
                if (longValue < MIN_TIME_DETAILS_VALUE || longValue > MAX_TIME_DETAILS_MINUTE_SECOND_VALUE) {
                    return FormValidation.error(Messages.DateMetadataDefinition_BadMinute());
                } else {
                    return FormValidation.ok();
                }
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }

        /**
         * Form validation for the second value..
         * The check fails if the number can't be parsed or is not between 0 and 59.
         *
         * @param value   the value.
         * @param request the http request.
         * @return {@link hudson.util.FormValidation#ok()} if the value is a number between 0 and 59.
         */
        public FormValidation doCheckSecondValue(@QueryParameter String value, StaplerRequest request) {
            try {
                long longValue = Long.parseLong(value);
                if (longValue < MIN_TIME_DETAILS_VALUE || longValue > MAX_TIME_DETAILS_MINUTE_SECOND_VALUE) {
                    return FormValidation.error(Messages.DateMetadataDefinition_BadSecond());
                } else {
                    return FormValidation.ok();
                }
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }
    }
}

