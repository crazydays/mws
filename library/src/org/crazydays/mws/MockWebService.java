package org.crazydays.mws;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.crazydays.mws.expect.Expectation;
import org.crazydays.mws.handler.ExpectationHttpRequestHandler;
import org.crazydays.mws.http.WebServer;

public class MockWebService
{
    private WebServer server;
    private List<Expectation> expectations;
    private Map<String, ExpectationHttpRequestHandler> handlers;

    /**
     * MockWebService constructor.
     * 
     * @param port Listen Port (Must be above 1024)
     */
    public MockWebService(int port)
    {
        server = new WebServer(port);
        expectations = new LinkedList<Expectation>();
        handlers = new HashMap<String, ExpectationHttpRequestHandler>();
    }

    /**
     * Add expectation.
     * 
     * @param path Expectation path mapping
     * @param expectation Expectation
     */
    public void expect(String path, Expectation expectation)
    {
        addExpectation(expectation);
        addHandler(path, expectation);
    }

    /**
     * Start the server with expectation mappings.
     */
    public void replay()
    {
        for (String path : handlers.keySet()) {
            server.addHandler(path, handlers.get(path));
        }
        server.start();
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

    /**
     * Verify all expectations were met.
     */
    public void verify()
    {
        for (Expectation expectation : expectations) {
            if (!expectation.didMatch()) {
                throw new AssertionFailedError("Expectation unmatched");
            }
        }
    }

    public void teardown()
    {
        server.stop();
    }
}
