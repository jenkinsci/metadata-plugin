/*
 * The MIT License
 *
 * Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sonyericsson.hudson.plugins.metadata.util;

import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import jenkins.model.Jenkins;

import java.util.List;

/**
 * Utility functions for handling extension points.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public abstract class ExtensionUtils {

    /**
     * Finds all descriptors extending {@link AbstractMetadataValue.AbstractMetaDataValueDescriptor}.
     *
     * @return the list.
     */
    public static List<AbstractMetadataValue.AbstractMetaDataValueDescriptor> getMetadataValueDescriptors() {
        return Jenkins.getInstance().getExtensionList(AbstractMetadataValue.AbstractMetaDataValueDescriptor.class);
    }

    /**
     * Utility Constructor.
     */
    private ExtensionUtils() {

    }
}
