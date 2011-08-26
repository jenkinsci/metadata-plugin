package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetaDataValue;
import hudson.Extension;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * A metadata definition of the type String.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class StringMetaDataDefinition extends AbstractMetaDataDefinition {

    private String defaultValue;

    /**
     * Standard Constructor.
     *
     * @param name         the name.
     * @param description  the description.
     * @param defaultValue the default value for this definition
     */
    @DataBoundConstructor
    public StringMetaDataDefinition(String name, String description, String defaultValue) {
        super(name, description);
        this.defaultValue = defaultValue;
    }

    /**
     * Standard Constructor.
     *
     * @param name the name.
     * @param defaultValue the default value.
     */
    public StringMetaDataDefinition(String name, String defaultValue) {
        super(name);
        this.defaultValue = defaultValue;
    }

    @Override
    public AbstractMetaDataValue createValue(StaplerRequest req, JSONObject jo) {
        StringMetaDataValue value = req.bindJSON(StringMetaDataValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

    /**
     * Returns default metadata values for this definition.
     *
     * @return default metadata values or null if no defaults are available
     */
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public AbstractMetaDataValue createValue(StaplerRequest req) {
        return null;
    }

    /**
     * The Descriptor.
     */
    @Extension
    public static class StringMetaDataDefinitionDescriptor extends
            AbstractMetaDataDefinition.AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.StringMetaDataDefinition_DisplayName();
        }
    }
}
