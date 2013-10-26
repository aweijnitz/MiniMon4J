#MiniMon4J
Java implementation of [MiniMon.js](https://github.com/aweijnitz/MiniMon) using [Undertow.io](http://undertow.io/) and [RounderDB4J](https://github.com/aweijnitz/RounderDB4J).

## Status
Early working prototype. 

The REST API is compatible with the Minomon.js API. Uses RounderDB4J as storage backend and Undertow for the REST service.


##Install and run
- Clone main dependency [RounderDB4J](https://github.com/aweijnitz/RounderDB4J)
- Compile and install RounderDB4J locally, `mvn install`
- Clone this repository
- Verify by running tests, `mvn test`
- Run server `mvn exec:java`
- Server is now running on localhost, port 8000. Test: `curl -X get http://localhost:8000/cpuLoad`

## Next
- DB persistence (need to add that to RounderDB)
- Configuration (host, port, intervals, etc)
- Pluggable data collectors (make it easy to add collectors and matching storage archives) 

