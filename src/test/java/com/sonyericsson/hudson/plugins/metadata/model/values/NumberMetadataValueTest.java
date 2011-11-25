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
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.NAME;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.VALUE;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.DESCRIPTION;
import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.EXPOSED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

//CS IGNORE MagicNumber FOR NEXT 200 LINES. REASON: TestData

/**
 * Tests for {@link NumberMetadataValue}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Hudson.class)
public class NumberMetadataValueTest {

    /**
     * Tests {@link com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue#toJson()}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testToJson() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        MockUtils.mockNumberMetadataValueDescriptor(hudson);
        String name = "nameTest";
        String description = "descrText";
        long value = 5000;
        NumberMetadataValue metadataValue = new NumberMetadataValue(name, description, value, false);
        JSONObject json = metadataValue.toJson();
        assertEquals(name, json.getString(JsonUtils.NAME));
        assertEquals(description, json.getString(JsonUtils.DESCRIPTION));
        assertEquals(value, json.getLong(JsonUtils.VALUE));
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
        long value = 5432;
        boolean exposed = true;

        JSONObject json = new JSONObject();
        json.put(NAME, name);
        json.put(DESCRIPTION, description);
        json.put(VALUE, value);
        json.put(EXPOSED, exposed);
        json.put(JsonUtils.METADATA_TYPE, "metadata-number");

        NumberMetadataValue metadataValue = (NumberMetadataValue)JsonUtils.toValue(json, mock(MetadataContainer.class));
        assertNotNull(metadataValue);
        assertEquals(name, metadataValue.getName());
        assertEquals(description, metadataValue.getDescription());
        assertEquals(value, metadataValue.getValue().longValue());
    }
}
