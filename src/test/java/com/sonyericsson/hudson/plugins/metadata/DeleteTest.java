package com.sonyericsson.hudson.plugins.metadata;

import jenkins.model.Jenkins;
import hudson.model.FreeStyleProject;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.HudsonTestCase;

import java.io.File;
import java.text.SimpleDateFormat;

 /**
  * @author christ66
  *         Date: 10/14/13
  *         Time: 7:16 PM
  */
public class DeleteTest extends HudsonTestCase {

     //CS IGNORE MagicNumber FOR NEXT 50 LINES. REASON: TestData

     /**
      * Tests that things are behaving when a job is deleted.
      * ZD-13803
      * @throws Exception if so
      */
    @Bug(13803)
    @Test
    public void testDeleteJob() throws Exception {
        FreeStyleProject freeStyleProject = jenkins.createProject(FreeStyleProject.class, "test");
        freeStyleProject.save();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        System.out.println("Before delete: "
                + sdf.format(new File(freeStyleProject.getRootDir(), "config.xml").lastModified()));
        freeStyleProject.delete();
        Thread.sleep(3 * 1000); // Give it a few seconds to create the file
        System.out.println("After delete: "
                + sdf.format(new File(freeStyleProject.getRootDir(), "config.xml").lastModified()));
        assertFalse("Project should have been deleted", freeStyleProject.getRootDir().exists());
        Jenkins.getInstance().restart();
        Jenkins.getInstance().doReload();
        System.out.println("After reload: "
                + sdf.format(new File(freeStyleProject.getRootDir(), "config.xml").lastModified()));
        assertFalse("Project should have been deleted", freeStyleProject.getRootDir().exists());
        assertFalse("Should not be disabled but instead deleted.", !freeStyleProject.isDisabled());
    }
}
