/*
 *  The MIT License
 *
 *  Copyright 2011 Sony Ericsson Mobile Communications. All rights reserved.
 *  Copyright 2012 Sony Mobile Communications AB. All rights reserved.
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
package com.sonyericsson.hudson.plugins.metadata.contributors;

import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.XmlFile;
import hudson.matrix.Combination;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.model.AbstractProject;
import hudson.model.Saveable;
import hudson.model.User;
import hudson.model.listeners.SaveableListener;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The controller for all {@link JobMetadataContributor}s. When a project is saved the controller will ask all
 * contributors for whatever metadata they please.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@Extension
public class JobContributorsController extends SaveableListener {

    private static final Logger logger = Logger.getLogger(JobContributorsController.class.getName());

    /**
     * One thread to rule them all.
     */
    protected static final int SINGLE_THREAD = 1;
    /**
     * And in one second bind them.
     */
    protected static final int SECOND = 1000;
    private ThreadPoolExecutor executor;
    private AbstractProject currentProject;

    /**
     * Default constructor.
     */
    public JobContributorsController() {
        executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(SINGLE_THREAD);
        executor.prestartAllCoreThreads();
        //TODO lower the level
        logger.info("Started");
    }

    @Override
    public void onChange(Saveable o, XmlFile file) {
        if (o instanceof AbstractProject && !isThisTheCurrentProject((AbstractProject)o)) {
            executor.submit(new SaveableOperation(this, (AbstractProject)o, User.current()));
            //TODO lower the level
            logger.info("Submitted to metadata-queue: " + o);
        }
    }

    /**
     * Set by the thread to indicate what project is currently being handled.
     *
     * @param project the project.
     */
    synchronized void setCurrentProject(AbstractProject project) {
        this.currentProject = project;
    }

    /**
     * If the provided project is the current project being handled by the thread.
     *
     * @param project the project to check.
     * @return true if so.
     */
    synchronized boolean isThisTheCurrentProject(AbstractProject project) {
        return currentProject != null && currentProject.equals(project);
    }

    /**
     * The actual operation that will be performed on a separate thread not to deadlock the listeners.
     */
    static class SaveableOperation implements Runnable {
        private JobContributorsController controller;
        private AbstractProject project;
        private User currentUser;

        /**
         * Standard Constructor.
         *
         * @param controller  the controller
         * @param project     the project to augment.
         * @param currentUser the user who updated.
         */
        SaveableOperation(JobContributorsController controller, AbstractProject project, User currentUser) {
            this.controller = controller;
            this.project = project;
            if (currentUser == null) {
                this.currentUser = User.getUnknown();
            } else {
                this.currentUser = currentUser;
            }
        }

        @Override
        public void run() {
            controller.setCurrentProject(project);
            try {
                /*TODO investigate why there is a concurrency issue.
                                        It seems like the project is serialized while this is called or something else.
                */
                Thread.sleep(SECOND);
            } catch (InterruptedException e) {
                logger.finest("interrupted");
            }
            MetadataJobProperty property = getOrCreateProperty();
            if (property == null) {
                return;
            }

            cleanGeneratedValues(property);

            TreeNodeMetadataValue[] tree = TreeStructureUtil.createTreePath("", "job-info", "last-saved");
            TreeNodeMetadataValue jobInfo = tree[0];
            TreeNodeMetadataValue lastSaved = tree[1];
            TreeStructureUtil.addValue(lastSaved, new Date(), "", false, "time");
            TreeStructureUtil.addValue(lastSaved, currentUser.getDisplayName(), "", "user", "display-name");
            TreeStructureUtil.addValue(lastSaved, currentUser.getFullName(), "", "user", "full-name");
            if (project instanceof MatrixConfiguration) {
                logger.log(Level.FINER, "Adding matrix combination data for {0}", project);
                MatrixConfiguration configuration = (MatrixConfiguration)project;
                TreeNodeMetadataValue[] path = TreeStructureUtil.createTreePath("", "matrix", "combination");
                TreeNodeMetadataValue matrixNode = path[0];
                TreeNodeMetadataValue combinationNode = path[1];
                Combination combination = configuration.getCombination();
                //ToString version of the combination in job-info.matrix.combination.value
                TreeStructureUtil.addValue(combinationNode, combination.toString(',', ':'), "", "value");
                //Each axis in job-info.matrix.combination.[name]=[value]
                for (Map.Entry<String, String> axis : combination.entrySet()) {
                    TreeStructureUtil.addValue(combinationNode, axis.getValue(), "", "axis", axis.getKey());
                }
                jobInfo.addChild(matrixNode);
            }
            logger.finer("Adding standard generated metadata");
            property.addChild(jobInfo);

            ExtensionList<JobMetadataContributor> contributors = JobMetadataContributor.all();
            for (JobMetadataContributor contributor : contributors) {
                List<MetadataValue> dataFor = contributor.getMetaDataFor(project);
                if (dataFor != null && !dataFor.isEmpty()) {
                    Collection<MetadataValue> leftover = property.addChildren(dataFor);
                    logger.warning("Failed to add the following contributor's[" + contributor + "] metadata to "
                            + project + "\n"
                            + TreeStructureUtil.prettyPrint(leftover, "\t"));
                }
            }

            try {
                logger.fine("Saving the project.");
                project.save();
            } catch (IOException e) {
                logger.severe("Failed to save the project: " + project);
            } finally {
                if (project instanceof MatrixProject) {
                    MatrixProject matrix = (MatrixProject)project;
                    for (MatrixConfiguration mc : matrix.getActiveConfigurations()) {
                        try {
                            mc.save();
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Failed to save MatrixConfiguration " + mc, e);
                        }
                    }
                }
                controller.setCurrentProject(null);
            }

        }

        /**
         * Finds the metadata property for the current project. If none is found a new gets created.
         *
         * @return the property for the current project.
         */
        private MetadataJobProperty getOrCreateProperty() {
            MetadataJobProperty property = (MetadataJobProperty)project.getProperty(MetadataJobProperty.class);
            if (property == null) {
                if (project.getParent() instanceof AbstractProject) {
                    property = createPropertyFromParent((AbstractProject)project.getParent());
                } else {
                    property = new MetadataJobProperty();
                }
                try {
                    project.addProperty(property);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to add the MetadataJobProperty to the project " + project, e);
                    return null;
                }
            } else if (project.getParent() instanceof AbstractProject) {
                try {
                    project.removeProperty(MetadataJobProperty.class);
                    property = createPropertyFromParent((AbstractProject)project.getParent());
                    project.addProperty(property);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Failed to replace the MetadataJobProperty to the project " + project, e);
                    return null;
                }
            }
            return property;
        }

        /**
         * Creates a new metadata property based on the parent of the one that should have it now.
         *
         * @param parent the parent containing metadata to inherit.
         * @return the property filled with goodies.
         */
        private MetadataJobProperty createPropertyFromParent(AbstractProject parent) {
            MetadataJobProperty property = new MetadataJobProperty();
            MetadataJobProperty parentProps = (MetadataJobProperty)parent.getProperty(MetadataJobProperty.class);
            if (parentProps != null) {
                if (logger.isLoggable(Level.FINER)) {
                    logger.log(Level.FINER, "Parent is a project with metadata, copy all user values"
                            + " from [{0}] to [{1}]",
                            new Object[]{project.getParent(), project});
                }
                property.addChildren(parentProps.getUserValues());
            }
            return property;
        }

        /**
         * Removes all generated values from the property so that it can be refilled. This method is recursive. It also
         * has the assumption that {@link com.sonyericsson.hudson.plugins.metadata.model.MetadataParent#getChildren()}
         * returns a direct reference to the internal collection of children and not a clone.
         *
         * @param parent the parent to clean.
         */
        private void cleanGeneratedValues(MetadataParent<MetadataValue> parent) {
            Collection<MetadataValue> values = parent.getChildren();
            List<MetadataValue> toRemove = new LinkedList<MetadataValue>();
            for (MetadataValue value : values) {
                if (value.isGenerated()) {
                    toRemove.add(value);
                } else if (value instanceof MetadataParent) {
                    cleanGeneratedValues((MetadataParent<MetadataValue>)value);
                }
            }
            values.removeAll(toRemove);
        }
    }
}
