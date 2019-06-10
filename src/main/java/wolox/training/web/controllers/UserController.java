package wolox.training.web.controllers;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
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
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;
import wolox.training.web.dtos.UserCreationRequestDto;
import wolox.training.web.dtos.UserDownloadDto;

/**
 * The {@link User}'s REST controller.
 */
@RestController
@RequestMapping(value = "users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Transactional(readOnly = true)
public class UserController {

    /**
     * The {@link UserRepository} used to read and write {@link User} data.
     */
    private final UserRepository userRepository;


    /**
     * Constructor.
     *
     * @param userRepository The {@link UserRepository} used to read and write {@link User} data.
     */
    @Autowired
    public UserController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Endpoint for getting all the {@link User}s in the system.
     *
     * @return A {@link ResponseEntity} of {@link Iterable} of {@link User}s stored in the system.
     * Might be empty.
     */
    @GetMapping
    public ResponseEntity<Iterable<UserDownloadDto>> getAllUsers() {
        final var users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
            .map(UserDownloadDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint for getting a {@link User} by its {@code id}.
     *
     * @param id The id of the {@link User} to be retrieved.
     * @return A {@link ResponseEntity} containing the {@link User} with the given {@code id} if it
     * exists, or with 404 Not Found {@link ResponseEntity} otherwise.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDownloadDto> getById(@PathVariable("id") final long id) {
        return userRepository.findById(id)
            .map(UserDownloadDto::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a {@link User} according to the given {@link UserCreationRequestDto}.
     *
     * @param dto The {@link UserCreationRequestDto} with needed data to create a {@link User}
     * @return A {@link ResponseEntity} containing the created {@link User}. The response will
     * contain a 201 Created status, together with the header Location where the {@link User} can be
     * found.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> createUser(@RequestBody final UserCreationRequestDto dto) {
        final var user = userRepository.save(
            new User(
                dto.getUsername(),
                dto.getName(),
                dto.getBirthDate()
            )
        );
        final var uri = ControllerLinkBuilder
            .linkTo(ControllerLinkBuilder.methodOn(UserController.class).getById(user.getId()))
            .toUri();
        return ResponseEntity.created(uri).body(user);
    }

    /**
     * Deletes the {@link User} with the given {@code id}.
     *
     * @param id The id of the {@link User} to be deleted.
     * @return A 204 No Content {@link ResponseEntity}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") final long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
        return ResponseEntity.noContent().build();
    }
}
