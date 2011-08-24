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
package com.sonyericsson.hudson.plugins.metadata.contributors.impl;

import com.sonyericsson.hudson.plugins.metadata.contributors.BuildMetaDataContributor;
import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetaDataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.Extension;
import hudson.model.AbstractBuild;

import java.util.LinkedList;
import java.util.List;

/**
 * Provides some standard metadata values to builds about the build.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@Extension
public class StandardBuildMetaDataContributor extends BuildMetaDataContributor {

    @Override
    public List<AbstractMetaDataValue> getMetaDataFor(AbstractBuild build) {
        TreeNodeMetaDataValue buildNode = TreeStructureUtil.createPath(build.getResult().toString(), "",
                "build", "result");
        AbstractMetaDataValue mdv = new NumberMetaDataValue("ms", build.getDuration());
        mdv.setGenerated(true);
        TreeStructureUtil.addValue(buildNode, mdv, "duration");
        mdv = new StringMetaDataValue("display", build.getDurationString());
        mdv.setGenerated(true);
        TreeStructureUtil.addValue(buildNode, mdv, "duration");
        TreeStructureUtil.addValue(buildNode, build.getBuiltOnStr(), null, "builtOn");
        TreeStructureUtil.addValue(buildNode, build.getTime(), null, "scheduled");
        List<AbstractMetaDataValue> values = new LinkedList<AbstractMetaDataValue>();
        values.add(buildNode);
        return values;
    }
}
