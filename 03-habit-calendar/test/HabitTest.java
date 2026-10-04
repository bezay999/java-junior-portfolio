import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class HabitTest {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempDirectory("habit-test-").resolve("habits.tsv");
        Clock clock = Clock.fixed(Instant.parse("2026-10-04T12:00:00Z"), ZoneOffset.UTC);

        HabitService firstRun = new HabitService(new HabitStore(file), clock);
        Habit reading = firstRun.add("Read 20 minutes");
        firstRun.mark(reading.id(), LocalDate.parse("2026-10-02"));
        firstRun.mark(reading.id(), LocalDate.parse("2026-10-03"));
        check(firstRun.find(reading.id()).streak(firstRun.today()) == 2,
                "Yesterday's streak should still count");

        HabitService secondRun = new HabitService(new HabitStore(file), clock);
        check(secondRun.find(reading.id()).checkIns().size() == 2, "Dates should survive a restart");
        secondRun.mark(reading.id(), LocalDate.parse("2026-10-04"));
        check(secondRun.find(reading.id()).streak(secondRun.today()) == 3,
                "Today's check-in should extend the streak");
        expectFailure(() -> secondRun.mark(reading.id(), LocalDate.parse("2026-10-04")),
                "Duplicate dates should fail");
        expectFailure(() -> secondRun.mark(reading.id(), LocalDate.parse("2026-10-05")),
                "Future dates should fail");
        check(secondRun.add("Walk outside").id() == 2, "New habits should get the next ID");

        HabitService thirdRun = new HabitService(new HabitStore(file), clock);
        check(thirdRun.list().size() == 2, "Habits should survive a restart");
        check(thirdRun.find(reading.id()).streak(thirdRun.today()) == 3,
                "The streak should survive a restart");

        System.out.println("Habit Calendar checks passed.");
    }

    private static void expectFailure(Action action, String message) throws Exception {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException expected) {
            return;
        }
        throw new AssertionError(message);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private interface Action {
        void run() throws Exception;
    }
}
