/*
 *  The MIT License
 *
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
package com.sonyericsson.hudson.plugins.metadata;


import hudson.ExtensionList;
import hudson.ExtensionPoint;
import com.sonyericsson.hudson.plugins.metadata.model.MetadataNodeProperty;
import hudson.model.Hudson;

/**
 * A listener that listens on updates in the Metadata.
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
public abstract class MetadataUpdateListener implements ExtensionPoint {

    /**
     * Finds all the MetadataUpdateListeners and runs metadataChanged on them
     * when changes are made in the Metadata for a Node.
     * @param property the new MetadataNodeProperty.
     */
    public static void notifyMetadaNodePropertyChanged(MetadataNodeProperty property) {
        ExtensionList<MetadataUpdateListener> extensionList =
                Hudson.getInstance().getExtensionList(MetadataUpdateListener.class);
        for (MetadataUpdateListener listener : extensionList) {
            listener.metadataNodePropertyChanged(property);
        }
    }

    /**
     * Run when there are changes in the Metadata for a Node.
     * @param property the new MetadataNodeProperty.
     */
    public void metadataNodePropertyChanged(MetadataNodeProperty property) {
    }
}
