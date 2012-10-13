package org.crazydays.mws.http;


import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class HttpHelpers
{
    public static boolean isEntityEnclosingRequest(HttpRequest request)
    {
        return request instanceof HttpEntityEnclosingRequest;
    }

    public static HttpEntityEnclosingRequest castAsHttpEntityEnclosingRequest(
        HttpRequest request)
    {
        return (HttpEntityEnclosingRequest) request;
    }

    public static JSONObject extractEntityJSON(HttpEntity entity)
        throws ParseException, JSONException, IOException
    {
        return (JSONObject) new JSONTokener(extractEntityContent(entity))
            .nextValue();
    }

    public static HttpEntity extractEntity(HttpRequest request)
    {
        return castAsHttpEntityEnclosingRequest(request).getEntity();
    }

    public static String extractEntityContent(HttpEntity entity)
        throws ParseException, IOException
    {
        return EntityUtils.toString(entity);
    }
}
