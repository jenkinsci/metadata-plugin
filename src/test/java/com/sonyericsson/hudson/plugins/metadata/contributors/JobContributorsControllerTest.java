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

import com.sonyericsson.hudson.plugins.metadata.model.MetadataParent;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.model.AbstractProject;
import hudson.model.User;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link JobContributorsController}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public class JobContributorsControllerTest {

    //CS IGNORE LineLength FOR NEXT 5 LINES. REASON: JavaDoc

    /**
     * Tests {@link JobContributorsController.SaveableOperation#cleanGeneratedValues(com.sonyericsson.hudson.plugins.metadata.model.MetadataParent)}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testCleanGeneratedValues() throws Exception {
        TreeNodeMetadataValue[] treePath = TreeStructureUtil.createTreePath("generated", "root", "this", "is");
        treePath[0].setGenerated(false);
        TreeStructureUtil.getPath(treePath[0], "this").setGenerated(false);
        TreeStructureUtil.addValue(treePath[1], "Bouyah", "no description", "generated");
        treePath[0].addChild(new StringMetadataValue("keep", "valid"));
        TreeStructureUtil.addValue(treePath[0], "too", "", false, "and", "keep", "this");
        TreeStructureUtil.addValue(treePath[0], "yes", "no description", "remove");
        System.out.println(TreeStructureUtil.prettyPrint(treePath[0], ""));
        JobContributorsController.SaveableOperation operation = new JobContributorsController.SaveableOperation(
                mock(JobContributorsController.class), mock(AbstractProject.class), mock(User.class));
        Method cleanGeneratedValues = PowerMockito.method(JobContributorsController.SaveableOperation.class,
                "cleanGeneratedValues", MetadataParent.class);
        cleanGeneratedValues.invoke(operation, treePath[0]);

        assertNull(TreeStructureUtil.getPath(treePath[0], "this", "is"));
        assertNotNull(TreeStructureUtil.getPath(treePath[0], "this"));
        assertNotNull(TreeStructureUtil.getPath(treePath[0], "keep"));
        assertNotNull(TreeStructureUtil.getPath(treePath[0], "and", "keep", "this"));
        assertNull(TreeStructureUtil.getPath(treePath[0], "remove"));
    }
}
