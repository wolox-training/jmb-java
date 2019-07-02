package wolox.training.web.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import wolox.training.models.User;

/**
 * A Data Transfer Object used to receive a needed data to change a user's password.
 */
@Getter
public class ChangePasswordRequestDto {

    /**
     * The current password.
     */
    private final String currentPassword; // Not validated as it won't be set anywhere.

    /**
     * The new password.
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
    private final String newPassword;


    /**
     * Constructor.
     *
     * @param currentPassword The current password.
     * @param newPassword The new password.
     */
    @JsonCreator
    public ChangePasswordRequestDto(
        @JsonProperty(value = "currentPassword", access = Access.WRITE_ONLY) final String currentPassword,
        @JsonProperty(value = "newPassword", access = Access.WRITE_ONLY) final String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
