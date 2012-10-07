package org.crazydays.mws.expect;


import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface Expectation
{
    public boolean didMatch();

    public boolean matches(HttpRequest request)
        throws IOException;

    public void respond(HttpResponse response)
        throws IOException;
}
