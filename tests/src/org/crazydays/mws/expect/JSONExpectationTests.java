package org.crazydays.mws.expect;


import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import junit.framework.AssertionFailedError;

import android.test.AndroidTestCase;

public class JSONExpectationTests
    extends AndroidTestCase
{
    public void testMatches_matches()
        throws JSONException, IOException
    {
        JSONObject expect = new JSONObject().put("expect", "yeppers");
        JSONObject response = new JSONObject().put("response", "uh-hu");

        JSONExpectation expectation = new JSONExpectation(expect, response);

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity(expect.toString()));

        assertTrue("!matches", expectation.matches(request));
        assertTrue("matched", expectation.didMatch());
    }

    public void testMatches_does_not_match()
        throws JSONException, IOException
    {
        JSONObject actual = new JSONObject().put("expect", "noppers");
        JSONObject expect = new JSONObject().put("expect", "yeppers");
        JSONObject response = new JSONObject().put("response", "uh-hu");

        JSONExpectation expectation = new JSONExpectation(expect, response);

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity(actual.toString()));

        assertFalse("!matches", expectation.matches(request));
        assertFalse("matched", expectation.didMatch());
    }

    public void testMatches_bad_json()
        throws JSONException, IOException
    {
        JSONObject expect = new JSONObject().put("expect", "yeppers");
        JSONObject response = new JSONObject().put("response", "uh-hu");

        JSONExpectation expectation = new JSONExpectation(expect, response);

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity("{ this is not json }"));

        try {
            assertFalse("!matches", expectation.matches(request));
            fail("Expected bad JSON");
        } catch (AssertionFailedError e) {
            // expected
        }
        assertFalse("matched", expectation.didMatch());
    }
}
