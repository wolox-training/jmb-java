package wolox.training.repositories;

import java.util.Optional;
import org.springframework.data.repository.Repository;
import wolox.training.models.Book;

/**
 * A repository for {@link Book}s.
 */
@org.springframework.stereotype.Repository
public interface BookRepository extends Repository<Book, Long> {

    /**
     * Returns one {@link Book} of the given {@code author}
     *
     * @param author The author used to search for a {@link Book}.
     * @return An {@link Optional} containing a {@link Book} of the given {@code author} if there is
     * such, or empty otherwise.
     */
    Optional<Book> getByAuthor(final String author);
}
