package org.crazydays.mws.json;


import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils
{
    public static boolean equals(JSONObject a, JSONObject b)
        throws JSONException
    {
        if (a.length() != b.length()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Iterator<String> keys = (Iterator<String>) a.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object ao = a.get(key);
            Object bo = b.has(key) ? b.get(key) : null;

            if (!equals(ao, bo)) {
                return false;
            }
        }

        return true;
    }

    public final static boolean equals(JSONArray a, JSONArray b)
        throws JSONException
    {
        if (a.length() != b.length()) {
            return false;
        }

        for (int i = 0; i < a.length(); i++) {
            if (!equals(a.get(i), b.get(i))) {
                return false;
            }
        }

        return true;
    }

    private final static boolean equals(Object a, Object b)
        throws JSONException
    {
        if (a instanceof JSONObject && b instanceof JSONObject) {
            if (!equals((JSONObject) a, (JSONObject) b)) {
                return false;
            }
        } else if (a instanceof JSONArray && b instanceof JSONArray) {
            if (!equals((JSONArray) a, (JSONArray) b)) {
                return false;
            }
        } else if (a == null || b == null) {
            return false;
        } else {
            if (!a.equals(b)) {
                return false;
            }
        }

        return true;
    }
}
