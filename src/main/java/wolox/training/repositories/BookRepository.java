package wolox.training.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.repository.CrudRepository;
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
     * Returns one {@link Book} that contains the given {@code authors} {@link List} as its author,
     * appended according to {@link Utils#sortAndJoinAuthors(Stream)}.
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
        final var appended = Utils.sortAndJoinAuthors(authors.stream());
        return getFirstByAuthor(appended);
    }
}
