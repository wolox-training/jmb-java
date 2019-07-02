package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import wolox.training.models.Book;
import wolox.training.utils.Utils;

/**
 * A repository for {@link Book}s.
 */
@Repository
public interface BookRepository extends CrudRepository<Book, Long> {

    /**
     * Returns one {@link Book} of the given {@code author}
     *
     * @param author The author used to search for a {@link Book}.
     * @return An {@link Optional} containing a {@link Book} of the given {@code author} if there is
     * such, or empty otherwise.
     */
    Optional<Book> getFirstByAuthor(final String author);

    /**
     * Returns one {@link Book} with the given {@code isbn}.
     *
     * @param isbn The isbn used to search for a {@link Book}.
     * @return An {@link Optional} containing a {@link Book} with the given {@code isbn} if there is
     * such, or empty otherwise.
     */
    Optional<Book> getFirstByIsbn(final String isbn);

    /**
     * Returns all the {@link Book}s whose publisher, genre and year match the given ones.
     *
     * @param publisher The publisher to be matched.
     * @param genre The genre to be matched.
     * @param year The year to be matched.
     * @return The {@link Book} matching the criteria.
     */
    List<Book> getByPublisherAndGenreAndYear(
        final String publisher,
        final String genre,
        final String year
    );

    /**
     * Same as {@link #getByPublisherAndGenreAndYear(String, String, String)}, but with optional
     * parameters.
     *
     * @param publisher The publisher to be matched.
     * @param genre The genre to be matched.
     * @param year The year to be matched.
     * @return The {@link Book} matching the criteria.
     */
    @Query(
        value = "SELECT b"
            + " FROM Book b"
            + " WHERE ( :publisher IS NULL OR b.publisher = :publisher )"
            + " AND ( :genre IS NULL OR b.genre = :genre )"
            + " AND ( :year IS NULL OR upper(b.year) = :year )"
    )
    List<Book> getWithPublisherGenreAndYear(
        @Param("publisher") final String publisher,
        @Param("genre") final String genre,
        @Param("year") final String year);

    /**
     * Returns one {@link Book} that contains the given {@code authors} {@link List} as its author,
     * appended according to {@link Utils#sortAndJoinWithCommas(Stream)}.
     *
     * @param authors The authors {@link List}
     * @return An {@link Optional} containing the {@link Book} that match the condition, or empty
     * otherwise.
     * @throws IllegalArgumentException If the {@code authors} {@link List} is null, or empty, or if
     * it contains {@link String} elements without text or a {@code null} value.
     */
    default Optional<Book> getByAuthorsExactMatch(final List<String> authors) {
        Assert.notEmpty(authors, "The authors list must not be null");
        authors.forEach(author -> Assert.hasText(author, "All authors must have text"));
        final var appended = Utils.sortAndJoinWithCommas(authors.stream());
        return getFirstByAuthor(appended);
    }
}
