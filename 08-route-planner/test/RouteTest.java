import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RouteTest {
    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("routes-", ".csv");
        Files.writeString(file, "from,to\nA,B\nB,C\nA,D\nD,E\nE,C\nX,Y\n");
        Map<String, Set<String>> graph = Main.readGraph(file);
        check(Main.route(graph, "A", "C").equals(List.of("A", "B", "C")),
                "BFS should choose the shortest route");
        check(Main.route(graph, "A", "A").equals(List.of("A")), "Same-stop route should work");
        check(Main.route(graph, "A", "Y").isEmpty(), "Disconnected stops have no route");
        try {
            Main.route(graph, "A", "Unknown");
            throw new AssertionError("Unknown stop should fail");
        } catch (IllegalArgumentException expected) {
        }
        System.out.println("Route Planner checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
