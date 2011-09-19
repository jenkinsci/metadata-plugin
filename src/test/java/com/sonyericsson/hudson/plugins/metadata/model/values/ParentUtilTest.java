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

import com.sonyericsson.hudson.plugins.metadata.model.MetaDataParent;
import com.sonyericsson.hudson.plugins.metadata.model.Metadata;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.AbstractMetaDataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.MetadataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.StringMetaDataDefinition;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.TreeNodeMetaDataDefinition;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        assertNull(parent.addChild(new StringMetaDataValue("child1", "value")));
        assertNotNull(parent.getChild("child1"));
    }

    /**
     * Tests to add one null child to a tree-node.
     *
     * @throws Exception if so.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddChildValueNull() throws Exception {
        TreeNodeMetaDataValue parent = new TreeNodeMetaDataValue("root");
        parent.addChild(null);
    }

    /**
     *  Tests to add a non empty list of children to the constructor of a tree-node.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddChildrenNonEmpty() throws Exception {
        List<MetadataValue> nonEmptyList = new LinkedList<MetadataValue>();
        StringMetaDataValue sampleValue = new StringMetaDataValue("sampleName", "sampleValue", "sampleDescription");
        nonEmptyList.add(sampleValue);
        TreeNodeMetaDataValue parent = new TreeNodeMetaDataValue("parentName", "parentDescription", nonEmptyList);
        assertNotNull(parent.getChild("sampleName"));
    }

    /**
     *  Tests to add an empty list of children to the constructor of a tree-node.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddChildrenEmpty() throws Exception {
        List<MetadataValue> emptyList = new LinkedList<MetadataValue>();
        TreeNodeMetaDataValue parent = new TreeNodeMetaDataValue("parentName", "parentDescription", emptyList);
        assertNull(parent.getChild("someName"));
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
        assertNull(parent.addChild(path));
        assertEquals("value", ((MetaDataParent)((MetaDataParent)parent.getChild("one"))
                .getChild("two")).getChild("three").getValue());
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
        startChild1.addChild(new StringMetaDataValue("child11", "something"));
        TreeNodeMetaDataValue startRoot = startTree[0];
        startRoot.addChild(new StringMetaDataValue("child2", "something"));
        TreeStructureUtil.addValue(startRoot, "something", "", "child3", "child31", "child311");
        TreeStructureUtil.addValue(startRoot, "something", "", "child3", "child31", "child312");

        TreeNodeMetaDataValue addRoot = TreeStructureUtil.createPath("something", "", "root", "child1", "child12");
        TreeStructureUtil.addValue(addRoot, "something else", "", "child2");
        TreeStructureUtil.addValue(addRoot, "something else", "", "child3", "child31", "child312");
        TreeStructureUtil.addValue(addRoot, "something", "", "child3", "child31", "child313");

        Collection<MetadataValue> returnedValues = startRoot.addChildren(addRoot.getChildren());

        //Verify the tree
        Metadata leaf = TreeStructureUtil.getPath(startRoot, "child1", "child11");
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
        Metadata child2 = null;
        Metadata child3 = null;
        for (Metadata value : returnedValues) {
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
        Metadata child31 = ((MetaDataParent)child3).getChild("child31");
        assertNotNull(child31);
        assertEquals("something else", ((MetaDataParent)child31).getChild("child312").getValue());
    }

    /**
     * Tests to add one tree structure into another with some leftovers expected because they already existed.
     *
     * @throws Exception if so.
     */
    @Test
    public void testAddChildDefinitionTreeNotAllMerged() throws Exception {
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
        TreeNodeMetaDataDefinition[] startTree = createTreePath("", "root", "child1");
        TreeNodeMetaDataDefinition startChild1 = startTree[1];
        startChild1.addChild(new StringMetaDataDefinition("child11", "something"));
        TreeNodeMetaDataDefinition startRoot = startTree[0];
        startRoot.addChild(new StringMetaDataDefinition("child2", "something"));
        addValue(startRoot, "something", "", "child3", "child31", "child311");
        addValue(startRoot, "something", "", "child3", "child31", "child312");

        TreeNodeMetaDataDefinition addRoot = createPath("something", "", "root", "child1", "child12");
        addValue(addRoot, "something else", "", "child2");
        addValue(addRoot, "something else", "", "child3", "child31", "child312");
        addValue(addRoot, "something", "", "child3", "child31", "child313");

        Collection<MetadataDefinition> returnedValues = startRoot.addChildren(addRoot.getChildren());

        //Verify the tree
        Metadata leaf = TreeStructureUtil.getPath(startRoot, "child1", "child11");
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
        Metadata child2 = null;
        Metadata child3 = null;
        for (Metadata value : returnedValues) {
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
        Metadata child31 = ((MetaDataParent)child3).getChild("child31");
        assertNotNull(child31);
        assertEquals("something else", ((MetaDataParent)child31).getChild("child312").getValue());
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
        startChild1.addChild(new StringMetaDataValue("child11", "something"));
        TreeNodeMetaDataValue startRoot = startTree[0];
        TreeStructureUtil.addValue(startRoot, "something", "", "child2", "child21", "child211");
        startRoot.addChild(new StringMetaDataValue("child3", "something"));

        TreeNodeMetaDataValue addRoot = TreeStructureUtil.createPath("something else", "",
                "root", "child1", "child11", "child111");
        TreeStructureUtil.addValue(addRoot, "something else", "", "child2", "child21");
        TreeStructureUtil.addValue(addRoot, "something", "", "child4");

        Collection<MetadataValue> returnedValues = startRoot.addChildren(addRoot.getChildren());

        //Verify the tree
        Metadata leaf = TreeStructureUtil.getPath(startRoot, "child1", "child11");
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
        Metadata child1 = null;
        Metadata child2 = null;
        for (Metadata value : returnedValues) {
            if (value.getName().equalsIgnoreCase("child1")) {
                child1 = value;
            } else if (value.getName().equalsIgnoreCase("child2")) {
                child2 = value;
            } else {
                fail("More values returned than expected! " + value.getName() + ": " + value.getValue());
            }
        }
        assertNotNull(child1);
        Metadata child11 = ((MetaDataParent)child1).getChild("child11");
        assertNotNull(child11);
        Metadata child111 = ((MetaDataParent)child11).getChild("child111");
        assertEquals("something else", child111.getValue());
        assertNotNull(child2);

        assertEquals("something else", ((MetaDataParent)child2).getChild("child21").getValue());
    }
    /**
     * Creates a straight tree-path. The method returns an array where index 0 is the root and index 1 is the leaf.
     *
     * @param description the description of the root.
     * @param path        the path to create.
     * @return the root and the leaf.
     */
    public static TreeNodeMetaDataDefinition[] createTreePath(String description, String... path) {
        TreeNodeMetaDataDefinition[] arr = new TreeNodeMetaDataDefinition[2];
        arr[1] = new TreeNodeMetaDataDefinition(path[path.length - 1], description);
        arr[0] = createPath(arr[1], Arrays.copyOf(path, path.length - 1));
        return arr;
    }
     /**
     * Creates a tree structured path with the provided leaf at the end.
     *
     * @param leaf       the leaf to put in the end.
     * @param parentPath the path to the leaf.
     * @return the root node of the path.
     */
    public static TreeNodeMetaDataDefinition createPath(AbstractMetaDataDefinition leaf, String... parentPath) {
        if (parentPath == null || parentPath.length < 1) {
            throw new IllegalArgumentException("The leaf must have at least one parent.");
        }
        TreeNodeMetaDataDefinition root = null;
        TreeNodeMetaDataDefinition parent = null;

        for (String name : parentPath) {
            TreeNodeMetaDataDefinition val = new TreeNodeMetaDataDefinition(name);
            if (parent != null) {
                parent.addChild(val);
            }
            parent = val;
            if (root == null) {
                root = val;
            }
        }
        parent.addChild(leaf);
        return root;
    }

    /**
     * Creates a path where the last element is a string with the provided value and description.
     *
     * @param value       the value
     * @param description the description
     * @param path        the full path to the leaf.
     * @return the tree.
     */
    public static TreeNodeMetaDataDefinition createPath(String value, String description, String... path) {
        StringMetaDataDefinition str = new StringMetaDataDefinition(path[path.length - 1], description, value);
        return createPath(str, Arrays.copyOf(path, path.length - 1));
    }

    /**
     * Adds a {@link StringMetaDataValue} to the root node with the specified path.
     *
     * @param root        the root to add the tree to.
     * @param value       the string value of the leaf node.
     * @param description the description of the leaf node.
     * @param path        the path to the leaf from the root.
     * @return true if there was no merge conflicts.
     */
    public static boolean addValue(MetaDataParent root, String value, String description, String... path) {
        StringMetaDataDefinition sVal = new StringMetaDataDefinition(path[path.length - 1], description, value);
        return addValue(root, sVal, Arrays.copyOf(path, path.length - 1));
    }
    /**
     * Adds a value with the specified path to the root.
     *
     * @param root       the root to add the tree to.
     * @param value      the value of the leaf.
     * @param parentPath the path of the parent of the leaf from the root.
     * @return true if there was no merge conflicts.
     */
    public static boolean addValue(MetaDataParent root, AbstractMetaDataDefinition value, String... parentPath) {
        if (parentPath == null || parentPath.length <= 0) {
            return root.addChild(value) == null;
        } else {
            TreeNodeMetaDataDefinition path = createPath(value, parentPath);
            return root.addChild(path) == null;
        }
    }
}
