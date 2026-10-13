import java.util.List;

public final class LogTest {
    public static void main(String[] args) {
        Main.Report report = Main.summarize(List.of(
                "2026-10-13T10:00:00Z INFO Started",
                "2026-10-13T10:01:00Z ERROR Timeout",
                "2026-10-13T10:02:00Z ERROR Timeout",
                "2026-10-13T10:03:00Z WARN Slow",
                "not a log line",
                "2026-10-13T10:04:00Z DEBUG Unknown level"));
        check(report.count(Main.Level.INFO) == 1, "INFO should count");
        check(report.count(Main.Level.ERROR) == 2, "ERROR should count");
        check(report.errors().get("Timeout") == 2, "Repeated messages should group");
        check(report.malformed() == 2, "Bad lines should be skipped and counted");
        System.out.println("Log Lens checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
