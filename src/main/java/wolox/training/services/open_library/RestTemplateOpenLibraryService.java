package wolox.training.services.open_library;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import wolox.training.models.Book;
import wolox.training.utils.Utils;

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
     * The base URL of the Open Library API.
     */
    private final String searchByIsbnUrl;

    /**
     * Constructor.
     *
     * @param restTemplate The {@link RestTemplate} used to communicate with Open Library through
     * its REST API.
     * @param openLibraryApiBaseUrl The base URL of the Open Library API.
     */
    @Autowired
    public RestTemplateOpenLibraryService(
        final RestTemplate restTemplate,
        @Value("${open-library.api.base-url}") final String openLibraryApiBaseUrl,
        @Value("${open-library.api.paths.books.search-by-isbn}") final String searchByIsbnPath) {
        this.restTemplate = restTemplate;
        this.searchByIsbnUrl = openLibraryApiBaseUrl + searchByIsbnPath;
    }


    @Override
    public Optional<Book> bookInfo(final String isbn) {
        final var response = restTemplate.getForEntity(searchByIsbnUrl, JsonNode.class, isbn);
        if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
            return Optional.empty();
        }
        return getFromJsonNode(response.getBody(), isbn);
    }

    /**
     * Builds a {@link Book} from the given {@code rootNode}.
     *
     * @param rootNode The root {@link JsonNode}.
     * @param isbn The {@link Book} isbn (i.e the {@code rootNode} has a property with the following
     * format: "ISBN:&lt;isbn&gt;".
     * @return The created {@link Book}.
     * @implNote This method uses JSON tree traversal using the {@link JsonNode} API of Jackson.
     * This allows to not create a very complex POJO to map the JSON into a Java Object.
     */
    private static Optional<Book> getFromJsonNode(final JsonNode rootNode, final String isbn) {
        Assert.notNull(rootNode, "The root JsonNode must not be null");
        Assert.hasText(isbn, "The ISBN must have text");

        return Optional.ofNullable(rootNode.get("ISBN:" + isbn))
            .map(jsonNode -> {

                // Initialize author and publisher which are turned as a list of N > 1 by Open Library
                final var author = Utils.sortAndJoinWithCommas(
                    StreamSupport.stream(jsonNode.path("authors").spliterator(), false)
                        .map(n -> n.path("name"))
                        .map(JsonNode::asText)
                );
                final var publisher = Utils.sortAndJoinWithCommas(
                    StreamSupport.stream(jsonNode.path("publishers").spliterator(), false)
                        .map(n -> n.path("name"))
                        .map(JsonNode::asText)
                );

                return new Book(
                    null, // Open Library does not have this information
                    author,
                    jsonNode.path("cover").path("large").asText(""),
                    jsonNode.path("title").asText(""),
                    jsonNode.path("subtitle").asText(""),
                    publisher,
                    jsonNode.path("publish_date").asText("").substring(0, 4), // Only year
                    jsonNode.path("number_of_pages").asInt(1),
                    isbn
                );
            });
    }
}
