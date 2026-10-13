import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class JobBoardTest {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempDirectory("job-board-test-").resolve("applications.tsv");
        List<Main.Application> applications = new ArrayList<>();
        Main.Application first = Main.add(applications, "Northstar", "Junior Java Developer",
                LocalDate.parse("2026-10-12"));
        Main.add(applications, "Blue Oak", "Backend Intern", LocalDate.parse("2026-10-20"));
        Main.save(file, applications);

        List<Main.Application> reloaded = Main.load(file);
        check(reloaded.size() == 2, "Applications should survive a restart");
        check(Main.due(reloaded, LocalDate.parse("2026-10-13")).size() == 1,
                "Only the overdue application is due");
        Main.update(reloaded, first.id(), Main.Stage.INTERVIEW, null);
        check(Main.due(reloaded, LocalDate.parse("2026-10-13")).size() == 1,
                "Interviews can still be due");
        Main.update(reloaded, first.id(), Main.Stage.OFFER, null);
        check(Main.due(reloaded, LocalDate.parse("2026-10-13")).isEmpty(),
                "An offer should not appear as due");
        Main.save(file, reloaded);
        check(Main.load(file).get(0).stage() == Main.Stage.OFFER, "Updated stage should be saved");
        System.out.println("Job Search Board checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
