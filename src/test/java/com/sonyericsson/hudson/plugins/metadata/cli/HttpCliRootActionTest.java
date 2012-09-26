/*
 *  The MIT License
 *
 *  Copyright 2011 Sony Ericsson Mobile Communications. All rights reserved.
 *  Copyright 2012 Sony Mobile Communications AB. All rights reserved.
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
import com.sonyericsson.hudson.plugins.metadata.model.MetadataContainer;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeStructureUtil;
import com.sonyericsson.hudson.plugins.metadata.util.ExtensionUtils;
import hudson.model.Hudson;
import hudson.model.Queue;
import hudson.security.ACL;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletOutputStream;
import java.util.Collections;

import static com.sonyericsson.hudson.plugins.metadata.cli.CliResponse.CONTENT_TYPE;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link HttpCliRootAction}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CliUtils.class, Hudson.class, ACL.class, Queue.class, ExtensionUtils.class })
public class HttpCliRootActionTest {

    private MetadataContainer<MetadataValue> container;
    private StaplerRequest request;
    private StaplerResponse response;
    private ServletOutputStream out;
    private HttpCliRootAction action;
    private final String job = "testJob1";
    private String printed;
    private ACL acl;
    private Queue queue;

    /**
     * Do some mocking for all tests.
     *
     * @throws Exception if so and fail.
     */
    @Before
    public void prepareSomeStuff() throws Exception {
        Hudson hudson = MockUtils.mockHudson();
        MockUtils.mockMetadataValueDescriptors(hudson);

        container = mock(MetadataContainer.class);
        acl = PowerMockito.mock(ACL.class);
        when(container.getACL()).thenReturn(acl);
        PowerMockito.mockStatic(Queue.class);
        queue = PowerMockito.mock(Queue.class);
        PowerMockito.when(Queue.getInstance()).thenReturn(queue);

        PowerMockito.mockStatic(CliUtils.class);

        request = mock(StaplerRequest.class);

        response = mock(StaplerResponse.class);
        out = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(out);

        action = new HttpCliRootAction();

        printed = null;
    }

    /**
     * Happy tests for {@link HttpCliRootAction#doUpdate(org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testDoUpdate() throws Exception {
        String value = (new StringMetadataValue("owner", "bobby")).toJson().toString();
        boolean exposed = true;
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("type", "ok");
        expectedJson.put("errorCode", 0);
        expectedJson.put("message", "OK");

        when(request.getParameter(eq("job"))).thenReturn(job);
        when(request.getParameter(eq("data"))).thenReturn(value);
        PowerMockito.when(CliUtils.getContainer(null, job, null, true)).thenReturn(container);

        action.doUpdate(request, response);

        verify(response).setContentType(eq(CONTENT_TYPE));
        verify(out).print(eq(expectedJson.toString()));
    }

    /**
     * Happy tests for {@link HttpCliRootAction#doUpdate(org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)}.
     *
     * @throws Exception if so.
     */
    @Test
    public void testDoUpdateWithReplace() throws Exception {
        StringMetadataValue value = new StringMetadataValue("owner", "bobby");
        String replace = (new StringMetadataValue("owner", "Tomas")).toJson().toString();


        JSONObject expectedJson = new JSONObject();
        expectedJson.put("type", "ok");
        expectedJson.put("errorCode", 0);
        expectedJson.put("message", "OK");

        when(request.getParameter(eq("job"))).thenReturn(job);
        when(request.getParameter(eq("data"))).thenReturn(replace);
        when(request.getParameter(eq("replace"))).thenReturn("true");
        PowerMockito.when(CliUtils.getContainer(null, job, null, true)).thenReturn(container);

        when(container.getChild(eq("owner"))).thenReturn(value);

        action.doUpdate(request, response);

        verify(response).setContentType(eq(CONTENT_TYPE));
        verify(out).print(eq(expectedJson.toString()));
        verify(container).setChild(anyInt(), any(MetadataValue.class));
    }

    /**
     * Double update test for {@link HttpCliRootAction#doUpdate(org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)}.
     * With a tree structure.
     *
     * @throws Exception if so.
     */
    @Test
    public void testDoUpdateTreeWithReplace() throws Exception {
        TreeNodeMetadataValue path = TreeStructureUtil.createPath("Bobby", "description", "owner", "name");
        TreeStructureUtil.addValue(path, "Admin", "What is the owner", "type");
        MetadataJobProperty myContainer = spy(new MetadataJobProperty());
        when(myContainer.getACL()).thenReturn(acl);
        myContainer.addChild(path);

        String replace = TreeStructureUtil.createPath("Tomas", "description", "owner", "name").toJson().toString();


        JSONObject expectedJson = new JSONObject();
        expectedJson.put("type", "ok");
        expectedJson.put("errorCode", 0);
        expectedJson.put("message", "OK");

        when(request.getParameter(eq("job"))).thenReturn(job);
        when(request.getParameter(eq("data"))).thenReturn(replace);
        when(request.getParameter(eq("replace"))).thenReturn("true");
        PowerMockito.when(CliUtils.getContainer(null, job, null, true)).thenReturn(myContainer);

        action.doUpdate(request, response);

        verify(response, atLeastOnce()).setContentType(eq(CONTENT_TYPE));
        verify(out).print(eq(expectedJson.toString()));

        assertEquals(1, myContainer.getChildren().size());
        assertEquals(2, ((TreeNodeMetadataValue)myContainer.getChild("owner")).getChildren().size());
        assertEquals("Tomas", TreeStructureUtil.getPath(myContainer, "owner", "name").getValue());
    }

    /**
     * Happy tests for {@link HttpCliRootAction#doGet(org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)} .
     *
     * @throws Exception if so.
     */
    @Test
    public void testDoGet() throws Exception {

        StringMetadataValue metadataValue = new StringMetadataValue("owner", "bobby");
        TreeNodeMetadataValue node = new TreeNodeMetadataValue("container",
                Collections.<MetadataValue>singletonList(metadataValue));

        JSONObject jsonObject = node.toJson();

        when(request.getParameter(eq("job"))).thenReturn(job);
        PowerMockito.when(CliUtils.getContainer(null, job, null, false)).thenReturn(container);
        when(container.toJson()).thenReturn(jsonObject);


        action.doGet(request, response);

        verify(response).setContentType(eq(CONTENT_TYPE));
        verify(out).print(eq(jsonObject.toString()));
    }


    /**
     * Tests for {@link HttpCliRootAction#doGet(org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)} . When no parameters has been defined.
     *
     * @throws Exception if so.
     */
    @Test
    public void testDoGetNothing() throws Exception {
        String message = "You must provide either a job or a node.";
        PowerMockito.when(CliUtils.getContainer(null, null, null, false))
                .thenThrow(new CmdLineException(null, message));

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                printed = (String)invocation.getArguments()[0];
                return new DoesNothing().answer(invocation);
            }
        }).when(out).print(any(String.class));

        action.doGet(request, response);

        verify(response).setContentType(eq(CONTENT_TYPE));
        verify(out).print(contains(message));

        JSONObject obj = (JSONObject)JSONSerializer.toJSON(printed);
        assertEquals("error", obj.getString("type"));
        assertEquals(CliUtils.Status.ERR_BAD_CMD.code(), obj.getInt("errorCode"));
        assertEquals(CliUtils.Status.ERR_BAD_CMD.name(), obj.getString("errorName"));
    }

    /**
     * Tests for {@link HttpCliRootAction#doGet(org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)} . When a non existing job has been defined.
     *
     * @throws Exception if so.
     */
    @Test
    public void testDoGetNoJob() throws Exception {

        when(request.getParameter(eq("job"))).thenReturn(job);

        PowerMockito.when(CliUtils.getContainer(null, job, null, false))
                .thenThrow(new CliUtils.NoItemException("No job."));

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                printed = (String)invocation.getArguments()[0];
                return new DoesNothing().answer(invocation);
            }
        }).when(out).print(any(String.class));

        action.doGet(request, response);

        verify(response).setContentType(eq(CONTENT_TYPE));

        JSONObject obj = (JSONObject)JSONSerializer.toJSON(printed);
        assertEquals("error", obj.getString("type"));
        assertEquals(CliUtils.Status.ERR_NO_ITEM.code(), obj.getInt("errorCode"));
        assertEquals(CliUtils.Status.ERR_NO_ITEM.name(), obj.getString("errorName"));
    }

    /**
     * Tests for {@link HttpCliRootAction#doGet(org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)} . When a job with no metadata has been defined.
     *
     * @throws Exception if so.
     */
    @Test
    public void testDoGetNoJobMetadata() throws Exception {

        when(request.getParameter(eq("job"))).thenReturn(job);

        PowerMockito.when(CliUtils.getContainer(null, job, null, false))
                .thenThrow(new CliUtils.NoMetadataException("No metadata."));

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                printed = (String)invocation.getArguments()[0];
                return new DoesNothing().answer(invocation);
            }
        }).when(out).print(any(String.class));

        action.doGet(request, response);

        verify(response).setContentType(eq(CONTENT_TYPE));

        JSONObject obj = (JSONObject)JSONSerializer.toJSON(printed);
        assertEquals("error", obj.getString("type"));
        assertEquals(CliUtils.Status.ERR_NO_METADATA.code(), obj.getInt("errorCode"));
        assertEquals(CliUtils.Status.ERR_NO_METADATA.name(), obj.getString("errorName"));
    }
}
