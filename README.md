#MiniMon4J
Java implementation of the [MiniMon.js](https://github.com/aweijnitz/MiniMon) using [Undertow.io](http://undertow.io/) and [RounderDB4J](https://github.com/aweijnitz/RounderDB4J).

## Status
Early working prototype. 

The REST API is compatible with the Minomon.js API. Uses RounderDB4J as storage backend and Undertow for the REST service.

## Next
- DB persistence (need to add that to RounderDB)
- Configuration (host, port, intervals, etc)
- Pluggable data collectors (make it easy to add collectors and matching storage archives) 

