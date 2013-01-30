/*
 *  The MIT License
 *
 *  Copyright 2013 Sony Mobile Communications AB. All rights reserved.
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

import com.sonyericsson.hudson.plugins.metadata.model.definitions.AbstractMetadataDefinition;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.sonyericsson.hudson.plugins.metadata.model.definitions.NumberMetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.StringChoiceMetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.StringMetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.DateMetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.TreeNodeMetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.DateMetadataValue;

/**
 * Tests for the conversion from definitions to values.
 *
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
public class DefinitionsConversionTest extends HudsonTestCase {
    private static final int TIMEOUT = 20000;
    private static final int TIME_INCREMENT = 1000;
    private static final int NUMBER = 5;

    /**
     * Tests the conversion from definitions to values for Numbers.
     *
     * @throws Exception if so.
     */
    public void testNumberConversion() throws Exception {
        NumberMetadataDefinition def = new NumberMetadataDefinition("number", NUMBER);
        List<AbstractMetadataDefinition> list = new LinkedList<AbstractMetadataDefinition>();
        list.add(def);
        PluginImpl.getInstance().setDefinitions(list);
        FreeStyleProject freeStyleProject = createFreeStyleProject();
        configRoundtrip(freeStyleProject);
        MetadataJobProperty property = freeStyleProject.getProperty(MetadataJobProperty.class);
        assertNotNull("No MetadataJobProperty", property);
        NumberMetadataValue number = (NumberMetadataValue)TreeStructureUtil.getLeaf(property, "number");
        assertNotNull(number);
        assertEquals(new Long(NUMBER), number.getValue());
    }

    /**
     * Tests the conversion from definitions to values for Stringchoices.
     *
     * @throws Exception if so.
     */
    public void testStringChoiceConversion() throws Exception {
        StringChoiceMetadataDefinition def = new StringChoiceMetadataDefinition("choices", "first\nsecond");
        List<AbstractMetadataDefinition> list = new LinkedList<AbstractMetadataDefinition>();
        list.add(def);
        PluginImpl.getInstance().setDefinitions(list);
        FreeStyleProject freeStyleProject = createFreeStyleProject();
        configRoundtrip(freeStyleProject);
        MetadataJobProperty property = freeStyleProject.getProperty(MetadataJobProperty.class);
        assertNotNull("No MetadataJobProperty", property);
        StringMetadataValue choiceValue = (StringMetadataValue)TreeStructureUtil.getLeaf(property, "choices");
        assertNotNull(choiceValue);
        assertEquals("first", choiceValue.getValue());
    }

    /**
     * Tests the conversion from definitions to values for TreeNodes.
     *
     * @throws Exception if so.
     */
    public void testNodeConversion() throws Exception {
        StringMetadataDefinition string = new StringMetadataDefinition("string", "myValue");
        TreeNodeMetadataDefinition tree = new TreeNodeMetadataDefinition("tree");
        tree.addChild(string);
        List<AbstractMetadataDefinition> list = new LinkedList<AbstractMetadataDefinition>();
        list.add(tree);
        PluginImpl.getInstance().setDefinitions(list);
        FreeStyleProject freeStyleProject = createFreeStyleProject();
        configRoundtrip(freeStyleProject);
        MetadataJobProperty property = freeStyleProject.getProperty(MetadataJobProperty.class);
        assertNotNull("No MetadataJobProperty", property);
        StringMetadataValue stringMetadataValue =
                (StringMetadataValue)TreeStructureUtil.getLeaf(property, "tree", "string");
        assertNotNull(stringMetadataValue);
        assertEquals("myValue", stringMetadataValue.getValue());
    }

    /**
     * Tests the conversion from definitions to values for Dates.
     *
     * @throws Exception if so.
     */
    public void testDateConversion() throws Exception {
        Calendar calendar = Calendar.getInstance();
        TimeDetails details = new TimeDetails(
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        DateMetadataDefinition def = new DateMetadataDefinition("date", "",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                details,
                false);
        List<AbstractMetadataDefinition> list = new LinkedList<AbstractMetadataDefinition>();
        list.add(def);
        PluginImpl.getInstance().setDefinitions(list);
        FreeStyleProject freeStyleProject = createFreeStyleProject();
        configRoundtrip(freeStyleProject);
        MetadataJobProperty property = freeStyleProject.getProperty(MetadataJobProperty.class);
        assertNotNull("No MetadataJobProperty", property);
        DateMetadataValue dateValue = (DateMetadataValue)TreeStructureUtil.getLeaf(property, "date");
        assertNotNull(dateValue);
        assertEquals(def.getHour(), dateValue.getHour());
        assertEquals(def.getMinute(), dateValue.getMinute());
        assertEquals(def.getSecond(), dateValue.getSecond());
        assertEquals(def.getDay(), dateValue.getDay());
        assertEquals(def.getMonth(), dateValue.getMonth());
        assertEquals(def.getYear(), dateValue.getYear());
    }
}
