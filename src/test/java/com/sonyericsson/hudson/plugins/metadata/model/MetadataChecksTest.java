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

import hudson.util.FormValidation;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Tests for {@link MetadataChecks}.
 *
 * @author Tomas Westling &lt;thomas.westling@sonyericsson.com&gt;
 */
public class MetadataChecksTest {

    /**
     * Tests the doCheckDateValue check.
     *
     * @throws Exception if so.
     */
    @Test
    public void testCheckDateValue() throws Exception {
        MetadataChecks checks = new MetadataChecks();

        FormValidation val = checks.doCheckDateValue("I am not a number", "5", "5");
        assertEquals(FormValidation.Kind.ERROR, val.kind);
        val = checks.doCheckDateValue("5", "me neither", "5");
        assertEquals(FormValidation.Kind.ERROR, val.kind);
        val = checks.doCheckDateValue("5", "5", "neither am I");
        assertEquals(FormValidation.Kind.ERROR, val.kind);

        val = checks.doCheckDateValue("5", "50", "5");
        assertEquals(FormValidation.Kind.ERROR, val.kind);
        val = checks.doCheckDateValue("5", "5", "50");
        assertEquals(FormValidation.Kind.ERROR, val.kind);

        val = checks.doCheckDateValue("5", "5", "5");
        assertEquals(FormValidation.Kind.OK, val.kind);
    }

    /**
     * Tests the doCheckTimeValue check.
     *
     * @throws Exception if so.
     */
    @Test
    public void testCheckTimeValue() throws Exception {
        MetadataChecks checks = new MetadataChecks();

        FormValidation val = checks.doCheckTimeValue("I am not a number", "5", "5");
        assertEquals(FormValidation.Kind.ERROR, val.kind);
        val = checks.doCheckTimeValue("5", "me neither", "5");
        assertEquals(FormValidation.Kind.ERROR, val.kind);
        val = checks.doCheckTimeValue("5", "5", "neither am I");
        assertEquals(FormValidation.Kind.ERROR, val.kind);

        val = checks.doCheckTimeValue("5", "67", "5");
        assertEquals(FormValidation.Kind.ERROR, val.kind);
        val = checks.doCheckTimeValue("5", "5", "-5");
        assertEquals(FormValidation.Kind.ERROR, val.kind);

        val = checks.doCheckTimeValue("5", "5", "5");
        assertEquals(FormValidation.Kind.OK, val.kind);
    }

    /**
     * Tests the doCheckNumberValue check.
     *
     *
     * @throws Exception if so.
     */
    @Test
    public void testCheckNumberValue() throws Exception {
        MetadataChecks checks = new MetadataChecks();
        FormValidation val = checks.doCheckNumberValue("I am not a human being");
        assertEquals(FormValidation.Kind.ERROR, val.kind);
        val = checks.doCheckNumberValue("43");
        assertEquals(FormValidation.Kind.OK, val.kind);
    }
}
