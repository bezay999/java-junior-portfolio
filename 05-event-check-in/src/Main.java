import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class Main {
    public record Guest(String name, String email) {
    }

    private Main() {
    }

    public static Map<String, Guest> readGuests(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).equals("name,email")) {
            throw new IOException("Guest file must begin with name,email");
        }
        Map<String, Guest> guests = new LinkedHashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",", -1);
            if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank()) {
                throw new IOException("Invalid guest on line " + (i + 1));
            }
            String email = fields[1].trim().toLowerCase(Locale.ROOT);
            if (guests.putIfAbsent(email, new Guest(fields[0].trim(), email)) != null) {
                throw new IOException("Duplicate guest email on line " + (i + 1));
            }
        }
        return guests;
    }

    public static Set<String> readArrivals(Path file, Map<String, Guest> guests) throws IOException {
        Set<String> arrivals = new HashSet<>();
        if (!Files.exists(file)) {
            return arrivals;
        }
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String email = line.trim().toLowerCase(Locale.ROOT);
            if (!guests.containsKey(email) || !arrivals.add(email)) {
                throw new IOException("Invalid or repeated arrival: " + line);
            }
        }
        return arrivals;
    }

    public static void checkIn(Path file, String email, Map<String, Guest> guests,
                               Set<String> arrivals) throws IOException {
        String key = email.trim().toLowerCase(Locale.ROOT);
        if (!guests.containsKey(key)) {
            throw new IllegalArgumentException("Guest was not found: " + email);
        }
        if (!arrivals.add(key)) {
            throw new IllegalArgumentException("Guest is already checked in: " + email);
        }
        Files.createDirectories(file.toAbsolutePath().getParent());
        Files.write(file, arrivals.stream().sorted().toList(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        if (args.length < 3 || args.length > 4) {
            System.out.println("Usage: Main list|stats|checkin GUESTS.csv ARRIVALS.txt [EMAIL]");
            return;
        }
        try {
            String command = args[0].toLowerCase(Locale.ROOT);
            Map<String, Guest> guests = readGuests(Path.of(args[1]));
            Set<String> arrivals = readArrivals(Path.of(args[2]), guests);
            switch (command) {
                case "list" -> {
                    requireLength(args, 3);
                    for (Guest guest : guests.values()) {
                        System.out.printf("%-9s %s <%s>%n",
                                arrivals.contains(guest.email()) ? "ARRIVED" : "EXPECTED",
                                guest.name(), guest.email());
                    }
                }
                case "stats" -> {
                    requireLength(args, 3);
                    System.out.println("Arrived: " + arrivals.size() + " / " + guests.size());
                }
                case "checkin" -> {
                    requireLength(args, 4);
                    checkIn(Path.of(args[2]), args[3], guests, arrivals);
                    String email = args[3].trim().toLowerCase(Locale.ROOT);
                    System.out.println("Checked in: " + guests.get(email).name());
                }
                default -> throw new IllegalArgumentException("Unknown command: " + command);
            }
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }

    private static void requireLength(String[] args, int length) {
        if (args.length != length) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }
    }
}
