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
package com.sonyericsson.hudson.plugins.metadata.search;

import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import org.jvnet.hudson.test.HudsonTestCase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests for {@link MetadataQuerySearch}.
 *
 * @author Shemeer S &lt;shemeer.x.sulaiman@sonymobile.com&gt;
 */
public class MetadataQuerySearchTest extends HudsonTestCase {
    /**
     * Test number value.
     */
    public static final long NUMBR_VAL1 = 5;
    /**
     * Test number value.
     */
    public static final long NUMBR_VAL2 = 9;

    /**
     * Tests a search with AND operator.
     *
     * @throws Exception if so.
     */
    public void testSearchAndOperator() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        StringMetadataValue name = new StringMetadataValue("name", "description", "somc");
        StringMetadataValue dept = new StringMetadataValue("dept", "description", "tool");
        list.add(name);
        list.add(dept);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        name = new StringMetadataValue("name", "description", "somc");
        list.add(name);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("name=somc && dept=tool");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
       assertEquals(1, mqs.searchQuery(itemList).size());
    }

    /**
     * Tests a search with OR operator.
     *
     * @throws Exception if so.
     */
    public void testSearchOrOperator() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        StringMetadataValue name = new StringMetadataValue("name", "description", "somc");
        StringMetadataValue dept = new StringMetadataValue("dept", "description", "tool");
        list.add(name);
        list.add(dept);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        name = new StringMetadataValue("name", "description", "somc");
        list.add(name);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("name=somc || dept=tool");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
        assertEquals(2, mqs.searchQuery(itemList).size());
    }

    /**
     * Tests a search with Equal operator.
     *
     * @throws Exception if so.
     */
    public void testSearchEqualOperator() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        StringMetadataValue name = new StringMetadataValue("name", "description", "somc");
        StringMetadataValue dept = new StringMetadataValue("dept", "description", "tool");
        list.add(name);
        list.add(dept);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        name = new StringMetadataValue("name", "description", "somc");
        list.add(name);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("dept=tool");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
       assertEquals(1, mqs.searchQuery(itemList).size());
    }
    
    
    /**
     * Tests a search with Equal operator. Value contains underscore character
     *
     * @throws Exception if so.
     */
    public void testSearchEqualOperatorWithUnderscores() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        StringMetadataValue name = new StringMetadataValue("name", "description", "somc");
        StringMetadataValue dept = new StringMetadataValue("dept", "description", "tool_1");
        list.add(name);
        list.add(dept);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        name = new StringMetadataValue("name", "description", "somc");
        list.add(name);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("dept=tool_1");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
       assertEquals(1, mqs.searchQuery(itemList).size());
    }

    /**
     * Tests a search with Greater than or Equal to operator.
     *
     * @throws Exception if so.
     */
    public void testSearchGraterthanEqualtoOperator() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        NumberMetadataValue number = new NumberMetadataValue("number", "description", NUMBR_VAL1, false);
        list.add(number);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        number = new NumberMetadataValue("number", "description", NUMBR_VAL2, false);
        list.add(number);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("number>=5");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
       assertEquals(2, mqs.searchQuery(itemList).size());
    }

     /**
     * Tests a search with Greater than operator.
     *
     * @throws Exception if so.
     */
    public void testSearchGraterthanOperator() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        NumberMetadataValue number = new NumberMetadataValue("number", "description", NUMBR_VAL1, false);
        list.add(number);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        number = new NumberMetadataValue("number", "description", NUMBR_VAL2, false);
        list.add(number);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("number>5");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
        assertEquals(1, mqs.searchQuery(itemList).size());
    }

    /**
     * Tests a search with Lesser than or Equal to operator.
     *
     * @throws Exception if so.
     */
    public void testSearchLesserthanEqualtoOperator() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        NumberMetadataValue number = new NumberMetadataValue("number", "description", NUMBR_VAL1, false);
        list.add(number);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        number = new NumberMetadataValue("number", "description", NUMBR_VAL2, false);
        list.add(number);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("number<=9");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
       assertEquals(2, mqs.searchQuery(itemList).size());
    }

     /**
     * Tests a search with Lesser than operator.
     *
     * @throws Exception if so.
     */
    public void testSearchLesserthanOperator() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        NumberMetadataValue number = new NumberMetadataValue("number", "description", NUMBR_VAL1, false);
        list.add(number);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        number = new NumberMetadataValue("number", "description", NUMBR_VAL2, false);
        list.add(number);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);
        MetadataQuerySearch mqs = MetadataQuerySearch.parseQuery("number<9");
        TopLevelItem tli1 = project;
        TopLevelItem tli2 = project2;
        List itemList = new ArrayList();
        itemList.add(tli1);
        itemList.add(tli2);
        assertEquals(1, mqs.searchQuery(itemList).size());
    }

}
