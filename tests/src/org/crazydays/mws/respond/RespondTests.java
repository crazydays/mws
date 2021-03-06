package org.crazydays.mws.respond;


import java.io.IOException;

import android.test.AndroidTestCase;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.crazydays.mws.json.JSONUtils;
import static org.crazydays.mws.http.HttpConstants.*;

public class RespondTests
    extends AndroidTestCase
{
    public void testWithStatus_200()
        throws IOException
    {
        Respond respond = new Respond().withStatusCode(HttpStatus.SC_NOT_FOUND);

        BasicHttpResponse response =
            new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, null);

        respond.respond(response);

        assertEquals("!404", HttpStatus.SC_NOT_FOUND, response.getStatusLine()
            .getStatusCode());
    }

    public void testWithJSON()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("status", "success");
        Respond respond = new Respond().withJSON(json);

        BasicHttpResponse response =
            new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, null);

        respond.respond(response);

        HttpEntity entity = response.getEntity();
        assertTrue("!StringEntity", entity instanceof StringEntity);
        assertEquals("", CONTENT_TYPE_APPLICATION_JSON, entity.getContentType()
            .getValue());
        assertTrue("json", JSONUtils.equals(json, (JSONObject) new JSONTokener(
            EntityUtils.toString(entity)).nextValue()));
    }

    public void testWithHeader()
        throws IOException
    {
        String name = "X-Made-Up-Heder";
        String value = "SomeValue";
        Respond respond = new Respond().withHeader(name, value);

        BasicHttpResponse response =
            new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1),
                HttpStatus.SC_OK, null);

        respond.respond(response);

        int count = 0;

        for (Header header : response.getAllHeaders()) {
            if (header.getName().equals(name)) {
                assertEquals("value", value, header.getValue());
                count++;
            } else {
                fail("Unexpected header: " + header.toString());
            }
        }

        assertEquals("count", 1, count);
    }
}
