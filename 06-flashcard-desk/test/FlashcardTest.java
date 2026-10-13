import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public final class FlashcardTest {
    public static void main(String[] args) throws Exception {
        Path deck = Files.createTempFile("cards-", ".tsv");
        Files.writeString(deck, "Question?\tAnswer\n");
        List<Main.Card> cards = Main.load(deck);
        StringWriter output = new StringWriter();
        int attempts = Main.quiz(cards, new StringReader("wrong\n answer \n"), output, new Random(1));
        check(attempts == 2, "Missed card should return next round");
        check(output.toString().contains("Round 2"), "Second round should be printed");
        check(output.toString().contains("Done in 2 attempts"), "Session should finish");
        int early = Main.quiz(cards, new StringReader(""), new StringWriter(), new Random(1));
        check(early == 0, "EOF should end a session cleanly");
        Files.writeString(deck, "bad line\n");
        try {
            Main.load(deck);
            throw new AssertionError("Invalid deck should fail");
        } catch (java.io.IOException expected) {
        }
        System.out.println("Flashcard Desk checks passed.");
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
