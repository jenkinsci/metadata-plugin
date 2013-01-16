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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.ParentUtil;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;

/**
 * Help class for definition and value comparison.
 *
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
public class MetadataValueDefinitionHelper {

    private Collection<MetadataValue> values;

    /**
     * Standard constructor.
     * @param values the MetadataValues to use as a starting point.
     */
    public MetadataValueDefinitionHelper(Collection<MetadataValue> values) {
        try {
            this.values = cloneValues(values);
        } catch (CloneNotSupportedException e) {
            //if for some reason a clone can not be made, just use the original list of values.
            this.values = values;
        }
    }

    /**
     * Clones the MetadataValues.
     * @param valuesToClone the MetadataValues to clone.
     * @return a clone of the MetadataValues
     * @throws CloneNotSupportedException if one of the MetadataValues could not be cloned.
     */
    private Collection<MetadataValue> cloneValues(Collection<MetadataValue> valuesToClone)
            throws CloneNotSupportedException {
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        for (MetadataValue value : valuesToClone) {
            list.add(value.clone());
        }
        return list;
    }

    /**
     * Standard getter for the values.
     * @return the MetadataValues.
     */
    public Collection<MetadataValue> getValues() {
        return values;
    }

    /**
     * Gets the value of a definition. If a corresponding MetadataValue exists for this definition,
     * use the value of the MetadataValue. If not, use the default value of the MetadataDefinition.
     *
     * @param definition the MetadataDefinition to find the corresponding String value for.
     * @return the user input value or default if not found.
     */
    public Object getValueForDefinition(Metadata definition) {
        String[] fullPath = definition.getFullPath();
        MetadataValue m = TreeStructureUtil.getLeaf(values, fullPath);
        if (m == null) {
            return definition;
        } else if (m.getParent() instanceof Metadata) {
            MetadataParent parent = m.getParent();
            ParentUtil.removeChild(parent, m);
            ParentUtil.removeEmptyBranches(values);
            return m;
        } else {
            ParentUtil.removeChild(values, m);
            return m;
        }
    }
}
