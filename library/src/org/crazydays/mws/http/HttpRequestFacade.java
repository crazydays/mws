package org.crazydays.mws.http;


import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;

public interface HttpRequestFacade
{
    public String getMethod();

    public List<String> getHeaderKeys();

    public List<String> getHeaders(String name);

    public boolean hasEntityContent();

    public String getEntityContent()
        throws ParseException, IOException;
}
