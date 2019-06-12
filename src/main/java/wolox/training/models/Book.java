package wolox.training.models;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a book.
 */
@ToString(
    doNotUseGetters = true
)
@EqualsAndHashCode(
    of = "id"
)
@Getter
public class Book {

    /**
     * The book's id.
     */
    private final long id;
    /**
     * The book's genre.
     */
    private final String genre;
    /**
     * The book's author.
     */
    private final String author;
    /**
     * The book's image.
     */
    private final String image;
    /**
     * The book's title.
     */
    private final String title;
    /**
     * The book's subtitle.
     */
    private final String subtitle;
    /**
     * The book's publisher.
     */
    private final String publisher;
    /**
     * The year the book was/is/will be published.
     */
    private final String year;
    /**
     * The amount of pages in the book.
     */
    private final int pages;
    /**
     * The book's ISBN.
     */
    private final String isbn;


    /**
     * Default constructor for JPA Provider.
     */
    /* package */ Book() {
        // Default constructor that sets final fields with default values
        // Real values will be set by JPA Provider
        this.id = 0;
        this.genre = null;
        this.author = null;
        this.image = null;
        this.title = null;
        this.subtitle = null;
        this.publisher = null;
        this.year = null;
        this.pages = 0;
        this.isbn = null;
    }

    /**
     * Constructor.
     *
     * @param genre The book's genre.
     * @param author The book's author.
     * @param image The book's image.
     * @param title The book's title.
     * @param subtitle The book's subtitle.
     * @param publisher The book's publisher.
     * @param year The year the book was/is/will be published.
     * @param pages The amount of pages in the book.
     * @param isbn The book's ISBN.
     * @throws IllegalArgumentException If any of the given values is invalid.
     */
    public Book(final String genre, final String author, final String image,
        final String title, final String subtitle, final String publisher,
        final String year, final int pages, final String isbn) {
        assertAuthor(author);
        assertImage(image);
        assertTitle(title);
        assertSubtitle(subtitle);
        assertPublisher(publisher);
        assertYear(year);
        assertPages(pages);
        assertIsbn(isbn);
        this.id = 0; // Will be set when saving by JPA provider
        this.genre = genre;
        this.author = author;
        this.image = image;
        this.title = title;
        this.subtitle = subtitle;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.isbn = isbn;
    }


    /**
     * Asserts the given {@code author}.
     *
     * @param author The author value to be asserted.
     * @throws NullPointerException If the given {@code author} is {@code null}.
     */
    private static void assertAuthor(final String author) {
        Preconditions.checkNotNull(author, "The author must not be null");
    }

    /**
     * Asserts the given {@code image}.
     *
     * @param image The image value to be asserted.
     * @throws NullPointerException If the given {@code image} is {@code null}.
     */
    private static void assertImage(final String image) {
        Preconditions.checkNotNull(image, "The image must not be null");
    }

    /**
     * Asserts the given {@code title}.
     *
     * @param title The title value to be asserted.
     * @throws NullPointerException If the given {@code title} is {@code null}.
     */
    private static void assertTitle(final String title) {
        Preconditions.checkNotNull(title, "The title must not be null");
    }

    /**
     * Asserts the given {@code subtitle}.
     *
     * @param subtitle The subtitle value to be asserted.
     * @throws NullPointerException If the given {@code subtitle} is {@code null}.
     */
    private static void assertSubtitle(final String subtitle) {
        Preconditions.checkNotNull(subtitle, "The subtitle must not be null");
    }

    /**
     * Asserts the given {@code publisher}.
     *
     * @param publisher The publisher value to be asserted.
     * @throws NullPointerException If the given {@code publisher} is {@code null}.
     */
    private static void assertPublisher(final String publisher) {
        Preconditions.checkNotNull(publisher, "The publisher must not be null");
    }

    /**
     * Asserts the given {@code year}.
     *
     * @param year The year value to be asserted.
     * @throws NullPointerException If the given {@code year} is {@code null}.
     */
    private static void assertYear(final String year) {
        Preconditions.checkNotNull(year, "The year must not be null");
    }

    /**
     * Asserts the given {@code pages}.
     *
     * @param pages The pages value to be asserted.
     * @throws NullPointerException If the given {@code pages} is {@code null}.
     * @throws IllegalArgumentException If the given {@code pages} value is not positive.
     */
    private static void assertPages(final int pages) {
        Preconditions.checkArgument(pages > 0, "The amount of pages must be positive");
    }

    /**
     * Asserts the given {@code isbn}.
     *
     * @param isbn The isbn value to be asserted.
     * @throws NullPointerException If the given {@code isbn} is {@code null}.
     */
    private static void assertIsbn(final String isbn) {
        Preconditions.checkNotNull(isbn, "The isbn must not be null");
    }
}
