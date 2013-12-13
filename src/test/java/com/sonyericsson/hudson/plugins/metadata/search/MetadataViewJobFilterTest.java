/*
 *  The MIT License
 *
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
package com.sonyericsson.hudson.plugins.metadata.search;

import com.gargoylesoftware.htmlunit.Page;
import com.sonyericsson.hudson.plugins.metadata.TestACL;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.jvnet.hudson.test.HudsonTestCase;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests for the filter.
 *
 * @author Tomas Westling&lt;tomas.westling@sonymobile.com&gt;
 */
public class MetadataViewJobFilterTest extends HudsonTestCase {
    /**
     * Tests that the Filter can properly filter out the correct projects.
     *
     * @throws IOException if so.
     */
    public void testFilter() throws IOException {
        FreeStyleProject project = createFreeStyleProject();
        FreeStyleProject project2 = createFreeStyleProject();
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        StringMetadataValue value = new StringMetadataValue("name", "description", "value");
        list.add(value);
        MetadataJobProperty property = new MetadataJobProperty(list);
        project.addProperty(property);
        MetadataViewJobFilter filter = new MetadataViewJobFilter("name=value");
        List<TopLevelItem> items = Hudson.getInstance().getItems();
        List<TopLevelItem> filter1 = filter.filter(null, items, Hudson.getInstance().getPrimaryView());
        assertEquals(2, items.size());
        assertEquals(1, filter1.size());
    }

    /**
     * Tests that the Filter can properly filter out the correct projects. Were one project is not readable by the
     * current user. This should work by default in a view; Jenkins seems to filter based on ACL before sending it to
     * the filter.
     *
     * @throws Exception if so.
     */
    public void testFilterACL() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        StringMetadataValue value = new StringMetadataValue("name", "description", "value");
        list.add(value);
        MetadataJobProperty property = new MetadataJobProperty(list);
        project.addProperty(property);
        list = new LinkedList<MetadataValue>();
        value = new StringMetadataValue("name", "description", "value");
        list.add(value);
        project2.addProperty(new MetadataJobProperty(list));

        MetadataViewJobFilter filter = new MetadataViewJobFilter("name=value");

        ListView view = new ListView("Test", hudson);
        view.getJobFilters().add(filter);
        hudson.addView(view);

        WebClient web = createWebClient();
        Page page = web.goTo("/view/Test/api/json", "application/json");
        JSONObject json = (JSONObject)JSONSerializer.toJSON(page.getWebResponse().getContentAsString());
        //Just to test that we can find both in an open installation.
        assertTrue(json.has("jobs"));
        JSONArray jobs = (JSONArray)json.get("jobs");
        assertEquals(2, jobs.size());

        //Lets secure Jenkins
        hudson.setSecurityRealm(createDummySecurityRealm());
        hudson.setAuthorizationStrategy(new TestACL());

        web = createWebClient();
        web = web.login("testuser");

        page = web.goTo("/view/Test/api/json", "application/json");
        json = (JSONObject)JSONSerializer.toJSON(page.getWebResponse().getContentAsString());
        //Does it still applies to secured projects?
        assertTrue(json.has("jobs"));
        jobs = (JSONArray)json.get("jobs");
        assertEquals(1, jobs.size());
    }
}
