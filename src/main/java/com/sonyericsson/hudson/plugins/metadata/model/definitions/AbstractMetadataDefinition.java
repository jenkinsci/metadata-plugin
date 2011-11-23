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
package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Constants;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import hudson.AbortException;
import hudson.DescriptorExtensionList;
import hudson.cli.CLICommand;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.Serializable;

/**
 * A metadata definition.
 */
@ExportedBean
public abstract class AbstractMetadataDefinition implements
        Describable<AbstractMetadataDefinition>, Serializable, MetadataDefinition {

    private MetadataParent parent;
    private final String name;
    private final String description;
    private boolean exposedToEnvironment;

    /**
     * Constructor with only a name.
     *
     * @param name the name of the definition.
     */
    public AbstractMetadataDefinition(String name) {
        this(name, null, false);
    }

    /**
     * Constructor with name and description.
     *
     * @param name        the name of the definition.
     * @param description the description of the definition.
     */
    public AbstractMetadataDefinition(String name, String description) {
        this(name, description, false);
    }

    /**
     * Constructor with name, description and exposedToEnvironment..
     *
     * @param name        the name of the definition.
     * @param description the description of the definition.
     * @param exposedToEnvironment If this definition should be exposed as an environment variable.
     */
    public AbstractMetadataDefinition(String name, String description, boolean exposedToEnvironment) {
        this.name = name;
        this.description = description;
        this.exposedToEnvironment = exposedToEnvironment;
    }

    /**
     * Returns the simple name of the class.
     *
     * @return the simple name of this class.
     */
    @Exported
    public String getType() {
        return this.getClass().getSimpleName();
    }

    @Exported
    @Override
    public String getName() {
        return name;
    }


    @Override
    public synchronized MetadataParent getParent() {
        return parent;
    }


    @Override
    public synchronized void setParent(MetadataParent parent) {
        this.parent = parent;
    }

    @Override
    public boolean isExposedToEnvironment() {
        return exposedToEnvironment;
    }

    @Override
    public void setExposeToEnvironment(boolean expose) {
        exposedToEnvironment = expose;
    }

    /**
     * This function will generate the full name, using the chosen separator.
     * @param separator the separator to use.
     * @return the full name.
     */
    @Exported
    public String getFullName(String separator) {
        MetadataParent myParent = getParent();
        if (myParent != null) {
            return myParent.getFullName() + separator + getName();
        }
        return getName();
    }

    /**
     * This function will generate the full name.
     * @return the full name.
     */
    @Exported
    public String getFullName() {
        return getFullName(Constants.DISPLAY_NAME_SEPARATOR);
    }

    /**
     * Returns the description of this definition.
     *
     * @return The description of this definition.
     */
    @Exported
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns the descriptor of this definition.
     *
     * @return the descriptor of this definition.
     */
    public AbstractMetaDataDefinitionDescriptor getDescriptor() {
        return (AbstractMetaDataDefinitionDescriptor)Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    /**
     * Create a metadata values from a form submission.
     * <p/>
     * <p/>
     * This method is invoked when the user fills in the metadata values in the HTML form
     * and submits it to the server.
     *
     * @param req the stapler request.
     * @param jo the JSON object.
     * @return the metadata values.
     */

    public abstract AbstractMetadataValue createValue(StaplerRequest req, JSONObject jo);

    /**
     * Create a metadata values from a GET with query string.
     * If no values is available in the request, it returns a default values if possible, or null.
     * <p/>
     * <p/>
     * Unlike {@link #createValue(StaplerRequest, JSONObject)}, this method is intended to support
     * the programmatic POST-ing of the build URL. This form is less expressive (as it doesn't support
     * the tree form), but it's more scriptable.
     * <p/>
     * <p/>
     * If a {@link AbstractMetadataDefinition} can't really support this mode of creating a values,
     * you may just always return null.
     *
     * @param req the stapler request.
     * @return the metadata values.
     */

    public abstract AbstractMetadataValue createValue(StaplerRequest req);

    /**
     * Create a metadata values from the string given in the CLI.
     *
     * @param command This is the command that got the parameter. You can use its {@link CLICommand#channel}
     *                for interacting with the CLI JVM.
     * @param value   the values passed in the CLI command
     * @return the metadata values.
     * @throws AbortException If the CLI processing should be aborted. Hudson will report the error message
     *                        without stack trace, and then exits this command. Useful for graceful termination.
     */

    public AbstractMetadataValue createValue(CLICommand command, String value) throws AbortException {
        throw new AbortException("CLI parameter submission is not supported for the " + getClass()
                + " type. Please file a bug report for this");
    }

    /**
     * Returns default metadata values for this definition.
     *
     * @return default metadata values or null if no defaults are available
     */
    @Exported
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public Object getValue() {
        return getDefaultValue();
    }

    /**
     * Returns all the registered {@link AbstractMetadataDefinition} descriptors.
     *
     * @return A DescriptorExtensionList of the descriptors.
     */
    public static DescriptorExtensionList<AbstractMetadataDefinition, AbstractMetaDataDefinitionDescriptor> getAll() {
        return Hudson.getInstance().<AbstractMetadataDefinition, AbstractMetaDataDefinitionDescriptor>
                getDescriptorList(AbstractMetadataDefinition.class);
    }

    /**
     * The descriptor for the AbstractMetadataDefinition.
     */
    public abstract static class AbstractMetaDataDefinitionDescriptor extends Descriptor<AbstractMetadataDefinition> {
    }

}

