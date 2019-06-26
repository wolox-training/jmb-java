package wolox.training.web.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
     * The user's password.
     */
    @NotNull(message = Constants.MISSING_PASSWORD)
    @Size(min = User.PASSWORD_MIN_LENGTH,
        message = Constants.SHORT_PASSWORD
    )
    @Pattern(
        regexp = User.CONTAIN_LOWERCASE_REGEX,
        message = Constants.PASSWORD_MISSING_LOWERCASE
    )
    @Pattern(
        regexp = User.CONTAIN_UPPERCASE_REGEX,
        message = Constants.PASSWORD_MISSING_UPPERCASE
    )
    @Pattern(
        regexp = User.CONTAIN_NUMBER_REGEX,
        message = Constants.PASSWORD_MISSING_NUMBER
    )
    @Pattern(
        regexp = User.CONTAIN_SPECIAL_CHARACTER_REGEX,
        message = Constants.PASSWORD_MISSING_SPECIAL
    )
    private final String password;
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
     * @param password The password.
     * @param name The user's name.
     * @param birthDate The user's birth date.
     */
    @JsonCreator
    public UserCreationRequestDto(
        @JsonProperty(value = "username", access = Access.WRITE_ONLY) final String username,
        @JsonProperty(value = "password", access = Access.WRITE_ONLY) final String password,
        @JsonProperty(value = "name", access = Access.WRITE_ONLY) final String name,
        @JsonProperty(value = "birthDate", access = Access.WRITE_ONLY) final LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
    }
}
