package org.crazydays.mws.handler;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import org.crazydays.mws.expect.Expect;
import org.crazydays.mws.http.CachedHttpRequestFacade;
import org.crazydays.mws.http.HttpRequestFacade;
import org.crazydays.mws.respond.Respond;

public class ExpectHttpRequestHandler
    implements HttpRequestHandler
{
    private List<Expect> expects = new LinkedList<Expect>();
    private List<Respond> responds = new LinkedList<Respond>();

    public void expectAndRespond(Expect expect, Respond respond)
    {
        expects.add(expect);
        responds.add(respond);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
        HttpContext context)
        throws HttpException, IOException
    {
        HttpRequestFacade facade = new CachedHttpRequestFacade(request);

        for (int i = 0; i < expects.size(); i++) {
            if (expects.get(i).matches(facade)) {
                responds.get(i).respond(response);
                return;
            }
        }
    }
}
