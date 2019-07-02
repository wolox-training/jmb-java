package wolox.training.web.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;

/**
 * A Data Transfer Object used to receive a username and password, in order to issue a JWT.
 */
@Getter
public class JwtTokenIssueRequestDto {

    /**
     * The username.
     */
    private final String username;
    /**
     * The password.
     */
    private final String password;


    /**
     * Constructor.
     *
     * @param username The username.
     * @param password The password.
     */
    @JsonCreator
    public JwtTokenIssueRequestDto(
        @JsonProperty(value = "username", access = Access.WRITE_ONLY) final String username,
        @JsonProperty(value = "password", access = Access.WRITE_ONLY) final String password) {
        this.username = username;
        this.password = password;
    }
}
