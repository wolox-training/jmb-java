package wolox.training.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
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
}
