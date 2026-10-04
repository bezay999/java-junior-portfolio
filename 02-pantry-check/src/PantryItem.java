import java.time.LocalDate;

public record PantryItem(int id, String name, int quantity, LocalDate expiresOn) {
    public PantryItem {
        if (id < 1 || quantity < 1) {
            throw new IllegalArgumentException("ID and quantity must be positive");
        }
        if (name == null || name.isBlank() || name.length() > 80
                || name.indexOf('\t') >= 0 || name.indexOf('\n') >= 0 || name.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Name must be 1–80 characters without tabs or line breaks");
        }
        if (expiresOn == null) {
            throw new IllegalArgumentException("Expiry date is required");
        }
    }
}
