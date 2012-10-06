package org.crazydays.mws.expect;


import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

public class JSONExpectationTests
    extends AndroidTestCase
{
    public void testIt()
        throws JSONException
    {
        JSONObject expect = new JSONObject().put("expect", "yeppers");
        JSONObject response = new JSONObject().put("response", "uh-hu");

        JSONExpectation expectation = new JSONExpectation(expect, response);

        assertEquals("expect", expect, expectation.getExpect());
        assertEquals("response", response, expectation.getResponse());
    }
}
