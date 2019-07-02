package wolox.training.services.authentication;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;

/**
 * Configuration class for the JWT stuff.
 */
@Configuration
public class JwtConfig {

    /**
     * The {@link Logger}.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtConfig.class);


    /**
     * Creates a bean of a {@link KeyFactory}.
     *
     * @return A {@link KeyFactory} bean.
     * @throws NoSuchAlgorithmException Never.
     */
    @Bean
    public KeyFactory keyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(Constants.KEY_FACTORY_ALGORITHM);
    }

    /**
     * Creates a {@link PublicKey} from the given {@code encodedPublicKey}.
     *
     * @param keyFactory The {@link KeyFactory} used to generate the {@link PublicKey}.
     * @param encodedPublicKey The encoded key.
     * @return The {@link PublicKey}.
     */
    @Bean
    public PublicKey publicKey(
        final KeyFactory keyFactory,
        @Value("${authentication.jwt.public-key}") final String encodedPublicKey) {
        return generateKey(
            keyFactory,
            encodedPublicKey,
            X509EncodedKeySpec::new,
            KeyFactory::generatePublic
        );
    }

    /**
     * Creates a {@link PrivateKey} from the given {@code encodedPrivateKey}.
     *
     * @param keyFactory The {@link KeyFactory} used to generate the {@link PrivateKey}.
     * @param encodedPrivateKey The encoded key.
     * @return The {@link PublicKey}.
     */
    @Bean
    public PrivateKey privateKey(
        final KeyFactory keyFactory,
        @Value("${authentication.jwt.private-key}") final String encodedPrivateKey) {
        return generateKey(
            keyFactory,
            encodedPrivateKey,
            PKCS8EncodedKeySpec::new,
            KeyFactory::generatePrivate
        );
    }


    /**
     * Generates a {@link Key} of type {@code K} from the given {@link KeySpec} of type {@code S},
     * using the given {@link KeyFactory}. Generates a {@link Key} of type {@code K} from the given
     * {@code encodedKey}.
     *
     * @param keyFactory The {@link KeyFactory} used to generate the {@link Key}.
     * @param encodedKey The encoded key.
     * @param keySpecGenerator A {@link Function} that given a {@code byte[]}, returns a {@link
     * KeySpec} of type {@code S}. Will be called with the decoded version of the given {@code
     * encodedKey}.
     * @param keyGenerator A {@link KeyGenerator}
     * @param <S> The concrete type of {@link KeySpec}.
     * @param <K> The concrete type of {@link Key}.
     * @return The generated {@link Key}.
     * @throws IllegalStateException If the key is invalid.
     * @implNote Will use {@link Base64Utils#decodeFromString(String)} to decode the given {@code
     * encodedKey}, and its result will be used to call the given {@code keySpecGenerator}.
     */
    private static <S extends KeySpec, K extends Key> K generateKey(
        final KeyFactory keyFactory,
        final String encodedKey,
        final Function<byte[], S> keySpecGenerator,
        KeyGenerator<S, K> keyGenerator) {

        final var decodedKeyString = Base64Utils.decodeFromString(encodedKey);
        final var keySpec = keySpecGenerator.apply(decodedKeyString);

        try {
            return keyGenerator.generateKey(keyFactory, keySpec);
        } catch (final InvalidKeySpecException e) {
            LOGGER.error("The key that was set is not valid!");
            throw new IllegalStateException("Invalid key", e);
        }
    }

    /**
     * Defines behaviour for an object that can generate {@link Key}s from a {@link KeyFactory} and
     * an {@link KeySpec}
     *
     * @param <S> The concrete type of {@link KeySpec}.
     * @param <K> The concrete type of {@link Key}.
     */
    @FunctionalInterface
    interface KeyGenerator<S extends KeySpec, K extends Key> {

        /**
         * Generates a {@link Key} of type {@code K} using the given {@code keyFactory} and {@code
         * keySpec}.
         *
         * @param keyFactory The {@link KeyFactory} used to generate the {@link Key}.
         * @param keySpec The {@link KeySpec} from where the {@link Key} will be created.
         * @return The generated {@link Key}.
         */
        K generateKey(final KeyFactory keyFactory, final S keySpec) throws InvalidKeySpecException;
    }
}
