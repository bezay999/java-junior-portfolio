import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class Main {
    private Main() {
    }

    public static Map<String, Set<String>> readGraph(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).equals("from,to")) {
            throw new IOException("Route file must begin with from,to");
        }
        Map<String, Set<String>> graph = new HashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] fields = lines.get(i).split(",", -1);
            if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank()) {
                throw new IOException("Invalid connection on line " + (i + 1));
            }
            String from = fields[0].trim();
            String to = fields[1].trim();
            if (from.equals(to)) {
                throw new IOException("A stop cannot connect to itself on line " + (i + 1));
            }
            graph.computeIfAbsent(from, key -> new HashSet<>()).add(to);
            graph.computeIfAbsent(to, key -> new HashSet<>()).add(from);
        }
        return graph;
    }

    public static List<String> route(Map<String, Set<String>> graph, String start, String end) {
        if (!graph.containsKey(start) || !graph.containsKey(end)) {
            throw new IllegalArgumentException("Both stops must exist in the network");
        }
        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> previous = new HashMap<>();
        queue.add(start);
        previous.put(start, null);
        while (!queue.isEmpty()) {
            String current = queue.remove();
            if (current.equals(end)) {
                List<String> path = new ArrayList<>();
                for (String stop = end; stop != null; stop = previous.get(stop)) {
                    path.add(stop);
                }
                Collections.reverse(path);
                return path;
            }
            for (String next : graph.get(current).stream().sorted().toList()) {
                if (!previous.containsKey(next)) {
                    previous.put(next, current);
                    queue.add(next);
                }
            }
        }
        return List.of();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -cp out Main CONNECTIONS.csv START END");
            return;
        }
        try {
            List<String> path = route(readGraph(Path.of(args[0])), args[1], args[2]);
            System.out.println(path.isEmpty() ? "No route found." : String.join(" -> ", path));
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }
}
