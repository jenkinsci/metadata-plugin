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

import java.util.Arrays;

/**
 * Utility methods for easier creation of tree structures of values.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public abstract class TreeStructureUtil {

    /**
     * Private utility constructor.
     */
    private TreeStructureUtil() {

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
    public static boolean addValue(MetaDataValueParent root, String value, String description, String... path) {
        StringMetaDataValue sVal = new StringMetaDataValue(path[path.length - 1], description, value);
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
    public static boolean addValue(MetaDataValueParent root, AbstractMetaDataValue value, String... parentPath) {
        if (parentPath == null || parentPath.length <= 0) {
            return root.addChildValue(value);
        } else {
            TreeNodeMetaDataValue path = createPath(value, parentPath);
            return root.addChildValue(path);
        }
    }

    /**
     * Creates a path where the last element is a string with the provided value and description.
     *
     * @param value       the value
     * @param description the description
     * @param path        the full path to the leaf.
     * @return the tree.
     */
    public static TreeNodeMetaDataValue createPath(String value, String description, String... path) {
        StringMetaDataValue str = new StringMetaDataValue(path[path.length - 1], description, value);
        return createPath(str, Arrays.copyOf(path, path.length - 1));
    }

    /**
     * Created a tree structured path with the provided leaf at the end.
     *
     * @param leaf       the leaf to put in the end.
     * @param parentPath the path to the leaf.
     * @return the root node of the path.
     */
    public static TreeNodeMetaDataValue createPath(AbstractMetaDataValue leaf, String... parentPath) {
        if (parentPath == null || parentPath.length < 1) {
            throw new IllegalArgumentException("The leaf must have at least one parent.");
        }
        TreeNodeMetaDataValue root = null;
        TreeNodeMetaDataValue parent = null;

        for (String name : parentPath) {
            TreeNodeMetaDataValue val = new TreeNodeMetaDataValue(name);
            if (parent != null) {
                parent.addChildValue(val);
            }
            parent = val;
            if (root == null) {
                root = val;
            }
        }
        parent.addChildValue(leaf);
        return root;
    }
}
