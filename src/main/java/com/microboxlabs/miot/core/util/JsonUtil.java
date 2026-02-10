package com.microboxlabs.miot.core.util;

import com.microboxlabs.miot.core.exception.MiotOperationException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Thin JSON serialization utilities.
 *
 * <p>Wraps {@link org.json.JSONObject} (already on the Alfresco classpath)
 * to provide consistent error handling.</p>
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    /**
     * Parses a JSON string into a {@link JSONObject}.
     *
     * @param json the JSON string
     * @return the parsed object
     * @throws MiotOperationException if the string is not valid JSON
     */
    public static JSONObject parse(String json) {
        Preconditions.checkNotBlank(json, "json");
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new MiotOperationException("Failed to parse JSON", e);
        }
    }

    /**
     * Creates a new empty {@link JSONObject}.
     *
     * @return a fresh JSON object
     */
    public static JSONObject newObject() {
        return new JSONObject();
    }
}
