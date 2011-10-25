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
package com.sonyericsson.hudson.plugins.metadata.contributors;

import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.XmlFile;
import hudson.model.AbstractProject;
import hudson.model.Saveable;
import hudson.model.User;
import hudson.model.listeners.SaveableListener;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
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
     * @param project the project.
     */
    synchronized void setCurrentProject(AbstractProject project) {
        this.currentProject = project;
    }

    /**
     * If the provided project is the current project being handled by the thread.
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
            MetadataJobProperty property = (MetadataJobProperty)project.getProperty(MetadataJobProperty.class);
            if (property == null) {
                property = new MetadataJobProperty();
                try {
                    project.addProperty(property);
                } catch (IOException e) {
                    logger.severe("Failed to add the MetadataJobProperty to the project " + project);
                    return;
                }
            }

            //TODO add a translatable description
            TreeNodeMetadataValue[] tree = TreeStructureUtil.createTreePath("", "job-info", "last-saved");
            TreeNodeMetadataValue jobInfo = tree[0];
            TreeNodeMetadataValue lastSaved = tree[1];
            TreeStructureUtil.addValue(lastSaved, new Date(), "", "time");
            TreeStructureUtil.addValue(lastSaved, currentUser.getDisplayName(), "", "user", "display-name");
            TreeStructureUtil.addValue(lastSaved, currentUser.getFullName(), "", "user", "full-name");
            //TODO lower the level
            logger.info("Adding standard generated metadata");
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
                controller.setCurrentProject(null);
            }
        }
    }
}
