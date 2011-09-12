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

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Standard bean holding the hour/minute/second time details.
 * Also holds information on if the time details checkbox is checked and thus,
 * visible on the page in question.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class TimeDetails {
    private int hour;
    private int minute;
    private int second;
    private boolean checked = false;

    /**
     * Returns the checked value, used to decide if the time details should be visible.
     *
     * @return the checked value.
     */
    public boolean isChecked() {
        return checked;
    }


    /**
     * Standard constructor.
     *
     * @param hour   the hour.
     * @param minute the minute.
     * @param second the second.
     */
    @DataBoundConstructor
    public TimeDetails(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        checked = true;
    }

    /**
     * Standard getter for hour.
     *
     * @return the hour.
     */
    public int getHour() {
        return hour;
    }

    /**
     * Standard setter for hour.
     *
     * @param hour the hour.
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Standard getter for minute.
     *
     * @return the minute.
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Standard setter for minute.
     *
     * @param minute the minute.
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * Standard getter for second.
     *
     * @return the second.
     */
    public int getSecond() {
        return second;
    }

    /**
     * Standard setter for second.
     *
     * @param second the second.
     */
    public void setSecond(int second) {
        this.second = second;
    }
}
