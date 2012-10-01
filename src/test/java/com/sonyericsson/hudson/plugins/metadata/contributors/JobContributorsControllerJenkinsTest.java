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

package com.sonyericsson.hudson.plugins.metadata.contributors;

import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import hudson.matrix.MatrixConfiguration;
import hudson.matrix.MatrixProject;
import hudson.matrix.TextAxis;
import jenkins.model.Jenkins;
import org.jvnet.hudson.test.HudsonTestCase;

//CS IGNORE MagicNumber FOR NEXT 100 LINES. REASON: TestData

/**
 * In-Jenkins tests for {@link JobContributorsController}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public class JobContributorsControllerJenkinsTest extends HudsonTestCase {

    /**
     * Tests that metadata gets put into matrix sub-projects.
     *
     * @throws Exception if so.
     */
    public void testMatrixConfiguration() throws Exception {
        MatrixProject matrix = this.createMatrixProject("myMatrix");
        matrix.getAxes().add(new TextAxis("Test1", "one", "two"));
        matrix.getAxes().add(new TextAxis("Test2", "A", "B"));
        MetadataJobProperty property = new MetadataJobProperty();
        property.addChild(TreeStructureUtil.createPath("hello", "my matrix description", false, false,
                "the", "world", "says"));
        matrix.addProperty(property);
        configRoundtrip(matrix);

        //give the controller some time to work on the separate thread
        Thread.sleep(4000);
        matrix = (MatrixProject)Jenkins.getInstance().getItem("myMatrix");
        property = matrix.getProperty(MetadataJobProperty.class);
        assertNotNull(property);
        MetadataValue path = TreeStructureUtil.getPath(property, "the", "world", "says");
        assertNotNull(path);
        assertEquals("hello", path.getValue());
        for (MatrixConfiguration config : matrix.getActiveConfigurations()) {
            property = config.getProperty(MetadataJobProperty.class);
            assertNotNull(property);
            path = TreeStructureUtil.getPath(property, "the", "world", "says");
            assertNotNull(path);
            assertEquals("hello", path.getValue());
        }
    }
}
