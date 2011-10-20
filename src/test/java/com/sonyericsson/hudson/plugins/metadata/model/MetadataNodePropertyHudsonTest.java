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

import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.slaves.DumbSlave;
import org.jvnet.hudson.test.HudsonTestCase;

import java.util.LinkedList;

/**
 * Configuration tests for {@link MetadataNodeProperty}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class MetadataNodePropertyHudsonTest extends HudsonTestCase {

    /**
     * Tests that a metadata configuration is still there after someone saves a page.
     *
     * @throws Exception if so.
     */
    public void testConfigRoundtrip() throws Exception {
        DumbSlave slave = createSlave();
        String name = slave.getNodeName();
        MetadataNodeProperty property = new MetadataNodeProperty(new LinkedList<AbstractMetaDataValue>());
        slave.getNodeProperties().add(property);
        TreeStructureUtil.addValue(property, "test", "description", "some", "kind", "of", "path");

        this.configRoundtrip(slave);

        Node node = Hudson.getInstance().getNode(name);
        property = node.getNodeProperties().get(MetadataNodeProperty.class);
        AbstractMetaDataValue value = TreeStructureUtil.getPath(property, "some", "kind", "of", "path");
        assertNotNull(value);
        assertEquals("test", value.getValue());
    }

    /**
     * Submits the configuration page for a Node.
     *
     * @param node the node
     * @throws Exception if so.
     */
    private void configRoundtrip(Node node) throws Exception {
        submit(createWebClient().goTo("computer/" + node.getNodeName() + "/configure").getFormByName("config"));
    }
}
