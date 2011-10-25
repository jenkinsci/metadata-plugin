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

import com.sonyericsson.hudson.plugins.metadata.model.MetadataBuildAction;
import com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import org.junit.Assert;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.SleepBuilder;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

//CS IGNORE MagicNumber FOR NEXT 200 LINES. REASON: test data.

/**
 * Tests for {@link StandardBuildMetadataContributor} and indirect of
 * {@link com.sonyericsson.hudson.plugins.metadata.contributors.BuildContributorsController}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class StandardBuildMetadataContributorTest extends HudsonTestCase {

    /**
     * Tests that standard metadata is attached to a simple FreeStyleBuild.
     *
     * @throws Exception if so.
     */
    public void testSimpleFreeStyle() throws Exception {
        FreeStyleProject project = createFreeStyleProject("test1");
        project.getBuildersList().add(new SleepBuilder(1000));

        FreeStyleBuild build = buildAndAssertSuccess(project);

        MetadataBuildAction action = build.getAction(MetadataBuildAction.class);
        assertNotNull(action);
        StringMetadataValue value = (StringMetadataValue)TreeStructureUtil.getPath(action, "build", "result");
        assertNotNull(value);
        assertEquals(Result.SUCCESS.toString(), value.getValue());
        assertNotNull(TreeStructureUtil.getPath(action, "build", "duration", "display"));
        NumberMetadataValue longValue = (NumberMetadataValue)TreeStructureUtil.getPath(action,
                "build", "duration", "ms");
        assertNotNull(longValue);
        Assert.assertThat(longValue.getValue(), greaterThanOrEqualTo(1000L));
        assertNotNull(TreeStructureUtil.getPath(action, "build", "builtOn"));
    }
}
