# Style_33 - Restful
Description from [Exercises in Programming Style](http://www.amazon.com/Exercises-Programming-Style-Cristina-Videira/dp/1482227371/)
* Interactive: end-to-end between an active agent (e.g. a person) and a backend.
* Separation between client and server. Communication between the two is synchronous in the form of request–response.
* Statelessness communication: every request from client to server must contain all the information necessary for the server to serve the request. The server should not store context of ongoing interaction; session state is on the client.
* Uniform interface: clients and servers handle resources, which have unique identifiers. Resources are operated on with a restrictive interface consisting of creation, modification, retrieval and deletion. The result of a resource request is a hypermedia representation that also drives the application state.
