/*
 *  The MIT License
 *
 *  Copyright 2013 Sony Mobile Communications AB. All rights reserved.
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

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.JobProperty;
import hudson.model.listeners.ItemListener;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This class listens for when a project is created and adds some initial Metadata to the project.
 *
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
@Extension
public class JobCreatedController extends ItemListener {

    private static final Logger logger = Logger.getLogger(JobCreatedController.class.getName());

    @Override
    public void onCreated(Item item) {
        if (item instanceof AbstractProject) {
            AbstractProject project = (AbstractProject)item;
            JobProperty metadataJobProperty = project.getProperty(MetadataJobProperty.class);
            if (metadataJobProperty == null) {
                try {
                    project.addProperty(new MetadataJobProperty());
                } catch (IOException e) {
                    logger.info("Could not create initial MetadataJobProperty for " + project.getName());
                }
            }
        }
    }
}
