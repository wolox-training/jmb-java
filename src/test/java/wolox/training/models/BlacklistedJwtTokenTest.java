package wolox.training.models;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testing for the {@link BlacklistedJwtToken} class.
 */
class BlacklistedJwtTokenTest {


    @Test
    @DisplayName("Create BlacklistedJwtToken - Valid arguments")
    void testCreateBlacklistedJwtTokenWithValidArguments() {
        Assertions.assertDoesNotThrow(
            () -> new BlacklistedJwtToken(UUID.randomUUID().toString()),
            "Creating a Blacklisted Jwt Token with valid values is throwing an unexpected Exception"
        );
    }

    @Test
    @DisplayName("Create BlacklistedJwtToken - Null id")
    void testCreateBlacklistedJwtTokenWithNullId() {
        Assertions.assertThrows(
            NullPointerException.class,
            () -> new BlacklistedJwtToken(null),
            "Creating a Blacklisted Jwt Token with a null id"
                + " is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create BlacklistedJwtToken - Empty String")
    void testCreateBlacklistedJwtTokenWithEmptyStringId() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new BlacklistedJwtToken(""),
            "Creating a Blacklisted Jwt Token with an empty string id"
                + " is not throwing a NullPointerException"
        );
    }

    @Test
    @DisplayName("Create BlacklistedJwtToken - Empty String")
    void testCreateBlacklistedJwtTokenWithIdWithoutText() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new BlacklistedJwtToken(" \t\n "),
            "Creating a Blacklisted Jwt Token with an id that does not contain text"
                + " is not throwing a NullPointerException"
        );
    }
}
