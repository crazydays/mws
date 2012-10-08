package org.crazydays.mws;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.AssertionFailedError;

import android.test.AndroidTestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.crazydays.mws.expect.JSONExpectation;
import org.crazydays.mws.json.JSONUtils;

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

    public void testIntegration_success()
        throws JSONException, ClientProtocolException, IOException
    {
        JSONObject expect = new JSONObject().put("one", "two");
        JSONObject respond = new JSONObject().put("abc", "zyx");

        service.expect("/foo/bar", new JSONExpectation(expect, respond));
        service.replay();

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

        service.verify();
    }

    public void testIntegration_failure()
        throws JSONException, ClientProtocolException, IOException
    {
        JSONObject actual = new JSONObject().put("not", "here");
        JSONObject expect = new JSONObject().put("one", "two");
        JSONObject respond = new JSONObject().put("abc", "zyx");

        service.expect("/foo/bar", new JSONExpectation(expect, respond));
        service.replay();

        StringEntity entity = new StringEntity(actual.toString(), "UTF-8");
        entity.setContentType("application/json");

        HttpPost post = new HttpPost();
        post.setURI(buildUri("/foo/bar"));
        post.setEntity(entity);

        HttpResponse response = client.execute(post);
        String data = EntityUtils.toString(response.getEntity());

        boolean failed = false;
        try {
            service.verify();
            failed = true;
        } catch (AssertionFailedError e) {
            // expected
        }
        if (failed) {
            fail("Expected verify to fail");
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
