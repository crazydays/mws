package org.crazydays.mws.expect;


import java.io.IOException;

import junit.framework.AssertionFailedError;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;

import android.test.AndroidTestCase;

public class AbstractExpectationTests
    extends AndroidTestCase
{
    class TestAbstractExpectation
        extends AbstractExpectation
    {
        @Override
        public boolean matches(HttpRequest request)
            throws IOException
        {
            return false;
        }

        @Override
        public void respond(HttpResponse response)
            throws IOException
        {
        }
    }

    public void testIsMatched_not_matched()
    {
        AbstractExpectation expectation = new TestAbstractExpectation();

        assertFalse("true", expectation.didMatch());
    }

    public void testIsMatched_matched()
    {
        AbstractExpectation expectation = new TestAbstractExpectation();
        expectation.matched();

        assertTrue("false", expectation.didMatch());
    }

    public void testMatched()
    {
        AbstractExpectation expectation = new TestAbstractExpectation();
        expectation.matched();

        assertTrue("false", expectation.didMatch());
    }

    public void testCastHttpEntityEnclosingRequest_expected_failure()
    {
        AbstractExpectation expectation = new TestAbstractExpectation();

        HttpRequest request = new BasicHttpRequest("GET", "http://127.0.0.1");
        try {
            expectation.castHttpEntityEnclosingRequest(request);
            fail("Expected Error");
        } catch (AssertionFailedError e) {
            assertEquals("Wrong failure",
                "Request doesn't implement HttpEntityEnclosingRequest",
                e.getMessage());
        }
    }

    public void testCastHttpEntityEnclosingRequest_expect_success()
    {
        AbstractExpectation expectation = new TestAbstractExpectation();

        HttpRequest request =
            new BasicHttpEntityEnclosingRequest("GET", "http://127.0.0.1");
        try {
            expectation.castHttpEntityEnclosingRequest(request);
        } catch (AssertionFailedError e) {
            fail("Expected Success");
        }
    }
}
