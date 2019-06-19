package wolox.training.services.open_library;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wolox.training.models.Book;

/**
 * Concrete implementation of {@link OpenLibraryService} that uses a {@link RestTemplate} to
 * communicate with Open Library through its REST API.
 */
@Service
class RestTemplateOpenLibraryService implements OpenLibraryService {

    /**
     * The {@link RestTemplate} used to communicate with Open Library through its REST API.
     */
    private final RestTemplate restTemplate;

    /**
     * Constructor.
     *
     * @param restTemplate The {@link RestTemplate} used to communicate with Open Library through
     * its REST API.
     */
    @Autowired
    public RestTemplateOpenLibraryService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public Optional<Book> bookInfo(final String isbn) {
        throw new RuntimeException("Not implemented yet");
    }
}
