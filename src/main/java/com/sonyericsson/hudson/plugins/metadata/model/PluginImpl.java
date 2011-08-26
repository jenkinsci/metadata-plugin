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

import com.sonyericsson.hudson.plugins.metadata.model.definitions.AbstractMetaDataDefinition;
import hudson.Extension;
import hudson.Plugin;
import hudson.model.Hudson;

import java.util.LinkedList;
import java.util.List;

/**
 * Main plugin singleton.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
@Extension
public class PluginImpl extends Plugin {

    private List<AbstractMetaDataDefinition> definitions = new LinkedList<AbstractMetaDataDefinition>();

    /**
     * Setter for the list of AbstractMetaDataDefinitions.
     *
     * @param definitions the list of AbstractMetaDataDefinitions
     */
    public void setDefinitions(List<AbstractMetaDataDefinition> definitions) {
        this.definitions = definitions;
    }

    /**
     * Getter for the list of AbstractMetaDataDefinitions.
     *
     * @return the list of AbstractMetaDataDefinitions
     */
    public List<AbstractMetaDataDefinition> getDefinitions() {
        return definitions;
    }

    /**
     * Gets the singleton instance of this Plugin.
     *
     * @return the instance.
     */
    public static PluginImpl getInstance() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }


}
