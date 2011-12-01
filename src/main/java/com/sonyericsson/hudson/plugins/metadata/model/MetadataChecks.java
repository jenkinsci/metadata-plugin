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
package com.sonyericsson.hudson.plugins.metadata.model;

import com.sonyericsson.hudson.plugins.metadata.Constants;
import com.sonyericsson.hudson.plugins.metadata.Messages;
import hudson.Extension;
import hudson.model.RootAction;
import hudson.util.FormValidation;
import org.kohsuke.stapler.QueryParameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks for the different Metadata values and definitions.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
@Extension
public class MetadataChecks implements RootAction {

    //Min and max values for the date details.
    private static final int MIN_TIME_DETAILS_VALUE = 0;
    private static final int MAX_TIME_DETAILS_MONTH_VALUE = 12;
    private static final int MAX_TIME_DETAILS_DAY_VALUE = 31;
    private static final int MAX_TIME_DETAILS_HOUR_VALUE = 23;
    private static final int MAX_TIME_DETAILS_MINUTE_SECOND_VALUE = 59;

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return Constants.METADATA_CHECKS_URL_NAME;

    }

    /**
     * Form validation for Metadata names.
     * The check fails if there is no name or if it contains whitespace.
     *
     * @param value the number value.
     * @return {@link hudson.util.FormValidation#ok()} if the value is a number.
     */
    public FormValidation doCheckName(@QueryParameter final String value) {
        if ("".equals(value)) {
           return FormValidation.error(Messages.MetadataChecks_NoNameGiven());
        }
        Pattern p = Pattern.compile("\\s");
        Matcher m = p.matcher(value);
        if (m.find()) {
            return FormValidation.error(Messages.MetadataChecks_NameContainsWhitespace());
        }
        return FormValidation.ok();
    }

    /**
     * Form validation for number values.
     * The check fails if the number can't be parsed.
     *
     * @param value the number value.
     * @return {@link hudson.util.FormValidation#ok()} if the value is a number.
     */
    public FormValidation doCheckNumberValue(@QueryParameter final String value) {
        try {
            Long.parseLong(value);
            return FormValidation.ok();
        } catch (NumberFormatException e) {
            return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
        }
    }

    /**
     * Form validation for the date values.
     * The check fails if the numbers can't be parsed or if they don't follow the formatting rules
     * for year/month/day.
     *
     * @param yearValue  the year value.
     * @param monthValue the month value.
     * @param dayValue   the day value.
     * @return {@link hudson.util.FormValidation#ok()} if the values are numbers and follow the rules.
     */
    public FormValidation doCheckDateValue(@QueryParameter final String yearValue,
                                           @QueryParameter final String monthValue,
                                           @QueryParameter final String dayValue) {
        long longMonthValue;
        long longDayValue;
        try {
            Long.parseLong(yearValue);
        } catch (NumberFormatException e) {
            return FormValidation.error(Messages.MetadataChecks_NotANumber(Messages.Date_details_year()));
        }
        try {
            longMonthValue = Long.parseLong(monthValue);
            if (longMonthValue <= MIN_TIME_DETAILS_VALUE || longMonthValue > MAX_TIME_DETAILS_MONTH_VALUE) {
                return FormValidation.error(Messages.DateMetadataDefinition_BadMonth());
            }
        } catch (NumberFormatException e) {
            return FormValidation.error(Messages.MetadataChecks_NotANumber(Messages.Date_details_month()));
        }
        try {
            longDayValue = Long.parseLong(dayValue);
            if (longDayValue <= MIN_TIME_DETAILS_VALUE || longDayValue > MAX_TIME_DETAILS_DAY_VALUE) {
                return FormValidation.error(Messages.DateMetadataDefinition_BadDay());
            }
        } catch (NumberFormatException e) {
            return FormValidation.error(Messages.MetadataChecks_NotANumber(Messages.Date_details_day()));
        }
        return FormValidation.ok();
    }

    /**
     * Form validation for the time values.
     * The check fails if the numbers can't be parsed or if they don't follow the formatting rules
     * for hour/minute/second.
     *
     * @param hourValue   the hour value.
     * @param minuteValue the minute value.
     * @param secondValue the second value.
     * @return {@link hudson.util.FormValidation#ok()} if the values are numbers and follow the rules.
     */
    public FormValidation doCheckTimeValue(@QueryParameter String hourValue, @QueryParameter String minuteValue,
                                           @QueryParameter String secondValue) {
        long longHourValue;
        long longMinuteValue;
        long longSecondValue;
        try {
            longHourValue = Long.parseLong(hourValue);
            if (longHourValue < MIN_TIME_DETAILS_VALUE || longHourValue > MAX_TIME_DETAILS_HOUR_VALUE) {
                return FormValidation.error(Messages.DateMetadataDefinition_BadHour());
            }
        } catch (NumberFormatException e) {
            return FormValidation.error(Messages.MetadataChecks_NotANumber(Messages.Date_details_hour()));
        }
        try {
            longMinuteValue = Long.parseLong(minuteValue);
            if (longMinuteValue < MIN_TIME_DETAILS_VALUE || longMinuteValue > MAX_TIME_DETAILS_MINUTE_SECOND_VALUE) {
                return FormValidation.error(Messages.DateMetadataDefinition_BadMinute());
            }
        } catch (NumberFormatException e) {
            return FormValidation.error(Messages.MetadataChecks_NotANumber(Messages.Date_details_minute()));
        }
        try {
            longSecondValue = Long.parseLong(secondValue);
            if (longSecondValue < MIN_TIME_DETAILS_VALUE || longSecondValue > MAX_TIME_DETAILS_MINUTE_SECOND_VALUE) {
                return FormValidation.error(Messages.DateMetadataDefinition_BadSecond());
            }
        } catch (NumberFormatException e) {
            return FormValidation.error(Messages.MetadataChecks_NotANumber(Messages.Date_details_second()));
        }
        return FormValidation.ok();
    }
}
