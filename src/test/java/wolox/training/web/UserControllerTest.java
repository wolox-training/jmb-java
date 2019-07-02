package wolox.training.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wolox.training.web.WebTestHelper.withJwt;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BlacklistedJwtTokenRepository;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.services.authentication.JwtTokenService;
import wolox.training.utils.TestHelper;
import wolox.training.utils.ValuesGenerator;
import wolox.training.web.JwtExtension.AuthenticatedWithJwt;
import wolox.training.web.JwtExtension.ValidJwt;
import wolox.training.web.controllers.BookController;
import wolox.training.web.controllers.UserController;
import wolox.training.web.dtos.UserCreationRequestDto;


/**
 * Testing for the {@link UserController}.
 */
@WebMvcTest(
    controllers = {
        UserController.class
    },
    includeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebTestJwtTokenServiceConfig.class)
    }
)

@MockBean(UserRepository.class)
@MockBean(BookRepository.class)
@MockBean(BlacklistedJwtTokenRepository.class)
class UserControllerTest {

    /**
     * The API base path for {@link User}s.
     */
    private static final String USERS_PATH = "/api/users/";
    /**
     * The API path for a specific {@link User} (located by its id).
     */
    private static final String USERS_PATH_ID = USERS_PATH + "{userId}/";
    /**
     * The API path for a {@link User}'s {@link wolox.training.models.Book}s
     */
    private static final String USER_BOOKS = USERS_PATH_ID + "books/";
    /**
     * The API path for a {@link User} and {@link wolox.training.models.Book} relationship.
     */
    private static final String USER_BOOK_ID = USER_BOOKS + "{bookId}";

    /**
     * The username of the user performing the operations (used to verify interactions).
     */
    private static final String AUTHENTICATED_USERNAME = "the-username";


    /**
     * The {@link MockMvc} instance to perform testing over the {@link UserController}.
     */
    private final MockMvc mockMvc;
    /**
     * A mocked {@link UserRepository} which was injected to the {@link UserController}.
     */
    private final UserRepository userRepository;
    /**
     * A mocked {@link BookRepository} which was injected to the {@link BookController}.
     */
    private final BookRepository bookRepository;

    /**
     * The {@link JwtExtension} to be registered.
     */
    @RegisterExtension
    /* package */ final JwtExtension jwtExtension; // @RegisterExtension requires non-private


    /**
     * Constructor.
     *
     * @param mockMvc The {@link MockMvc} to perform testing over the {@link UserController}.
     * @param userRepository A mocked {@link UserRepository} which was injected to the {@link
     * UserController}.
     * @param bookRepository A mocked {@link BookRepository} which was injected to the {@link
     * BookController}.
     * @param jwtTokenService The {@link JwtTokenService} to be passed to the {@link JwtExtension}.
     * @implNote The {@link UserRepository} is also used to configure the {@link JwtExtension} in
     * order to simulate the registration of a {@link wolox.training.models.User}, so JWTs can be
     * created for them.
     */
    @Autowired
    UserControllerTest(
        final MockMvc mockMvc,
        final UserRepository userRepository,
        final BookRepository bookRepository,
        final JwtTokenService jwtTokenService) {

        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.jwtExtension = new JwtExtension(
            jwtTokenService,
            user -> when(userRepository.getFirstByUsername(user.getUsername()))
                .thenReturn(Optional.of(user))
        );
    }


    /**
     * Configures the {@link BlacklistedJwtTokenRepository} mock to make all tokens be not
     * blacklisted.
     */
    @BeforeEach
    void allTokensAreValid(
        @Autowired final BlacklistedJwtTokenRepository blacklistedJwtTokenRepository) {
        when(blacklistedJwtTokenRepository.existsById(Mockito.any())).thenReturn(false);
    }


