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

import com.sonyericsson.hudson.plugins.metadata.Messages;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A Meta Data value of the type String.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class StringMetaDataValue extends AbstractMetaDataValue {

    private String value;

    /**
     * Standard Constructor.
     *
     * @param name        the name.
     * @param description the description.
     * @param value       the value.
     */
    @DataBoundConstructor
    public StringMetaDataValue(String name, String description, String value) {
        super(name, description);
        this.value = value;
    }

    /**
     * Standard Constructor.
     *
     * @param name  the name
     * @param value the value.
     */
    public StringMetaDataValue(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Descriptor<AbstractMetaDataValue> getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(StringMetaDataValueDescriptor.class);
    }

    /**
     * The Descriptor.
     */
    @Extension
    public static class StringMetaDataValueDescriptor extends AbstractMetaDataValueDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StringMetaDataValue_DisplayName();
        }
    }
}
