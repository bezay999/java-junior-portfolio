# Event Check-In

This is a small desk tool for a meetup: start with a guest list, mark arrivals by email, and see who is still expected. The sample list is fictional, so you can try it without preparing anything.

With JDK 17+, run from this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main list samples/guests.csv data/arrivals.txt
java -cp out Main checkin samples/guests.csv data/arrivals.txt ada@example.com
java -cp out Main stats samples/guests.csv data/arrivals.txt
```

The guest CSV has a `name,email` header and simple unquoted fields. Email matching ignores case. Duplicate guests and a second check-in are rejected. Arrivals are stored in a plain text file, ignored by Git.

Run the checks with `javac --release 17 -d out src/*.java test/*.java` followed by `java -cp out EventTest`.
