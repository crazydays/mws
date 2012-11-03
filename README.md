# Mock Web Server

Mock Web Server (MWS) is a library intended to ease testing calls to a web
server.  It works similar to other mock libraries with expect, replay, and
verify pattern.

* Expect

Expectations which can be set are the http method (GET, POST, PUT, DELETE, HEAD,
OPTIONS, TRACE), verifying exact header values, and a JSON object.  The number of
times that an expectation is matched can also be defined.

* Respond

Each expecation defines a response.  Header and JSON objects can be defined for
the response which is returned to the request.

* Replay

Start the web service with the currently defined expectations.

* Verify

Assert that all the set expectations were actually met the expected number of times.

## Example

An [example](https://github.com/crazydays/mws/blob/master/tests/src/org/crazydays/mws/MockWebServiceTests.java)
which shows the intended layout of the expect, replay, and verify pattern.

## Usage

MWS can be be used in two ways, either as a library project in the Android test
project or as a library jar.

### Library Project

It can be included into an Android test project by adding the dependency in the
test project which will require MWS to be checked out directly from git.

- Clone the github project
- Import the `mws/library` project into eclipse
- Add the project as a library to the desired test project

### Jar

MWS can also be built as a jar and that jar can be included test project libs directory
and included into the eclipse build path.

- Clone the github project
- Run `ant release` in the mws/library directory
- Rename `mws/library/bin/classes.jar` to `mws.jar`
- Copy `mws.jar` to your test project `libs` directory
- In eclipse add the `mws.jar` to your Android test project build path
