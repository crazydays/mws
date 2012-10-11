package org.crazydays.mws.expect;


import java.io.IOException;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import static org.crazydays.mws.http.HttpConstants.*;

public class ExpectTests
    extends AndroidTestCase
{
    public void testWithJSON()
        throws JSONException, IOException
    {
        JSONObject json = new JSONObject().put("abc", "xyz");

        BasicHttpEntityEnclosingRequest request =
            new BasicHttpEntityEnclosingRequest("POST", "http://127.0.0.1");

        StringEntity entity = new StringEntity(json.toString());
        entity.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        request.setEntity(entity);

        Expect expect = new Expect().withJSON(json);

        assertTrue("!matched", expect.matches(request));
    }
}
