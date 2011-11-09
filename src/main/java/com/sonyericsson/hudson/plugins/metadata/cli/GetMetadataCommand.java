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

import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.PluginImpl;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import hudson.Extension;
import hudson.cli.CLICommand;
import net.sf.json.JSON;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

/**
 * Command for listing/viewing data on stuff.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@Extension
public class GetMetadataCommand extends CLICommand {

    //CS IGNORE VisibilityModifier FOR NEXT 25 LINES. REASON: Standard Jenkins Args4J design pattern.

    /**
     * The job argument. The name of the job/project to add data to. If job is not provided {@link #node} must be.
     */
    @Option(name = "-job", usage = "The name of the job to get.")
    public String job;

    /**
     * The build argument. The build number of the job to add data to. If build is provided {@link #job} must be.
     */
    @Option(name = "-build", usage = "The build number of the build to get. Requires -job")
    public Integer build;

    /**
     * The node argument. The name of the node/computer to add data to. If node is not provided {@link #job} must be.
     */
    @Option(name = "-node", usage = "The name of the node/computer to get.")
    public String node;

    @Override
    public String getShortDescription() {
        return "Gets the metadata for a job, build or node.";
    }

    @Override
    protected int run() throws Exception {

        MetadataContainer<MetadataValue> container = null;
        try {
            container = CliUtils.getContainer(node, job, build, false);
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
            container.getACL().checkPermission(PluginImpl.READ_METADATA);
            JSON json = container.toJson();
            stdout.println(json.toString());
        } else {
            stderr.println("No metadata container found.");
            return CliUtils.Status.ERR_BAD_CMD.code();
        }

        return 0;
    }
}
