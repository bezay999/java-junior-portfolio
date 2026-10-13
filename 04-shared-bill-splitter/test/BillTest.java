import java.math.BigDecimal;
import java.util.List;

public final class BillTest {
    public static void main(String[] args) {
        List<Main.Share> shares = Main.split(new BigDecimal("10.01"), List.of("Ada", "Ben", "Cara"));
        check(shares.get(0).cents() == 334, "First person should get the spare cent");
        check(shares.get(1).cents() == 334 && shares.get(2).cents() == 333, "Split should be fair");
        check(shares.stream().mapToLong(Main.Share::cents).sum() == 1001, "No money may disappear");
        check(Main.split(new BigDecimal("0.01"), List.of("A", "B")).get(1).cents() == 0,
                "A one-cent bill can include a zero share");
        expectFailure(() -> Main.split(new BigDecimal("2.001"), List.of("A")));
        expectFailure(() -> Main.split(new BigDecimal("2"), List.of("Ada", "ada")));
        System.out.println("Shared Bill Splitter checks passed.");
    }

    private static void expectFailure(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected invalid input to fail");
        } catch (IllegalArgumentException expected) {
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