    /**
     * Tests the API response when requesting all {@link User}s (i.e using the controller method
     * {@link UserController#getAllUsers()}), and none is returned by the {@link UserRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all users - Empty")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testGetAllUsersReturningEmptyList(@ValidJwt final String jwt) throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(
            withJwt(get(USERS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)))
        ;
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when requesting all {@link User}s (i.e using the controller method
     * {@link UserController#getAllUsers()}), and a non-empty {@link Iterable} is returned by the
     * {@link UserRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all users - Not Empty")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testGetAllUsersReturningNonEmptyList(@ValidJwt final String jwt) throws Exception {
        final var maxListSize = 10;
        final var mockedList = TestHelper.mockUserList(maxListSize);
        when(userRepository.findAll()).thenReturn(mockedList);
        mockMvc.perform(
            withJwt(get(USERS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(mockedList.size())))
            .andExpect(WebTestHelper.userListJsonResultMatcher(mockedList))
        ;
        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }


    /**
     * Tests the API response when requesting a {@link User} by its id (i.e the controller method is
     * {@link UserController#getById(long)} ()}), and an empty {@link Optional} is returned by the
     * {@link UserRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User by id - Not exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testGetNonExistenceUser(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(
            withJwt(get(USERS_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when requesting a {@link User} by its id (i.e the controller method is
     * {@link UserController#getById(long)} ()}), and a non empty {@link Optional} is returned by
     * the {@link UserRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User by id - Exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testGetExistingUser(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validUserId();
        final var mockedUser = TestHelper.mockUser();
        TestHelper.addId(mockedUser);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockedUser));
        mockMvc.perform(
            withJwt(get(USERS_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(WebTestHelper.userJsonResultMatcher(mockedUser, "$"))
        ;
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when creating a {@link User} (i.e the controller method is {@link
     * UserController#createUser(UserCreationRequestDto)}), and an empty body is sent.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create User - Empty body")
    void testCreateWithNoBody() throws Exception {
        mockMvc.perform(
            post(USERS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(WebTestHelper.emptyContent())
        ).andExpect(status().isBadRequest())
        ;
        verifyZeroInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when creating a {@link User} (i.e the controller method is {@link
     * UserController#createUser(UserCreationRequestDto)}), and the sent JSON has invalid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create User - Invalid arguments")
    void testCreateWithInvalidArguments() throws Exception {
        mockMvc.perform(
            post(USERS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(WebTestHelper.invalidUserCreationRequest())
        ).andExpect(status().isBadRequest())
        ;
        verifyZeroInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when creating a {@link User} (i.e the controller method is {@link
     * UserController#createUser(UserCreationRequestDto)}), and the sent JSON has valid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create User - Valid arguments")
    void testCreateWithValidArguments() throws Exception {
        when(userRepository.save(any(User.class))).then(i -> i.getArgument(0));
        mockMvc.perform(
            post(USERS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(WebTestHelper.validUserCreationRequest())
        )
            .andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        ;
        verify(userRepository, only()).save(any(User.class));
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when deleting a {@link User} by its id (i.e the controller method is
     * {@link UserController#deleteUser(long)} ), and the {@link UserRepository} indicates there is
     * no {@link User} with the said id.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete User - Not exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testDeleteNonExistingUser(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.existsById(id)).thenReturn(false);
        mockMvc.perform(withJwt(delete(USERS_PATH_ID, id), jwt))
            .andExpect(status().isNoContent());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when deleting a {@link User} by its id (i.e the controller method is
     * {@link UserController#deleteUser(long)} ), and the {@link UserRepository} indicates that a
     * {@link User} with the said id exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete User - Exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testDeleteExistingUser(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository).deleteById(id);
        mockMvc.perform(withJwt(delete(USERS_PATH_ID, id), jwt))
            .andExpect(status().isNoContent());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verify(userRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }


    /**
     * Tests the API response when getting the {@link Book}s of a {@link User} (i.e the controller
     * method is {@link UserController#getUserBooks(long)} ), and the {@link UserRepository}
     * indicates that a {@link User} not exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User books - User not exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testGetUserBooksForNonExistingUser(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(
            withJwt(get(USERS_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when getting the {@link Book}s of a {@link User} (i.e the controller
     * method is {@link UserController#getUserBooks(long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, but it does not have any {@link Book}s.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User books - User does not have books")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testGetUserBooksForUserWithoutBooks(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validUserId();
        final var mockedUser = TestHelper.mockUser();
        TestHelper.addId(mockedUser);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockedUser));
        mockMvc.perform(
            withJwt(get(USER_BOOKS, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)))
        ;
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when getting the {@link Book}s of a {@link User} (i.e the controller
     * method is {@link UserController#getUserBooks(long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and it has {@link Book}s.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User books - User has books")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testGetUserBooksForUserWithBooks(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validUserId();
        final var mockedUser = Mockito.mock(User.class);
        final var maxListSize = 10;
        final var mockedBooks = TestHelper.mockBookSet(maxListSize);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockedUser));
        when(mockedUser.getBooks()).thenReturn(mockedBooks);
        mockMvc.perform(
            withJwt(get(USER_BOOKS, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(mockedBooks.size())))
            .andExpect(WebTestHelper.bookListJsonResultMatcher(new LinkedList<>(mockedBooks)))
        ;
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }


    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that the {@link User} not exists, and the {@link BookRepository} indicates that the
     * {@link Book} not exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book not exists, User not exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testAddNonExistingBookToNonExistingUser(@ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(withJwt(put(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that the {@link User} not exists, but the {@link BookRepository} indicates that the
     * {@link Book} exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book exists, User not exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testAddExistingBookToNonExistingUser(@ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedBook = Mockito.mock(Book.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(withJwt(put(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, but the {@link BookRepository} indicates that the
     * {@link Book} not exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book not exists, User exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testAddNonExistingBookToExistingUser(@ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(withJwt(put(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verify(bookRepository, only()).findById(bookId);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and the {@link BookRepository} indicates that a {@link
     * Book} exists, and a {@link BookAlreadyOwnedException} is throws (i.e the {@link User} already
     * contains the {@link Book}).
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book exists, User exists, BookAlreadyOwnedException is thrown")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testAddExistingBookToExistingUserAndItThrowsBookAlreadyOwnedException(
        @ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        final var mockedBook = Mockito.mock(Book.class);
        doThrow(BookAlreadyOwnedException.class).when(mockedUser).addBook(mockedBook);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(withJwt(put(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isConflict())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verify(bookRepository, only()).findById(bookId);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and the {@link BookRepository} indicates that a {@link
     * Book} exists, and the operation finishes without an {@link BookAlreadyOwnedException} being
     * thrown (i.e {@link User} does not contain the {@link Book}).
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book exists, User exists, BookAlreadyOwnedException is not thrown")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testAddExistingBookToExistingUserAndBookAlreadyOwnedExceptionIsNotThrown(
        @ValidJwt final String jwt)
        throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        final var mockedBook = Mockito.mock(Book.class);
        doNothing().when(mockedUser).addBook(mockedBook);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(withJwt(put(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNoContent())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verify(userRepository, times(1)).save(mockedUser);
        verifyNoMoreInteractions(userRepository);
        verify(bookRepository, only()).findById(bookId);
    }


    /**
     * Tests the API response when removing a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#removeBook(long, long)} ), and the {@link UserRepository}
     * indicates that the {@link User} not exists, and the {@link BookRepository} indicates that the
     * {@link Book} not exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book not exists, User not exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testRemoveNonExistingBookToNonExistingUser(
        @ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(withJwt(delete(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when removing a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#removeBook(long, long)} ), and the {@link UserRepository}
     * indicates that the {@link User} not exists, but the {@link BookRepository} indicates that the
     * {@link Book} exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book exists, User not exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testRemoveExistingBookToNonExistingUser(@ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedBook = Mockito.mock(Book.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(withJwt(delete(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when removing a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#removeBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, but the {@link BookRepository} indicates that the
     * {@link Book} not exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book not exists, User exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testRemoveNonExistingBookToExistingUser(@ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(withJwt(delete(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verifyNoMoreInteractions(userRepository);
        verify(bookRepository, only()).findById(bookId);
    }

    /**
     * Tests the API response when removing a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#removeBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and the {@link BookRepository} indicates that a {@link
     * Book} exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book exists, User exists")
    @AuthenticatedWithJwt(username = AUTHENTICATED_USERNAME)
    void testRemoveExistingBookToExistingUser(@ValidJwt final String jwt) throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        final var mockedBook = Mockito.mock(Book.class);
        doNothing().when(mockedUser).removeBook(mockedBook);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(withJwt(delete(USER_BOOK_ID, userId, bookId), jwt))
            .andExpect(status().isNoContent())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).getFirstByUsername(AUTHENTICATED_USERNAME);
        verify(userRepository, times(1)).save(mockedUser);
        verifyNoMoreInteractions(userRepository);
        verify(bookRepository, only()).findById(bookId);
    }
}
