import java.time.Instant;

public record Ticket(int id, String title, TicketStatus status, Instant createdAt) {
    public Ticket {
        if (id < 1) {
            throw new IllegalArgumentException("Ticket ID must be positive");
        }
        if (title == null || title.isBlank() || title.length() > 100
                || title.indexOf('\t') >= 0 || title.indexOf('\n') >= 0 || title.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Title must be 1–100 characters without tabs or line breaks");
        }
        if (status == null || createdAt == null) {
            throw new IllegalArgumentException("Ticket status and creation time are required");
        }
    }

    public Ticket close() {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Ticket #" + id + " is already closed");
        }
        return new Ticket(id, title, TicketStatus.CLOSED, createdAt);
    }
}

