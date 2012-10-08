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

    public class MockWebServiceTests
        extends AndroidTestCase
    {
        private final static int PORT = 8080;

        private HttpClient client;
        private MockWebService service;

        @Override
        public void setUp()
            throws Exception
        {
            super.setUp();
            client = new DefaultHttpClient();
            service = new MockWebService(PORT);
        }

        @Override
        public void tearDown()
            throws Exception
        {
            service.teardown();
            super.tearDown();
        }

        public void testIntegration_success()
            throws JSONException, ClientProtocolException, IOException
        {
            JSONObject expect = new JSONObject().put("one", "two");
            JSONObject respond = new JSONObject().put("abc", "zyx");

            service.expect("/foo/bar", new JSONExpectation(expect, respond));
            service.replay();

            StringEntity entity = new StringEntity(expect.toString(), "UTF-8");
            entity.setContentType("application/json");

            HttpPost post = new HttpPost();
            post.setURI(buildUri("/foo/bar"));
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            assertTrue("response", JSONUtils.equals(respond,
                (JSONObject) new JSONTokener(EntityUtils.toString(response
                    .getEntity())).nextValue()));

            service.verify();
        }

        private URI buildUri(String context)
        {
            try {
                return new URI("http", null, "127.0.0.1", PORT, context, null, null);
            } catch (URISyntaxException e) {
                fail("Unable to build URI: " + e.getMessage());
                return null;
            }
        }
    }
