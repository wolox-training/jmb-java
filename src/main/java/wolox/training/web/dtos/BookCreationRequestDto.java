package wolox.training.web.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Getter;
import wolox.training.models.Book;

/**
 * A Data Transfer Object to receive information to create a new {@link Book}.
 */
@Getter
public class BookCreationRequestDto {

    /**
     * The book's genre.
     */
    private final String genre;
    /**
     * The book's author.
     */
    @NotNull(message = "The author is missing.")
    private final String author;
    /**
     * The book's image.
     */
    @NotNull(message = "The image is missing.")
    private final String image;
    /**
     * The book's title.
     */
    @NotNull(message = "The title is missing.")
    private final String title;
    /**
     * The book's subtitle.
     */
    @NotNull(message = "The subtitle is missing.")
    private final String subtitle;
    /**
     * The book's publisher.
     */
    @NotNull(message = "The publisher is missing.")
    private final String publisher;
    /**
     * The year the book was/is/will be published.
     */
    @NotNull(message = "The year is missing.")
    private final String year;
    /**
     * The amount of pages in the book.
     */
    @NotNull(message = "The pages value is missing.")
    @Positive(message = "The pages value must be positive")
    private final Integer pages;
    /**
     * The book's ISBN.
     */
    @NotNull(message = "The isbn is missing.")
    private final String isbn;

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
     */
    @JsonCreator
    public BookCreationRequestDto(
        @JsonProperty(value = "genre", access = Access.WRITE_ONLY) final String genre,
        @JsonProperty(value = "author", access = Access.WRITE_ONLY) final String author,
        @JsonProperty(value = "image", access = Access.WRITE_ONLY) final String image,
        @JsonProperty(value = "title", access = Access.WRITE_ONLY) final String title,
        @JsonProperty(value = "subtitle", access = Access.WRITE_ONLY) final String subtitle,
        @JsonProperty(value = "publisher", access = Access.WRITE_ONLY) final String publisher,
        @JsonProperty(value = "year", access = Access.WRITE_ONLY) final String year,
        @JsonProperty(value = "pages", access = Access.WRITE_ONLY) final Integer pages,
        @JsonProperty(value = "isbn", access = Access.WRITE_ONLY) final String isbn) {
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
}
