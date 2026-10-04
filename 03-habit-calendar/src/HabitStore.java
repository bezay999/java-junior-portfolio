import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class HabitStore {
    private final Path file;

    public HabitStore(Path file) {
        this.file = file;
    }

    public List<Habit> load() throws IOException {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        List<Habit> habits = new ArrayList<>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNumber++;
            String[] fields = line.split("\t", -1);
            if (fields.length != 3) {
                throw new IOException("Invalid habit data on line " + lineNumber);
            }
            try {
                Set<LocalDate> days = new HashSet<>();
                if (!fields[2].isEmpty()) {
                    for (String value : fields[2].split(",", -1)) {
                        if (!days.add(LocalDate.parse(value))) {
                            throw new IllegalArgumentException("Duplicate check-in date");
                        }
                    }
                }
                habits.add(new Habit(Integer.parseInt(fields[0]), fields[1], days));
            } catch (IllegalArgumentException | DateTimeException error) {
                throw new IOException("Invalid habit data on line " + lineNumber, error);
            }
        }
        return habits;
    }

    public void save(List<Habit> habits) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        List<String> lines = new ArrayList<>();
        for (Habit habit : habits) {
            String dates = habit.checkIns().stream().sorted().map(LocalDate::toString)
                    .collect(Collectors.joining(","));
            lines.add(habit.id() + "\t" + habit.name() + "\t" + dates);
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}
