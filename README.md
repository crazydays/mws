Mock Web Server
===============
Mock Web Server is a library intended to ease testing calls to a web server.
It works similar to other mock libraries with expect, replay, and verify.

* Expect

Currently the only supported type of expectation is JSONExpectation which will
listen for an expected JSON object and then respond will the specified JSON
object.

* Replay

Start the web service with the currently defined expectations.

* Verify

Assert that all the set expectations were actually met.


Example
=======

An example which shows the intended layout of the expect, replay, and verify 
pattern. [link](https://github.com/crazydays/mws/blob/master/tests/src/org/crazydays/mws/MockWebServiceTests.java)