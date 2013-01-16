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
package com.sonyericsson.hudson.plugins.metadata.model;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.LinkedList;
import java.util.List;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.MetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.TreeNodeMetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.StringMetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;

/**
 * Tests for the {@link MetadataValueDefinitionHelper}.
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
public class HelperTest {
    private TreeNodeMetadataDefinition startTreeNode;
    private List<MetadataValue> values;


    /**
     * Sets up a tree of values and one of definitions, to simulate a real environment.
     */
    @Before
    public void setup() {
        TreeNodeMetadataValue[] startTree = TreeStructureUtil.createTreePath("", "root", "child1");
        TreeNodeMetadataValue treeValue = startTree[0];
        TreeStructureUtil.addValue(treeValue, "nonDefaultVal", "", "child1", "child12");
        values = new LinkedList<MetadataValue>();
        values.add(treeValue);
        StringMetadataDefinition stringDef = new StringMetadataDefinition("child12", "defaultVal");
        TreeNodeMetadataDefinition subTreeDef = new TreeNodeMetadataDefinition("child1");
        subTreeDef.addChild(stringDef);
        startTreeNode = new TreeNodeMetadataDefinition("root", new LinkedList<MetadataDefinition>());
        startTreeNode.addChild(subTreeDef);
    }

    /**
     * Tests getting a StringMetadataValue for a StringMetadataDefinition.
     */
    @Test
    public void testGetStringValueForDefinition() {
        MetadataValueDefinitionHelper helper = new MetadataValueDefinitionHelper(values);
        MetadataDefinition definitionLeaf = TreeStructureUtil.getLeaf(startTreeNode, "child1", "child12");
        Object valueForDefinition = helper.getValueForDefinition(definitionLeaf);
        Assert.assertThat(valueForDefinition, instanceOf(StringMetadataValue.class));
        StringMetadataValue stringMetadataValue = (StringMetadataValue)valueForDefinition;
        assertThat(stringMetadataValue.getValue(), equalTo("nonDefaultVal"));
        assertThat(helper.getValues().size(), equalTo(0));
    }
}
