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

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;


/**
 * Tests for {@link ParentUtil}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class ParentUtilTest {
    /**
     * Tests to add one child to a tree-node.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddChildValue() throws Exception {
        TreeNodeMetaDataValue parent = new TreeNodeMetaDataValue("root");
        assertNull(parent.addChildValue(new StringMetaDataValue("child1", "value")));
        assertNotNull(parent.getChildValue("child1"));
    }

    /**
     * Tests to add one straight branch one/two/three=value to a tree-node.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddChildValueTree() throws Exception {
        TreeNodeMetaDataValue parent = new TreeNodeMetaDataValue("root");
        AbstractMetaDataValue path = TreeStructureUtil.createPath("value", "description", "one", "two", "three");
        assertNull(parent.addChildValue(path));
        assertEquals("value", ((MetaDataValueParent)((MetaDataValueParent)parent.getChildValue("one"))
                .getChildValue("two")).getChildValue("three").getValue());
    }

    /**
     * Tests to add one tree structure into another with some leftovers expected because they already existed.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddChildValueTreeNotAllMerged() throws Exception {
        /*
                _start tree_
                root
                    child1
                        child11=something
                    child2=something
                    child3
                        child31
                            child311=something
                            child312=something

                _tree to add_
                    child1
                        child12=something
                    child2=something else
                    child3
                        child31
                            child312=something else
                            child313=something

                _expected result_
                root
                    child1
                        child11=something
                        child12=something
                    child2=something
                    child3
                        child31
                            child311=something
                            child312=something
                            child313=something

                _expected leftovers_
                    child2=something else
                    child3
                        child31
                            child312=something else
             */
        TreeNodeMetaDataValue[] startTree = TreeStructureUtil.createTreePath("", "root", "child1");
        TreeNodeMetaDataValue startChild1 = startTree[1];
        startChild1.addChildValue(new StringMetaDataValue("child11", "something"));
        TreeNodeMetaDataValue startRoot = startTree[0];
        startRoot.addChildValue(new StringMetaDataValue("child2", "something"));
        TreeStructureUtil.addValue(startRoot, "something", "", "child3", "child31", "child311");
        TreeStructureUtil.addValue(startRoot, "something", "", "child3", "child31", "child312");

        TreeNodeMetaDataValue addRoot = TreeStructureUtil.createPath("something", "", "root", "child1", "child12");
        TreeStructureUtil.addValue(addRoot, "something else", "", "child2");
        TreeStructureUtil.addValue(addRoot, "something else", "", "child3", "child31", "child312");
        TreeStructureUtil.addValue(addRoot, "something", "", "child3", "child31", "child313");

        Collection<AbstractMetaDataValue> returnedValues = startRoot.addChildValues(addRoot.getChildren());

        //Verify the tree
        AbstractMetaDataValue leaf = TreeStructureUtil.getPath(startRoot, "child1", "child11");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child1", "child12");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child2");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child3", "child31", "child311");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child3", "child31", "child312");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child3", "child31", "child313");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());

        //Verify the leftovers
        assertNotNull(returnedValues);
        AbstractMetaDataValue child2 = null;
        AbstractMetaDataValue child3 = null;
        for (AbstractMetaDataValue value : returnedValues) {
            if (value.getName().equalsIgnoreCase("child2")) {
                child2 = value;
            } else if (value.getName().equalsIgnoreCase("child3")) {
                child3 = value;
            } else {
                fail("More values returned than expected! " + value.getName() + ": " + value.getValue());
            }
        }
        assertNotNull(child2);
        assertEquals("something else", child2.getValue());
        assertNotNull(child3);
        AbstractMetaDataValue child31 = ((MetaDataValueParent)child3).getChildValue("child31");
        assertNotNull(child31);
        assertEquals("something else", ((MetaDataValueParent)child31).getChildValue("child312").getValue());
    }

    /**
     * Tests to add one tree structure into another with some leftovers expected
     * because they already existed and are of different type.
     * I.e. Trying to add a leaf where there is a node and vice versa.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddChildValueTreeNotAllMergedLeafOnNode() throws Exception {
        /*
                _start tree_
                root
                    child1
                        child11=something
                    child2
                        child21
                            child211=something
                    child3=something

                _tree to add_
                    child1
                        child11
                            child111=something
                    child2
                        child21=something else
                    child4=something

                _expected result_
                root
                    child1
                        child11=something
                    child2
                        child21
                            child211=something
                    child3=something
                    child4=something

                _expected leftovers_
                    child1
                        child11
                            child111=something
                    child2
                        child21=something else
             */
        TreeNodeMetaDataValue[] startTree = TreeStructureUtil.createTreePath("", "root", "child1");
        TreeNodeMetaDataValue startChild1 = startTree[1];
        startChild1.addChildValue(new StringMetaDataValue("child11", "something"));
        TreeNodeMetaDataValue startRoot = startTree[0];
        TreeStructureUtil.addValue(startRoot, "something", "", "child2", "child21", "child211");
        startRoot.addChildValue(new StringMetaDataValue("child3", "something"));

        TreeNodeMetaDataValue addRoot = TreeStructureUtil.createPath("something else", "",
                "root", "child1", "child11", "child111");
        TreeStructureUtil.addValue(addRoot, "something else", "", "child2", "child21");
        TreeStructureUtil.addValue(addRoot, "something", "", "child4");

        Collection<AbstractMetaDataValue> returnedValues = startRoot.addChildValues(addRoot.getChildren());

        //Verify the tree
        AbstractMetaDataValue leaf = TreeStructureUtil.getPath(startRoot, "child1", "child11");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child2", "child21", "child211");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child3");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());
        leaf = TreeStructureUtil.getPath(startRoot, "child4");
        assertNotNull(leaf);
        assertEquals("something", leaf.getValue());

        //Verify the leftovers
        assertNotNull(returnedValues);
        AbstractMetaDataValue child1 = null;
        AbstractMetaDataValue child2 = null;
        for (AbstractMetaDataValue value : returnedValues) {
            if (value.getName().equalsIgnoreCase("child1")) {
                child1 = value;
            } else if (value.getName().equalsIgnoreCase("child2")) {
                child2 = value;
            } else {
                fail("More values returned than expected! " + value.getName() + ": " + value.getValue());
            }
        }
        assertNotNull(child1);
        AbstractMetaDataValue child11 = ((MetaDataValueParent)child1).getChildValue("child11");
        assertNotNull(child11);
        AbstractMetaDataValue child111 = ((MetaDataValueParent)child11).getChildValue("child111");
        assertEquals("something else", child111.getValue());
        assertNotNull(child2);

        assertEquals("something else", ((MetaDataValueParent)child2).getChildValue("child21").getValue());
    }
}
