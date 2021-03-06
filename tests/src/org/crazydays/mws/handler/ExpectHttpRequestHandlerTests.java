package org.crazydays.mws.handler;


import java.io.IOException;

import android.test.AndroidTestCase;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.crazydays.mws.expect.Expect;
import org.crazydays.mws.json.JSONUtils;
import org.crazydays.mws.respond.Respond;
import static org.crazydays.mws.http.HttpConstants.*;

public class ExpectHttpRequestHandlerTests
    extends AndroidTestCase
{
    public void testHandle_match()
        throws HttpException, IOException, JSONException
    {
        JSONObject json1 = new JSONObject().put("abc", "xyz");
        JSONObject json2 = new JSONObject().put("foo", "bar");

        Expect expect = new Expect().withJSON(json1);
        Respond respond = new Respond().withJSON(json2);

        ExpectHttpRequestHandler handler = new ExpectHttpRequestHandler();
        handler.expectAndRespond(expect, respond);

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON));
        request.setEntity(new StringEntity(json1.toString()));

        BasicHttpResponse response =
            new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, null);

        handler.handle(request, response, new BasicHttpContext());

        assertTrue("!responded", JSONUtils.equals(
            json2,
            (JSONObject) new JSONTokener(EntityUtils.toString(response
                .getEntity())).nextValue()));
    }

    public void testHandle_no_match()
        throws HttpException, IOException, JSONException
    {
        JSONObject json1 = new JSONObject().put("abc", "xyz");
        JSONObject json2 = new JSONObject().put("foo", "bar");
        JSONObject json3 = new JSONObject().put("baz", "123");

        Expect expect = new Expect().withJSON(json1);
        Respond respond = new Respond().withJSON(json2);

        ExpectHttpRequestHandler handler = new ExpectHttpRequestHandler();
        handler.expectAndRespond(expect, respond);

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON));
        request.setEntity(new StringEntity(json3.toString()));

        BasicHttpResponse response =
            new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, null);

        handler.handle(request, response, new BasicHttpContext());

        assertNull("response", response.getEntity());
    }
}
