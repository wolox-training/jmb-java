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

import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import wolox.training.utils.ValuesGenerator;
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
     * A mocked {@link BookRepository} which was injected to the {@link BookController}.
     */
    private final BookRepository bookRepository;


    /**
     * Constructor.
     *
     * @param mockMvc The {@link MockMvc} to perform testing over the {@link BookController}.
     * @param bookRepository A mocked {@link BookRepository} which was injected to the {@link
     * BookController}.
     */
    @Autowired
    BookControllerTest(final MockMvc mockMvc, final BookRepository bookRepository) {
        this.mockMvc = mockMvc;
        this.bookRepository = bookRepository;
    }


    /**
     * Tests the API response when requesting all {@link Book}s (i.e using the controller method
     * {@link BookController#getAllBooks()}), and none is returned by the {@link BookRepository}.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all books - Empty")
    void testGetAllBooksReturningEmptyList() throws Exception {
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
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get all books - Not Empty")
    void testGetAllBooksReturningNonEmptyList() throws Exception {
        final var maxListSize = 10;
        final var mockedList = TestHelper.mockBookList(maxListSize);
        mockedList.forEach(TestHelper::addId);
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
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get Book by id - Not exists")
    void testGetNonExistenceBook() throws Exception {
        final var id = ValuesGenerator.validBookId();
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
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Get Book by id - Exists")
    void testGetExistingBook() throws Exception {
        final var id = ValuesGenerator.validBookId();
        final var mockedBook = TestHelper.mockBook();
        TestHelper.addId(mockedBook);
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
    void testCreateWithNoBody() throws Exception {
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
     * BookController#createBook(BookCreationRequestDto)}), and the sent JSON has invalid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create Book - Invalid arguments")
    void testCreateWithInvalidArguments() throws Exception {
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
     * BookController#createBook(BookCreationRequestDto)}), and the sent JSON has valid values.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Create Book - Valid arguments")
    void testCreateWithValidArguments() throws Exception {
        when(bookRepository.save(any(Book.class))).then(i -> i.getArgument(0));
        mockMvc.perform(
            post(BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(WebTestHelper.validBookCreationRequest())
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
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete Book - Not exists")
    void testDeleteNonExistingBook() throws Exception {
        final var id = ValuesGenerator.validBookId();
        when(bookRepository.existsById(id)).thenReturn(false);
        mockMvc.perform(delete(BOOK_PATH_ID, id))
            .andExpect(status().isNoContent());
        verify(bookRepository, only()).existsById(id);
    }

    /**
     * Tests the API response when deleting a {@link Book} by its id (i.e the controller method is
     * {@link BookController#deleteBook(long)} ), and the {@link BookRepository} indicates that a
     * {@link Book} with the said id exists.
     *
     * @throws Exception if {@link MockMvc#perform(RequestBuilder)} throws it.
     */
    @Test
    @DisplayName("Delete Book - Exists")
    void testDeleteExistingBook() throws Exception {
        final var id = ValuesGenerator.validBookId();
        when(bookRepository.existsById(id)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(id);
        mockMvc.perform(delete(BOOK_PATH_ID, id))
            .andExpect(status().isNoContent());
        verify(bookRepository, times(1)).existsById(id);
        verify(bookRepository, times(1)).deleteById(id);
        verifyNoMoreInteractions(bookRepository);
    }
}
