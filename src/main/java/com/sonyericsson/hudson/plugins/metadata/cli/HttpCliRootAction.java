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
package com.sonyericsson.hudson.plugins.metadata.cli;

import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.PluginImpl;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import hudson.Extension;
import hudson.model.RootAction;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Http interface for the CLI commands.
 * <p/>
 * As some systems prefer to have a bit more intimate call API towards other systems than what {@link
 * hudson.cli.CLICommand}s provide. This action exposes {@link GetMetadataCommand} and {@link UpdateMetadataCommand} to
 * a standard HTTP post or GET.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@Extension
@SuppressWarnings("unused")
public class HttpCliRootAction implements RootAction {

    /**
     * The URL to this action.
     */
    protected static final String URL = "metadata-httpcli";

    /**
     * The JSON content type to use when writing responses.
     */
    protected static final String CONTENT_TYPE = "application/json";

    @Override
    public String getIconFileName() {
        //Should not be displayed on the main page.
        return null;
    }

    @Override
    public String getDisplayName() {
        //Should not be displayed on the main page.
        return null;
    }

    @Override
    public String getUrlName() {
        return URL;
    }

    /**
     * Update the metadata in a container. Valid http parameters are : data and (node or job or (job and build)). Ex:
     * <code> http://jenkinshost/metadata-httpcli/update?node=bobby&data={metadata-type: "metadata-string" name="owner"
     * value="bobby"} </code> would update the metadata in on the node named "bobby" with a metadata string with the
     * name owner and the value "bobby".
     *
     * @param request  the request.
     * @param response the response
     * @throws Exception if something unknown happened.
     * @see UpdateMetadataCommand
     */
    @SuppressWarnings("unused")
    public void doUpdate(StaplerRequest request, StaplerResponse response) throws Exception {
        ContainerParams params = new ContainerParams(request, response).invoke();
        if (!params.isUsable()) {
            return;
        }
        MetadataContainer<MetadataValue> container;
        String dataDocument = request.getParameter("data");
        if (dataDocument == null || dataDocument.isEmpty()) {
            sendError(CliUtils.Status.ERR_BAD_CMD, "No metadata provided!", response);
            return;
        }
        try {
            container = CliUtils.getContainer(params.getNode(), params.getJob(), params.getBuild(), true);
        } catch (CmdLineException e) {
            sendError(CliUtils.Status.ERR_BAD_CMD, e.getMessage(), response);
            return;
        } catch (CliUtils.NoItemException e) {
            sendError(CliUtils.Status.ERR_NO_ITEM, e.getMessage(), response);
            return;
        } catch (CliUtils.NoMetadataException e) {
            sendError(CliUtils.Status.ERR_NO_METADATA, e.getMessage(), response);
            return;
        }

        if (container != null) {
            container.getACL().checkPermission(PluginImpl.UPDATE_METADATA);
            JSON json = JSONSerializer.toJSON(dataDocument);
            try {
                List<MetadataValue> values = JsonUtils.toValues(json);
                Collection<MetadataValue> leftOvers = container.addChildren(values);
                response.setContentType(CONTENT_TYPE);
                if (leftOvers != null && !leftOvers.isEmpty()) {
                    JSONObject jsonMessage = new JSONObject();
                    jsonMessage.put("type", "warning");
                    jsonMessage.put("errorCode", 0);
                    jsonMessage.put("message", "Since the engineer who implemented this command is lazy "
                            + "there is yet a replace option, "
                            + "the following data could not be replaced because it already existed.");
                    jsonMessage.put("leftOvers", JsonUtils.toJson(leftOvers));
                    response.getOutputStream().print(jsonMessage.toString());
                } else {
                    JSONObject jsonMessage = new JSONObject();
                    jsonMessage.put("type", "ok");
                    jsonMessage.put("errorCode", 0);
                    jsonMessage.put("message", "OK");
                    response.getOutputStream().print(jsonMessage.toString());
                }
                container.save();
            } catch (JsonUtils.ParseException e) {
                sendError(CliUtils.Status.ERR_BAD_DATA, e.getMessage(), response);
            } catch (IOException ex) {
                sendError(CliUtils.Status.WARN_NO_SAVE,
                        "Warning Could not save the data to disk, the data is added in memory.\n"
                                + ex.getMessage(), response);
            }
        } else {
            sendError(CliUtils.Status.ERR_BAD_CMD, "No metadata container found.", response);
        }
    }

