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
package com.sonyericsson.hudson.plugins.metadata.model;

import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link MetadataEnvironmentContributor}.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class MetadataEnvironmentContributorTest {

    /**
     * Tests the buildEnvironmentFor method in MetadataEnvironmentContributor
     * Adds 2 tree nodes, each with 2 values.
     * The first tree has exposeAll set to true, the other to false.
     * 1 of the children of each tree has exposedToEnvironment set to true, the other to false.
     * Asserts that the whole first tree and the first child of the second tree are added as variables.
     *
     * @throws Exception if so.
     */
    @Test
    public void testBuildEnvironmentFor() throws Exception {

        TreeNodeMetadataValue allShouldBeExposed = new TreeNodeMetadataValue("allExposed", "");
        allShouldBeExposed.setExposeToEnvironment(true);
        StringMetadataValue exposedParentExposed = new StringMetadataValue(
                "exposedParentExposed", "", "exposedParentExposedValue", true);
        StringMetadataValue notExposedParentExposed = new StringMetadataValue(
                "notExposedParentExposed", "", "notExposedParentExposedValue", false);
        allShouldBeExposed.addChild(exposedParentExposed);
        allShouldBeExposed.addChild(notExposedParentExposed);

        TreeNodeMetadataValue allShouldNotBeExposed = new TreeNodeMetadataValue("notAllExposed", "");
        StringMetadataValue exposedParentNotExposed = new StringMetadataValue(
                "exposedParentNotExposed", "", "exposedParentNotExposedValue", true);
        StringMetadataValue notExposedParentNotExposed = new StringMetadataValue(
                "notExposedParentNotExposed", "", "notExposedParentNotExposedValue", false);
        allShouldBeExposed.addChild(exposedParentExposed);
        allShouldBeExposed.addChild(notExposedParentExposed);
        allShouldNotBeExposed.addChild(exposedParentNotExposed);
        allShouldNotBeExposed.addChild(notExposedParentNotExposed);

        MetadataBuildAction buildAction = new MetadataBuildAction();
        buildAction.addChild(allShouldBeExposed);
        buildAction.addChild(allShouldNotBeExposed);

        AbstractBuild build = mock(AbstractBuild.class);
        when(build.getAction(MetadataBuildAction.class)).thenReturn(buildAction);

        MetadataEnvironmentContributor contributor = new MetadataEnvironmentContributor();
        EnvVars variables = new EnvVars();
        contributor.buildEnvironmentFor(build, variables, null);
        assertEquals(exposedParentExposed.getValue(), variables.get(exposedParentExposed.getEnvironmentName()));
        assertEquals(notExposedParentExposed.getValue(), variables.get(notExposedParentExposed.getEnvironmentName()));
        assertEquals(exposedParentNotExposed.getValue(), variables.get(exposedParentNotExposed.getEnvironmentName()));
        assertEquals(null, variables.get(notExposedParentNotExposed.getEnvironmentName()));

    }
}
