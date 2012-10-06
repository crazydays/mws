package org.crazydays.mws.expect;


import android.test.AndroidTestCase;

public class AbstractExpectationTests
    extends AndroidTestCase
{
    public void testIsMatched_not_matched()
    {
        AbstractExpectation expectation = new AbstractExpectation() {};

        assertFalse("true", expectation.isMatched());
    }

    public void testIsMatched_matched()
    {
        AbstractExpectation expectation = new AbstractExpectation() {};
        expectation.matched();

        assertTrue("false", expectation.isMatched());
    }

    public void testMatched()
    {
        AbstractExpectation expectation = new AbstractExpectation() {};
        expectation.matched();

        assertTrue("false", expectation.isMatched());
    }
}
