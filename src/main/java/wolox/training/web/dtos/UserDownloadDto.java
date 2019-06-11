package wolox.training.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDate;
import wolox.training.models.User;

/**
 * A Data Transfer Object that wraps a {@link User}, sending only selected data.
 */
public class UserDownloadDto {

    /**
     * The wrapped {@link User}.
     */
    private final User user;


    /**
     * Constructor.
     *
     * @param user The wrapped {@link User}.
     */
    public UserDownloadDto(final User user) {
        this.user = user;
    }


    @JsonProperty(value = "id", access = Access.READ_ONLY)
    final long getId() {
        return user.getId();
    }

    @JsonProperty(value = "username", access = Access.READ_ONLY)
    final String getUsername() {
        return user.getUsername();
    }

    @JsonProperty(value = "name", access = Access.READ_ONLY)
    final String getName() {
        return user.getName();
    }

    @JsonProperty(value = "birthDate", access = Access.READ_ONLY)
    final LocalDate getBirthDate() {
        return user.getBirthDate();
    }

}
