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

import com.sonyericsson.hudson.plugins.metadata.MockUtils;
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.Date;

import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.VALUE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;

/**
 * Tests for {@link DateMetadataValue}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Hudson.class)
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
        Date value = new Date();
        DateMetadataValue metadataValue = new DateMetadataValue(name, description, value, false);
        JSONObject json = metadataValue.toJson();
        assertEquals(name, json.getString(NAME));
        assertEquals(description, json.getString(DESCRIPTION));
        assertEquals(value.getTime(), json.getLong(VALUE));
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
        Date value = new Date();
        boolean exposed = true;

        JSONObject json = new JSONObject();
        json.put(NAME, name);
        json.put(DESCRIPTION, description);
        json.put(VALUE, value.getTime());
        json.put(EXPOSED, exposed);
        json.put(JsonUtils.METADATA_TYPE, "metadata-date");

        DateMetadataValue metadataValue = (DateMetadataValue)JsonUtils.toValue(json);
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
        Date value = new Date();
        DateMetadataValue originalValue = new DateMetadataValue("name", "description", value, false);
        DateMetadataValue clonedValue = originalValue.clone();
        assertEquals(originalValue.getValue(), clonedValue.getValue());

        Calendar originalCalendar = (Calendar)Whitebox.getInternalState(originalValue, "value");
        Calendar clonedCalendar = (Calendar)Whitebox.getInternalState(clonedValue, "value");
        assertNotSame(originalCalendar, clonedCalendar);
    }

}
