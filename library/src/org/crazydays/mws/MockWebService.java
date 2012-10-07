package org.crazydays.mws;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import junit.framework.AssertionFailedError;

import org.crazydays.mws.expect.Expectation;
import org.crazydays.mws.handler.ExpectationHttpRequestHandler;
import org.crazydays.mws.http.WebServer;

public class MockWebService
{
    private WebServer server;

    private List<Expectation> expectations;

    private Map<String, ExpectationHttpRequestHandler> handlers;

    public MockWebService(int port)
    {
        server = new WebServer(port);
        expectations = new LinkedList<Expectation>();
        handlers = new HashMap<String, ExpectationHttpRequestHandler>();
    }

    public void setup()
    {
        for (String path : handlers.keySet()) {
            server.addHandler(path, handlers.get(path));
        }
        server.start();
    }

    public void teardown()
    {
        server.interrupt();
        try {
            server.join();
        } catch (InterruptedException e) {
            Log.e(getClass().getSimpleName(), e.toString());
            throw new AssertionFailedError("Failed to stop MockWebServer");
        }
    }

    public void expect(String path, Expectation expectation)
    {
        addExpectation(expectation);
        addHandler(path, expectation);
    }

    private void addExpectation(Expectation expectation)
    {
        expectations.add(expectation);
    }

    private void addHandler(String path, Expectation expectation)
    {
        if (!handlers.containsKey(path)) {
            handlers.put(path, new ExpectationHttpRequestHandler());
        }

        handlers.get(path).expect(expectation);
    }

    public void verify()
    {
        for (Expectation expectation : expectations) {
            if (!expectation.didMatch()) {
                throw new AssertionFailedError("Expectation unmatched");
            }
        }
    }
}
