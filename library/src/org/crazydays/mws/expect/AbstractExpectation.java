package org.crazydays.mws.expect;


import junit.framework.AssertionFailedError;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;

public abstract class AbstractExpectation
    implements Expectation
{
    private boolean matched;

    protected void matched()
    {
        matched = true;
    }

    @Override
    public boolean didMatch()
    {
        return matched;
    }

    /**
     * Helper method to cast HttpRequset as HttpEntityEnclosingRequest.
     * 
     * @param request Request
     * @return HttpEntityEnclosingRequest
     */
    protected HttpEntityEnclosingRequest castHttpEntityEnclosingRequest(
        HttpRequest request)
    {
        if (request instanceof HttpEntityEnclosingRequest) {
            return (HttpEntityEnclosingRequest) request;
        } else {
            throw new AssertionFailedError(
                "Request doesn't implement HttpEntityEnclosingRequest");
        }
    }
}
