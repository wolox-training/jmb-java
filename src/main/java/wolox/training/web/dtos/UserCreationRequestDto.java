package wolox.training.web.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDate;
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
    private final String username;
    /**
     * The user's name.
     */
    private final String name;
    /**
     * The user's birth date.
     */
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
