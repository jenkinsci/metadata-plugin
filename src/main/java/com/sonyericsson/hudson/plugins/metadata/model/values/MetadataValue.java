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

import com.sonyericsson.hudson.plugins.metadata.model.Metadata;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import hudson.EnvVars;
import net.sf.json.JSONObject;

/**
 * A metadata value interface.
 */
public interface MetadataValue extends Metadata<MetadataValue> {

    /**
     * If this value is generated or user created.
     *
     * @return true if generated.
     */
    boolean isGenerated();

    /**
     * Set if this value is generated or user created.
     *
     * @param generated true if generated.
     */
    void setGenerated(boolean generated);

    /**
     * Convert this object into a JSON object.
     * @return the JSON version.
     */
    JSONObject toJson();

    /**
     * A signal sent to this object that it is the replacement of another object.
     * The intention from the system is that the object should take this opportunity
     * to try and salvage as much as possible from the old object.
     *
     * @param old the object that it is the replacement of.
     */
    void replacementOf(MetadataValue old);

    /**
     * Adds the environment variables for this Metadata to the variables map.
     * @param variables the map of current environment variables.
     * @param exposeAll whether all Metadata should be added to the map. Inherited from the parent.
     */
    void addEnvironmentVariables(EnvVars variables, boolean exposeAll);

    /**
     * Gives the full name from the given parent.
     *
     * @param base the parent to base the tree structure from.
     * @return a dot separated path from the base up to this item.
     */
    String getFullNameFrom(MetadataParent<MetadataValue> base);
}
