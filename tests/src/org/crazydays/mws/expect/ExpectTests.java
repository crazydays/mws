package org.crazydays.mws.expect;


import java.io.IOException;

import android.test.AndroidTestCase;

import junit.framework.AssertionFailedError;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.crazydays.mws.http.CachedHttpRequestFacade;

import org.json.JSONException;
import org.json.JSONObject;

import static org.crazydays.mws.http.HttpConstants.*;

public class ExpectTests
    extends AndroidTestCase
{
    public void testAsGet_post()
        throws IOException
    {
        HttpRequest request = new HttpPost();

        Expect expect = new Expect().asGet();

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsGet_get()
        throws IOException
    {
        HttpRequest request = new HttpGet();

        Expect expect = new Expect().asGet();

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsPost_post()
        throws IOException
    {
        HttpRequest request = new HttpPost();

        Expect expect = new Expect().asPost();

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsPost_get()
        throws IOException
    {
        HttpRequest request = new HttpGet();

        Expect expect = new Expect().asPost();

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsPut_put()
        throws IOException
    {
        HttpRequest request = new HttpPut();

        Expect expect = new Expect().asPut();

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsPut_get()
        throws IOException
    {
        HttpRequest request = new HttpGet();

        Expect expect = new Expect().asPut();

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsDelete_delete()
        throws IOException
    {
        HttpRequest request = new HttpDelete();

        Expect expect = new Expect().asDelete();

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsDelete_get()
        throws IOException
    {
        HttpRequest request = new HttpGet();

        Expect expect = new Expect().asDelete();

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsHead_head()
        throws IOException
    {
        HttpRequest request = new HttpHead();

        Expect expect = new Expect().asHead();

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsHead_get()
        throws IOException
    {
        HttpRequest request = new HttpGet();

        Expect expect = new Expect().asHead();

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsOptions_options()
        throws IOException
    {
        HttpRequest request = new HttpOptions();

        Expect expect = new Expect().asOptions();

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsOptions_get()
        throws IOException
    {
        HttpRequest request = new HttpGet();

        Expect expect = new Expect().asOptions();

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsTrace_trace()
        throws IOException
    {
        HttpRequest request = new HttpTrace();

        Expect expect = new Expect().asTrace();

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testAsTrace_get()
        throws IOException
    {
        HttpRequest request = new HttpGet();

        Expect expect = new Expect().asTrace();

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithJSON()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON));
        request.setEntity(new StringEntity(json.toString()));

        Expect expect = new Expect().withJSON(json);

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithJSON_no_json_header()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity(json.toString()));

        Expect expect = new Expect().withJSON(json);

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithJSON_text_plain_header()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(CONTENT_TYPE, "text/plain"));
        request.setEntity(new StringEntity(json.toString()));

        Expect expect = new Expect().withJSON(json);

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithJSON_not_entity_enclosing_request()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");

        HttpRequest request = new BasicHttpRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON));

        Expect expect = new Expect().withJSON(json);

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithJSON_invalid_json()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON));
        request.setEntity(new StringEntity("{ not really json }"));

        Expect expect = new Expect().withJSON(json);

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithJSON_withHeader()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");
        String name = "X-Random-Header";
        String value = "AValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(name, value);
        request.addHeader(new BasicHeader(CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON));
        request.setEntity(new StringEntity(json.toString()));

        Expect expect = new Expect().withHeader(name, value).withJSON(json);

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithJSON_not_same()
        throws JSONException, IOException
    {
        JSONObject json1 = new JSONObject().put("abc", "xyz");
        JSONObject json2 = new JSONObject().put("abc", "xxx");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(CONTENT_TYPE,
            CONTENT_TYPE_APPLICATION_JSON));
        request.setEntity(new StringEntity(json2.toString()));

        Expect expect = new Expect().withJSON(json1);

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithHeader()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value);

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithHeader_missing_one()
        throws IOException
    {
        String name1 = "X-Made-Up-Header";
        String value1 = "SomeValue";
        String name2 = "X-Also-Made-Up";
        String value2 = "AnotherValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name1, value1));

        Expect expect =
            new Expect().withHeader(name1, value1).withHeader(name2, value2);

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithHeader_wrong_value()
        throws IOException
    {
        String name1 = "X-Made-Up-Header";
        String value1 = "SomeValue";
        String value2 = "AnotherValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(name1, value1));

        Expect expect = new Expect().withHeader(name1, value2);

        assertFalse("matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testWithHeader_extra_one()
        throws IOException
    {
        String name1 = "X-Made-Up-Header";
        String value1 = "SomeValue";
        String name2 = "X-Also-Made-Up";
        String value2 = "AnotherValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name1, value1));
        request.addHeader(new BasicHeader(name2, value2));

        Expect expect = new Expect().withHeader(name1, value1);

        assertTrue("!matched",
            expect.matches(new CachedHttpRequestFacade(request)));
    }

    public void testCount_none()
    {
        Expect expect = new Expect();

        assertEquals("count", 0, expect.getCount());
    }

    public void testCount_one()
        throws IOException
    {
        String name1 = "X-Made-Up-Header";
        String value1 = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name1, value1));

        Expect expect = new Expect().withHeader(name1, value1);

        expect.matches(new CachedHttpRequestFacade(request));

        assertEquals("count", 1, expect.getCount());
    }

    public void testCount_two()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value);

        expect.matches(new CachedHttpRequestFacade(request));
        expect.matches(new CachedHttpRequestFacade(request));

        assertEquals("count", 2, expect.getCount());
    }

    public void testVerify_no_matches()
    {
        Expect expect = new Expect();

        try {
            expect.verify();
            fail("Expected failure");
        } catch (AssertionFailedError e) {
            // success
        }
    }

    public void testVerify_one_match()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value);
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
        } catch (AssertionFailedError e) {
            fail("Expected no errors");
        }
    }

    public void testVerify_two_match()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value);
        expect.matches(new CachedHttpRequestFacade(request));
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
            fail("Expected failure");
        } catch (AssertionFailedError e) {
            // success
        }
    }

    public void testVerify_one_times_two()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value).times(2);
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
            fail("Expected failure");
        } catch (AssertionFailedError e) {
            // success
        }
    }

    public void testVerify_two_times_two()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value).times(2);
        expect.matches(new CachedHttpRequestFacade(request));
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
            fail("Expected failure");
        } catch (AssertionFailedError e) {
            // success
        }
    }

    public void testVerify_any_times_zero()
        throws IOException
    {
        Expect expect = new Expect().anyTimes();

        try {
            expect.verify();
        } catch (AssertionFailedError e) {
            fail("Expected no failure");
        }
    }

    public void testVerify_any_times_one()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value).anyTimes();
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
        } catch (AssertionFailedError e) {
            fail("Expected no failure");
        }
    }

    public void testVerify_any_times_two()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value).anyTimes();
        expect.matches(new CachedHttpRequestFacade(request));
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
        } catch (AssertionFailedError e) {
            fail("Expected no failure");
        }
    }

    public void testVerify_atleast_once_none()
        throws IOException
    {
        Expect expect = new Expect().atleastOnce();

        try {
            expect.verify();
            fail("Expected failure");
        } catch (AssertionFailedError e) {
            // success
        }
    }

    public void testVerify_atleast_once_one()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value).atleastOnce();
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
        } catch (AssertionFailedError e) {
            fail("Expected no failure");
        }
    }

    public void testVerify_atleast_once_two()
        throws IOException
    {
        String name = "X-Made-Up-Header";
        String value = "SomeValue";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        request.addHeader(new BasicHeader(name, value));

        Expect expect = new Expect().withHeader(name, value).atleastOnce();
        expect.matches(new CachedHttpRequestFacade(request));
        expect.matches(new CachedHttpRequestFacade(request));

        try {
            expect.verify();
        } catch (AssertionFailedError e) {
            fail("Expected no failure");
        }
    }
}
