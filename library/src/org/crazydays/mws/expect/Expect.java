package org.crazydays.mws.expect;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ParseException;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import junit.framework.AssertionFailedError;

import org.crazydays.mws.json.JSONUtils;
import static org.crazydays.mws.http.HttpConstants.*;

public class Expect
{
    private final static int DEFAULT_TIMES = 1;
    private final static int ANY_TIMES = -1;
    private final static int ATLEAST_ONCE_TIMES = -2;

    private List<Header> headers = new LinkedList<Header>();
    private JSONObject json;

    private int times = DEFAULT_TIMES;
    private int count;

    public Expect withHeader(String name, String value)
    {
        headers.add(new BasicHeader(name, value));
        return this;
    }

    public Expect withJSON(JSONObject json)
    {
        this.json = json;
        return this;
    }

    public boolean matches(HttpRequest request)
        throws IOException
    {
        if (!matchesHeaders(request)) {
            return false;
        }

        if (isJSON()) {
            return matchesJSON(request);
        } else {
            count++;
            return true;
        }
    }

    private boolean matchesHeaders(HttpRequest request)
    {
        for (Header matcher : headers) {
            if (request.containsHeader(matcher.getName())) {
                for (Header actual : request.getHeaders(matcher.getName())) {
                    if (!matchesHeader(matcher, actual)) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean matchesHeader(Header matcher, Header actual)
    {
        return actual.getName().equals(matcher.getName())
            && actual.getValue().equals(matcher.getValue());
    }

    private boolean isJSON()
    {
        return json != null;
    }

    private boolean matchesJSON(HttpRequest request)
        throws IOException
    {
        if (!isEntityEnclosingRequest(request)) {
            return false;
        }

        HttpEntity entity = extractEntity(request);
        if (!entity.getContentType().getValue()
            .equals(CONTENT_TYPE_APPLICATION_JSON)) {
            return false;
        }

        try {
            return JSONUtils.equals(extractEntityJSON(entity), json);
        } catch (ParseException e) {
            Log.w(getClass().getSimpleName(), e.getMessage());
            return false;
        } catch (JSONException e) {
            Log.w(getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    private boolean isEntityEnclosingRequest(HttpRequest request)
    {
        return request instanceof HttpEntityEnclosingRequest;
    }

    private JSONObject extractEntityJSON(HttpEntity entity)
        throws ParseException, JSONException, IOException
    {
        return (JSONObject) new JSONTokener(extractEntityContent(entity))
            .nextValue();
    }

    private String extractEntityContent(HttpEntity entity)
        throws ParseException, IOException
    {
        return EntityUtils.toString(entity);
    }

    private HttpEntity extractEntity(HttpRequest request)
    {
        return castAsHttpEntityEnclosingRequest(request).getEntity();
    }

    private HttpEntityEnclosingRequest castAsHttpEntityEnclosingRequest(
        HttpRequest request)
    {
        return (HttpEntityEnclosingRequest) request;
    }

    public int getCount()
    {
        return count;
    }

    public Expect times(int times)
    {
        this.times = times;
        return this;
    }

    public Expect anyTimes()
    {
        this.times = ANY_TIMES;
        return this;
    }

    public Expect atleastOnce()
    {
        this.times = ATLEAST_ONCE_TIMES;
        return this;
    }

    public void verify()
    {
        if (count == times) {
            return;
        } else if (times == ANY_TIMES) {
            return;
        } else if (times == ATLEAST_ONCE_TIMES && count > 0) {
            return;
        } else {
            throw new AssertionFailedError(String.format(
                "Expect %d matches, actual: %d", times, count));
        }
    }
}
