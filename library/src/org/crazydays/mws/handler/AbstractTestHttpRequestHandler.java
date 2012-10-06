package org.crazydays.mws.handler;


import java.util.LinkedList;
import java.util.List;

import org.apache.http.protocol.HttpRequestHandler;

import org.crazydays.mws.expect.Expectation;

public abstract class AbstractTestHttpRequestHandler<T extends Expectation>
    implements HttpRequestHandler
{
    protected List<T> expectations;

    protected AbstractTestHttpRequestHandler()
    {
        expectations = new LinkedList<T>();
    }

    public void expect(T expectation)
    {
        expectations.add(expectation);
    }
}
