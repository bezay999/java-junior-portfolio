import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class TicketStore {
    private final Path file;

    public TicketStore(Path file) {
        this.file = file;
    }

    public List<Ticket> load() throws IOException {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }

        List<Ticket> tickets = new ArrayList<>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNumber++;
            String[] fields = line.split("\t", -1);
            if (fields.length != 4) {
                throw new IOException("Invalid ticket data on line " + lineNumber);
            }
            try {
                tickets.add(new Ticket(
                        Integer.parseInt(fields[0]),
                        fields[3],
                        TicketStatus.valueOf(fields[1]),
                        Instant.parse(fields[2])));
            } catch (IllegalArgumentException error) {
                throw new IOException("Invalid ticket data on line " + lineNumber, error);
            }
        }
        return tickets;
    }

    public void save(List<Ticket> tickets) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        Files.createDirectories(parent);

        List<String> lines = new ArrayList<>();
        for (Ticket ticket : tickets) {
            lines.add(ticket.id() + "\t" + ticket.status() + "\t"
                    + ticket.createdAt() + "\t" + ticket.title());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}

