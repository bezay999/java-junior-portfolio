# Shared Bill Splitter

Splitting a bill sounds easy until it has a cent that cannot be divided evenly. This little tool gives each person a share in cents, so the printed amounts always add back up to the original bill.

With JDK 17+, run from this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main 42.17 Ada Ben Cara
```

The first people on the command line receive any leftover cents. Names must be distinct (ignoring case), and the amount must be positive with at most two decimal places. There is no saved file; run it again for a new bill.

To run the checks:

```bash
javac --release 17 -d out src/*.java test/*.java
java -cp out BillTest
```
