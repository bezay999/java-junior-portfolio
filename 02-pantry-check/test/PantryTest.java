import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class PantryTest {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempDirectory("pantry-test-").resolve("pantry.tsv");
        Clock clock = Clock.fixed(Instant.parse("2026-10-04T12:00:00Z"), ZoneOffset.UTC);

        PantryService firstRun = new PantryService(new PantryStore(file), clock);
        PantryItem milk = firstRun.add("Milk", 2, LocalDate.parse("2026-10-06"));
        firstRun.add("Rice", 1, LocalDate.parse("2027-01-01"));
        firstRun.add("Old cheese", 1, LocalDate.parse("2026-10-03"));

        PantryService secondRun = new PantryService(new PantryStore(file), clock);
        check(secondRun.list().size() == 3, "Items should survive a restart");
        check(secondRun.soon(3).size() == 1, "Only milk expires soon");
        check(secondRun.soon(3).get(0).id() == milk.id(), "Soon should find milk");
        check(secondRun.expired().size() == 1, "Old cheese is expired");
        secondRun.remove(milk.id());
        check(secondRun.add("Eggs", 6, LocalDate.parse("2026-10-10")).id() == 4,
                "New items should receive the next ID in this list");

        PantryService thirdRun = new PantryService(new PantryStore(file), clock);
        check(thirdRun.list().size() == 3, "Removal should be saved");
        check(thirdRun.soon(3).isEmpty(), "Milk should be gone");

        System.out.println("Pantry Check checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
