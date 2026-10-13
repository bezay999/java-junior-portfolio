import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public final class WeatherTest {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("weather-", ".csv");
        Files.writeString(file, "date,tempC,rainMm\n2026-10-01,10,0\n2026-10-02,14,2.5\n");
        List<Main.Observation> data = Main.load(file);
        Main.Summary summary = Main.summarize(data, LocalDate.parse("2026-10-01"),
                LocalDate.parse("2026-10-07"));
        check(summary.days() == 2, "Should count observed days");
        check(summary.averageTemperature() == 12, "Average should use observed days");
        check(summary.totalRain() == 2.5, "Rain should add up");
        check(summary.warmest() == 14, "Warmest day should be found");
        check(Main.summarize(data, LocalDate.parse("2026-11-01"),
                LocalDate.parse("2026-11-07")).days() == 0, "Empty range should work");
        Files.writeString(file, "date,tempC,rainMm\n2026-10-01,10,0\n2026-10-01,11,0\n");
        try {
            Main.load(file);
            throw new AssertionError("Duplicate dates should fail");
        } catch (java.io.IOException expected) {
        }
        System.out.println("Weather Notes checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
