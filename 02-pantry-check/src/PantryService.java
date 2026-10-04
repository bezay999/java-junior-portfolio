import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PantryService {
    private final PantryStore store;
    private final Clock clock;
    private final List<PantryItem> items;

    public PantryService(PantryStore store, Clock clock) throws IOException {
        this.store = store;
        this.clock = clock;
        this.items = new ArrayList<>(store.load());
    }

    public PantryItem add(String name, int quantity, LocalDate expiresOn) throws IOException {
        int nextId = items.stream().mapToInt(PantryItem::id).max().orElse(0) + 1;
        PantryItem item = new PantryItem(nextId, name, quantity, expiresOn);
        items.add(item);
        store.save(items);
        return item;
    }

    public void remove(int id) throws IOException {
        boolean removed = items.removeIf(item -> item.id() == id);
        if (!removed) {
            throw new IllegalArgumentException("Item #" + id + " was not found");
        }
        store.save(items);
    }

    public List<PantryItem> list() {
        return items.stream()
                .sorted(Comparator.comparing(PantryItem::expiresOn).thenComparingInt(PantryItem::id))
                .toList();
    }

    public List<PantryItem> soon(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days must be zero or greater");
        }
        LocalDate today = LocalDate.now(clock);
        LocalDate deadline = today.plusDays(days);
        return list().stream()
                .filter(item -> !item.expiresOn().isBefore(today) && !item.expiresOn().isAfter(deadline))
                .toList();
    }

    public List<PantryItem> expired() {
        LocalDate today = LocalDate.now(clock);
        return list().stream().filter(item -> item.expiresOn().isBefore(today)).toList();
    }
}
