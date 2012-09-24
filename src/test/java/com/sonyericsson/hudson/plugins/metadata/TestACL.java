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

import hudson.model.Job;
import hudson.security.ACL;
import hudson.security.AuthorizationStrategy;
import hudson.security.Permission;
import org.acegisecurity.Authentication;

import java.util.Collection;
import java.util.Collections;

/**
 * An authorization strategy that allows any permission except for projects named "secure".
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public class TestACL extends AuthorizationStrategy {
    @Override
    public ACL getRootACL() {
        return new ACL() {
            @Override
            public boolean hasPermission(Authentication a, Permission permission) {
                return true;
            }
        };
    }

    @Override
    public Collection<String> getGroups() {
        return Collections.emptyList();
    }

    @Override
    public ACL getACL(final Job<?, ?> project) {
        return new ACL() {
            @Override
            public boolean hasPermission(Authentication a, Permission permission) {
                return a == SYSTEM || !"secure".equals(project.getName());
            }
        };
    }
}
