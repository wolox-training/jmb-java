package wolox.training.web.dtos;

import org.springframework.web.bind.annotation.RequestParam;
import wolox.training.repositories.BookSpecification;

/**
 * A DTO for {@link BookSpecification}
 */
public class BookSpecificationDto extends SpecificationDto<BookSpecification> {

    /**
     * Constructor
     *
     * @param genre The genre for the {@link BookSpecification}.
     * @param author The author for the {@link BookSpecification}.
     * @param image The image for the {@link BookSpecification}.
     * @param title The title for the {@link BookSpecification}.
     * @param subtitle The subtitle for the {@link BookSpecification}.
     * @param publisher The publisher for the {@link BookSpecification}.
     * @param year The year for the {@link BookSpecification}.
     * @param pages The pages for the {@link BookSpecification}.
     * @param isbn The isbn for the {@link BookSpecification}.
     */
    public BookSpecificationDto(
        @RequestParam(name = "genre", required = false) final String genre,
        @RequestParam(name = "author", required = false) final String author,
        @RequestParam(name = "image", required = false) final String image,
        @RequestParam(name = "title", required = false) final String title,
        @RequestParam(name = "subtitle", required = false) final String subtitle,
        @RequestParam(name = "publisher", required = false) final String publisher,
        @RequestParam(name = "year", required = false) final String year,
        @RequestParam(name = "pages", required = false) final Integer pages,
        @RequestParam(name = "isbn", required = false) final String isbn) {
        super(
            new BookSpecification(
                genre,
                author,
                image,
                title,
                subtitle,
                publisher,
                year,
                pages,
                isbn
            )
        );
    }
}
