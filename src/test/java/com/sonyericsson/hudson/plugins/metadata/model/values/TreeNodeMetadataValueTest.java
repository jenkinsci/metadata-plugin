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

import com.sonyericsson.hudson.plugins.metadata.Constants;
import com.sonyericsson.hudson.plugins.metadata.MockUtils;
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import hudson.model.Hudson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.*;
import static junit.framework.Assert.*;

//CS IGNORE MagicNumber FOR NEXT 200 LINES. REASON: TestData

/**
 * Tests for {@link TreeNodeMetadataValue}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Hudson.class)
public class TreeNodeMetadataValueTest {

    /**
     * Tests {@link com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue#toJson()}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testToJson() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        MockUtils.mockMetadataValueDescriptors(hudson);
        String name = "nameTest";
        String description = "descrText";

        String stringName = "stringNameTest";
        String stringDescription = "stringDescrText";
        String stringValue = "Who are you gonna call?!";

        TreeNodeMetadataValue metadataValue = new TreeNodeMetadataValue(name, description);
        StringMetadataValue stringMetadataValue = new StringMetadataValue(stringName, stringDescription, stringValue);
        metadataValue.addChild(stringMetadataValue);

        JSONObject json = metadataValue.toJson();
        assertEquals(name, json.getString(JsonUtils.NAME));
        assertEquals(description, json.getString(JsonUtils.DESCRIPTION));

        JSONArray array = json.getJSONArray(JsonUtils.CHILDREN);
        assertEquals(1, array.size());
        JSONObject strJson = array.getJSONObject(0);
        assertEquals(stringName, strJson.getString(JsonUtils.NAME));
        assertEquals(stringDescription, strJson.getString(JsonUtils.DESCRIPTION));
        assertEquals(stringValue, strJson.getString(JsonUtils.VALUE));
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

        JSONObject json = new JSONObject();
        json.put(NAME, name);
        json.put(DESCRIPTION, description);
        json.put(JsonUtils.METADATA_TYPE, "metadata-tree");

        String numberName = "numberNameTest";
        long numberValue = 5;

        JSONObject numberJson = new JSONObject();
        numberJson.put(NAME, numberName);
        numberJson.put(VALUE, numberValue);
        numberJson.put(METADATA_TYPE, "metadata-number");

        String stringName = "str";
        String stringValue = "me and by boys were walking on the beach.";
        String stringDescription = "Then we got to see the most beautiful rock.";

        JSONObject stringJson = new JSONObject();
        stringJson.put(NAME, stringName);
        stringJson.put(DESCRIPTION, stringDescription);
        stringJson.put(VALUE, stringValue);
        stringJson.put(METADATA_TYPE, "metadata-string");

        String nodeName = "layer2";

        JSONObject nodeJson = new JSONObject();
        nodeJson.put(NAME, nodeName);
        nodeJson.put(METADATA_TYPE, "metadata-tree");
        JSONArray nodeChildren = new JSONArray();
        nodeChildren.add(stringJson);
        nodeJson.put(CHILDREN, nodeChildren);

        JSONArray children = new JSONArray();
        children.add(numberJson);
        children.add(stringJson);
        children.add(nodeJson);

        json.put(CHILDREN, children);

        TreeNodeMetadataValue metadataValue = (TreeNodeMetadataValue)JsonUtils.toValue(json);
        assertNotNull(metadataValue);
        assertEquals(name, metadataValue.getName());
        assertEquals(description, metadataValue.getDescription());

        for (MetadataValue value : metadataValue.getChildren()) {
            if (value instanceof NumberMetadataValue) {
                assertEquals(numberName, value.getName());
                assertEquals(numberValue, value.getValue());
                assertEquals(name + Constants.SEPARATOR_DOT + numberName,
                        ((NumberMetadataValue)value).getFullName());
            } else if (value instanceof StringMetadataValue) {
                assertEquals(stringName, value.getName());
                assertEquals(stringValue, value.getValue());
                assertEquals(stringDescription, value.getDescription());
                assertEquals(name + Constants.SEPARATOR_DOT + stringName,
                        ((StringMetadataValue)value).getFullName());
            } else if (value instanceof TreeNodeMetadataValue) {
                assertEquals(nodeName, value.getName());
                assertEquals(name + Constants.SEPARATOR_DOT + nodeName,
                        ((TreeNodeMetadataValue)value).getFullName());
                StringMetadataValue child = (StringMetadataValue)((TreeNodeMetadataValue)value).getChild(stringName);
                assertNotNull(child);
                assertEquals(name + Constants.SEPARATOR_DOT + nodeName
                        + Constants.SEPARATOR_DOT + stringName,
                        child.getFullName());
            } else {
                fail("Unexpected metadata type: " + value.getClass().getName());
            }
        }
    }
}
