package org.crazydays.mws.handler;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.util.Log;

import junit.framework.AssertionFailedError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.crazydays.mws.expect.JSONExpectation;
import org.crazydays.mws.json.JSONUtils;

public class JSONRequestHandler
    extends AbstractTestHttpRequestHandler<JSONExpectation>
{
    private final static String CONTENT_TYPE_JSON = "application/json";

    @Override
    public void handle(HttpRequest request, HttpResponse response,
        HttpContext context)
        throws HttpException, IOException
    {
        Log.i(getClass().getSimpleName(), "request.class: "
            + request.getClass().getCanonicalName());

        JSONObject actual = extractJSON(request);
        JSONExpectation expectation = findMatch(actual);
        response.setEntity(buildHttpEntity(expectation));
    }

    private JSONObject extractJSON(HttpRequest request)
        throws ParseException, IOException
    {
        HttpEntity entity = castHttpEntityEnclosingRequest(request).getEntity();
        String jsonString = EntityUtils.toString(entity, "UTF-8");
        Log.i(getClass().getSimpleName(), jsonString);
        try {
            return (JSONObject) new JSONTokener(jsonString).nextValue();
        } catch (JSONException e) {
            throw new AssertionFailedError("Unable to parse JSON String: "
                + jsonString);
        }
    }

    private HttpEntityEnclosingRequest castHttpEntityEnclosingRequest(
        HttpRequest request)
    {
        if (request instanceof HttpEntityEnclosingRequest) {
            return (HttpEntityEnclosingRequest) request;
        } else {
            throw new AssertionFailedError(
                "Request doesn't implement HttpEntityEnclosingRequest");
        }
    }

    private JSONExpectation findMatch(JSONObject actual)
    {
        for (JSONExpectation expectation : expectations) {
            try {
                if (JSONUtils.equals(actual, expectation.getExpect())) {
                    expectation.matched(); // flag as matched
                    return expectation;
                }
            } catch (JSONException e) {
                throw new AssertionFailedError("JSONException: " + e.toString());
            }
        }

        throw new AssertionFailedError("No matches for actual found: "
            + actual.toString());
    }

    private HttpEntity buildHttpEntity(JSONExpectation expectation)
        throws UnsupportedEncodingException
    {
        StringEntity entity =
            new StringEntity(expectation.getResponse().toString());

        entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
            CONTENT_TYPE_JSON));

        return entity;
    }
}
