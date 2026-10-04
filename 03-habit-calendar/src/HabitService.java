import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public final class HabitService {
    private final HabitStore store;
    private final Clock clock;
    private final List<Habit> habits;

    public HabitService(HabitStore store, Clock clock) throws IOException {
        this.store = store;
        this.clock = clock;
        this.habits = new ArrayList<>(store.load());
    }

    public Habit add(String name) throws IOException {
        int nextId = habits.stream().mapToInt(Habit::id).max().orElse(0) + 1;
        Habit habit = new Habit(nextId, name, Set.of());
        habits.add(habit);
        store.save(habits);
        return habit;
    }

    public Habit mark(int id, LocalDate day) throws IOException {
        if (day.isAfter(LocalDate.now(clock))) {
            throw new IllegalArgumentException("Cannot mark a future date");
        }
        for (int index = 0; index < habits.size(); index++) {
            Habit habit = habits.get(index);
            if (habit.id() == id) {
                Habit updated = habit.mark(day);
                habits.set(index, updated);
                store.save(habits);
                return updated;
            }
        }
        throw new IllegalArgumentException("Habit #" + id + " was not found");
    }

    public List<Habit> list() {
        return habits.stream().sorted(Comparator.comparingInt(Habit::id)).toList();
    }

    public Habit find(int id) {
        return habits.stream().filter(habit -> habit.id() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Habit #" + id + " was not found"));
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }
}
