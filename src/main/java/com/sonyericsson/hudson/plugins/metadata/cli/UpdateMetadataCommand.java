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

import com.sonyericsson.hudson.plugins.metadata.Messages;
import com.sonyericsson.hudson.plugins.metadata.model.JsonUtils;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.PluginImpl;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import hudson.AbortException;
import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.remoting.Callable;
import hudson.util.IOUtils;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * Command for adding/updating metadata onto stuff.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@Extension
public class UpdateMetadataCommand extends CLICommand {

    //CS IGNORE VisibilityModifier FOR NEXT 30 LINES. REASON: Standard Jenkins Args4J design pattern.

    /**
     * The job argument. The name of the job/project to add data to. If job is not provided {@link #node} must be.
     */
    @Option(name = "-job", usage = "The name of the job to add data to.")
    public String job;

    /**
     * The build argument. The build number of the job to add data to. If build is provided {@link #job} must be.
     */
    @Option(name = "-build", usage = "The build number of the job to add data to.")
    public Integer build;

    /**
     * The node argument. The name of the node/computer to add data to. If node is not provided {@link #job} must be.
     */
    @Option(name = "-node", usage = "The name of the node/computer to add data to.")
    public String node;

    /**
     * The data argument. File or URL to the document containing the data to add. If no data argument is provided it is
     * assumed to come on {@link #stdin}.
     */
    @Option(name = "-data", usage = "File or URL to the document"
            + " containing the data to add.\n"
            + "If no data argument is provided  "
            + "it is assumed to come on stdin")
    public String data;


    @Override
    public String getShortDescription() {
        return Messages.UpdateMetadataCommand_ShortDescription();
    }

    @Override
    protected int run() throws Exception {
        MetadataContainer<MetadataValue> container;
        String dataDocument;
        try {
            dataDocument = loadData();
            container = CliUtils.getContainer(node, job, build, true);
        } catch (CmdLineException e) {
            stderr.println(e.getMessage());
            return CliUtils.Status.ERR_BAD_CMD.code();
        } catch (CliUtils.NoItemException e) {
            stderr.println(e.getMessage());
            return CliUtils.Status.ERR_NO_ITEM.code();
        } catch (CliUtils.NoMetadataException e) {
            stderr.println(e.getMessage());
            return CliUtils.Status.ERR_NO_METADATA.code();
        }

        if (container != null) {
            container.getACL().checkPermission(PluginImpl.UPDATE_METADATA);
            JSON json = JSONSerializer.toJSON(dataDocument);
            try {
                List<MetadataValue> values = JsonUtils.toValues(json);
                Collection<MetadataValue> leftOvers = container.addChildren(values);
                if (leftOvers != null && !leftOvers.isEmpty()) {
                    stderr.println("Since the engineer who implemented this command is lazy "
                            + "there is yet a replace option, "
                            + "the following data on stdout could not be replaced because it already existed.");
                    stdout.println(JsonUtils.toJson(leftOvers).toString());
                }
                container.save();
            } catch (JsonUtils.ParseException e) {
                stderr.println(e.getMessage());
                return CliUtils.Status.ERR_BAD_DATA.code();
            } catch (IOException ex) {
                stderr.println("Warning Could not save the data to disk, the data is added in memory.\n"
                        + ex.getMessage());
                return CliUtils.Status.WARN_NO_SAVE.code();
            }
        } else {
            stderr.println("No metadata container found.");
            return CliUtils.Status.ERR_BAD_CMD.code();
        }
        return 0;
    }

    /**
     * Loads the data from the argument.
     *
     * @return the data hopefully in JSON format.
     *
     * @throws java.io.IOException  if so
     * @throws InterruptedException if so
     */
    private String loadData() throws IOException, InterruptedException {
        if (data == null || data.isEmpty()) {
            return IOUtils.toString(stdin);
        }

        return channel.call(new Callable<String, IOException>() {
            public String call() throws IOException {
                File f = new File(data);
                if (f.exists()) {
                    return FileUtils.readFileToString(f);
                }

                URL url;
                try {
                    url = new URL(data);
                } catch (MalformedURLException e) {
                    throw new AbortException("Unable to find the data " + data);
                }
                InputStream s = url.openStream();
                try {
                    return IOUtils.toString(s);
                } finally {
                    s.close();
                }
            }
        });
    }
}
