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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import wolox.training.exceptions.BookAlreadyOwnedException;
import wolox.training.models.Book;
import wolox.training.models.User;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.utils.TestHelper;
import wolox.training.utils.ValuesGenerator;
import wolox.training.web.controllers.BookController;
import wolox.training.web.controllers.UserController;
import wolox.training.web.dtos.UserCreationRequestDto;


/**
 * Testing for the {@link UserController}.
 */
@WebMvcTest(controllers = {
    UserController.class
})
@MockBeans({
    @MockBean(UserRepository.class),
    @MockBean(BookRepository.class),
})
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
     * Constructor.
     *
     * @param mockMvc The {@link MockMvc} to perform testing over the {@link UserController}.
     * @param userRepository A mocked {@link UserRepository} which was injected to the {@link
     * UserController}.
     * @param bookRepository A mocked {@link BookRepository} which was injected to the {@link
     * BookController}.
     */
    @Autowired
    UserControllerTest(final MockMvc mockMvc,
        final UserRepository userRepository,
        final BookRepository bookRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }


    /**
     * Tests the API response when requesting all {@link User}s (i.e using the controller method
     * {@link UserController#getAllUsers()}), and none is returned by the {@link UserRepository}.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all users - Empty")
    void testGetAllUsersReturningEmptyList() throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(USERS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)))
        ;
        verify(userRepository, only()).findAll();
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when requesting all {@link User}s (i.e using the controller method
     * {@link UserController#getAllUsers()}), and a non-empty {@link Iterable} is returned by the
     * {@link UserRepository}.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all users - Not Empty")
    void testGetAllUsersReturningNonEmptyList() throws Exception {
        final var maxListSize = 10;
        final var mockedList = TestHelper.mockUserList(maxListSize);
        when(userRepository.findAll()).thenReturn(mockedList);
        mockMvc.perform(get(USERS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(mockedList.size())))
            .andExpect(WebTestHelper.userListJsonResultMatcher(mockedList))
        ;
        verify(userRepository, only()).findAll();
        verifyZeroInteractions(bookRepository);
    }


    /**
     * Tests the API response when requesting a {@link User} by its id (i.e the controller method is
     * {@link UserController#getById(long)} ()}), and an empty {@link Optional} is returned by the
     * {@link UserRepository}.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User by id - Not exists")
    void testGetNonExistenceUser() throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get(USERS_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(id);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when requesting a {@link User} by its id (i.e the controller method is
     * {@link UserController#getById(long)} ()}), and a non empty {@link Optional} is returned by
     * the {@link UserRepository}.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User by id - Exists")
    void testGetExistingUser() throws Exception {
        final var id = ValuesGenerator.validUserId();
        final var mockedUser = TestHelper.mockUser();
        TestHelper.addId(mockedUser);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockedUser));
        mockMvc.perform(get(USERS_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(WebTestHelper.userJsonResultMatcher(mockedUser, "$"))
        ;
        verify(userRepository, only()).findById(id);
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
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete User - Not exists")
    void testDeleteNonExistingUser() throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.existsById(id)).thenReturn(false);
        mockMvc.perform(delete(USERS_PATH_ID, id))
            .andExpect(status().isNoContent());
        verify(userRepository, only()).existsById(id);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when deleting a {@link User} by its id (i.e the controller method is
     * {@link UserController#deleteUser(long)} ), and the {@link UserRepository} indicates that a
     * {@link User} with the said id exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete User - Exists")
    void testDeleteExistingUser() throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.existsById(id)).thenReturn(true);
        doNothing().when(userRepository).deleteById(id);
        mockMvc.perform(delete(USERS_PATH_ID, id))
            .andExpect(status().isNoContent());
        verify(userRepository, times(1)).existsById(id);
        verify(userRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(userRepository);
        verifyZeroInteractions(bookRepository);
    }


    /**
     * Tests the API response when getting the {@link Book}s of a {@link User} (i.e the controller
     * method is {@link UserController#getUserBooks(long)} ), and the {@link UserRepository}
     * indicates that a {@link User} not exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User books - User not exists")
    void testGetUserBooksForNonExistingUser() throws Exception {
        final var id = ValuesGenerator.validUserId();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get(USERS_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(id);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when getting the {@link Book}s of a {@link User} (i.e the controller
     * method is {@link UserController#getUserBooks(long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, but it does not have any {@link Book}s.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User books - User does not have books")
    void testGetUserBooksForUserWithoutBooks() throws Exception {
        final var id = ValuesGenerator.validUserId();
        final var mockedUser = TestHelper.mockUser();
        TestHelper.addId(mockedUser);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockedUser));
        mockMvc.perform(get(USER_BOOKS, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)))
        ;
        verify(userRepository, only()).findById(id);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when getting the {@link Book}s of a {@link User} (i.e the controller
     * method is {@link UserController#getUserBooks(long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and it has {@link Book}s.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get User books - User has books")
    void testGetUserBooksForUserWithBooks() throws Exception {
        final var id = ValuesGenerator.validUserId();
        final var mockedUser = Mockito.mock(User.class);
        final var maxListSize = 10;
        final var mockedBooks = TestHelper.mockBookSet(maxListSize);
        when(userRepository.findById(id)).thenReturn(Optional.of(mockedUser));
        when(mockedUser.getBooks()).thenReturn(mockedBooks);
        mockMvc.perform(get(USER_BOOKS, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(mockedBooks.size())))
            .andExpect(WebTestHelper.bookListJsonResultMatcher(new LinkedList<>(mockedBooks)))
        ;
        verify(userRepository, only()).findById(id);
        verifyZeroInteractions(bookRepository);
    }


    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that the {@link User} not exists, and the {@link BookRepository} indicates that the
     * {@link Book} not exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book not exists, User not exists")
    void testAddNonExistingBookToNonExistingUser() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(put(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(userId);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that the {@link User} not exists, but the {@link BookRepository} indicates that the
     * {@link Book} exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book exists, User not exists")
    void testAddExistingBookToNonExistingUser() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedBook = Mockito.mock(Book.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(put(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(userId);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, but the {@link BookRepository} indicates that the
     * {@link Book} not exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book not exists, User exists")
    void testAddNonExistingBookToExistingUser() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(put(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(userId);
        verify(bookRepository, only()).findById(bookId);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and the {@link BookRepository} indicates that a {@link
     * Book} exists, and a {@link BookAlreadyOwnedException} is throws (i.e the {@link User} already
     * contains the {@link Book}).
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book exists, User exists, BookAlreadyOwnedException is thrown")
    void testAddExistingBookToExistingUserAndItThrowsBookAlreadyOwnedException() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        final var mockedBook = Mockito.mock(Book.class);
        doThrow(BookAlreadyOwnedException.class).when(mockedUser).addBook(mockedBook);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(put(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isConflict())
        ;
        verify(userRepository, only()).findById(userId);
        verify(bookRepository, only()).findById(bookId);
    }

    /**
     * Tests the API response when adding a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#addBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and the {@link BookRepository} indicates that a {@link
     * Book} exists, and the operation finishes without an {@link BookAlreadyOwnedException} being
     * thrown (i.e {@link User} does not contain the {@link Book}).
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Add Book - Book exists, User exists, BookAlreadyOwnedException is not thrown")
    void testAddExistingBookToExistingUserAndBookAlreadyOwnedExceptionIsNotThrown()
        throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        final var mockedBook = Mockito.mock(Book.class);
        doNothing().when(mockedUser).addBook(mockedBook);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(put(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNoContent())
        ;
        verify(userRepository, times(1)).findById(userId);
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
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book not exists, User not exists")
    void testRemoveNonExistingBookToNonExistingUser() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(delete(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(userId);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when removing a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#removeBook(long, long)} ), and the {@link UserRepository}
     * indicates that the {@link User} not exists, but the {@link BookRepository} indicates that the
     * {@link Book} exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book exists, User not exists")
    void testRemoveExistingBookToNonExistingUser() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedBook = Mockito.mock(Book.class);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(delete(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(userId);
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when removing a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#removeBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, but the {@link BookRepository} indicates that the
     * {@link Book} not exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book not exists, User exists")
    void testRemoveNonExistingBookToExistingUser() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());
        mockMvc.perform(delete(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNotFound())
        ;
        verify(userRepository, only()).findById(userId);
        verify(bookRepository, only()).findById(bookId);
    }

    /**
     * Tests the API response when removing a {@link Book} to a {@link User} (i.e the controller
     * method is {@link UserController#removeBook(long, long)} ), and the {@link UserRepository}
     * indicates that a {@link User} exists, and the {@link BookRepository} indicates that a {@link
     * Book} exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Remove Book - Book exists, User exists")
    void testRemoveExistingBookToExistingUser() throws Exception {
        final var userId = ValuesGenerator.validUserId();
        final var bookId = ValuesGenerator.validBookId();
        final var mockedUser = Mockito.mock(User.class);
        final var mockedBook = Mockito.mock(Book.class);
        doNothing().when(mockedUser).removeBook(mockedBook);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(delete(USER_BOOK_ID, userId, bookId))
            .andExpect(status().isNoContent())
        ;
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(mockedUser);
        verifyNoMoreInteractions(userRepository);
        verify(bookRepository, only()).findById(bookId);
    }
}
