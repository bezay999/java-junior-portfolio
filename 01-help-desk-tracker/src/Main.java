import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        String fileName = System.getenv("HELP_DESK_FILE");
        Path file = Path.of(fileName == null || fileName.isBlank()
                ? "data/tickets.tsv" : fileName);

        try {
            TicketService service = new TicketService(new TicketStore(file), Clock.systemUTC());
            run(args, service);
        } catch (IOException | IllegalArgumentException | IllegalStateException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }

    private static void run(String[] args, TicketService service) throws IOException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                requireArguments(args, 2);
                Ticket ticket = service.add(args[1]);
                System.out.println("Added ticket #" + ticket.id() + ": " + ticket.title());
            }
            case "list" -> {
                if (args.length > 2) {
                    throw new IllegalArgumentException("Usage: list [open|closed]");
                }
                TicketStatus status = args.length == 2 ? parseStatus(args[1]) : null;
                printTickets(service.list(status));
            }
            case "close" -> {
                requireArguments(args, 2);
                int id = Integer.parseInt(args[1]);
                Ticket ticket = service.close(id);
                System.out.println("Closed ticket #" + ticket.id() + ": " + ticket.title());
            }
            case "stats" -> {
                requireArguments(args, 1);
                System.out.println("Open: " + service.count(TicketStatus.OPEN));
                System.out.println("Closed: " + service.count(TicketStatus.CLOSED));
            }
            default -> printUsage();
        }
    }

    private static TicketStatus parseStatus(String value) {
        return switch (value.toLowerCase()) {
            case "open" -> TicketStatus.OPEN;
            case "closed" -> TicketStatus.CLOSED;
            default -> throw new IllegalArgumentException("Status must be open or closed");
        };
    }

    private static void requireArguments(String[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Wrong number of arguments; run without arguments for help");
        }
    }

    private static void printTickets(List<Ticket> tickets) {
        if (tickets.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }
        for (Ticket ticket : tickets) {
            System.out.printf("#%d  %-6s  %s  %s%n",
                    ticket.id(), ticket.status(), ticket.createdAt(), ticket.title());
        }
    }

    private static void printUsage() {
        System.out.println("Help Desk Tracker");
        System.out.println("  add \"ticket title\"   Create a ticket");
        System.out.println("  list [open|closed]  Show tickets");
        System.out.println("  close ID            Close a ticket");
        System.out.println("  stats               Show ticket counts");
    }
}

