import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        String fileName = System.getenv("PANTRY_FILE");
        Path file = Path.of(fileName == null || fileName.isBlank() ? "data/pantry.tsv" : fileName);
        try {
            run(args, new PantryService(new PantryStore(file), Clock.systemDefaultZone()));
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }

    private static void run(String[] args, PantryService service) throws IOException {
        if (args.length == 0) {
            usage();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add" -> {
                requireArgs(args, 4);
                PantryItem item = service.add(args[1], Integer.parseInt(args[2]), LocalDate.parse(args[3]));
                System.out.println("Added item #" + item.id() + ": " + item.name());
            }
            case "list" -> {
                requireArgs(args, 1);
                print(service.list());
            }
            case "soon" -> {
                requireArgs(args, 2);
                print(service.soon(Integer.parseInt(args[1])));
            }
            case "expired" -> {
                requireArgs(args, 1);
                print(service.expired());
            }
            case "remove" -> {
                requireArgs(args, 2);
                int id = Integer.parseInt(args[1]);
                service.remove(id);
                System.out.println("Removed item #" + id);
            }
            default -> usage();
        }
    }

    private static void print(List<PantryItem> items) {
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }
        for (PantryItem item : items) {
            System.out.printf("#%d  %-24s  x%d  expires %s%n",
                    item.id(), item.name(), item.quantity(), item.expiresOn());
        }
    }

    private static void requireArgs(String[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Wrong number of arguments; run without arguments for help");
        }
    }

    private static void usage() {
        System.out.println("Pantry Check");
        System.out.println("  add \"name\" QUANTITY YYYY-MM-DD   Add an item");
        System.out.println("  list                            Show all items");
        System.out.println("  soon DAYS                       Expiring within DAYS");
        System.out.println("  expired                         Show expired items");
        System.out.println("  remove ID                       Remove an item");
    }
}
