import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class Main {
    public enum Stage { APPLIED, INTERVIEW, OFFER, REJECTED }

    public record Application(int id, String company, String role, Stage stage, LocalDate followUp) {
        public Application {
            if (id < 1 || !valid(company) || !valid(role) || stage == null || followUp == null) {
                throw new IllegalArgumentException("Invalid application details");
            }
        }

        private static boolean valid(String text) {
            return text != null && !text.isBlank() && text.length() <= 100
                    && text.indexOf('\t') < 0 && text.indexOf('\n') < 0 && text.indexOf('\r') < 0;
        }

        public Application withStage(Stage next) {
            return new Application(id, company, role, next, followUp);
        }

        public Application withFollowUp(LocalDate day) {
            return new Application(id, company, role, stage, day);
        }
    }

    private Main() {
    }

    public static List<Application> load(Path file) throws IOException {
        List<Application> applications = new ArrayList<>();
        if (!Files.exists(file)) {
            return applications;
        }
        int lineNumber = 0;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNumber++;
            String[] fields = line.split("\t", -1);
            if (fields.length != 5) {
                throw new IOException("Invalid application on line " + lineNumber);
            }
            try {
                applications.add(new Application(Integer.parseInt(fields[0]), fields[1], fields[2],
                        Stage.valueOf(fields[3]), LocalDate.parse(fields[4])));
            } catch (IllegalArgumentException | DateTimeException error) {
                throw new IOException("Invalid application on line " + lineNumber, error);
            }
        }
        return applications;
    }

    public static void save(Path file, List<Application> applications) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        List<String> lines = new ArrayList<>();
        for (Application item : applications) {
            lines.add(item.id() + "\t" + item.company() + "\t" + item.role()
                    + "\t" + item.stage() + "\t" + item.followUp());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public static Application add(List<Application> applications, String company, String role,
                                  LocalDate followUp) {
        int id = applications.stream().mapToInt(Application::id).max().orElse(0) + 1;
        Application item = new Application(id, company, role, Stage.APPLIED, followUp);
        applications.add(item);
        return item;
    }

    public static Application update(List<Application> applications, int id, Stage stage,
                                     LocalDate followUp) {
        for (int i = 0; i < applications.size(); i++) {
            Application item = applications.get(i);
            if (item.id() == id) {
                Application changed = stage != null ? item.withStage(stage) : item.withFollowUp(followUp);
                applications.set(i, changed);
                return changed;
            }
        }
        throw new IllegalArgumentException("Application #" + id + " was not found");
    }

    public static List<Application> due(List<Application> applications, LocalDate today) {
        return applications.stream()
                .filter(item -> item.stage() == Stage.APPLIED || item.stage() == Stage.INTERVIEW)
                .filter(item -> !item.followUp().isAfter(today))
                .sorted(Comparator.comparing(Application::followUp).thenComparingInt(Application::id))
                .toList();
    }

    private static void print(List<Application> applications) {
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        }
        for (Application item : applications) {
            System.out.printf("#%d  %-10s  %-18s  %-24s  follow up %s%n", item.id(),
                    item.stage(), item.company(), item.role(), item.followUp());
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: Main add COMPANY ROLE YYYY-MM-DD | list [STAGE] | status ID STAGE | followup ID YYYY-MM-DD | due");
            return;
        }
        String fileName = System.getenv("JOB_BOARD_FILE");
        Path file = Path.of(fileName == null || fileName.isBlank() ? "data/applications.tsv" : fileName);
        try {
            List<Application> applications = load(file);
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "add" -> {
                    requireLength(args, 4);
                    Application item = add(applications, args[1], args[2], LocalDate.parse(args[3]));
                    save(file, applications);
                    System.out.println("Added application #" + item.id());
                }
                case "list" -> {
                    if (args.length == 1) {
                        print(applications);
                    } else {
                        requireLength(args, 2);
                        Stage stage = Stage.valueOf(args[1].toUpperCase(Locale.ROOT));
                        print(applications.stream().filter(item -> item.stage() == stage).toList());
                    }
                }
                case "status" -> {
                    requireLength(args, 3);
                    update(applications, Integer.parseInt(args[1]),
                            Stage.valueOf(args[2].toUpperCase(Locale.ROOT)), null);
                    save(file, applications);
                    System.out.println("Stage updated.");
                }
                case "followup" -> {
                    requireLength(args, 3);
                    update(applications, Integer.parseInt(args[1]), null, LocalDate.parse(args[2]));
                    save(file, applications);
                    System.out.println("Follow-up updated.");
                }
                case "due" -> {
                    requireLength(args, 1);
                    print(due(applications, LocalDate.now()));
                }
                default -> throw new IllegalArgumentException("Unknown command: " + args[0]);
            }
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }

    private static void requireLength(String[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Wrong number of arguments; run without arguments for help");
        }
    }
}
