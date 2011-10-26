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
package com.sonyericsson.hudson.plugins.metadata;

import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.DateMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue;
import hudson.ExtensionList;
import hudson.init.InitMilestone;
import hudson.model.Hudson;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Mockito.when;

/**
 * Utility class for doing common mocking tasks.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public final class MockUtils {
    /**
     * Basic {@link hudson.model.Hudson} mocking setup.
     *
     * @return The mocked Hudson object.
     */
    public static Hudson mockHudson() {
        PowerMockito.mockStatic(Hudson.class);
        Hudson instance = PowerMockito.mock(Hudson.class);
        PowerMockito.when(Hudson.getInstance()).thenReturn(instance);
        when(instance.getInitLevel()).thenReturn(InitMilestone.STARTED);
        return instance;
    }

    //CS IGNORE LineLength FOR NEXT 8 LINES. REASON: Javadoc

    /**
     * Creates a "real"
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue.TreeNodeMetaDataValueDescriptor}.
     * And makes sure that it is returned from {@link Hudson#getDescriptorByType(Class)}.
     *
     * @param hudson the mocked hudson object.
     * @param list   the list of all descriptors - used by {@link #mockMetadataValueDescriptors(hudson.model.Hudson)}
     */
    public static void mockTreeNodeMetadataValueDescriptor(Hudson hudson,
                           ExtensionList<AbstractMetadataValue.AbstractMetaDataValueDescriptor> list) {
        TreeNodeMetadataValue.TreeNodeMetaDataValueDescriptor descriptor =
                new TreeNodeMetadataValue.TreeNodeMetaDataValueDescriptor();
        when(hudson.getDescriptorByType(TreeNodeMetadataValue.TreeNodeMetaDataValueDescriptor.class)).
                thenReturn(descriptor);
        if (list != null) {
            list.add(descriptor);
        }
    }

    /**
     * Creates a "real"
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue.StringMetaDataValueDescriptor}.
     * And makes sure that it is returned from {@link Hudson#getDescriptorByType(Class)}.
     *
     * @param hudson the mocked hudson object.
     */
    public static void mockStringMetadataValueDescriptor(Hudson hudson) {
        mockStringMetadataValueDescriptor(hudson, null);
    }

    /**
     * Creates a "real"
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue.StringMetaDataValueDescriptor}.
     * And makes sure that it is returned from {@link Hudson#getDescriptorByType(Class)}.
     *
     * @param hudson the mocked hudson object.
     * @param list   the list of all descriptors - used by {@link #mockMetadataValueDescriptors(hudson.model.Hudson)}
     */
    public static void mockStringMetadataValueDescriptor(Hudson hudson,
                           ExtensionList<AbstractMetadataValue.AbstractMetaDataValueDescriptor> list) {
        StringMetadataValue.StringMetaDataValueDescriptor stringDescriptor =
                new StringMetadataValue.StringMetaDataValueDescriptor();
        when(hudson.getDescriptorByType(StringMetadataValue.StringMetaDataValueDescriptor.class)).
                thenReturn(stringDescriptor);
        if (list != null) {
            list.add(stringDescriptor);
        }
    }

    /**
     * Creates a "real"
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.DateMetadataValue.DateMetaDataValueDescriptor}.
     * And makes sure that it is returned from {@link Hudson#getDescriptorByType(Class)}.
     *
     * @param hudson the mocked hudson object.
     */
    public static void mockDateMetadataValueDescriptor(Hudson hudson) {
        mockDateMetadataValueDescriptor(hudson, null);
    }

    /**
     * Creates a "real"
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.DateMetadataValue.DateMetaDataValueDescriptor}.
     * And makes sure that it is returned from {@link Hudson#getDescriptorByType(Class)}.
     *
     * @param hudson the mocked hudson object.
     * @param list   the list of all descriptors - used by {@link #mockMetadataValueDescriptors(hudson.model.Hudson)}
     */
    public static void mockDateMetadataValueDescriptor(Hudson hudson,
                           ExtensionList<AbstractMetadataValue.AbstractMetaDataValueDescriptor> list) {
        DateMetadataValue.DateMetaDataValueDescriptor descriptor =
                new DateMetadataValue.DateMetaDataValueDescriptor();
        when(hudson.getDescriptorByType(DateMetadataValue.DateMetaDataValueDescriptor.class)).
                thenReturn(descriptor);
        if (list != null) {
            list.add(descriptor);
        }
    }

    /**
     * Creates a "real"
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue.NumberMetaDataValueDescriptor}
     * And makes sure that it is returned from {@link Hudson#getDescriptorByType(Class)}.
     *
     * @param hudson the mocked hudson object.
     */
    public static void mockNumberMetadataValueDescriptor(Hudson hudson) {
        mockNumberMetadataValueDescriptor(hudson, null);
    }

    /**
     * Creates a "real"
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue.NumberMetaDataValueDescriptor}
     * And makes sure that it is returned from {@link Hudson#getDescriptorByType(Class)}.
     *
     * @param hudson the mocked hudson object.
     * @param list   the list of all descriptors - used by {@link #mockMetadataValueDescriptors(hudson.model.Hudson)}
     */
    public static void mockNumberMetadataValueDescriptor(Hudson hudson,
                           ExtensionList<AbstractMetadataValue.AbstractMetaDataValueDescriptor> list) {
        NumberMetadataValue.NumberMetaDataValueDescriptor descriptor =
                new NumberMetadataValue.NumberMetaDataValueDescriptor();
        when(hudson.getDescriptorByType(NumberMetadataValue.NumberMetaDataValueDescriptor.class)).
                thenReturn(descriptor);
        if (list != null) {
            list.add(descriptor);
        }
    }

    /**
     * Creates mocks for all the MetadataValue descriptors, and makes sure they are returned from {@link
     * Hudson#getExtensionList(Class)}.
     *
     * @param hudson the mocked hudson object.
     * @see #mockDateMetadataValueDescriptor(hudson.model.Hudson)
     * @see #mockNumberMetadataValueDescriptor(hudson.model.Hudson)
     * @see #mockStringMetadataValueDescriptor(hudson.model.Hudson)
     * @see #mockTreeNodeMetadataValueDescriptor(hudson.model.Hudson, hudson.ExtensionList)
     */
    public static void mockMetadataValueDescriptors(Hudson hudson) {
        ExtensionList<AbstractMetadataValue.AbstractMetaDataValueDescriptor> list =
                ExtensionList.create(hudson, AbstractMetadataValue.AbstractMetaDataValueDescriptor.class);
        mockTreeNodeMetadataValueDescriptor(hudson, list);
        mockStringMetadataValueDescriptor(hudson, list);
        mockDateMetadataValueDescriptor(hudson, list);
        mockNumberMetadataValueDescriptor(hudson, list);

        when(hudson.getExtensionList(AbstractMetadataValue.AbstractMetaDataValueDescriptor.class)).thenReturn(list);
    }

    /**
     * Utility constructor.
     */
    private MockUtils() {

    }
}
