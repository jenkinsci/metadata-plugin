/*
 *  The MIT License
 *
 *  Copyright 2011 Sony Ericsson Mobile Communications. All rights reserved.
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
package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A metadata definition of the type String.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class StringMetadataDefinition extends AbstractMetadataDefinition {

    private String defaultValue;

    /**
     * Standard Constructor.
     *
     * @param name         the name.
     * @param description  the description.
     * @param defaultValue the default value for this definition
     * @param exposedToEnvironment if this definition should be exposed to the build as an
     *                      environment variable.
     */
    @DataBoundConstructor
    public StringMetadataDefinition(String name, String description, String defaultValue, boolean exposedToEnvironment) {
        super(name, description, exposedToEnvironment);
        this.defaultValue = defaultValue;
    }

    /**
     * Standard Constructor.
     *
     * @param name the name.
     * @param defaultValue the default value.
     */
    public StringMetadataDefinition(String name, String defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    @Override
    public StringMetadataValue createValue(Object o) {
        String value = defaultValue;
        if (o instanceof String && !o.equals("")) {
            value = (String)o;
        }
        StringMetadataValue metadataValue =
                new StringMetadataValue(getName(), getDescription(), value, isExposedToEnvironment());
        return metadataValue;
    }

    /**
     * Returns default metadata values for this definition.
     *
     * @return default metadata values or null if no defaults are available
     */
    @Override
    public synchronized String getDefaultValue() {
        return defaultValue;
    }

    /**
     * The Descriptor.
     */
    @Extension
    public static class StringMetaDataDefinitionDescriptor extends
            AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StringMetadataDefinition_DisplayName();
        }
    }
}
