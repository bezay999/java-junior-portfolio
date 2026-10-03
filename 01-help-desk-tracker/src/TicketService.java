import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TicketService {
    private final TicketStore store;
    private final Clock clock;
    private final List<Ticket> tickets;

    public TicketService(TicketStore store, Clock clock) throws IOException {
        this.store = store;
        this.clock = clock;
        this.tickets = new ArrayList<>(store.load());
    }

    public Ticket add(String title) throws IOException {
        int nextId = tickets.stream().mapToInt(Ticket::id).max().orElse(0) + 1;
        Ticket ticket = new Ticket(nextId, title, TicketStatus.OPEN, clock.instant());
        tickets.add(ticket);
        store.save(tickets);
        return ticket;
    }

    public Ticket close(int id) throws IOException {
        for (int index = 0; index < tickets.size(); index++) {
            Ticket ticket = tickets.get(index);
            if (ticket.id() == id) {
                Ticket closed = ticket.close();
                tickets.set(index, closed);
                store.save(tickets);
                return closed;
            }
        }
        throw new IllegalArgumentException("Ticket #" + id + " was not found");
    }

    public List<Ticket> list(TicketStatus status) {
        return tickets.stream()
                .filter(ticket -> status == null || ticket.status() == status)
                .sorted(Comparator.comparingInt(Ticket::id))
                .toList();
    }

    public long count(TicketStatus status) {
        return tickets.stream().filter(ticket -> ticket.status() == status).count();
    }
}

