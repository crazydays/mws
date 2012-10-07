package org.crazydays.mws;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.crazydays.mws.expect.JSONExpectation;
import org.crazydays.mws.json.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.test.AndroidTestCase;

public class MockWebServiceTests
    extends AndroidTestCase
{
    private final static int PORT = 8080;

    private HttpClient client;

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        client = new DefaultHttpClient();
    }

    public void testIntegration()
        throws JSONException, ClientProtocolException, IOException
    {
        JSONObject expect = new JSONObject().put("one", "two");
        JSONObject respond = new JSONObject().put("abc", "zyx");

        MockWebService service = new MockWebService(PORT);
        service.expect("/foo/bar", new JSONExpectation(expect, respond));
        try {
            service.setup();

            StringEntity entity = new StringEntity(expect.toString(), "UTF-8");
            entity.setContentType("application/json");

            HttpPost post = new HttpPost();
            post.setURI(buildUri("/foo/bar"));
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            assertTrue("response", JSONUtils.equals(
                respond,
                (JSONObject) new JSONTokener(EntityUtils.toString(response
                    .getEntity())).nextValue()));
        } finally {
            service.teardown();
        }
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
}
