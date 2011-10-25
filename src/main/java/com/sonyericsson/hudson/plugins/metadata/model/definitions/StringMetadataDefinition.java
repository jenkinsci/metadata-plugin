package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import hudson.Extension;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

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
     */
    @DataBoundConstructor
    public StringMetadataDefinition(String name, String description, String defaultValue) {
        super(name, description);
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
    public AbstractMetadataValue createValue(StaplerRequest req, JSONObject jo) {
        StringMetadataValue value = req.bindJSON(StringMetadataValue.class, jo);
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
    public AbstractMetadataValue createValue(StaplerRequest req) {
        return null;
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
