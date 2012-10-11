package org.crazydays.mws.expect;


import java.io.IOException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import static org.crazydays.mws.http.HttpConstants.*;

public class ExpectTests
    extends AndroidTestCase
{
    public void testWithJSON()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        StringEntity entity = new StringEntity(json.toString());
        entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        request.setEntity(entity);

        Expect expect = new Expect().withJSON(json);

        assertTrue("!matched", expect.matches(request));
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

        StringEntity entity = new StringEntity(json.toString());
        entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        request.setEntity(entity);

        Expect expect = new Expect().withHeader(name, value).withJSON(json);

        assertTrue("!matched", expect.matches(request));
    }

    public void testWithJSON_not_same()
        throws JSONException, IOException
    {
        JSONObject json1 = new JSONObject().put("abc", "xyz");
        JSONObject json2 = new JSONObject().put("abc", "xxx");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        StringEntity entity = new StringEntity(json2.toString());
        entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        request.setEntity(entity);

        Expect expect = new Expect().withJSON(json1);

        assertFalse("matched", expect.matches(request));
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

        assertTrue("!matched", expect.matches(request));
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

        assertFalse("matched", expect.matches(request));
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

        assertTrue("!matched", expect.matches(request));
    }

}
