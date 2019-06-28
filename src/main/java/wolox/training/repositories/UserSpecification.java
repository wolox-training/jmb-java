package wolox.training.repositories;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import wolox.training.models.User;

/**
 * A {@link Specification} for {@link User}s.
 */
@AllArgsConstructor
public class UserSpecification implements Specification<User> {

    /**
     * Filter for username.
     */
    private final String username;
    /**
     * Filter for name.
     */
    private final String name;
    /**
     * The min. {@link LocalDate} all returned {@link User}s when using this {@link Specification}
     * will have.
     */
    private final LocalDate from;
    /**
     * The max. {@link LocalDate} all returned {@link User}s when using this {@link Specification}
     * will have.
     */
    private final LocalDate to;


    @Override
    public Predicate toPredicate(
        final Root<User> root,
        final CriteriaQuery<?> query,
        final CriteriaBuilder criteriaBuilder) {

        final List<Predicate> predicates = new LinkedList<>();
        Optional.ofNullable(username)
            .map(value -> criteriaBuilder.equal(root.get("username"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(name)
            .map(value -> criteriaBuilder.equal(root.get("name"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(from)
            .map(value -> criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), value))
            .ifPresent(predicates::add);
        Optional.ofNullable(to)
            .map(value -> criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), value))
            .ifPresent(predicates::add);

        return predicates.stream().reduce(criteriaBuilder.and(), criteriaBuilder::and);
    }
}
