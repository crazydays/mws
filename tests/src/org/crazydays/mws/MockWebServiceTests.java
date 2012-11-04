package org.crazydays.mws;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import android.test.AndroidTestCase;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.crazydays.mws.expect.Expect;
import org.crazydays.mws.json.JSONUtils;
import org.crazydays.mws.respond.Respond;
import static org.crazydays.mws.http.HttpConstants.*;

public class MockWebServiceTests
    extends AndroidTestCase
{
    private final static int PORT = 8080;

    private HttpClient client;
    private MockWebService service;

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        client = new DefaultHttpClient();
        service = new MockWebService(PORT);
    }

    @Override
    public void tearDown()
        throws Exception
    {
        service.teardown();
        super.tearDown();
    }

    private URI buildUri(String context)
    {
        try {
            return new URI("http", null, "127.0.0.1", PORT, context, null, null);
        } catch (URISyntaxException e) {
            fail("Unable to build URI: " + e.getMessage());
            return null;
        }
    }

    // Expect and Respond API
    public void testExpectAndRespondIntegration_matches()
        throws JSONException, ClientProtocolException, IOException
    {
        String eHeader = "X-Expect-Header";
        String eValue = "eValue";

        String rHeader = "X-Response-Header";
        String rValue = "rValue";

        String path = "/foo/bar";
        JSONObject expect = new JSONObject().put("one", "two");
        JSONObject respond = new JSONObject().put("abc", "zyx");

        service.expectAndRespond(
            path,
            new Expect().asPost().withHeader(eHeader, eValue).withJSON(expect),
            new Respond().withStatusCode(HttpStatus.SC_OK)
                .withHeader(rHeader, rValue).withJSON(respond));
        service.replay();

        StringEntity entity = new StringEntity(expect.toString());
        entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);

        HttpPost post = new HttpPost();
        post.setURI(buildUri(path));
        post.addHeader(eHeader, eValue);
        post.setEntity(entity);

        HttpResponse response = client.execute(post);
        assertEquals("ok", HttpStatus.SC_OK, response.getStatusLine()
            .getStatusCode());
        Header[] headers = response.getHeaders(rHeader);
        assertEquals("value", rValue, headers[0].getValue());
        assertTrue("response", JSONUtils.equals(
            respond,
            (JSONObject) new JSONTokener(EntityUtils.toString(response
                .getEntity())).nextValue()));

        service.verify();
    }
}
