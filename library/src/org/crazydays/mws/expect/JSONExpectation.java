package org.crazydays.mws.expect;


import org.json.JSONObject;

public class JSONExpectation
    extends AbstractExpectation
{
    private JSONObject expect;
    private JSONObject response;

    public JSONExpectation(JSONObject expect, JSONObject response)
    {
        this.expect = expect;
        this.response = response;
    }

    public JSONObject getExpect()
    {
        return expect;
    }

    public JSONObject getResponse()
    {
        return response;
    }
}