    /**
     * Get the metadata in a container. Valid http parameters are : node or job or (job and build). Ex:
     * <code>http://jenkinshost/metadata-httpcli/get?node=bobby</code> would give you the metadata in JSON format for
     * the node named "bobby"
     *
     * @param request  the request.
     * @param response the response
     * @throws Exception if something unknown happened.
     * @see GetMetadataCommand
     */
    @SuppressWarnings("unused")
    public void doGet(StaplerRequest request, StaplerResponse response) throws Exception {
        ContainerParams params = new ContainerParams(request, response).invoke();
        if (!params.isUsable()) {
            return;
        }
        MetadataContainer<MetadataValue> container;
        try {
            container = CliUtils.getContainer(params.getNode(), params.getJob(), params.getBuild(), false);
        } catch (CmdLineException e) {
            sendError(CliUtils.Status.ERR_BAD_CMD, e.getMessage(), response);
            return;
        } catch (CliUtils.NoItemException e) {
            sendError(CliUtils.Status.ERR_NO_ITEM, e.getMessage(), response);
            return;
        } catch (CliUtils.NoMetadataException e) {
            sendError(CliUtils.Status.ERR_NO_METADATA, e.getMessage(), response);
            return;
        }
        if (container != null) {
            container.getACL().checkPermission(PluginImpl.READ_METADATA);
            JSON json = container.toJson();
            response.setContentType(CONTENT_TYPE);
            response.getOutputStream().print(json.toString());
        } else {
            sendError(CliUtils.Status.ERR_BAD_CMD, "No metadata container found.", response);
        }
    }

    /**
     * Sends an error message in json format on the response object. The http response will still be 200 OK since the
     * J2EE API doesn't allow content for the other status codes.
     *
     * @param status   the status code.
     * @param message  the human readable message.
     * @param response the response object to send to.
     * @throws IOException if so.
     */
    private static void sendError(CliUtils.Status status, String message, StaplerResponse response) throws IOException {
        JSONObject json = new JSONObject();
        json.put("type", "error");
        json.put("errorCode", status.code());
        json.put("errorName", status.name());
        json.put("message", message);
        response.setContentType(CONTENT_TYPE);
        response.getOutputStream().print(json.toString());
    }

    /**
     * Helper class for the common HTTP parameters. Actually nicely auto generated by the IDE.
     */
    static class ContainerParams {
        private boolean myResult;
        private StaplerRequest request;
        private StaplerResponse response;
        private String node;
        private String job;
        private Integer build;

        /**
         * Standard constructor.
         *
         * @param request  the request
         * @param response the response for sending bad param results.
         */
        public ContainerParams(StaplerRequest request, StaplerResponse response) {
            this.request = request;
            this.response = response;
        }

        /**
         * Are there some usable parameters?
         *
         * @return true if so.
         */
        boolean isUsable() {
            return myResult;
        }

        /**
         * The node param.
         *
         * @return the node.
         */
        public String getNode() {
            return node;
        }

        /**
         * The Job param.
         *
         * @return the job.
         */
        public String getJob() {
            return job;
        }

        /**
         * The build param.
         *
         * @return the build.
         */
        public Integer getBuild() {
            return build;
        }

        /**
         * Get the parameters.
         *
         * @return itself.
         *
         * @throws IOException if so.
         */
        public ContainerParams invoke() throws IOException {
            node = request.getParameter("node");
            job = request.getParameter("job");
            build = null;
            String buildStr = request.getParameter("build");
            if (buildStr != null) {
                try {
                    build = Integer.parseInt(buildStr);
                } catch (NumberFormatException e) {
                    sendError(CliUtils.Status.ERR_BAD_CMD, "Not a number: " + buildStr, response);
                    myResult = false;
                    return this;
                }
            }
            myResult = true;
            return this;
        }
    }
}
