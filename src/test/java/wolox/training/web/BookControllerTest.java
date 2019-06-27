package wolox.training.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wolox.training.web.WebTestHelper.bookJsonResultMatcher;
import static wolox.training.web.WebTestHelper.bookListJsonResultMatcher;
import static wolox.training.web.WebTestHelper.emptyContent;
import static wolox.training.web.WebTestHelper.invalidBookCreationRequest;
import static wolox.training.web.WebTestHelper.validBookCreationRequest;
import static wolox.training.web.WebTestHelper.withJwt;

import java.util.Collections;
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
import wolox.training.models.Book;
import wolox.training.repositories.BlacklistedJwtTokenRepository;
import wolox.training.repositories.BookRepository;
import wolox.training.repositories.UserRepository;
import wolox.training.services.authentication.JwtTokenService;
import wolox.training.services.open_library.OpenLibraryService;
import wolox.training.utils.TestHelper;
import wolox.training.utils.ValuesGenerator;
import wolox.training.web.JwtExtension.AuthenticatedWithJwt;
import wolox.training.web.JwtExtension.ValidJwt;
import wolox.training.web.controllers.BookController;
import wolox.training.web.dtos.BookCreationRequestDto;

/**
 * Testing for the {@link BookController}.
 */
@WebMvcTest(
    controllers = {
        BookController.class,
    },
    includeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebTestJwtTokenServiceConfig.class),
//        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtExtension.JwtExtensionConfigurer.class),
    }
)
@MockBean(BookRepository.class)
@MockBean(OpenLibraryService.class)
@MockBean(BlacklistedJwtTokenRepository.class)
@MockBean(UserRepository.class)
//@ExtendWith(JwtExtension.class)
class BookControllerTest {

    /**
     * The API base path for {@link Book}s.
     */
    private static final String BOOKS_PATH = "/api/books/";
    /**
     * The API path for a specific {@link Book} (located by its id).
     */
    private static final String BOOK_PATH_ID = BOOKS_PATH + "{id}/";


