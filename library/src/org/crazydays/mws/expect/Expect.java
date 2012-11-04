package org.crazydays.mws.expect;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.message.BasicHeader;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import junit.framework.AssertionFailedError;

import org.crazydays.mws.http.HttpRequestFacade;
import org.crazydays.mws.json.JSONUtils;
import static org.crazydays.mws.http.HttpConstants.*;

public class Expect
{
    private final static int DEFAULT_TIMES = 1;
    private final static int ANY_TIMES = -1;
    private final static int ATLEAST_ONCE_TIMES = -2;

    private String method;
    private final List<Header> headers = new LinkedList<Header>();
    private JSONObject json;

    private int times = DEFAULT_TIMES;
    private int count;

    public Expect asGet()
    {
        method = HttpGet.METHOD_NAME;
        return this;
    }

    public Expect asPost()
    {
        method = HttpPost.METHOD_NAME;
        return this;
    }

    public Expect asPut()
    {
        method = HttpPut.METHOD_NAME;
        return this;
    }

    public Expect asDelete()
    {
        method = HttpDelete.METHOD_NAME;
        return this;
    }

    public Expect asHead()
    {
        method = HttpHead.METHOD_NAME;
        return this;
    }

    public Expect asOptions()
    {
        method = HttpOptions.METHOD_NAME;
        return this;
    }

    public Expect asTrace()
    {
        method = HttpTrace.METHOD_NAME;
        return this;
    }

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

    public boolean matches(HttpRequestFacade request)
        throws IOException
    {
        if (!matchesMethod(request)) {
            return false;
        }

        if (!matchesHeaders(request)) {
            return false;
        }

        if (isJSON()) {
            if (matchesJSON(request)) {
                count++;
                return true;
            } else {
                return false;
            }
        } else {
            count++;
            return true;
        }
    }

    private boolean matchesMethod(HttpRequestFacade request)
    {
        if (method == null) {
            return true;
        } else {
            return method.equals(request.getMethod());
        }
    }

    private boolean matchesHeaders(HttpRequestFacade request)
    {
        for (Header matcher : headers) {
            boolean matched = false;

            for (String value : request.getHeaders(matcher.getName())) {
                if (matcher.getValue().equals(value)) {
                    matched = true;
                }
            }

            if (!matched) {
                return false;
            }
        }

        return true;
    }

    private boolean isJSON()
    {
        return json != null;
    }

    private boolean matchesJSON(HttpRequestFacade request)
        throws IOException
    {
        if (!isContentTypeJSON(request)) {
            return false;
        }

        if (!request.hasEntityContent()) {
            return false;
        }

        String content = request.getEntityContent();

        try {
            return JSONUtils.equals(buildJSON(content), json);
        } catch (JSONException e) {
            Log.w(getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    private boolean isContentTypeJSON(HttpRequestFacade request)
    {
        List<String> values = request.getHeaders(CONTENT_TYPE);
        for (String value : values) {
            if (value.equals(CONTENT_TYPE_APPLICATION_JSON)) {
                return true;
            }
        }

        return false;
    }

    private JSONObject buildJSON(String content)
        throws JSONException
    {
        return (JSONObject) new JSONTokener(content).nextValue();
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
