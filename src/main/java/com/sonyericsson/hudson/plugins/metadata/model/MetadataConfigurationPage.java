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

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.definitions.AbstractMetadataDefinition;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.RootAction;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A configuration page for setting preset metadata definitions that end up
 * on the job configuration page.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
@Extension
public class MetadataConfigurationPage implements RootAction {
    private static final String URL_NAME = "MetaDataConfiguration";

    /**
     * Standard DataBound Constructor.
     *
     * @param definitions the meta data definitions.
     */
    @DataBoundConstructor
    public MetadataConfigurationPage(List<AbstractMetadataDefinition> definitions) {
        PluginImpl.getInstance().setDefinitions(definitions);
    }

    /**
     * Default constructor. <strong>Do not use unless you are a serializer.</strong>
     */
    public MetadataConfigurationPage() {
    }

    @Override
    public String getIconFileName() {
        return "clock.png";
    }

    @Override
    public String getDisplayName() {
        return Messages.ConfigurationPage_DisplayName();
    }

    @Override
    public String getUrlName() {
        return URL_NAME;
    }

    /**
     * All registered metadata definition descriptors. To be used by a hetero-list.
     *
     * @param request the current http request.
     * @return the list of descriptors.
     */
    public List<AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor>
    getDefinitionDescriptors(StaplerRequest request) {
        List<AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor> list =
                new LinkedList<AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor>();
        ExtensionList<AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor> extensionList =
                Hudson.getInstance().getExtensionList(
                        AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor.class);
        for (AbstractMetadataDefinition.AbstractMetaDataDefinitionDescriptor d : extensionList) {
            list.add(d);
        }
        return list;
    }

    /**
     * The list of preset metadata definitions.
     *
     * @return the list.
     */
    public List<AbstractMetadataDefinition> getDefinitions() {
        return PluginImpl.getInstance().getDefinitions();
    }

    /**
     * Save the metadata entered on the configuration page.
     *
     * @param request  the current http request.
     * @param response the response from this operation.
     * @throws ServletException         if there is a problem in getting the submitted form.
     * @throws Descriptor.FormException if there is a problem creating the descriptor instances
     * @throws IOException              if there is a problem in the save or redirect.
     */
    public void doConfigureSubmit(StaplerRequest request, StaplerResponse response)
            throws ServletException, Descriptor.FormException, IOException {
        JSONObject formData = request.getSubmittedForm();
        List<AbstractMetadataDefinition> list = Descriptor.
                newInstancesFromHeteroList(request, formData, "definitions", getDefinitionDescriptors(request));
        PluginImpl.getInstance().setDefinitions(list);
        PluginImpl.getInstance().save();
        response.sendRedirect("..");
    }

}
