package org.crazydays.mws;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.crazydays.mws.expect.Expect;
import org.crazydays.mws.handler.ExpectHttpRequestHandler;
import org.crazydays.mws.http.WebServer;
import org.crazydays.mws.respond.Respond;

public class MockWebService
{
    private WebServer server;

    private List<Expect> expects;
    private Map<String, ExpectHttpRequestHandler> handlers;

    /**
     * MockWebService constructor.
     * 
     * @param port Listen Port (Must be above 1024)
     */
    public MockWebService(int port)
    {
        server = new WebServer(port);
        expects = new LinkedList<Expect>();
        handlers = new HashMap<String, ExpectHttpRequestHandler>();
    }

    /**
     * Add expect and respond for specified path.
     * 
     * @param path Path
     * @param expect Expect
     * @param respond Respond
     */
    public void expectAndRespond(String path, Expect expect, Respond respond)
    {
        addExpect(expect);
        addExpectAndRespondToHandler(path, expect, respond);
    }

    private void addExpect(Expect expect)
    {
        expects.add(expect);
    }

    private void addExpectAndRespondToHandler(String path, Expect expect,
        Respond respond)
    {
        ExpectHttpRequestHandler handler = getHandler(path);
        handler.expectAndRespond(expect, respond);
    }

    private ExpectHttpRequestHandler getHandler(String path)
    {
        if (!handlers.containsKey(path)) {
            handlers.put(path, new ExpectHttpRequestHandler());
        }

        return handlers.get(path);
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

    /**
     * Verify all expectations were met.
     */
    public void verify()
    {
        for (Expect expect : expects) {
            expect.verify();
        }
    }

    public void teardown()
    {
        server.stop();
    }
}
