package org.crazydays.mws.http;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.test.AndroidTestCase;

import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

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

    public void testGetHeader_exists_twice()
    {
        String name = "X-Random-Header";
        String value = "A-Value";

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.addHeader(new BasicHeader(name, value));

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        List<String> values1 = facade.getHeaders(name);

        assertEquals("size", 1, values1.size());
        assertEquals("header.value", value, values1.get(0));

        List<String> values2 = facade.getHeaders(name);

        assertEquals("size", 1, values2.size());
        assertEquals("header.value", value, values2.get(0));
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

    public void testHasEntityContent_non_entity_enclosing_request()
    {
        BasicHttpRequest request =
            new BasicHttpRequest("POST", "http://127.0.0.1");

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        assertFalse("true", facade.hasEntityContent());
    }

    public void testHasEntityContent_no_entity()
    {
        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        assertFalse("true", facade.hasEntityContent());
    }

    public void testHasEntityContent_with_entity_empty_string()
        throws UnsupportedEncodingException
    {
        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity(""));

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        assertTrue("false", facade.hasEntityContent());
    }

    public void testGetEntityContent_empty()
        throws ParseException, IOException
    {
        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity(""));

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        assertEquals("empty", "", facade.getEntityContent());
    }

    public void testGetEntityContent_empty_cached()
        throws ParseException, IOException
    {
        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");
        request.setEntity(new StringEntity(""));

        CachedHttpRequestFacade facade = new CachedHttpRequestFacade(request);

        assertEquals("first", "", facade.getEntityContent());
        assertEquals("second", "", facade.getEntityContent());
    }
}
