package org.crazydays.mws;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import junit.framework.AssertionFailedError;

import org.crazydays.mws.expect.Expectation;
import org.crazydays.mws.expect.JSONExpectation;
import org.crazydays.mws.handler.AbstractTestHttpRequestHandler;
import org.crazydays.mws.handler.JSONRequestHandler;
import org.crazydays.mws.http.WebServer;

public class MockWebService
{
    private WebServer server;

    private List<Expectation> expectations;

    private Map<String, AbstractTestHttpRequestHandler<? extends Expectation>> handlers;

    public MockWebService(int port)
    {
        server = new WebServer(port);
        expectations = new LinkedList<Expectation>();
        handlers =
            new HashMap<String, AbstractTestHttpRequestHandler<? extends Expectation>>();
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
            if (expectation instanceof JSONExpectation) {
                handlers.put(path, new JSONRequestHandler());
            } else {
                throw new AssertionFailedError("Unsupported expectation type: "
                    + expectation.getClass().getSimpleName());
            }
        }

        AbstractTestHttpRequestHandler<? extends Expectation> handler =
            handlers.get(path);
        if (handler instanceof JSONRequestHandler) {
            ((JSONRequestHandler) handler)
                .expect((JSONExpectation) expectation);
        } else {
            throw new AssertionFailedError(
                "Unsupported expectation handler type: "
                    + handler.getClass().getSimpleName());
        }
    }

    public void verify()
    {
        for (Expectation expectation : expectations) {
            if (!expectation.isMatched()) {
                throw new AssertionFailedError("Expectation unmatched");
            }
        }
    }
}
