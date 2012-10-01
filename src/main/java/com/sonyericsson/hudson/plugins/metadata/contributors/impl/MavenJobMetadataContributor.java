/*
 * The MIT License
 *
 * Copyright 2012 Sony Mobile Communications AB. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sonyericsson.hudson.plugins.metadata.contributors.impl;

import com.sonyericsson.hudson.plugins.metadata.contributors.JobMetadataContributor;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.Extension;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.ModuleName;
import hudson.model.AbstractProject;

import java.util.LinkedList;
import java.util.List;

/**
 * Job Contributor for Maven projects.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
@Extension
public class MavenJobMetadataContributor extends JobMetadataContributor {

    @Override
    public List<MetadataValue> getMetaDataFor(AbstractProject job) {
        if (job instanceof MavenModule) {
            MavenModule module = (MavenModule)job;
            return getModuleData(module);
        } else if (job instanceof MavenModuleSet) {
            MavenModuleSet p = (MavenModuleSet)job;
            MavenModule module = p.getRootModule();
            return getModuleData(module);
        }
        return null;
    }

    /**
     * Creates metadata about a maven module.
     *
     * @param module the module to do
     * @return the metadata.
     */
    private List<MetadataValue> getModuleData(MavenModule module) {
        ModuleName moduleName = module.getModuleName();
        TreeNodeMetadataValue[] path = TreeStructureUtil.createTreePath(null, "maven", "module");
        TreeNodeMetadataValue moduleNode = path[1];
        TreeStructureUtil.addValue(moduleNode, moduleName.groupId, "", "groupId");
        TreeStructureUtil.addValue(moduleNode, moduleName.artifactId, "", "artifactId");
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        list.add(path[0]);
        return list;
    }
}
