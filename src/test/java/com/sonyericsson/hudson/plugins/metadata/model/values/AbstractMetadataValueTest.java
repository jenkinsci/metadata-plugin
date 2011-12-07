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
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link AbstractMetadataValue}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class AbstractMetadataValueTest {

    /**
     * Tests {@link com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue#getFullName()}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testGetFullName() throws Exception {
        TreeNodeMetadataValue[] treePath = TreeStructureUtil.createTreePath("description", "some", "path");
        StringMetadataValue value = new StringMetadataValue("name", "description", "value");
        treePath[1].addChild(value);
        assertEquals("some.path.name", value.getFullName());
    }

    /**
     * Tests {@link com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue#getFullName(String)}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testGetFullNameSeparator() throws Exception {
        TreeNodeMetadataValue[] treePath = TreeStructureUtil.createTreePath("description", "some", "path");
        StringMetadataValue value = new StringMetadataValue("name", "description", "value");
        treePath[1].addChild(value);
        assertEquals("some_path_name", value.getFullName("_"));
    }

    /**
     * Tests {@link AbstractMetadataValue#getEnvironmentName()}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testGetEnvironmentName() throws Exception {
        TreeNodeMetadataValue[] treePath = TreeStructureUtil.createTreePath("description", "some", "path");
        StringMetadataValue value = new StringMetadataValue("name", "description", "value");
        treePath[1].addChild(value);
        assertEquals(Constants.METADATA_ENV_PREFIX + "SOME_PATH_NAME", value.getEnvironmentName());
    }

    /**
     * Tests {@link AbstractMetadataValue#getEnvironmentName()} with some special chars in the names.
     *
     * @throws Exception if so.
     */
    @Test
    public void testGetEnvironmentNameWithSpecials() throws Exception {
        TreeNodeMetadataValue[] treePath = TreeStructureUtil.createTreePath("description", "some?maybe", "cool-path");
        StringMetadataValue value = new StringMetadataValue("$anyName", "description", "value");
        treePath[1].addChild(value);
        assertEquals(Constants.METADATA_ENV_PREFIX + "SOME_MAYBE_COOL_PATH__ANYNAME", value.getEnvironmentName());
    }
}
