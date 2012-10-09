package org.crazydays.mws.respond;


import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.crazydays.mws.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.test.AndroidTestCase;

public class RespondTests
    extends AndroidTestCase
{
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
        assertEquals("", Respond.CONTENT_TYPE_APPLICATION_JSON, entity
            .getContentType().getValue());
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

        for (Header header : response.getAllHeaders()) {
            if (header.getName().equals(name)) {
                assertEquals("value", value, header.getValue());
            } else {
                fail("Unexpected header: " + header.toString());
            }
        }
    }
}
