package com.sonyericsson.hudson.plugins.metadata.model.definitions;

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import hudson.Extension;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * A metadata definition of the type Number..
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class NumberMetadataDefinition extends AbstractMetadataDefinition {

    private long defaultValue;

    /**
     * Standard Constructor.
     *
     * @param name         the name.
     * @param description  the description.
     * @param defaultValue the default value for this definition
     */
    @DataBoundConstructor
    public NumberMetadataDefinition(String name, String description, long defaultValue) {
        super(name, description);
        this.defaultValue = defaultValue;
    }

    /**
     * Standard Constructor.
     *
     * @param name the name.
     * @param defaultValue the default value.
     */
    public NumberMetadataDefinition(String name, long defaultValue) {
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
    public Long getDefaultValue() {
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
    public static class NumberMetaDataDefinitionDescriptor extends
            AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.NumberMetadataDefinition_DisplayName();
        }
          /**
         * Form validation for the value. It will try to parse the value to a long.
         *
         * @param value   the value.
         * @return {@link hudson.util.FormValidation#ok()} if the value can be parsed to a long.
         */
        public FormValidation doCheckNumberValue(@QueryParameter ("value") final String value) {
            try {
                Long.parseLong(value);
                return FormValidation.ok();
            } catch (NumberFormatException e) {
                return FormValidation.error(hudson.model.Messages.Hudson_NotANumber());
            }
        }
    }
}
