package org.crazydays.mws.http;


import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;

import static org.crazydays.mws.http.HttpHelpers.*;

public class CachedHttpRequestFacade
    implements HttpRequestFacade
{
    private HttpRequest request;
    private List<String> headerKeys;
    private Map<String, List<String>> cachedHeaders =
        new HashMap<String, List<String>>();
    private String entityContent;

    public CachedHttpRequestFacade(HttpRequest request)
    {
        this.request = request;
    }

    @Override
    public String getMethod()
    {
        return request.getRequestLine().getMethod();
    }

    @Override
    public List<String> getHeaderKeys()
    {
        return doCacheHeaderKeys();
    }

    private List<String> doCacheHeaderKeys()
    {
        if (isCachedHeaderKeys()) {
            return headerKeys;
        }

        headerKeys = new LinkedList<String>();

        for (Header header : request.getAllHeaders()) {
            headerKeys.add(header.getName());
        }

        return doCacheHeaderKeys();
    }

    private boolean isCachedHeaderKeys()
    {
        return headerKeys != null;
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

        // cache
        List<String> values = new LinkedList<String>();

        for (Header header : request.getHeaders(name)) {
            values.add(header.getValue());
        }

        cachedHeaders.put(name, values);

        return doCacheHeader(name);
    }

    private boolean isCachedHeader(String name)
    {
        return cachedHeaders.containsKey(name);
    }

    @Override
    public boolean hasEntityContent()
    {
        if (isEntityEnclosingRequest(request)) {
            return castAsHttpEntityEnclosingRequest(request).getEntity() != null;
        } else {
            return false;
        }
    }

    @Override
    public String getEntityContent()
        throws ParseException, IOException
    {
        return doCacheEntityContent();
    }

    private String doCacheEntityContent()
        throws ParseException, IOException
    {
        if (isCachedEntityContent()) {
            return entityContent;
        }

        // cache
        HttpEntity entity = extractEntity(request);
        entityContent = extractEntityContent(entity);

        return doCacheEntityContent();
    }

    private boolean isCachedEntityContent()
    {
        return entityContent != null;
    }
}
