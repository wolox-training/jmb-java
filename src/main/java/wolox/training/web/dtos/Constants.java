package wolox.training.web.dtos;

import wolox.training.models.User;

/**
 * Class containing constants to be used accross all the DTOs.
 */
/* package */ class Constants {

    /**
     * Private constructor to avoid instantiation.
     */
    private Constants() {
    }


    /**
     * Message to be displayed when validating a DTO that has a password, and it is missing.
     */
    /* package */ static final String MISSING_PASSWORD = "The password is missing.";

    /**
     * Message to be displayed when validating a DTO that has a password, and it is too short.
     */
    /* package */ static final String SHORT_PASSWORD =
        "The password must contain at least " + User.PASSWORD_MIN_LENGTH + " characters";

    /**
     * Message to be displayed when validating a DTO that has a password, and it has a lowercase
     * letter missing.
     */
    /* package */ static final String PASSWORD_MISSING_LOWERCASE =
        "The password must contain a lowercase letter";

    /**
     * Message to be displayed when validating a DTO that has a password, and it has an uppercase
     * letter missing.
     */
    /* package */ static final String PASSWORD_MISSING_UPPERCASE
        = "The password must contain an uppercase letter";

    /**
     * Message to be displayed when validating a DTO that has a password, and it has a number
     * missing.
     */
    /* package */ static final String PASSWORD_MISSING_NUMBER =
        "The password must contain a number";

    /**
     * Message to be displayed when validating a DTO that has a password, and it has a special
     * character missing.
     */
    /* package */ static final String PASSWORD_MISSING_SPECIAL =
        "The password must contain a special character";


}
