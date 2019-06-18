package wolox.training.models;

import static wolox.training.models.ModelsTestHelper.BookField.AUTHOR;
import static wolox.training.models.ModelsTestHelper.BookField.GENRE;
import static wolox.training.models.ModelsTestHelper.BookField.IMAGE;
import static wolox.training.models.ModelsTestHelper.BookField.ISBN;
import static wolox.training.models.ModelsTestHelper.BookField.PAGES;
import static wolox.training.models.ModelsTestHelper.BookField.PUBLISHER;
import static wolox.training.models.ModelsTestHelper.BookField.SUBTITLE;
import static wolox.training.models.ModelsTestHelper.BookField.TITLE;
import static wolox.training.models.ModelsTestHelper.BookField.YEAR;
import static wolox.training.models.ModelsTestHelper.UserField.BIRTH_DATE;
import static wolox.training.models.ModelsTestHelper.UserField.NAME;
import static wolox.training.models.ModelsTestHelper.UserField.USERNAME;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;
import wolox.training.utils.ValuesGenerator;

/**
 * Helper class for models testing.
 */
/* package */ class ModelsTestHelper {

    /**
     * Private constructor to avoid instantiation.
     */
    private ModelsTestHelper() {
    }

    /**
     * Builds a {@link Map} of {@link BookField} and {@link Object} containing valid values to be
     * used when creating a {@link Book}.
     *
     * @return A {@link Map} of {@link BookField} and {@link Object} containing valid values to be
     * used when creating a {@link Book}.
     */
    /* package */
    static Map<BookField, Object> buildBookMap() {
        final Map<BookField, Object> map = new HashMap<>();
        map.put(GENRE, ValuesGenerator.validBookGenre());
        map.put(AUTHOR, ValuesGenerator.validBookAuthor());
        map.put(IMAGE, ValuesGenerator.validBookImage());
        map.put(TITLE, ValuesGenerator.validBookTitle());
        map.put(SUBTITLE, ValuesGenerator.validBookSubtitle());
        map.put(PUBLISHER, ValuesGenerator.validBookPublisher());
        map.put(YEAR, ValuesGenerator.validBookYear());
        map.put(PAGES, ValuesGenerator.validBookPages());
        map.put(ISBN, ValuesGenerator.validBookIsbn());
        return map;
    }

    /**
     * Builds a {@link Map} of {@link UserField} and {@link Object} containing valid values to be
     * used when creating a {@link User}.
     *
     * @return A {@link Map} of {@link UserField} and {@link Object} containing valid values to be
     * used when creating a {@link User}.
     */
    /* package */
    static Map<UserField, Object> buildUserMap() {
        final Map<UserField, Object> map = new HashMap<>();
        map.put(USERNAME, ValuesGenerator.validUserUsername());
        map.put(NAME, ValuesGenerator.validUserName());
        map.put(BIRTH_DATE, ValuesGenerator.validUserBirthDate());
        return map;
    }


    /**
     * Builds a {@link Book} using the values in the given {@code map} of {@link BookField} and
     * {@link Object}, applying the corresponding casts.
     *
     * @param map The {@link Map} with the values to be used to create the {@link Book}.
     * @return The created {@link Book}.
     */
    /* package */
    static Book buildBookFromMap(final Map<BookField, Object> map) {
        Assert.isTrue(map.containsKey(GENRE), "The genre is not present");
        Assert.isTrue(map.containsKey(AUTHOR), "The author is not present");
        Assert.isTrue(map.containsKey(IMAGE), "The image is not present");
        Assert.isTrue(map.containsKey(TITLE), "The title is not present");
        Assert.isTrue(map.containsKey(SUBTITLE), "The subtitle is not present");
        Assert.isTrue(map.containsKey(PUBLISHER), "The publisher is not present");
        Assert.isTrue(map.containsKey(YEAR), "The year is not present");
        Assert.isTrue(map.containsKey(PAGES), "The pages is not present");
        Assert.isTrue(map.containsKey(ISBN), "The isbn is not present");

        return new Book(
            (String) map.get(GENRE),
            (String) map.get(AUTHOR),
            (String) map.get(IMAGE),
            (String) map.get(TITLE),
            (String) map.get(SUBTITLE),
            (String) map.get(PUBLISHER),
            (String) map.get(YEAR),
            (Integer) map.get(PAGES),
            (String) map.get(ISBN)
        );
    }

    /**
     * Builds a {@link User} using the values in the given {@code map} of {@link UserField} and
     * {@link Object}, applying the corresponding casts.
     *
     * @param map The {@link Map} with the values to be used to create the {@link User}.
     * @return The created {@link User}.
     */
    /* package */
    static User buildUserFromMap(final Map<UserField, Object> map) {
        Assert.isTrue(map.containsKey(USERNAME), "The username is not present");
        Assert.isTrue(map.containsKey(NAME), "The name is not present");
        Assert.isTrue(map.containsKey(BIRTH_DATE), "The date is not present");

        return new User(
            (String) map.get(USERNAME),
            (String) map.get(NAME),
            (LocalDate) map.get(BIRTH_DATE)
        );
    }


    /**
     * An enum containing the {@link Book} fields.
     */
    /* package */  enum BookField {
        /**
         * The genre field.
         */
        GENRE,
        /**
         * The author field.
         */
        AUTHOR,
        /**
         * The image field.
         */
        IMAGE,
        /**
         * The title field.
         */
        TITLE,
        /**
         * The subtitle field.
         */
        SUBTITLE,
        /**
         * The publisher field.
         */
        PUBLISHER,
        /**
         * The year field.
         */
        YEAR,
        /**
         * The pages field.
         */
        PAGES,
        /**
         * The isbn field.
         */
        ISBN,
        ;
    }

    /**
     * An enum containing the {@link Book} fields.
     */
    /* package */  enum UserField {
        /**
         * The username field.
         */
        USERNAME,
        /**
         * The name field.
         */
        NAME,
        /**
         * The birth date field.
         */
        BIRTH_DATE,
        ;
    }
}
