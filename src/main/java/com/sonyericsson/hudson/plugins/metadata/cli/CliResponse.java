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

import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Common util functions for sending HTTP responses in JSON format.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public final class CliResponse {

    /**
     * The JSON content type to use when writing responses.
     */
    protected static final String CONTENT_TYPE = "application/json";

    /**
     * Sends an error message in json format on the response object.
     *
     * @param status   the status code.
     * @param message  the human readable message.
     * @param response the response object to send to.
     * @throws java.io.IOException if so.
     */
    public static void sendError(CliUtils.Status status, String message, StaplerResponse response) throws IOException {
        sendResponse(Type.error, status.code(), status.name(), message, response, status.httpStatus());
    }

    /**
     * Sends an OK status message in JSON format.
     *
     * @param response the response handle.
     * @throws IOException if so.
     */
    public static void sendOk(StaplerResponse response) throws IOException {
        sendResponse(Type.ok, 0, null, "OK", response);
    }

    /**
     * Sends an OK status message with Ignored as the text in JSON format.
     *
     * @param response the response handle.
     * @throws IOException if so.
     */
    public static void sendIgnored(StaplerResponse response) throws IOException {
        sendResponse(Type.ok, 0, null, "Ignored", response);
    }

    /**
     * Sends a status message in JSON format.
     *
     * @param type      the response type (ok, error, warning)
     * @param errorCode the errorCode
     * @param errorName the name of the error if any.
     * @param message   the message
     * @param response  the response handle.
     * @param httpStatus the HTTP Status code
     * @throws IOException if so.
     */
    public static void sendResponse(Type type, int errorCode, String errorName, String message,
                                    StaplerResponse response, int httpStatus) throws IOException {
        JSONObject json = createResponse(type, errorCode, errorName, message);
        sendResponse(response, json, httpStatus);
    }

    /**
     * Sends a status message in JSON format.
     *
     * @param type      the response type (ok, error, warning)
     * @param errorCode the errorCode
     * @param errorName the name of the error if any.
     * @param message   the message
     * @param response  the response handle.
     * @throws IOException if so.
     */
    public static void sendResponse(Type type, int errorCode, String errorName, String message,
                                    StaplerResponse response) throws IOException {
        sendResponse(type, errorCode, errorName, message, response, HTTP_OK);
    }

    /**
     * Sends the pre-formatted response object.
     *
     * @param response the http response handle to write to.
     * @param json     the response object to send.
     * @param httpStatus the HTTP Status code
     * @throws IOException if so.
     */
    public static void sendResponse(StaplerResponse response, JSONObject json, int httpStatus) throws IOException {
        response.setStatus(httpStatus);
        response.setContentType(CONTENT_TYPE);
        response.getOutputStream().print(json.toString());
    }

    /**
     * Creates a Response JSON Object.
     *
     * @param type      the response type (ok, error, warning)
     * @param errorCode the errorCode
     * @param errorName the name of the error if any.
     * @param message   the human readable message
     * @return the response object, ready to be fed to {@link #sendResponse(org.kohsuke.stapler.StaplerResponse,
     *         net.sf.json.JSONObject, int)}
     */
    public static JSONObject createResponse(Type type, int errorCode, String errorName, String message) {
        JSONObject json = new JSONObject();
        json.put("type", type.name());
        json.put("errorCode", errorCode);
        if (errorName != null) {
            json.put("errorName", errorName);
        }
        json.put("message", message);
        return json;
    }

    /**
     * Utility constructor.
     */
    private CliResponse() {

    }

    /**
     * A Type of Response that is sent to the client.
     */
    public static enum Type {
        /**
         * Everything went fine.
         */
        ok,

        /**
         * Something went really wrong.
         */
        error,

        /**
         * Current state is ok, but there are some concerns.
         */
        warning
    }
}
