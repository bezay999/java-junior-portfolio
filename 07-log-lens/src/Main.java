import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Main {
    public enum Level { INFO, WARN, ERROR }

    public record Report(Map<Level, Integer> counts, Map<String, Integer> errors, int malformed) {
        public int count(Level level) {
            return counts.getOrDefault(level, 0);
        }
    }

    private Main() {
    }

    public static Report summarize(List<String> lines) {
        Map<Level, Integer> counts = new EnumMap<>(Level.class);
        Map<String, Integer> errors = new HashMap<>();
        int malformed = 0;
        for (String line : lines) {
            String[] parts = line.split(" ", 3);
            if (parts.length != 3 || parts[2].isBlank()) {
                malformed++;
                continue;
            }
            try {
                Instant.parse(parts[0]);
                Level level = Level.valueOf(parts[1]);
                counts.merge(level, 1, Integer::sum);
                if (level == Level.ERROR) {
                    errors.merge(parts[2], 1, Integer::sum);
                }
            } catch (DateTimeParseException | IllegalArgumentException error) {
                malformed++;
            }
        }
        return new Report(Map.copyOf(counts), Map.copyOf(errors), malformed);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -cp out Main LOG_FILE");
            return;
        }
        try {
            Report report = summarize(Files.readAllLines(Path.of(args[0]), StandardCharsets.UTF_8));
            for (Level level : Level.values()) {
                System.out.println(level + ": " + report.count(level));
            }
            System.out.println("Malformed: " + report.malformed());
            System.out.println("Most common errors:");
            report.errors().entrySet().stream()
                    .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                            .reversed().thenComparing(Map.Entry::getKey))
                    .limit(3)
                    .forEach(entry -> System.out.println("  " + entry.getValue() + "x " + entry.getKey()));
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }
}
