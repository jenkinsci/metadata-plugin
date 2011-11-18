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

import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.Metadata;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for {@link TreeStructureUtil}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class TreeStructureUtilTest {

    /**
     * Tests {@link TreeStructureUtil#
     * addValue(com.sonyericsson.hudson.plugins.metadata.model.MetadataParent, String, String, String...)}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddStringValue() throws Exception {
        TreeNodeMetadataValue root = new TreeNodeMetadataValue("root");
        boolean result = TreeStructureUtil.addValue(root, "value", "description", "one", "two", "three");
        assertTrue(result);
        TreeNodeMetadataValue one = (TreeNodeMetadataValue)root.getChild("one");
        assertNotNull(one);
        assertSame(root, one.getParent());
        TreeNodeMetadataValue two = (TreeNodeMetadataValue)one.getChild("two");
        assertNotNull(two);
        assertSame(one, two.getParent());
        Metadata three = two.getChild("three");
        assertNotNull(three);
        assertSame(two, three.getParent());
        assertEquals("value", three.getValue());
        assertEquals("description", three.getDescription());
    }

    /**
     * Tests {@link TreeStructureUtil#
     * addValue(com.sonyericsson.hudson.plugins.metadata.model.MetadataParent, String, String, String...)}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddStringValueToJobProperty() throws Exception {
        MetadataJobProperty root = new MetadataJobProperty();
        boolean result = TreeStructureUtil.addValue(root, "value", "description", "one", "two");
        assertTrue(result);
        TreeNodeMetadataValue one = (TreeNodeMetadataValue)root.getChild("one");
        assertNotNull(one);
        assertSame(root, one.getParent());
        Metadata two = one.getChild("two");
        assertNotNull(two);
        assertSame(one, two.getParent());
        assertEquals("value", two.getValue());
        assertEquals("description", two.getDescription());
    }

    /**
     * Tests {@link TreeStructureUtil#
     * addValue(com.sonyericsson.hudson.plugins.metadata.model.MetadataParent, String, String, String...)}.
     * With null description.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddStringValueNoDescription() throws Exception {
        TreeNodeMetadataValue root = new TreeNodeMetadataValue("root");
        boolean result = TreeStructureUtil.addValue(root, "value", null, "one");
        assertTrue(result);
        assertNotNull(root.getChild("one"));
        assertNull(root.getChild("one").getDescription());
    }

    /**
     * Test {@link TreeStructureUtil#createPath(String, String, String...)}.
     */
    @Test
    public void testCreatePath() {
        TreeNodeMetadataValue root = TreeStructureUtil.createPath("value", "description", "one", "two");
        assertEquals("one", root.getName());
        assertNotNull(root.getChild("two"));
        assertSame(root, root.getChild("two").getParent());
        assertEquals("value", root.getChild("two").getValue());
        assertEquals("description", root.getChild("two").getDescription());
    }

    /**
     * Test {@link TreeStructureUtil#createPath(String, String, String...)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreatePathNoParent() {
        TreeStructureUtil.createPath("value", "description", "one");
    }
}
