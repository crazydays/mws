package org.crazydays.mws.expect;


public abstract class AbstractExpectation
    implements Expectation
{
    private boolean matched;

    public void matched()
    {
        matched = true;
    }

    @Override
    public boolean isMatched()
    {
        return matched;
    }
}
