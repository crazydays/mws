package org.crazydays.mws.expect;


import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.crazydays.mws.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import static org.crazydays.mws.http.HttpConstants.*;

public class Expect
{
    private JSONObject json;

    public Expect withJSON(JSONObject json)
    {
        this.json = json;
        return this;
    }

    public boolean matches(HttpRequest request)
        throws IOException
    {
        if (isJSON()) {
            return matchesJSON(request);
        }

        return false;
    }

    private boolean isJSON()
    {
        return json != null;
    }

    private boolean matchesJSON(HttpRequest request)
        throws IOException
    {
        if (!isEntityEnclosingRequest(request)) {
            return false;
        }

        HttpEntity entity = extractEntity(request);
        if (!entity.getContentType().getValue()
            .equals(CONTENT_TYPE_APPLICATION_JSON)) {
            return false;
        }

        try {
            return JSONUtils.equals(extractEntityJSON(entity), json);
        } catch (ParseException e) {
            Log.w(getClass().getSimpleName(), e.getMessage());
            return false;
        } catch (JSONException e) {
            Log.w(getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    private boolean isEntityEnclosingRequest(HttpRequest request)
    {
        return request instanceof HttpEntityEnclosingRequest;
    }

    private JSONObject extractEntityJSON(HttpEntity entity)
        throws ParseException, JSONException, IOException
    {
        return (JSONObject) new JSONTokener(extractEntityContent(entity))
            .nextValue();
    }

    private String extractEntityContent(HttpEntity entity)
        throws ParseException, IOException
    {
        return EntityUtils.toString(entity);
    }

    private HttpEntity extractEntity(HttpRequest request)
    {
        return castAsHttpEntityEnclosingRequest(request).getEntity();
    }

    private HttpEntityEnclosingRequest castAsHttpEntityEnclosingRequest(
        HttpRequest request)
    {
        return (HttpEntityEnclosingRequest) request;
    }
}
