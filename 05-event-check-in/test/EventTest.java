import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public final class EventTest {
    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("event-test-");
        Path guestsFile = folder.resolve("guests.csv");
        Path arrivalsFile = folder.resolve("arrivals.txt");
        Files.writeString(guestsFile, "name,email\nAda,ada@example.com\nBen,ben@example.com\n");
        Map<String, Main.Guest> guests = Main.readGuests(guestsFile);
        Set<String> arrivals = Main.readArrivals(arrivalsFile, guests);
        Main.checkIn(arrivalsFile, "ADA@example.com", guests, arrivals);
        check(Main.readArrivals(arrivalsFile, guests).size() == 1, "Arrival should survive restart");
        expectFailure(() -> Main.checkIn(arrivalsFile, "ada@example.com", guests, arrivals));
        expectFailure(() -> Main.checkIn(arrivalsFile, "missing@example.com", guests, arrivals));
        Files.writeString(guestsFile, "name,email\nAda,ada@example.com\nOther,ADA@example.com\n");
        try {
            Main.readGuests(guestsFile);
            throw new AssertionError("Duplicate guest should fail");
        } catch (java.io.IOException expected) {
        }
        System.out.println("Event Check-In checks passed.");
    }

    private static void expectFailure(Action action) throws Exception {
        try {
            action.run();
            throw new AssertionError("Invalid check-in should fail");
        } catch (IllegalArgumentException expected) {
        }
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
