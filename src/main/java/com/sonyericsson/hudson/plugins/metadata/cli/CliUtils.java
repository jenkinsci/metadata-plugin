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

import com.sonyericsson.hudson.plugins.metadata.model.MetadataBuildAction;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataNodeProperty;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import org.kohsuke.args4j.CmdLineException;

import java.io.IOException;

/**
 * Common utility functions for the CLI commands.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public abstract class CliUtils {

    /**
     * Status codes to be returned from the cli commands.
     */
    public static enum Status {
        /**
         * Commandline status code indicating {@link NoItemException}. (-1)
         */
        ERR_NO_ITEM(-1),
        /**
         * Commandline status code indicating {@link NoMetadataException}. (-2)
         */
        ERR_NO_METADATA(-2),
        /**
         * Commandline status code indicating illegal commandline argument combinations. (1)
         */
        ERR_BAD_CMD(1),
        /**
         * Commandline status code indicating a
         * {@link com.sonyericsson.hudson.plugins.metadata.model.JsonUtils.ParseException}.
         * (2)
         */
        ERR_BAD_DATA(2),
        /**
         * Commandline status indicating that the metadata could not be saved to disk.
         */
        WARN_NO_SAVE(10);

        private final int code;

        /**
         * Constructor.
         * @param code the error code.
         * @see #code()
         */
        Status(int code) {
            this.code = code;
        }

        /**
         * The error code to return from the command.
         *
         * @return the error code.
         */
        public int code() {
            return code;
        }
    }

    /**
     * Finds the container that has been selected on the commandline.
     *
     * @param node            the node parameter.
     * @param job             the job parameter
     * @param build           the build parameter
     * @param createContainer create the container when it doesn't exist instead of failing, if the specified item
     *                        doesn't exist it will still fail..
     * @return the container if one is found.
     *
     * @throws CmdLineException    if the combination of arguments are bad.
     * @throws NoItemException     if the requested item (node, job or build) couldn't be found.
     * @throws NoMetadataException if the item doesn't contain any metadata.
     * @throws java.io.IOException if a metadata container needed to be added and it failed to do so.
     */
    //CS IGNORE RedundantThrows FOR NEXT 3 LINES. REASON: NometadataException listed twice but it's not.
    public static MetadataContainer<MetadataValue> getContainer(String node, String job, Integer build,
                                                                boolean createContainer)
            throws CmdLineException, NoItemException, NoMetadataException, IOException {

        if ((node == null || node.isEmpty()) && (job == null || job.isEmpty())) {
            throw new CmdLineException(null, "You must provide either a job or a node.");
        }
        if (build != null && (job == null || job.isEmpty())) {
            throw new CmdLineException(null, "You must provide a job for this build.");
        }
        MetadataContainer<MetadataValue> container = null;
        if (node != null && !node.isEmpty()) {
            Node theNode = Hudson.getInstance().getNode(node);
            if (theNode == null) {
                throw new NoItemException("No node with the name " + node + " exists on this server.");
            }
            DescribableList<NodeProperty<?>, NodePropertyDescriptor> properties = theNode.getNodeProperties();
            if (properties == null) {
                throw new NoMetadataException("The node " + node + " has no associated properties.");
            }
            MetadataNodeProperty property = properties.get(MetadataNodeProperty.class);
            if (property != null) {
                container = property;
            } else {
                if (createContainer) {
                    container = MetadataNodeProperty.MetadataNodePropertyDescriptor.instanceFor(theNode);
                } else {
                    throw new NoMetadataException("The node " + node + " has no associated metadata.");
                }
            }
        } else if (job != null && !job.isEmpty()) {
            TopLevelItem item = Hudson.getInstance().getItem(job);
            if (item != null && item instanceof AbstractProject) {
                AbstractProject project = (AbstractProject)item;
                if (build != null && build >= 0) {
                    Run buildByNumber = project.getBuildByNumber(build);
                    if (buildByNumber != null) {
                        MetadataBuildAction action = buildByNumber.getAction(MetadataBuildAction.class);
                        if (action != null) {
                            container = action;
                        } else if (createContainer) {
                            action = new MetadataBuildAction(buildByNumber);
                            buildByNumber.addAction(action);
                            container = action;
                        } else {
                            throw new NoMetadataException("Build #" + build + " of job "
                                    + job + " has no associated metadata.");
                        }
                    } else {
                        throw new NoItemException("There is no build #" + build + " for job " + job);
                    }
                } else {
                    MetadataJobProperty property = (MetadataJobProperty)project.getProperty(MetadataJobProperty.class);
                    if (property != null) {
                        container = property;
                    } else if (createContainer) {
                        container = MetadataJobProperty.MetaDataJobPropertyDescriptor.instanceFor(project);
                    } else {
                        throw new NoMetadataException("Job " + job + " has no associated metadata.");
                    }
                }
            } else {
                throw new NoItemException("No job with the name " + job + " exists on this server.");
            }
        }

        return container;
    }

    /**
     * Utility constructor.
     */
    private CliUtils() {

    }

    /**
     * Exception thrown when the user has selected something that doesn't exist.
     */
    public static class NoItemException extends Exception {

        /**
         * Default constructor.
         */
        public NoItemException() {
        }

        /**
         * Standard Constructor.
         *
         * @param message the message.
         * @see Exception#Exception(String)
         */
        public NoItemException(String message) {
            super(message);
        }

        /**
         * Standard Constructor.
         *
         * @param message the message.
         * @param cause   the cause.
         * @see Exception#Exception(String, Throwable)
         */
        public NoItemException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Standard Constructor.
         *
         * @param cause the cause.
         * @see Exception#Exception(Throwable)
         */
        public NoItemException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Exception thrown when a user has selected something that doesn't have any metadata.
     */
    public static class NoMetadataException extends Exception {
        /**
         * Default constructor.
         */
        public NoMetadataException() {
        }

        /**
         * Standard Constructor.
         *
         * @param message the message.
         * @see Exception#Exception(String)
         */
        public NoMetadataException(String message) {
            super(message);
        }

        /**
         * Standard Constructor.
         *
         * @param message the message.
         * @param cause   the cause.
         * @see Exception#Exception(String, Throwable)
         */
        public NoMetadataException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Standard Constructor.
         *
         * @param cause the cause.
         * @see Exception#Exception(Throwable)
         */
        public NoMetadataException(Throwable cause) {
            super(cause);
        }
    }
}
