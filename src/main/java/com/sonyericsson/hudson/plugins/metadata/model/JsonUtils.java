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

import com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue;
import com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//CS IGNORE LineLength FOR NEXT 2 LINES. REASON: import.
import static com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue.AbstractMetaDataValueDescriptor.findForJsonType;

/**
 * Constants regarding JSON conversions.
 *
 * @author Robert Sandell &lt;robert.sandell@sonyericsson.com&gt;
 */
public abstract class JsonUtils {
    /**
     * The {@link com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue#getName()} attribute of a JSON
     * object. Usually required.
     */
    public static final String NAME = "name";

    /**
     * The {@link com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue#getDescription()} attribute of a
     * JSON object. Usually not required.
     */
    public static final String DESCRIPTION = "description";

    //CS IGNORE LineLength FOR NEXT 5 LINES. REASON: Documentation.

    /**
     * The type of the metadata object mapped via
     * {@link com.sonyericsson.hudson.plugins.metadata.model.values.AbstractMetadataValue.AbstractMetaDataValueDescriptor#findForJsonType(String)}
     * to create the correct POJO implementation.
     */
    public static final String METADATA_TYPE = "metadata-type";

    /**
     * The {@link com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue#getValue()} attribute of a JSON
     * object. Usually required.
     */
    public static final String VALUE = "value";

    /**
     * The {@link com.sonyericsson.hudson.plugins.metadata.model.values.TreeNodeMetadataValue#getChildren()} attribute
     * of a node JSON object. Used instead of {@link #VALUE} for nodes.
     */
    public static final String CHILDREN = "children";

    /**
     * The {@link com.sonyericsson.hudson.plugins.metadata.model.values.MetadataValue#isGenerated()} attribute of a JSON
     * object. Usually not required.
     */
    public static final String GENERATED = "generated";

    /**
     * Utility constructor.
     */
    private JsonUtils() {

    }

    /**
     * Converts the given JSON object to {@link MetadataValue}s. If the object is an array the method will return a list
     * of them all else it will be a list of one items.
     *
     * @param json the JSON data to convert.
     * @return a list of converted values.
     *
     * @throws ParseException if for example mandatory fields are missing in the data. The exception will contain the
     *                        JSONObject that was the offending one.
     * @see #toValues(net.sf.json.JSONArray)
     */
    public static List<MetadataValue> toValues(JSON json) throws ParseException {
        if (json.isArray()) {
            return toValues((JSONArray)json);
        } else {
            return Collections.singletonList(toValue((JSONObject)json));
        }
    }

    /**
     * Converts the given JSON array to {@link MetadataValue}s.
     *
     * @param json the JSON array to convert.
     * @return a list of converted values.
     *
     * @throws ParseException if for example mandatory fields are missing in the data. The exception will contain the
     *                        JSONObject that was the offending one.
     * @see #toValue(net.sf.json.JSONObject)
     */
    public static List<MetadataValue> toValues(JSONArray json) throws ParseException {
        List<MetadataValue> list = new LinkedList<MetadataValue>();
        for (int i = 0; i < json.size(); i++) {
            JSONObject object = json.getJSONObject(i);
            list.add(toValue(object));
        }
        return list;
    }

    /**
     * Converts the given JSON object to {@link MetadataValue}.
     *
     * @param json the JSON data to convert.
     * @return the converted value.
     *
     * @throws ParseException if for example mandatory fields are missing in the data. The exception will contain the
     *                        JSONObject that was the offending one, it could be something further down the hierarchy
     *                        than the object provided..
     * @see #toValues(net.sf.json.JSONArray)
     */
    public static MetadataValue toValue(JSONObject json) throws ParseException {
        String type = json.optString(METADATA_TYPE);
        if (type != null && !type.isEmpty()) {
            AbstractMetadataValue.AbstractMetaDataValueDescriptor descriptor = findForJsonType(type);
            if (descriptor != null) {
                return descriptor.fromJson(json);
            } else {
                throw new ParseException("Not a valid metadata type", json);
            }
        } else {
            throw new ParseException("Missing a metadata type", json);
        }
    }

    /**
     * Converts the given values into a JSON array.
     *
     * @param values the values to convert.
     * @return a JSON array.
     */
    public static JSON toJson(Collection<MetadataValue> values) {
        JSONArray array = new JSONArray();
        for (MetadataValue value : values) {
            array.add(value.toJson());
        }
        return array;
    }

    /**
     * Utility method for checking if a required attribute is present in the JSON object.
     *
     * @param json      the object to check.
     * @param attribute the attribute to check for.
     * @throws ParseException if the attribute is missing.
     */
    public static void checkRequiredJsonAttribute(JSONObject json, String attribute) throws ParseException {
        if (!json.has(attribute)) {
            throw new ParseException("Missing required attribute " + attribute, json);
        }
    }

    /**
     * Exception thrown during the conversion from JSON to internal POJO representations if something went wrong. For
     * example if a required field is missing.
     */
    public static class ParseException extends Exception {
        private JSON json;

        /**
         * Standard constructor.
         *
         * @param message the error message.
         * @param json    the offending JSON object.
         * @see Exception#Exception(String)
         */
        public ParseException(String message, JSON json) {
            super(message + " : " + json.toString());
            this.json = json;
        }

        /**
         * Standard constructor.
         *
         * @param message the error message.
         * @param json    the offending JSON object.
         * @param cause   some other that caused the error.
         * @see Exception#Exception(String, Throwable)
         */
        public ParseException(String message, Throwable cause, JSON json) {
            super(message + " : " + json.toString(), cause);
            this.json = json;
        }

        /**
         * Standard constructor.
         *
         * @param cause some other that caused the error.
         * @param json  the offending JSON object.
         * @see Exception#Exception(Throwable)
         */
        public ParseException(Throwable cause, JSON json) {
            super("Parse error for " + json.toString(), cause);
            this.json = json;
        }

        /**
         * Default constructor.
         */
        public ParseException() {
        }

        /**
         * The offending JSON object.
         *
         * @return the bad seed.
         */
        public JSON getJson() {
            return json;
        }
    }
}
