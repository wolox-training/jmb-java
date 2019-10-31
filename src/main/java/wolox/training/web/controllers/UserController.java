package wolox.training.web.controllers;

import java.security.Principal;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wolox.training.exceptions.AuthenticationException;
import wolox.training.exceptions.AuthorizationException;
import wolox.training.exceptions.NoSuchEntityException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.web.dtos.ChangePasswordRequestDto;
import wolox.training.web.dtos.UserCreationRequestDto;
import wolox.training.web.dtos.UserDownloadDto;
import wolox.training.web.dtos.UserSpecificationDto;

/**
 * The {@link User}'s REST controller.
 */
@RestController
@Transactional(readOnly = true)
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {

    /**
     * The {@link UserRepository} used to read and write {@link User} data.
     */
    private final UserRepository userRepository;

    /**
     * The {@link BookRepository} used to search for {@link Book}s when they need to be added to a
     * {@link User}'s {@link Book} list.
     */
    private final BookRepository bookRepository;


    /**
     * Constructor.
     *
     * @param userRepository The {@link UserRepository} used to read and write {@link User} data.
     * @param bookRepository The {@link BookRepository} used to search for {@link Book}s when they
     * need to be added to a {@link User}'s {@link Book} list.
     */
    @Autowired
    public UserController(final UserRepository userRepository,
        final BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }


    /**
     * Endpoint for getting all the {@link User}s in the system.
     *
     * @return A {@link ResponseEntity} of {@link Iterable} of {@link User}s stored in the system.
     * Might be empty.
     */
    @GetMapping
    public ResponseEntity<Iterable<UserDownloadDto>> getAllUsers(final UserSpecificationDto dto) {
        final var users = userRepository.findAll(dto.getSpecification())
            .stream()
            .map(UserDownloadDto::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint for getting the currently authenticated {@link User}.
     *
     * @param principal The {@link Principal} representing the currently authenticated {@link
     * User}.
     * @return A {@link ResponseEntity} containing the currently authenticated {@link User} it
     * exists, or with 401 Unauthorized {@link ResponseEntity} otherwise.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDownloadDto> getAuthenticatedUser(final Principal principal) {
        return userRepository.getFirstByUsername(principal.getName())
            .map(UserDownloadDto::new)
            .map(ResponseEntity::ok)
            .orElseThrow(AuthenticationException::new);
    }

    /**
     * Endpoint for getting a {@link User} by its {@code userId}.
     *
     * @param userId The id of the {@link User} to be retrieved.
     * @return A {@link ResponseEntity} containing the {@link User} with the given {@code userId} if
     * it exists, or with 404 Not Found {@link ResponseEntity} otherwise.
     */
    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<UserDownloadDto> getById(@PathVariable("userId") final long userId) {
        return userRepository.findById(userId)
            .map(UserDownloadDto::new)
            .map(ResponseEntity::ok)
            .orElseThrow(NoSuchEntityException::new)
            ;
    }

    /**
     * Endpoint for creating a {@link User} according to the given {@link UserCreationRequestDto}.
     *
     * @param dto The {@link UserCreationRequestDto} with needed data to create a {@link User}
     * @return A {@link ResponseEntity} containing the created {@link User}. The response will
     * contain a 201 Created status, together with the header Location where the {@link User} can be
     * found.
     */
    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDownloadDto> createUser(
        @RequestBody @Valid final UserCreationRequestDto dto) {
        final var user = new User(dto.getUsername(), dto.getName(), dto.getBirthDate());
        user.changePassword(dto.getPassword());
        final var savedUser = userRepository.save(user);
        final var uri = ControllerLinkBuilder
            .linkTo(ControllerLinkBuilder.methodOn(UserController.class).getById(savedUser.getId()))
            .toUri();
        return ResponseEntity.created(uri).body(new UserDownloadDto(savedUser));
    }

    /**
     * Endpoint for changing a {@link User}'s password.
     *
     * @param userId The id of the {@link User} whose password must be changed.
     * @param dto The {@link ChangePasswordRequestDto} containing the actual and new password.
     * @return A 204 No Content {@link ResponseEntity} if there is any issue ({@link User} exists,
     * and the current password matches the one sent by the caller).
     * @throws NoSuchEntityException If no {@link User} exists with the given {@code userId}.
     * @throws AuthorizationException If the {@link User}'s current password does not match the sent
     * by the caller.
     */
    @Transactional
    @PutMapping("/{userId:\\d+}/password")
    public ResponseEntity changePassword(
        @PathVariable("userId") final long userId,
        @RequestBody final ChangePasswordRequestDto dto) {
        final var user = userRepository.findById(userId).orElseThrow(NoSuchEntityException::new);
        if (user.passwordMatches(dto.getCurrentPassword())) {
            user.changePassword(dto.getNewPassword());
            userRepository.save(user);
            return ResponseEntity.noContent().build();
        }
        throw new AuthorizationException("Password doesn't match");
    }

    /**
     * Endpoint for deleting the {@link User} with the given {@code userId}.
     *
     * @param userId The id of the {@link User} to be deleted.
     * @return A 204 No Content {@link ResponseEntity}.
     */
    @Transactional
    @DeleteMapping("/{userId:\\d+}")
    public ResponseEntity deleteUser(@PathVariable("userId") final long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint for getting the {@link Book}s of the {@link User} with the given {@code userId}.
     *
     * @param userId The id of the {@link User} whose {@link Book}s will be returned.
     * @return A {@link ResponseEntity} of {@link Iterable} of {@link Book}s that belong to the
     * {@link User} with the given {@code userId}. Might be empty.
     */
    @GetMapping("/{userId:\\d+}/books")
    public ResponseEntity<Set<Book>> getUserBooks(@PathVariable("userId") final long userId) {
        return userRepository.findById(userId)
            .map(User::getBooks)
            .map(books -> {
                books.size(); // Initialize lazy collection
                return ResponseEntity.ok(books);
            })
            .orElseThrow(NoSuchEntityException::new)
            ;
    }

    /**
     * Endpoint for adding the {@link Book} with the given {@code bookId} to the {@link User} with
     * the given {@code userId}.
     *
     * @param userId The id of the {@link User}.
     * @param bookId The id of the {@link Book}.
     * @return A 204 No Content {@link ResponseEntity} if the {@link Book} could be added without
     * problem, or a 404 Not Found {@link ResponseEntity} if the {@link User} or the {@link Book}
     * does not exist.
     */
    @Transactional
    @PutMapping("/{userId:\\d+}/books/{bookId:\\d+}")
    public ResponseEntity addBook(
        @PathVariable("userId") final long userId,
        @PathVariable("bookId") final long bookId) {
        return operateOverBookAndUser(userId, bookId, User::addBook);
    }

    /**
     * Endpoint for removing the {@link Book} with the given {@code bookId} from the {@link User}
     * with the given {@code userId}.
     *
     * @param userId The id of the {@link User}.
     * @param bookId The id of the {@link Book}.
     * @return A 204 No Content {@link ResponseEntity} if the {@link Book} could be removed without
     * problem, or a 404 Not Found {@link ResponseEntity} if the {@link User} or the {@link Book}
     * does not exist.
     * @apiNote This is an idempotent operation (i.e removing a {@link Book} that is not present in
     * the {@link User}'s list does not cause any error).
     */
    @Transactional
    @DeleteMapping("/{userId:\\d+}/books/{bookId:\\d+}")
    public ResponseEntity removeBook(
        @PathVariable("userId") final long userId,
        @PathVariable("bookId") final long bookId) {
        return operateOverBookAndUser(userId, bookId, User::removeBook);
    }

    /**
     * Performs an operation over the {@link User} and {@link Book} with the given {@code userId}
     * {@code bookId}, according to the given {@code operation} {@link BiConsumer}.
     *
     * @param userId The id of the {@link User}.
     * @param bookId The id of the {@link Book}.
     * @param operation A {@link BiConsumer} of {@link User} and {@link Book} to which the operation
     * is delegated.
     * @return A 204 No Content {@link ResponseEntity} if the {@code operation} could be performed
     * without problem, or a 404 Not Found {@link ResponseEntity} in case the {@link User} or the
     * {@link Book} do not exist.
     */
    private ResponseEntity operateOverBookAndUser(
        final long userId,
        final long bookId,
        final BiConsumer<User, Book> operation) {
        return userRepository.findById(userId)
            .flatMap(user ->
                bookRepository.findById(bookId)
                    .map(book -> {
                        operation.accept(user, book);
                        userRepository.save(user);
                        return ResponseEntity.noContent().build();
                    }))
            .orElseThrow(NoSuchEntityException::new)
            ;
    }
}
