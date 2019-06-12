package wolox.training.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
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
import wolox.training.models.Book;
import wolox.training.repositories.BookRepository;
import wolox.training.utils.TestHelper;
import wolox.training.web.controllers.BookController;
import wolox.training.web.dtos.BookCreationRequestDto;


/**
 * Testing for the {@link BookController}.
 */
@WebMvcTest(controllers = {
    BookController.class
})
@MockBeans({
    @MockBean(BookRepository.class),
})
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
     * The {@link BookController} to be tested.
     */
    private final BookController bookController;

    /**
     * Constructor.
     *
     * @param mockMvc The {@link MockMvc} to perform testing over the {@link BookController}.
     * @param bookController The {@link BookController} to be tested.
     */
    @Autowired
    BookControllerTest(final MockMvc mockMvc,
        final BookController bookController) {
        this.mockMvc = mockMvc;
        this.bookController = bookController;
    }


    /**
     * Tests that this test is loaded correctly.
     */
    @Test
    @DisplayName("Initialization test")
    void testLoadsCorrectly(@Autowired final BookRepository bookRepository) {
        Assertions.assertAll(
            "The test class did not load correctly",
            () -> Assertions.assertNotNull(mockMvc, "The MockMvc instance was not loaded"),
            () -> Assertions.assertNotNull(bookController, "The book controller was not loaded."),
            () -> Assertions.assertNotNull(bookRepository, "The book repository was not loaded.")
        );
    }


    /**
     * Tests the API response when requesting all {@link Book}s (i.e using the controller method
     * {@link BookController#getAllBooks()}), and none is returned by the {@link BookRepository}.
     *
     * @param bookRepository A mocked {@link BookRepository} to be injected to the controller.
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all books - Empty")
    void testGetAllBooksReturningEmptyList(@Autowired final BookRepository bookRepository)
        throws Exception {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(BOOKS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
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
     * @param bookRepository A mocked {@link BookRepository} to be injected to the controller.
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all books - Not Empty")
    void testGetAllBooksReturningNonEmptyList(@Autowired final BookRepository bookRepository)
        throws Exception {
        final var maxListSize = 10;
        final var mockedList = TestHelper.mockBookList(maxListSize);
        when(bookRepository.findAll()).thenReturn(mockedList);
        mockMvc.perform(get(BOOKS_PATH).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(mockedList.size())))
            .andExpect(WebTestHelper.bookListJsonResultMatcher(mockedList))
        ;
        verify(bookRepository, only()).findAll();
    }


    /**
     * Tests the API response when requesting a {@link Book} by its id (i.e the controller method is
     * {@link BookController#getById(long)} ()}), and an empty {@link Optional} is returned by the
     * {@link BookRepository}.
     *
     * @param bookRepository A mocked {@link BookRepository} to be injected to the controller.
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get Book by id - Not exists")
    void testGetNonExistenceBook(@Autowired final BookRepository bookRepository) throws Exception {
        final var id = TestHelper.mockBookId();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get(BOOK_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isNotFound())
        ;
        verify(bookRepository, only()).findById(id);
    }

    /**
     * Tests the API response when requesting a {@link Book} by its id (i.e the controller method is
     * {@link BookController#getById(long)} ()}), and a non empty {@link Optional} is returned by
     * the {@link BookRepository}.
     *
     * @param bookRepository A mocked {@link BookRepository} to be injected to the controller.
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get Book by id - Exists")
    void testGetExistingBook(@Autowired final BookRepository bookRepository) throws Exception {
        final var id = TestHelper.mockBookId();
        final var mockedBook = TestHelper.mockBook();
        when(bookRepository.findById(id)).thenReturn(Optional.of(mockedBook));
        mockMvc.perform(get(BOOK_PATH_ID, id).accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(WebTestHelper.bookJsonResultMatcher(mockedBook, "$"))
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
    @DisplayName("Create Book - Empty body")
    void testCreateWithNoBody(@Autowired final BookRepository bookRepository) throws Exception {
        mockMvc.perform(
            post(BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(WebTestHelper.emptyContent())
        ).andExpect(status().isBadRequest())
        ;
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when creating a {@link Book} (i.e the controller method is {@link
     * BookController#createBook(BookCreationRequestDto)}), and a the sent JSON has invalid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create Book - Invalid arguments")
    void testCreateWithInvalidArguments(@Autowired final BookRepository bookRepository)
        throws Exception {
        mockMvc.perform(
            post(BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(WebTestHelper.invalidBookCreationRequest())
        ).andExpect(status().isBadRequest())
        ;
        verifyZeroInteractions(bookRepository);
    }

    /**
     * Tests the API response when creating a {@link Book} (i.e the controller method is {@link
     * BookController#createBook(BookCreationRequestDto)}), and a the sent JSON has valid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create Book - Valid arguments")
    void testCreateWithValidArguments(@Autowired final BookRepository bookRepository)
        throws Exception {
        when(bookRepository.save(Mockito.any(Book.class))).then(i -> i.getArgument(0));
        mockMvc.perform(
            post(BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(WebTestHelper.validBookCreationRequest())
        )
            .andExpect(status().isCreated())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        ;
    }


}
