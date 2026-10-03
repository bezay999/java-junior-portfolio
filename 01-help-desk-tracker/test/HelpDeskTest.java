import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public final class HelpDeskTest {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempDirectory("help-desk-test-").resolve("tickets.tsv");
        Clock clock = Clock.fixed(Instant.parse("2026-10-03T12:00:00Z"), ZoneOffset.UTC);

        TicketService firstRun = new TicketService(new TicketStore(file), clock);
        Ticket first = firstRun.add("Printer is offline");
        check(first.id() == 1, "First ticket should use ID 1");
        check(firstRun.count(TicketStatus.OPEN) == 1, "New ticket should be open");

        TicketService secondRun = new TicketService(new TicketStore(file), clock);
        Ticket second = secondRun.add("Screen is flickering");
        check(second.id() == 2, "IDs should continue after a restart");
        secondRun.close(first.id());

        TicketService thirdRun = new TicketService(new TicketStore(file), clock);
        check(thirdRun.count(TicketStatus.OPEN) == 1, "One ticket should remain open");
        check(thirdRun.count(TicketStatus.CLOSED) == 1, "Closed ticket should be saved");
        check(thirdRun.list(null).get(0).title().equals("Printer is offline"),
                "Ticket titles should survive a restart");

        System.out.println("Help Desk Tracker checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
