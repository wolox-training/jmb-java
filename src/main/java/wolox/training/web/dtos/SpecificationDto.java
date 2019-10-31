package wolox.training.web.dtos;

import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

/**
 * A generic DTO for {@link Specification}.
 */
@Getter
public class SpecificationDto<S extends Specification<?>> {

    /**
     * The {@link Specification} being wrapped.
     */
    private final S specification;

    /**
     * Constructor.
     *
     * @param specification The {@link Specification} being wrapped.
     */
    public SpecificationDto(final S specification) {
        this.specification = specification;
    }
}
