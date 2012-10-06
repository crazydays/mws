package org.crazydays.mws.json;


import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

public class JSONUtilsTests
    extends AndroidTestCase
{
    public void testJSONObject_empty()
        throws JSONException
    {
        JSONObject a = new JSONObject();
        JSONObject b = new JSONObject();

        assertTrue("a != b", JSONUtils.equals(a, b));
    }
}
