package wolox.training.web.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import lombok.Getter;
import wolox.training.models.User;

/**
 * A Data Transfer Object to receive information to create a new {@link User}.
 */
@Getter
public class UserCreationRequestDto {

    /**
     * The username.
     */
    @NotNull(message = "The username is missing.")
    private final String username;
    /**
     * The user's name.
     */
    @NotNull(message = "The name is missing.")
    private final String name;
    /**
     * The user's birth date.
     */
    @NotNull(message = "The birth date is missing.")
    @Past(message = "The birth date must be in the past")
    private final LocalDate birthDate;


    /**
     * Constructor.
     *
     * @param username The username.
     * @param name The user's name.
     * @param birthDate The user's birth date.
     */
    @JsonCreator
    public UserCreationRequestDto(
        @JsonProperty(value = "username", access = Access.WRITE_ONLY) final String username,
        @JsonProperty(value = "name", access = Access.WRITE_ONLY) final String name,
        @JsonProperty(value = "birthDate", access = Access.WRITE_ONLY) final LocalDate birthDate) {
        this.username = username;
        this.name = name;
        this.birthDate = birthDate;
    }
}
