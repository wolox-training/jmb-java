package wolox.training.services.open_library;

import java.util.Optional;
import wolox.training.models.Book;

/**
 * Defines behaviour for an object that can communicate with the Open Library API to offer features
 * of it.
 */
public interface OpenLibraryService {

    /**
     * Returns the {@link Book} with the given {@code isbn} from Open Library.
     *
     * @param isbn The isbn of the {@link Book} to be retrieved.
     * @return An {@link Optional} containing the {@link Book} with the given {@code isbn} if it
     * exists, or empty otherwise.
     */
    Optional<Book> bookInfo(final String isbn);
}
