import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class Main {
    public record Card(String question, String answer) {
    }

    private Main() {
    }

    public static List<Card> load(Path file) throws IOException {
        List<Card> cards = new ArrayList<>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNumber++;
            String[] fields = line.split("\t", -1);
            if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank()) {
                throw new IOException("Invalid card on line " + lineNumber);
            }
            cards.add(new Card(fields[0].trim(), fields[1].trim()));
        }
        if (cards.isEmpty()) {
            throw new IOException("Deck is empty");
        }
        return cards;
    }

    public static int quiz(List<Card> cards, Reader input, Writer output, Random random) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        PrintWriter writer = new PrintWriter(output, true);
        List<Card> round = new ArrayList<>(cards);
        Collections.shuffle(round, random);
        int attempts = 0;
        int roundNumber = 1;
        while (!round.isEmpty()) {
            writer.println("Round " + roundNumber + " (" + round.size() + " cards)");
            List<Card> missed = new ArrayList<>();
            for (Card card : round) {
                writer.print(card.question() + " > ");
                writer.flush();
                String answer = reader.readLine();
                if (answer == null) {
                    writer.println("Session ended early.");
                    return attempts;
                }
                attempts++;
                if (answer.trim().toLowerCase(Locale.ROOT)
                        .equals(card.answer().toLowerCase(Locale.ROOT))) {
                    writer.println("Correct.");
                } else {
                    writer.println("Answer: " + card.answer());
                    missed.add(card);
                }
            }
            round = missed;
            roundNumber++;
        }
        writer.println("Done in " + attempts + " attempts.");
        return attempts;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -cp out Main DECK.tsv");
            return;
        }
        try {
            quiz(load(Path.of(args[0])), new java.io.InputStreamReader(System.in, StandardCharsets.UTF_8),
                    new java.io.OutputStreamWriter(System.out, StandardCharsets.UTF_8), new Random());
        } catch (IOException | IllegalArgumentException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }
}
