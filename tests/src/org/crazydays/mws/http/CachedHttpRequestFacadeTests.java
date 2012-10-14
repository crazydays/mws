package org.crazydays.mws.http;


import java.util.List;

import android.test.AndroidTestCase;

import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

public class CachedHttpRequestFacadeTests
    extends AndroidTestCase
{
    public void testGetHeader_none()
    {
        String name = "X-Random-Header";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        List<String> values = facade.getHeaders(name);

        assertEquals("!empty", 0, values.size());
    }

    public void testGetHeader_exists()
    {
        String name = "X-Random-Header";
        String value = "A-Value";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(name, value));

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        List<String> values = facade.getHeaders(name);

        assertEquals("size", 1, values.size());
        assertEquals("header.value", value, values.get(0));
    }

    public void testGetHeader_multiple()
    {
        String name = "X-Random-Header";
        String valueA = "A-Value";
        String valueB = "B-Value";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(name, valueA));
        request.addHeader(new BasicHeader(name, valueB));

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        List<String> values = facade.getHeaders(name);

        assertEquals("size", 2, values.size());

        for (String actual : values) {
            if (actual.equals(valueA) || actual.equals(valueB)) {
                continue;
            }
            fail("Unexpected value: " + actual);
        }
    }
}
