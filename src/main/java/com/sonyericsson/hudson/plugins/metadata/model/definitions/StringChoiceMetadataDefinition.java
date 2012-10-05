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

package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import hudson.Extension;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;

import java.util.Arrays;
import java.util.List;

/**
 * A Metadata definition that displays a drop down list of strings to choose from.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public class StringChoiceMetadataDefinition extends AbstractMetadataDefinition {

    private List<String> choices;

    /**
     * Standard Constructor.
     *
     * @param name    the name
     * @param choices the choices separated by new line.
     */
    public StringChoiceMetadataDefinition(String name, String choices) {
        super(name);
        setChoicesText(choices);
    }

    /**
     * Standard Constructor.
     *
     * @param name        the name
     * @param choices     the choices separated by new line.
     * @param description the description.
     */
    public StringChoiceMetadataDefinition(String name, String choices, String description) {
        super(name, description);
        setChoicesText(choices);
    }

    /**
     * Standard Constructor.
     *
     * @param name                 the name
     * @param choices              the choices separated by new line.
     * @param description          the description.
     * @param exposedToEnvironment if the value should be exposed as an environment variable or not.
     * @see AbstractMetadataDefinition#AbstractMetadataDefinition(String, String, boolean)
     */
    @DataBoundConstructor
    public StringChoiceMetadataDefinition(String name, String choices,
                                          String description, boolean exposedToEnvironment) {
        super(name, description, exposedToEnvironment);
        setChoicesText(choices);
    }

    /**
     * The list of choices.
     *
     * @return the list.
     */
    @Exported
    public List<String> getChoices() {
        return choices;
    }

    /**
     * The list of choices separated by new line. As they are written on the configuration page.
     *
     * @return the list of choices as a string.
     *
     * @see #getChoices()
     */
    public String getChoicesText() {
        return StringUtils.join(choices, "\n");
    }

    /**
     * Converts the provided string into a list of choices, by splitting on new line.
     *
     * @param choicesText the choices as they get written on the configuration page.
     * @see #getChoicesText()
     * @see #getChoices()
     */
    protected void setChoicesText(String choicesText) {
        if (choicesText.length() == 0) {
            throw new IllegalArgumentException("No choices found");
        }
        this.choices = Arrays.asList(choicesText.split("\\r?\\n"));
    }

    @Override
    public AbstractMetadataValue createValue(Object o) {
        String value = "";
        if (o instanceof String && !o.equals("")) {
            value = (String)o;
        } else if (choices.size() > 0) { //if for some reason nothing is chosen, take the first in the list.
            value = choices.get(0);
        }
        StringMetadataValue metadataValue =
                new StringMetadataValue(getName(), getDescription(), value, isExposedToEnvironment());
        return metadataValue;
    }

    /**
     * The {@link hudson.model.Descriptor}.
     */
    @Extension
    public static class StringChoiceMetadataDefinitionDescriptor extends AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StringChoiceMetadataDefinitionDescriptor_DisplayName();
        }
    }
}
