package wolox.training.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.Assert;
import wolox.training.models.Book;

/**
 * Helper class for testing the web layer.
 */
/* package */ class WebTestHelper {

    /**
     * Private constructor to avoid instantiation.
     */
    private WebTestHelper() {
    }


    /**
     * Creates a {@link ResultMatcher} to test that a JSON object matches a given {@link Book}.
     *
     * @param book The {@link Book} to be matched.
     * @param baseExpression The JsonPath base expression (e.g the first element of a list would be
     * $[0]).
     * @return The created {@link ResultMatcher}.
     */
    /* package */
    static ResultMatcher bookJsonResultMatcher(final Book book, final String baseExpression) {
        Assert.notNull(book, "The book must not be null");
        Assert.notNull(baseExpression, "The base expression must not be null");
        return ResultMatcher.matchAll(
            jsonPath(baseExpression + ".id").value(is(book.getId()), Long.class),
            jsonPath(baseExpression + ".genre", is(book.getGenre())),
            jsonPath(baseExpression + ".author", is(book.getAuthor())),
            jsonPath(baseExpression + ".image", is(book.getImage())),
            jsonPath(baseExpression + ".title", is(book.getTitle())),
            jsonPath(baseExpression + ".subtitle", is(book.getSubtitle())),
            jsonPath(baseExpression + ".publisher", is(book.getPublisher())),
            jsonPath(baseExpression + ".year", is(book.getYear())),
            jsonPath(baseExpression + ".pages", is(book.getPages())),
            jsonPath(baseExpression + ".isbn", is(book.getIsbn()))
        );
    }

    /**
     * Creates a {@link ResultMatcher} to test that a list of JSON objects matches a given {@link
     * List} of {@link Book}s.
     *
     * @param books The {@link List} of {@link Book}s to be matched.
     * @return The created {@link ResultMatcher}.
     */
    /* package */
    static ResultMatcher bookListJsonResultMatcher(final List<Book> books) {
        Assert.notNull(books, "The books list must not be null");
        return ResultMatcher.matchAll(
            Stream.concat(
                Stream.of(jsonPath("$", hasSize(books.size()))),
                IntStream.range(0, books.size())
                    .mapToObj(
                        i -> WebTestHelper.bookJsonResultMatcher(books.get(i), "$[" + i + "]"))
            ).toArray(ResultMatcher[]::new)
        );
    }


    /**
     * Creates an invalid JSON representation of the body to be sent when creating a {@link Book}.
     *
     * @return The created JSON.
     */
    /* package */
    static String invalidBookCreationRequest() {
        return bookCreationRequestJson(
            Faker.instance().book().genre(), // Genre does not have any precondition.
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    /**
     * Creates a valid JSON representation of the body to be sent when creating a {@link Book}.
     *
     * @return The created JSON.
     */
    /* package */
    static String validBookCreationRequest() {
        return bookCreationRequestJson(
            Faker.instance().book().genre(),// Genre does not have any precondition.
            Faker.instance().book().author(),
            Faker.instance().internet().image(),
            Faker.instance().book().title(),
            Faker.instance().book().title(), // Use this as subtitle
            Faker.instance().book().publisher(),
            Long.toString(Faker.instance().number().numberBetween(1950, LocalDate.now().getYear())),
            Faker.instance().number().numberBetween(1, 2000), // Book has at most 2000 pages
            Faker.instance().code().isbn13()
        );
    }

    /**
     * Creates a JSON representation of the body to be sent when creating a {@link Book}.
     *
     * @param genre The book's genre.
     * @param author The book's author.
     * @param image The book's image.
     * @param title The book's title.
     * @param subtitle The book's subtitle.
     * @param publisher The book's publisher.
     * @param year The year the book was/is/will be published.
     * @param pages The amount of pages in the book.
     * @param isbn The book's ISBN.
     * @return The created JSON.
     */
    /* package */
    static String bookCreationRequestJson(
        final String genre,
        final String author,
        final String image,
        final String title,
        final String subtitle,
        final String publisher,
        final String year,
        final Integer pages,
        final String isbn) {
        final Map<String, Object> map = new HashMap<>();
        map.put("genre", genre);
        map.put("author", author);
        map.put("image", image);
        map.put("title", title);
        map.put("subtitle", subtitle);
        map.put("publisher", publisher);
        map.put("year", year);
        map.put("pages", pages);
        map.put("isbn", isbn);
        return toJsonString(map);
    }

    /**
     * Transforms the given {@code object} into its JSON representation.
     *
     * @param object The object to be converted.
     * @param <T> The concrete type of the {@code object}.
     * @return A JSON representation of the {@code object}.
     * @implNote This method uses {@link ObjectMapper#writeValueAsString(Object)}.
     */
    /* package */
    static <T> String toJsonString(final T object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates an empty content {@code byte} array.
     *
     * @return An empty {@code byte} array.
     */
    /* package */
    static byte[] emptyContent() {
        return new byte[0];
    }
}
