import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Main {
    public record Observation(LocalDate date, double temperature, double rain) {
    }

    public record Summary(int days, double averageTemperature, double totalRain, double warmest) {
    }

    private Main() {
    }

    public static List<Observation> load(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).equals("date,tempC,rainMm")) {
            throw new IOException("Weather file must begin with date,tempC,rainMm");
        }
        List<Observation> observations = new ArrayList<>();
        Set<LocalDate> dates = new HashSet<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",", -1);
            if (fields.length != 3) {
                throw new IOException("Invalid observation on line " + (i + 1));
            }
            try {
                LocalDate date = LocalDate.parse(fields[0]);
                double temperature = Double.parseDouble(fields[1]);
                double rain = Double.parseDouble(fields[2]);
                if (!dates.add(date) || !Double.isFinite(temperature)
                        || !Double.isFinite(rain) || rain < 0) {
                    throw new IllegalArgumentException("Duplicate date or invalid measurement");
                }
                observations.add(new Observation(date, temperature, rain));
            } catch (IllegalArgumentException | DateTimeException error) {
                throw new IOException("Invalid observation on line " + (i + 1), error);
            }
        }
        return observations;
    }

    public static Summary summarize(List<Observation> observations, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
        List<Observation> selected = observations.stream()
                .filter(item -> !item.date().isBefore(from) && !item.date().isAfter(to)).toList();
        if (selected.isEmpty()) {
            return new Summary(0, 0, 0, 0);
        }
        double average = selected.stream().mapToDouble(Observation::temperature).average().orElseThrow();
        double rain = selected.stream().mapToDouble(Observation::rain).sum();
        double warmest = selected.stream().mapToDouble(Observation::temperature).max().orElseThrow();
        return new Summary(selected.size(), average, rain, warmest);
    }

    private static void print(String label, Summary summary) {
        if (summary.days() == 0) {
            System.out.println(label + ": no observations");
            return;
        }
        System.out.printf(Locale.ROOT, "%s: %d days, average %.1f C, rain %.1f mm, warmest %.1f C%n",
                label, summary.days(), summary.averageTemperature(), summary.totalRain(), summary.warmest());
    }

    public static void main(String[] args) {
        if (args.length != 4 || (!args[0].equals("summary") && !args[0].equals("compare"))) {
            System.out.println("Usage: Main summary FILE.csv FROM TO | compare FILE.csv WEEK1_START WEEK2_START");
            return;
        }
        try {
            List<Observation> observations = load(Path.of(args[1]));
            LocalDate first = LocalDate.parse(args[2]);
            LocalDate second = LocalDate.parse(args[3]);
            if (args[0].equals("summary")) {
                print("Summary", summarize(observations, first, second));
            } else {
                Summary a = summarize(observations, first, first.plusDays(6));
                Summary b = summarize(observations, second, second.plusDays(6));
                if (a.days() == 0 || b.days() == 0) {
                    throw new IllegalArgumentException("Each week needs at least one observation");
                }
                print("Week 1", a);
                print("Week 2", b);
                System.out.printf(Locale.ROOT, "Average temperature change: %+.1f C%n",
                        b.averageTemperature() - a.averageTemperature());
            }
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }
}
