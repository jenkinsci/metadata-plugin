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

import com.sonyericsson.hudson.plugins.metadata.MockUtils;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataBuildAction;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataNodeProperty;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import hudson.matrix.MatrixProject;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;
import hudson.model.JobProperty;
import hudson.model.Node;
import hudson.model.TopLevelItem;
import hudson.util.DescribableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.args4j.CmdLineException;
import org.mockito.Matchers;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.isA;

//CS IGNORE MagicNumber FOR NEXT 400 LINES. REASON: TestData

/**
 * Tests for {@link CliUtils}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Hudson.class)
public class CliUtilsTest {

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)} with no job or node.
     *
     * @throws Exception if so.
     */
    @Test(expected = CmdLineException.class)
    public void testGetContainerBadParams() throws Exception {
        CliUtils.getContainer(null, null, null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)} with no job but a node and a build.
     *
     * @throws Exception if so.
     */
    @Test(expected = CmdLineException.class)
    public void testGetContainerBadParams2() throws Exception {
        CliUtils.getContainer("Test", null, 1, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)} with no job or node as empty strings.
     *
     * @throws Exception if so.
     */
    @Test(expected = CmdLineException.class)
    public void testGetContainerBadParams3() throws Exception {
        CliUtils.getContainer("", "", null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)} with no job but a node and a build as empty
     * strings.
     *
     * @throws Exception if so.
     */
    @Test(expected = CmdLineException.class)
    public void testGetContainerBadParams4() throws Exception {
        CliUtils.getContainer("Test", "", 1, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a non existent node.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoItemException.class)
    public void testGetContainerNoNode() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        when(hudson.getNode("fake")).thenReturn(null);
        CliUtils.getContainer("fake", null, null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent node but no metadata.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoMetadataException.class)
    public void testGetContainerNoMetadataOnNode() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        Node node = mock(Node.class);
        when(hudson.getNode("theNode")).thenReturn(node);
        DescribableList describableList = mock(DescribableList.class);
        when(node.getNodeProperties()).thenReturn(describableList);
        when(describableList.get(MetadataNodeProperty.class)).thenReturn(null);
        CliUtils.getContainer("theNode", null, null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a node.
     *
     * @throws Exception if so.
     */
    @Test()
    public void testGetContainerOnNode() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        Node node = mock(Node.class);
        when(hudson.getNode("theNode")).thenReturn(node);
        DescribableList describableList = mock(DescribableList.class);
        when(node.getNodeProperties()).thenReturn(describableList);
        MetadataNodeProperty property = mock(MetadataNodeProperty.class);
        when(describableList.get(MetadataNodeProperty.class)).thenReturn(property);
        MetadataParent container = CliUtils.getContainer("theNode", null, null, false);
        assertNotNull(container);
        assertSame(property, container);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent node but no metadata.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoMetadataException.class)
    public void testGetContainerNoPropertiesOnNode() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        Node node = mock(Node.class);
        when(hudson.getNode("theNode")).thenReturn(node);
        when(node.getNodeProperties()).thenReturn(null);
        CliUtils.getContainer("theNode", null, null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent node but no metadata.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoMetadataException.class)
    public void testGetContainerNoPropertiesOnNodeCreate() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        Node node = mock(Node.class);
        when(hudson.getNode("theNode")).thenReturn(node);
        when(node.getNodeProperties()).thenReturn(null);
        CliUtils.getContainer("theNode", null, null, true);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent node and no metadata that
     * should be created.
     *
     * @throws Exception if so.
     */
    @Test
    public void testGetContainerNoMetadataOnNodeCreate() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        Node node = mock(Node.class);
        when(hudson.getNode("theNode")).thenReturn(node);
        DescribableList describableList = mock(DescribableList.class);
        when(node.getNodeProperties()).thenReturn(describableList);
        when(describableList.get(MetadataNodeProperty.class)).thenReturn(null);
        MetadataParent container = CliUtils.getContainer("theNode", null, null, true);
        assertNotNull(container);

        verify(describableList).add(same(container));
        assertSame(node, Whitebox.getInternalState(container, "node"));
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a non existent job.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoItemException.class)
    public void testGetContainerNoJob() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        when(hudson.getItem("fake")).thenReturn(null);
        CliUtils.getContainer(null, "fake", null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a job.
     *
     * @throws Exception if so.
     */
    @Test
    public void testGetContainerJob() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        FreeStyleProject project = mock(FreeStyleProject.class);
        when(hudson.getItem("fake")).thenReturn(project);
        MetadataJobProperty property = mock(MetadataJobProperty.class);
        when(project.getProperty(MetadataJobProperty.class)).thenReturn(property);
        MetadataParent<MetadataValue> container = CliUtils.getContainer(null, "fake", null, false);
        assertNotNull(container);
        assertSame(property, container);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent item that is not a job.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoItemException.class)
    public void testGetContainerItemButNoJob() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        TopLevelItem itemMock = mock(TopLevelItem.class);
        when(hudson.getItem("fake")).thenReturn(itemMock);
        CliUtils.getContainer(null, "fake", null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent job but no metadata.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoMetadataException.class)
    public void testGetContainerNoMetadataOnJob() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        FreeStyleProject project = mock(FreeStyleProject.class);
        when(hudson.getItem("theJob")).thenReturn(project);
        when(project.getProperty(MetadataJobProperty.class)).thenReturn(null);

        CliUtils.getContainer(null, "theJob", null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent job but no metadata.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoMetadataException.class)
    public void testGetContainerNoMetadataOnMatrixJob() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        MatrixProject project = mock(MatrixProject.class);
        when(hudson.getItem("theJob")).thenReturn(project);
        when(project.getProperty(MetadataJobProperty.class)).thenReturn(null);

        CliUtils.getContainer(null, "theJob", null, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent job and no metadata that
     * should be created.
     *
     * @throws Exception if so.
     */
    @Test
    public void testGetContainerNoMetadataOnJobCreate() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        FreeStyleProject project = mock(FreeStyleProject.class);
        when(hudson.getItem("theJob")).thenReturn(project);
        when(project.getProperty(MetadataJobProperty.class)).thenReturn(null);
        MetadataParent<MetadataValue> container = CliUtils.getContainer(null, "theJob", null, true);
        assertNotNull(container);

        verify(project).addProperty(Matchers.<JobProperty>same((JobProperty)container));
        assertSame(project, Whitebox.getInternalState(container, "owner"));
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent build but no metadata.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoMetadataException.class)
    public void testGetContainerNoMetadataOnBuild() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        FreeStyleProject project = mock(FreeStyleProject.class);
        when(hudson.getItem("theJob")).thenReturn(project);
        FreeStyleBuild build = mock(FreeStyleBuild.class);
        when(project.getBuildByNumber(1)).thenReturn(build);
        when(build.getAction(MetadataBuildAction.class)).thenReturn(null);


        CliUtils.getContainer(null, "theJob", 1, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent build but no metadata.
     *
     * @throws Exception if so.
     */
    @Test()
    public void testGetContainerOnBuild() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        FreeStyleProject project = mock(FreeStyleProject.class);
        when(hudson.getItem("theJob")).thenReturn(project);
        FreeStyleBuild build = mock(FreeStyleBuild.class);
        when(project.getBuildByNumber(1)).thenReturn(build);
        MetadataBuildAction action = mock(MetadataBuildAction.class);
        when(build.getAction(MetadataBuildAction.class)).thenReturn(action);


        MetadataParent<MetadataValue> container = CliUtils.getContainer(null, "theJob", 1, false);
        assertNotNull(container);
        assertSame(action, container);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}  with a existent build but no metadata.
     *
     * @throws Exception if so.
     */
    @Test(expected = CliUtils.NoItemException.class)
    public void testGetContainerNoBuild() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        FreeStyleProject project = mock(FreeStyleProject.class);
        when(hudson.getItem("theJob")).thenReturn(project);
        when(project.getBuildByNumber(10)).thenReturn(null);

        CliUtils.getContainer(null, "theJob", 10, false);
    }

    /**
     * Tests {@link CliUtils#getContainer(String, String, Integer, boolean)}
     * with a existent job build no metadata, that should be created.
     *
     * @throws Exception if so.
     */
    @Test()
    public void testGetContainerNoMetadataOnBuildCreate() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        FreeStyleProject project = mock(FreeStyleProject.class);
        when(hudson.getItem("theJob")).thenReturn(project);
        FreeStyleBuild build = mock(FreeStyleBuild.class);
        when(project.getBuildByNumber(1)).thenReturn(build);
        when(build.getAction(MetadataBuildAction.class)).thenReturn(null);

        MetadataParent container = CliUtils.getContainer(null, "theJob", 1, true);
        assertNotNull(container);
        verify(build).addAction(isA(MetadataBuildAction.class));
    }

}
