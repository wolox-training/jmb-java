package wolox.training.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import wolox.training.models.Book;

/**
 * A {@link Specification} for {@link Book}s.
 */
@AllArgsConstructor
public class BookSpecification implements Specification<Book> {

    /**
     * Filter for genre.
     */
    private final String genre;
    /**
     * Filter for author.
     */
    private final String author;
    /**
     * Filter for image.
     */
    private final String image;
    /**
     * Filter for title.
     */
    private final String title;
    /**
     * Filter for subtitle.
     */
    private final String subtitle;
    /**
     * Filter for publisher.
     */
    private final String publisher;
    /**
     * Filter for year.
     */
    private final String year;
    /**
     * Filter for pages.
     */
    private final Integer pages;
    /**
     * Filter for isbn.
     */
    private final String isbn;


    @Override
    public Predicate toPredicate(
        final Root<Book> root,
        final CriteriaQuery<?> query,
        final CriteriaBuilder criteriaBuilder) {

        final List<Predicate> predicates = new LinkedList<>();
        Optional.ofNullable(genre)
            .map(value -> criteriaBuilder.equal(root.get("genre"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(author)
            .map(value -> criteriaBuilder.equal(root.get("author"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(image)
            .map(value -> criteriaBuilder.equal(root.get("image"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(title)
            .map(value -> criteriaBuilder.equal(root.get("title"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(subtitle)
            .map(value -> criteriaBuilder.equal(root.get("subtitle"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(publisher)
            .map(value -> criteriaBuilder.equal(root.get("publisher"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(year)
            .map(value -> criteriaBuilder.equal(root.get("year"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(pages)
            .map(value -> criteriaBuilder.equal(root.get("pages"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(isbn)
            .map(value -> criteriaBuilder.equal(root.get("isbn"), value))
            .ifPresent(predicates::add);

        return predicates.stream().reduce(criteriaBuilder.and(), criteriaBuilder::and);
    }
}
