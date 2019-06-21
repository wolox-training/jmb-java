package wolox.training.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class containing several helper methods.
 */
public class Utils {

    /**
     * Private constructor to avoid instantiation.
     */
    private Utils() {
    }

    /**
     * Sorts the given {@code authorsStream}, and then joins the values with ", ".
     *
     * @param authorsStream The {@link Stream} to be processed.
     * @return A {@link String} consisting of the values of the given {@code authorsStream} being
     * sorted and joined.
     */
    public static String sortAndJoinAuthors(final Stream<String> authorsStream) {
        return authorsStream.sorted().collect(Collectors.joining(", "));
    }

}
