package org.crazydays.mws.expect;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import junit.framework.AssertionFailedError;

import org.crazydays.mws.json.JSONUtils;

public class JSONExpectation
    extends AbstractExpectation
{
    private final static String CONTENT_TYPE_JSON = "application/json";

    private JSONObject expect;
    private JSONObject response;

    public JSONExpectation(JSONObject expect, JSONObject response)
    {
        this.expect = expect;
        this.response = response;
    }

    @Override
    public boolean matches(HttpRequest request)
        throws IOException
    {
        try {
            if (JSONUtils.equals(expect, extractJSON(request))) {
                matched();
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            Log.w(getClass().getSimpleName(), e.toString());
            return false;
        } catch (JSONException e) {
            Log.w(getClass().getSimpleName(), e.toString());
            return false;
        }
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

    @Override
    public void respond(HttpResponse response)
        throws IOException
    {
        response.setEntity(buildResponseEntity());
    }

    private HttpEntity buildResponseEntity()
        throws UnsupportedEncodingException
    {
        StringEntity entity = new StringEntity(this.response.toString());

        entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
            CONTENT_TYPE_JSON));

        return entity;
    }
}
