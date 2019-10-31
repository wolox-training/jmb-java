package wolox.training.web.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.services.open_library.OpenLibraryService;
import wolox.training.web.dtos.BookCreationRequestDto;
import wolox.training.web.dtos.BookSpecificationDto;

/**
 * The {@link Book}s REST controller.
 */
@RestController
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Transactional(readOnly = true)
public class BookController {

    /**
     * The {@link BookRepository} used to read and write {@link Book} data.
     */
    private final BookRepository bookRepository;

    /**
     * The {@link OpenLibraryService} used to access Open Library to search for {@link Book}s when
     * it does not exist in the local database.
     */
    private final OpenLibraryService openLibraryService;


    /**
     * Constructor.
     *
     * @param bookRepository The {@link BookRepository} used to read and write {@link Book} data.
     * @param openLibraryService The {@link OpenLibraryService} used to access Open Library to
     * search for {@link Book}s when it does not exist in the local database.
     */
    @Autowired
    public BookController(
        final BookRepository bookRepository,
        final OpenLibraryService openLibraryService) {
        this.bookRepository = bookRepository;
        this.openLibraryService = openLibraryService;
    }


    /**
     * Endpoint for getting all the {@link Book}s in the system.
     *
     * @return A {@link ResponseEntity} of {@link Iterable} of {@link Book}s stored in the system.
     * Might be empty.
     */
    @GetMapping
    public ResponseEntity<Iterable<Book>> getAllBooks(final BookSpecificationDto dto) {
        final var books = bookRepository.findAll(dto.getSpecification());
        return ResponseEntity.ok(books);
    }

    /**
     * Endpoint for getting a {@link Book} by its {@code id}.
     *
     * @param id The id of the {@link Book} to be retrieved.
     * @return A {@link ResponseEntity} containing the {@link Book} with the given {@code id} if it
     * exists, or with 404 Not Found otherwise.
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<Book> getById(@PathVariable("id") final long id) {
        return bookRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint for creating a {@link Book} according to the given {@link BookCreationRequestDto}.
     *
     * @param dto The {@link BookCreationRequestDto} with needed data to create a {@link Book}
     * @return A {@link ResponseEntity} containing the created {@link Book}. The response will
     * contain a 201 Created status, together with the header Location where the {@link Book} can be
     * found.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @Transactional
    public ResponseEntity<Book> createBook(@RequestBody @Valid final BookCreationRequestDto dto) {
        final var book = bookRepository.save(
            new Book(
                dto.getGenre(),
                dto.getAuthor(),
                dto.getImage(),
                dto.getTitle(),
                dto.getSubtitle(),
                dto.getPublisher(),
                dto.getYear(),
                dto.getPages(),
                dto.getIsbn()
            )
        );
        final var uri = ControllerLinkBuilder
            .linkTo(ControllerLinkBuilder.methodOn(BookController.class).getById(book.getId()))
            .toUri();
        return ResponseEntity.created(uri).body(book);
    }

    /**
     * Endpoint for deleting the {@link Book} with the given {@code id}.
     *
     * @param id The id of the {@link Book} to be deleted.
     * @return A 204 No Content {@link ResponseEntity}.
     */
    @DeleteMapping("/{id:\\d+}")
    @Transactional
    public ResponseEntity deleteBook(@PathVariable("id") final long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint for getting a {@link Book} by its isbn.
     *
     * @param isbn The isbn of the {@link Book} to be retrieved.
     * @return A {@link ResponseEntity} containing the {@link Book} with the given {@code isbn} if
     * it exists (in the local database, or in Open Library), or with 404 Not Found otherwise.
     * @implNote This method first search the local database for the {@link Book} with the given
     * {@code isbn}. If it's not found, it searches Open Library (using {@link
     * OpenLibraryService#bookInfo(String)}). In case it is found there, it persist it for later
     * retrieval without having to access the external service.
     */
    @GetMapping("/isbn={isbn:.+}")
    @Transactional
    public ResponseEntity<Book> searchByIsbn(@PathVariable("isbn") final String isbn) {
        return bookRepository.getFirstByIsbn(isbn)
            .or(() -> openLibraryService.bookInfo(isbn).map(bookRepository::save))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
