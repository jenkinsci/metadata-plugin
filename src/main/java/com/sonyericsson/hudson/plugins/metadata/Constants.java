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

/**
 * Common constants.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public abstract class Constants {
    /**
     * Request attribute on the /computer/name page set by
     * {@link com.sonyericsson.hudson.plugins.metadata.model.MetadataNodeProperty}
     * and {@link com.sonyericsson.hudson.plugins.metadata.model.MetadataJobProperty},
     * read by {@link com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue}
     * to determine what container type that is currently edited.
     */
    public static final String REQUEST_ATTR_METADATA_CONTAINER = "jenkins-metadata-container";

    /**
     * The separator constant used for the display name.
     */
    public static final String DISPLAY_NAME_SEPARATOR = ".";

    /**
     * The separator constant used for the environment variable name.
     */
    public static final String ENVIRONMENT_SEPARATOR = "_";

    /**
     * The prefix for the environment variable names..
     */
    public static final String METADATA_ENV_PREFIX = "MD" + ENVIRONMENT_SEPARATOR;

    /**
     * The URL to the actions.
     */
    public static final String COMMON_URL_NAME = "metadata";

    /**
     * The URL to the Metadata checks.
     */
    public static final String METADATA_CHECKS_URL_NAME = "MetadataChecks";

    /**
     * The icon to display for the actions.
     */
    public static final String COMMON_ICON = "clipboard.png";

    /**
     * Serialization alias (XStream and JSON) for
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.DateMetadataValue}.
     */
    public static final String SERIALIZATION_ALIAS_DATE = "metadata-date";

    /**
     * Serialization alias (XStream and JSON) for
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.NumberMetadataValue}.
     */
    public static final String SERIALIZATION_ALIAS_NUMBER = "metadata-number";

    /**
     * Serialization alias (XStream and JSON) for
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.StringMetadataValue}.
     */
    public static final String SERIALIZATION_ALIAS_STRING = "metadata-string";

    /**
     * Serialization alias (XStream and JSON) for
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue}.
     */
    public static final String SERIALIZATION_ALIAS_TREE = "metadata-tree";

    /**
     * Default hour,minute,second if no defaults are added.
     */
    public static final int DEFAULT_TIME_DETAILS = 0;

    /**
     * Subtracted from month value since it starts on 1 instead of 0.
     */
    public static final int DEFAULT_MONTH_ADJUSTMENT = 1;

    /**
     * Empty utility constructor.
     */
    private Constants() {

    }

}
