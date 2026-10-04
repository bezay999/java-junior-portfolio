import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        String fileName = System.getenv("HABIT_FILE");
        Path file = Path.of(fileName == null || fileName.isBlank() ? "data/habits.tsv" : fileName);
        try {
            run(args, new HabitService(new HabitStore(file), Clock.systemDefaultZone()));
        } catch (IOException | IllegalArgumentException | IllegalStateException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }

    private static void run(String[] args, HabitService service) throws IOException {
        if (args.length == 0) {
            usage();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add" -> {
                requireArgs(args, 2);
                Habit habit = service.add(args[1]);
                System.out.println("Added habit #" + habit.id() + ": " + habit.name());
            }
            case "mark" -> {
                if (args.length != 2 && args.length != 3) {
                    throw new IllegalArgumentException("Usage: mark ID [YYYY-MM-DD]");
                }
                int id = Integer.parseInt(args[1]);
                LocalDate day = args.length == 3 ? LocalDate.parse(args[2]) : service.today();
                Habit habit = service.mark(id, day);
                System.out.println("Marked " + habit.name() + " on " + day);
            }
            case "list" -> {
                requireArgs(args, 1);
                if (service.list().isEmpty()) {
                    System.out.println("No habits yet.");
                }
                for (Habit habit : service.list()) {
                    System.out.printf("#%d  %-24s  %d days logged  streak %d%n",
                            habit.id(), habit.name(), habit.checkIns().size(), habit.streak(service.today()));
                }
            }
            case "history" -> {
                requireArgs(args, 2);
                Habit habit = service.find(Integer.parseInt(args[1]));
                System.out.println(habit.name());
                habit.checkIns().stream().sorted().forEach(System.out::println);
            }
            default -> usage();
        }
    }

    private static void requireArgs(String[] args, int expected) {
        if (args.length != expected) {
            throw new IllegalArgumentException("Wrong number of arguments; run without arguments for help");
        }
    }

    private static void usage() {
        System.out.println("Habit Calendar");
        System.out.println("  add \"habit name\"        Add a habit");
        System.out.println("  mark ID [YYYY-MM-DD]     Mark today or an earlier day");
        System.out.println("  list                    Show habits and streaks");
        System.out.println("  history ID              Show logged dates");
    }
}
