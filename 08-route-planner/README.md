# Route Planner

This one treats stops as a small network. Give it two stops and it finds a route with the fewest connections using breadth-first search. It is not a real map or timetable; the sample network is just enough to make the algorithm visible.

With JDK 17+, run from this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main samples/stops.csv Library Station
```

The CSV has a `from,to` header and one undirected connection per line. Stop names are case-sensitive and cannot contain commas. Unknown or disconnected stops get a clear message.

To run the checks, compile `src/*.java test/*.java` into `out` and run `java -cp out RouteTest`.
