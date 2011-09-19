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

/**
 * Common interface for definitions and values.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public interface Metadata {
    /**
     * Returns the name of this metadata.
     *
     * @return the name of this metadata.
     */
    String getName();

    /**
     * Returns the description of this metadata.
     *
     * @return The description of this metadata.
     */
    String getDescription();

    /**
     * Get the value.
     *
     * @return the value.
     */
    Object getValue();

    /**
     * The parent of this metadata.
     *
     * @return the parent.
     */
    MetaDataParent getParent();

    /**
     * The parent of this metadata.
     *
     * @param parent the metadata.
     */
    void setParent(MetaDataParent parent);
}
