package org.crazydays.mws.json;


import org.json.JSONArray;
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

    public void testJSONObject_single_equal()
        throws JSONException
    {
        JSONObject a = new JSONObject().put("foo", "bar");
        JSONObject b = new JSONObject().put("foo", "bar");

        assertTrue("a != b", JSONUtils.equals(a, b));
    }

    public void testJSONObject_single_not_equal()
        throws JSONException
    {
        JSONObject a = new JSONObject().put("foo", "bar");
        JSONObject b = new JSONObject().put("foo", "baz");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

    public void testJSONObject_different_size()
        throws JSONException
    {
        JSONObject a = new JSONObject().put("foo", "bar").put("boo", "baz");
        JSONObject b = new JSONObject().put("foo", "bar");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

    public void testJSONObject_different_values()
        throws JSONException
    {
        JSONObject a = new JSONObject().put("foo", "bar").put("boo", "baz");
        JSONObject b = new JSONObject().put("foo", "bar").put("boo", "zab");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

    public void testJSONObject_different_keys()
        throws JSONException
    {
        JSONObject a = new JSONObject().put("foo", "bar").put("boo", "baz");
        JSONObject b = new JSONObject().put("foo", "bar").put("oob", "baz");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

    public void testJSONObject_key_to_JSONObject_empty()
        throws JSONException
    {
        JSONObject a = new JSONObject().put("foo", new JSONObject());
        JSONObject b = new JSONObject().put("foo", new JSONObject());

        assertTrue("a != b", JSONUtils.equals(a, b));
    }

    public void testJSONObject_key_to_JSONObject_elements_equal()
        throws JSONException
    {
        JSONObject a =
            new JSONObject().put("foo", new JSONObject().put("bar", "baz"));
        JSONObject b =
            new JSONObject().put("foo", new JSONObject().put("bar", "baz"));

        assertTrue("a != b", JSONUtils.equals(a, b));
    }

    public void testJSONArray_empty()
        throws JSONException
    {
        JSONArray a = new JSONArray();
        JSONArray b = new JSONArray();

        assertTrue("a != b", JSONUtils.equals(a, b));
    }

    public void testJSONArray_one_equals()
        throws JSONException
    {
        JSONArray a = new JSONArray().put("one");
        JSONArray b = new JSONArray().put("one");

        assertTrue("a != b", JSONUtils.equals(a, b));
    }

    public void testJSONArray_two_different_order()
        throws JSONException
    {
        JSONArray a = new JSONArray().put("one").put("two");
        JSONArray b = new JSONArray().put("two").put("one");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

    public void testJSONArray_different_length()
        throws JSONException
    {
        JSONArray a = new JSONArray().put("one").put("two");
        JSONArray b = new JSONArray().put("one");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

    public void testJSONArray_complex_equal()
        throws JSONException
    {
        JSONArray a =
            new JSONArray()
                .put("one")
                .put(
                    new JSONObject().put("foo",
                        new JSONArray().put("bar").put("baz"))).put("two");
        JSONArray b =
            new JSONArray()
                .put("one")
                .put(
                    new JSONObject().put("foo",
                        new JSONArray().put("bar").put("baz"))).put("two");

        assertTrue("a != b", JSONUtils.equals(a, b));
    }

    public void testJSONArray_complex_sub_JSONObject_inequal()
        throws JSONException
    {
        JSONArray a =
            new JSONArray()
                .put("one")
                .put(
                    new JSONObject().put("aaa", "bbb").put("foo",
                        new JSONArray().put("bar").put("baz"))).put("two");
        JSONArray b =
            new JSONArray()
                .put("one")
                .put(
                    new JSONObject().put("foo",
                        new JSONArray().put("bar").put("baz"))).put("two");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

    public void testJSONArray_complex_sub_JSONArray_inequal()
        throws JSONException
    {
        JSONArray a =
            new JSONArray()
                .put("one")
                .put(
                    new JSONObject().put("foo",
                        new JSONArray().put("bar").put("baz").put("boo")))
                .put("two");
        JSONArray b =
            new JSONArray()
                .put("one")
                .put(
                    new JSONObject().put("foo",
                        new JSONArray().put("bar").put("baz"))).put("two");

        assertFalse("a == b", JSONUtils.equals(a, b));
    }

}
