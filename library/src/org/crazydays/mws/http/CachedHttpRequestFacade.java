package org.crazydays.mws.http;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

public class CachedHttpRequestFacade
    implements HttpRequestFacade
{
    private HttpRequest request;
    private Map<String, List<String>> cachedHeaders =
        new HashMap<String, List<String>>();

    public CachedHttpRequestFacade(HttpRequest request)
    {
        this.request = request;
    }

    @Override
    public List<String> getHeaders(String name)
    {
        return doCacheHeader(name);
    }

    private List<String> doCacheHeader(String name)
    {
        if (isCachedHeader(name)) {
            return cachedHeaders.get(name);
        }

        List<String> values = new LinkedList<String>();

        for (Header header : request.getHeaders(name)) {
            values.add(header.getValue());
        }

        cachedHeaders.put(name, values);

        return values;
    }

    private boolean isCachedHeader(String name)
    {
        return cachedHeaders.containsKey(name);
    }
}
