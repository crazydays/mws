package org.crazydays.mws.handler;


import java.io.IOException;

import android.test.AndroidTestCase;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.crazydays.mws.expect.JSONExpectation;
import org.crazydays.mws.json.JSONUtils;

public class ExpectationHttpRequestHandlerTests
    extends AndroidTestCase
{
    public void testHandleMatches()
        throws JSONException, HttpException, IOException
    {
        JSONObject expect = new JSONObject().put("foo", "bar");
        JSONObject respond = new JSONObject().put("oof", "baz");
        JSONExpectation expectation = new JSONExpectation(expect, respond);

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity(expect.toString()));

        BasicHttpResponse response =
            new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, null);

        ExpectationHttpRequestHandler handler =
            new ExpectationHttpRequestHandler();
        handler.expect(expectation);

        handler.handle(request, response, null);

        JSONObject responded =
            (JSONObject) new JSONTokener(EntityUtils.toString(response
                .getEntity())).nextValue();

        assertTrue("not equal", JSONUtils.equals(respond, responded));
    }
}
