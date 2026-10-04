import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public record Habit(int id, String name, Set<LocalDate> checkIns) {
    public Habit {
        if (id < 1) {
            throw new IllegalArgumentException("Habit ID must be positive");
        }
        if (name == null || name.isBlank() || name.length() > 80
                || name.indexOf('\t') >= 0 || name.indexOf('\n') >= 0 || name.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Name must be 1–80 characters without tabs or line breaks");
        }
        if (checkIns == null || checkIns.stream().anyMatch(day -> day == null)) {
            throw new IllegalArgumentException("Check-in dates are required");
        }
        checkIns = Set.copyOf(checkIns);
    }

    public Habit mark(LocalDate day) {
        if (checkIns.contains(day)) {
            throw new IllegalStateException("Habit #" + id + " is already marked on " + day);
        }
        Set<LocalDate> updated = new HashSet<>(checkIns);
        updated.add(day);
        return new Habit(id, name, updated);
    }

    public int streak(LocalDate today) {
        LocalDate day = checkIns.contains(today) ? today : today.minusDays(1);
        int count = 0;
        while (checkIns.contains(day)) {
            count++;
            day = day.minusDays(1);
        }
        return count;
    }
}