    /**
     * The {@link MockMvc} instance to perform testing over the {@link BookController}.
     */
    private final MockMvc mockMvc;
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
     * @param mockMvc The {@link MockMvc} to perform testing over the {@link BookController}.
     * @param bookRepository A mocked {@link BookRepository} which was injected to the {@link
     * BookController}.
     * @param jwtTokenService The {@link JwtTokenService} to be passed to the {@link JwtExtension}.
     * @param userRepository The {@link UserRepository} used to configure the {@link JwtExtension}
     * in order to simulate the registration of a {@link wolox.training.models.User}, so JWTs can be
     * created for them.
     */
    @Autowired
    BookControllerTest(
        final MockMvc mockMvc,
        final BookRepository bookRepository,
        final JwtTokenService jwtTokenService,
        final UserRepository userRepository) {

        this.mockMvc = mockMvc;
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
     * Tests the API response when requesting all {@link Book}s (i.e using the controller method
     * {@link BookController#getAllBooks()}), and none is returned by the {@link BookRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all books - Empty - Authenticated")
    @AuthenticatedWithJwt
    void testGetAllBooksReturningEmptyList(@ValidJwt final String jwt) throws Exception {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(withJwt(get(BOOKS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(0)))
        ;
        verify(bookRepository, only()).findAll();
    }

    /**
     * Tests the API response when requesting all {@link Book}s (i.e using the controller method
     * {@link BookController#getAllBooks()}), and a non-empty {@link Iterable} is returned by the
     * {@link BookRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all books - Not Empty - Authenticated")
    @AuthenticatedWithJwt
    void testGetAllBooksReturningNonEmptyList(@ValidJwt final String jwt) throws Exception {
        final var maxListSize = 10;
        final var mockedList = TestHelper.mockBookList(maxListSize);
        mockedList.forEach(TestHelper::addId);
        when(bookRepository.findAll()).thenReturn(mockedList);
        mockMvc.perform(withJwt(get(BOOKS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(mockedList.size())))
            .andExpect(bookListJsonResultMatcher(mockedList))
        ;
        verify(bookRepository, only()).findAll();
    }


    /**
     * Tests the API response when requesting a {@link Book} by its id (i.e the controller method is
     * {@link BookController#getById(long)} ()}), and an empty {@link Optional} is returned by the
     * {@link BookRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get Book by id - Not exists - Authenticated")
    @AuthenticatedWithJwt
    void testGetNonExistenceBook(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validBookId();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(
            withJwt(get(BOOK_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isNotFound())
        ;
        verify(bookRepository, only()).findById(id);
    }

    /**
     * Tests the API response when requesting a {@link Book} by its id (i.e the controller method is
     * {@link BookController#getById(long)} ()}), and a non empty {@link Optional} is returned by
     * the {@link BookRepository}.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get Book by id - Exists - Authenticated")
    @AuthenticatedWithJwt
    void testGetExistingBook(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validBookId();
        final var mockedBook = TestHelper.mockBook();
        TestHelper.addId(mockedBook);
        when(bookRepository.findById(id)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(
            withJwt(get(BOOK_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE), jwt)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(bookJsonResultMatcher(mockedBook, "$"))
        ;
        verify(bookRepository, only()).findById(id);
    }

    /**
     * Tests the API response when creating a {@link Book} (i.e the controller method is {@link
     * BookController#createBook(BookCreationRequestDto)}), and an empty body is sent.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create Book - Empty body - Not authenticated")
    void testCreateWithNoBody() throws Exception {
        mockMvc.perform(
            post(BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(emptyContent())
        )
            .andExpect(status().isBadRequest())
        ;
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when creating a {@link Book} (i.e the controller method is {@link
     * BookController#createBook(BookCreationRequestDto)}), and the sent JSON has invalid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create Book - Invalid arguments - Not authenticated")
    void testCreateWithInvalidArguments() throws Exception {
        mockMvc.perform(
            post(BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(invalidBookCreationRequest())
        )
            .andExpect(status().isBadRequest())
        ;
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when creating a {@link Book} (i.e the controller method is {@link
     * BookController#createBook(BookCreationRequestDto)}), and the sent JSON has valid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create Book - Valid arguments - Not authenticated")
    void testCreateWithValidArguments() throws Exception {
        when(bookRepository.save(any(Book.class))).then(i -> i.getArgument(0));
        mockMvc.perform(
            post(BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(validBookCreationRequest())
        )
            .andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        ;
        verify(bookRepository, only()).save(any(Book.class));
    }

    /**
     * Tests the API response when deleting a {@link Book} by its id (i.e the controller method is
     * {@link BookController#deleteBook(long)} ), and the {@link BookRepository} indicates there is
     * no {@link Book} with the said id.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete Book - Not exists - Authenticated")
    @AuthenticatedWithJwt
    void testDeleteNonExistingBook(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validBookId();
        when(bookRepository.existsById(id)).thenReturn(false);
        mockMvc.perform(withJwt(delete(BOOK_PATH_ID, id), jwt))
            .andExpect(status().isNoContent());
        verify(bookRepository, only()).existsById(id);
    }

    /**
     * Tests the API response when deleting a {@link Book} by its id (i.e the controller method is
     * {@link BookController#deleteBook(long)} ), and the {@link BookRepository} indicates that a
     * {@link Book} with the said id exists.
     *
     * @param jwt An injected JWT to be sent in order to authenticate with the server
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete Book - Exists - Authenticated")
    @AuthenticatedWithJwt
    void testDeleteExistingBook(@ValidJwt final String jwt) throws Exception {
        final var id = ValuesGenerator.validBookId();
        when(bookRepository.existsById(id)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(id);
        mockMvc.perform(withJwt(delete(BOOK_PATH_ID, id), jwt))
            .andExpect(status().isNoContent());
        verify(bookRepository, times(1)).existsById(id);
        verify(bookRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(bookRepository);
    }
}
