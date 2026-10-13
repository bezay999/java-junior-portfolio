import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Main {
    public record Share(String name, long cents) {
        public String amount() {
            return BigDecimal.valueOf(cents, 2).setScale(2, RoundingMode.UNNECESSARY).toPlainString();
        }
    }

    private Main() {
    }

    public static List<Share> split(BigDecimal amount, List<String> names) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new IllegalArgumentException("Amount must be positive with at most two decimal places");
        }
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("Add at least one person");
        }
        long cents = amount.movePointRight(2).longValueExact();
        Set<String> seen = new HashSet<>();
        for (String name : names) {
            if (name == null || name.isBlank() || !seen.add(name.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Names must be non-empty and distinct");
            }
        }
        long base = cents / names.size();
        long remainder = cents % names.size();
        List<Share> shares = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            shares.add(new Share(names.get(i), base + (i < remainder ? 1 : 0)));
        }
        return shares;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java -cp out Main AMOUNT PERSON [PERSON ...]");
            return;
        }
        try {
            for (Share share : split(new BigDecimal(args[0]), List.of(args).subList(1, args.length))) {
                System.out.println(share.name() + ": " + share.amount());
            }
        } catch (IllegalArgumentException | ArithmeticException error) {
            System.err.println("Error: " + error.getMessage());
            System.exit(1);
        }
    }
}
