package org.crazydays.mws.respond;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import org.json.JSONObject;

import static org.crazydays.mws.http.HttpConstants.*;

public class Respond
{
    private int statusCode;

    private final List<Header> headers = new LinkedList<Header>();

    private JSONObject json;

    public Respond withStatusCode(int statusCode)
    {
        this.statusCode = statusCode;
        return this;
    }

    public Respond withHeader(String name, String value)
    {
        headers.add(new BasicHeader(name, value));
        return this;
    }

    public Respond withJSON(JSONObject json)
    {
        this.json = json;
        return this;
    }

    public void respond(HttpResponse response)
        throws IOException
    {
        respondWithStatusCode(response);
        respondWithHeaders(response);
        respondWithEntity(response);
    }

    private void respondWithStatusCode(HttpResponse response)
    {
        if (statusCode != 0) {
            response.setStatusCode(statusCode);
        }
    }

    private void respondWithHeaders(HttpResponse response)
    {
        for (Header header : headers) {
            response.addHeader(header);
        }
    }

    private void respondWithEntity(HttpResponse response)
        throws UnsupportedEncodingException
    {
        if (json != null) {
            respondWithJSONEntity(response);
        }
    }

    private void respondWithJSONEntity(HttpResponse response)
        throws UnsupportedEncodingException
    {
        StringEntity entity = new StringEntity(json.toString());
        entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        response.setEntity(entity);
    }
}
