package org.crazydays.mws.handler;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.crazydays.mws.expect.Expectation;

public class ExpectationHttpRequestHandler
    implements HttpRequestHandler
{
    private List<Expectation> expectations;

    public ExpectationHttpRequestHandler()
    {
        expectations = new LinkedList<Expectation>();
    }

    public void expect(Expectation expectation)
    {
        expectations.add(expectation);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
        HttpContext context)
        throws HttpException, IOException
    {
        for (Expectation expectation : expectations) {
            if (expectation.matches(request)) {
                expectation.respond(response);
                return;
            }
        }
    }
}
