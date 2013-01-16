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

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.sonyericsson.hudson.plugins.metadata.TestACL;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests for {@link MetadataSearchPage}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public class MetadataSearchPageTest extends HudsonTestCase {

    /**
     * Tests a search with ACL settings.
     *
     * @throws Exception if so.
     */
    public void testSearchACL() throws Exception {
        FreeStyleProject project = createFreeStyleProject("open");
        FreeStyleProject project2 = createFreeStyleProject("secure");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        StringMetadataValue value = new StringMetadataValue("name", "description", "value");
        list.add(value);
        MetadataJobProperty property = project.getProperty(MetadataJobProperty.class);
        property.addChildren(list);
        list = new LinkedList<MetadataValue>();
        value = new StringMetadataValue("name", "description", "value");
        list.add(value);
        MetadataJobProperty property2 = project2.getProperty(MetadataJobProperty.class);
        property2.addChildren(list);

        //First search without ACL
        WebClient web = createWebClient();
        doTestSearch(web, "name=value", 2);

        //Setup ACL
        hudson.setSecurityRealm(createDummySecurityRealm());
        hudson.setAuthorizationStrategy(new TestACL());

        //Do the test with ACL
        web = createWebClient();
        web = web.login("testuser");
        doTestSearch(web, "name=value", 1);
    }

    /**
     * Opens the search page and performs a search.
     *
     * @param web           the client to connect with.
     * @param searchString  the search expression
     * @param expectedCount the expected number of jobs to be displayed.
     * @throws IOException  if so.
     * @throws SAXException if so.
     */
    private void doTestSearch(WebClient web, String searchString, int expectedCount) throws IOException, SAXException {
        HtmlPage htmlPage = web.goTo("/metadata-search/searchMetadata?metadata.search.queryString=" + searchString);
        HtmlElement documentElement = htmlPage.getDocumentElement();
        HtmlTable element = (HtmlTable)documentElement.getElementById("projectstatus");
        assertEquals(expectedCount + 1, element.getRowCount()); //One extra for the table header.
    }
}
