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

import com.sonyericsson.hudson.plugins.metadata.MockUtils;
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.util.ExtensionUtils;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.sonyericsson.hudson.plugins.metadata.model.TimeDetails;

import java.util.Calendar;

import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.VALUE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DateMetadataValue}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Hudson.class, ExtensionUtils.class })
public class DateMetadataValueTest {

    /**
     * Tests {@link com.sonyericsson.hudson.plugins.metadata.model.values.DateMetadataValue#toJson()}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testToJson() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        MockUtils.mockDateMetadataValueDescriptor(hudson);
        String name = "nameTest";
        String description = "descrText";
        Calendar value = Calendar.getInstance();
        DateMetadataValue metadataValue = new DateMetadataValue(name, description, value, false, false);
        JSONObject json = metadataValue.toJson();
        assertEquals(name, json.getString(NAME));
        assertEquals(description, json.getString(DESCRIPTION));
        assertEquals(value.getTimeInMillis(), json.getLong(VALUE));
    }

    /**
     * Tests deserialization from JSON to the correct POJO.
     *
     * @throws Exception if so.
     */
    @Test
    public void testFromJson() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        MockUtils.mockMetadataValueDescriptors(hudson);
        String name = "nameTest";
        String description = "descrText";
        Calendar value = Calendar.getInstance();
        boolean exposed = true;

        JSONObject json = new JSONObject();
        json.put(NAME, name);
        json.put(DESCRIPTION, description);
        json.put(VALUE, value.getTimeInMillis());
        json.put(EXPOSED, exposed);
        json.put(JsonUtils.METADATA_TYPE, "metadata-date");

        DateMetadataValue metadataValue = (DateMetadataValue)JsonUtils.toValue(json, mock(MetadataContainer.class));
        assertNotNull(metadataValue);
        assertEquals(name, metadataValue.getName());
        assertEquals(description, metadataValue.getDescription());
        assertEquals(value, metadataValue.getValue());
    }

    /**
     * Tests the cloning functionality of a DateMetaDataValue.
     * Makes sure that no shallow copying occurs.
     * @throws Exception if so.
     */
    @Test
    public void testClone() throws Exception {
        Calendar value = Calendar.getInstance();
        DateMetadataValue originalValue = new DateMetadataValue("name", "description", value, false, false);
        DateMetadataValue clonedValue = originalValue.clone();
        assertEquals(originalValue.getValue(), clonedValue.getValue());

        Calendar originalCalendar = (Calendar)Whitebox.getInternalState(originalValue, "value");
        Calendar clonedCalendar = (Calendar)Whitebox.getInternalState(clonedValue, "value");
        assertNotSame(originalCalendar, clonedCalendar);
    }


    //CS IGNORE MagicNumber FOR NEXT 40 LINES. REASON: TestData
    /**
     * Tests that the compareTo method of DateMetadataValue works as it should.
     * @throws Exception if so.
     */
    @Test
    public void testCompare() throws Exception {
        TimeDetails details = new TimeDetails(05, 06, 07);
        DateMetadataValue value = new DateMetadataValue("name", "description",
                2012, 11, 11, details, false);
        DateMetadataValue sameValue = value.clone();
        assertEquals(0, value.compareTo(sameValue));
        DateMetadataValue largerValue = new DateMetadataValue("name", "description",
                2013, 10, 05, details, false);
        assertEquals(-1, value.compareTo(largerValue));
        DateMetadataValue smallerValue = new DateMetadataValue("name", "description",
                2012, 10, 11, details, false);
        assertEquals(1, value.compareTo(smallerValue));
        String dateStringWithoutDetails = "2012-11-11";
        assertEquals(0, value.compareTo(dateStringWithoutDetails));
        String dateStringWithDetails = "2012-11-11-05:06:07";
        assertEquals(0, value.compareTo(dateStringWithDetails));
        String dateStringLargerThan = "2013-10-05-03:53:53";
        assertEquals(-1, value.compareTo(dateStringLargerThan));
        String dateStringLessThan = "2012-10-05-03:53:53";
        assertEquals(1, value.compareTo(dateStringLessThan));
    }
}
