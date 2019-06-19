package wolox.training.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import wolox.training.models.Book;

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
}
