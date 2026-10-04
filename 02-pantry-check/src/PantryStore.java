import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class PantryStore {
    private final Path file;

    public PantryStore(Path file) {
        this.file = file;
    }

    public List<PantryItem> load() throws IOException {
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        List<PantryItem> items = new ArrayList<>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNumber++;
            String[] fields = line.split("\t", -1);
            if (fields.length != 4) {
                throw new IOException("Invalid pantry data on line " + lineNumber);
            }
            try {
                items.add(new PantryItem(Integer.parseInt(fields[0]), fields[1],
                        Integer.parseInt(fields[2]), LocalDate.parse(fields[3])));
            } catch (IllegalArgumentException | DateTimeException error) {
                throw new IOException("Invalid pantry data on line " + lineNumber, error);
            }
        }
        return items;
    }

    public void save(List<PantryItem> items) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        List<String> lines = new ArrayList<>();
        for (PantryItem item : items) {
            lines.add(item.id() + "\t" + item.name() + "\t" + item.quantity()
                    + "\t" + item.expiresOn());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}
