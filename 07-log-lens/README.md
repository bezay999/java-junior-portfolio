# Log Lens

When an app writes a lot of log lines, a quick summary is easier to read than scrolling through the whole file. Log Lens counts INFO, WARN and ERROR entries and shows the most repeated error messages.

With JDK 17+, run from this folder:

```bash
mkdir -p out
javac --release 17 -d out src/*.java
java -cp out Main samples/app.log
```

Each line must be `ISO_INSTANT LEVEL message`, for example `2026-10-13T10:00:00Z ERROR Connection timed out`. Malformed lines are counted and skipped, so one bad line does not hide the rest of the report. This tool reads a file but does not change it.

To run the checks, compile `src/*.java test/*.java` into `out` and run `java -cp out LogTest`.
