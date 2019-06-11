package wolox.training.web.controllers;

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
import wolox.training.web.dtos.BookCreationRequestDto;

/**
 * The {@link Book}s REST controller.
 */
@RestController
@RequestMapping(value = "books", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Transactional(readOnly = true)
public class BookController {

    /**
     * The {@link BookRepository} used to read and write {@link Book} data.
     */
    private final BookRepository bookRepository;


    /**
     * Constructor.
     *
     * @param bookRepository The {@link BookRepository} used to read and write {@link Book} data.
     */
    @Autowired
    public BookController(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    /**
     * Endpoint for getting all the {@link Book}s in the system.
     *
     * @return A {@link ResponseEntity} of {@link Iterable} of {@link Book}s stored in the system.
     * Might be empty.
     */
    @GetMapping
    public ResponseEntity<Iterable<Book>> getAllBooks() {
        final var books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }

    /**
     * Endpoint for getting a {@link Book} by its {@code id}.
     *
     * @param id The id of the {@link Book} to be retrieved.
     * @return A {@link ResponseEntity} containing the {@link Book} with the given {@code id} if it
     * exists, or with 404 Not Found {@link ResponseEntity} otherwise.
     */
    @GetMapping("/{id}")
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
    public ResponseEntity<Book> createBook(@RequestBody final BookCreationRequestDto dto) {
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
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deleteBook(@PathVariable("id") final long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }
}
