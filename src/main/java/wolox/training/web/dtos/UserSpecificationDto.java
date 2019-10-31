package wolox.training.web.dtos;

import java.time.LocalDate;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestParam;
import wolox.training.repositories.UserSpecification;

/**
 * A DTO for {@link UserSpecification}
 */
@Getter
public class UserSpecificationDto extends SpecificationDto<UserSpecification> {

    /**
     * Constructor.
     *
     * @param username The username for the {@link UserSpecification}.
     * @param name The name for the {@link UserSpecification}.
     * @param from The from for the {@link UserSpecification}.
     * @param to The to for the {@link UserSpecification}.
     */
    public UserSpecificationDto(
        @RequestParam(name = "username", required = false) final String username,
        @RequestParam(name = "name", required = false) final String name,
        @RequestParam(name = "from", required = false) @DateTimeFormat(iso = ISO.DATE) final LocalDate from,
        @RequestParam(name = "to", required = false) @DateTimeFormat(iso = ISO.DATE) final LocalDate to) {
        super(new UserSpecification(username, name, from, to));
    }
}
