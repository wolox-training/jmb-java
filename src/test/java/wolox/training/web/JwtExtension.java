package wolox.training.web;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static org.springframework.core.annotation.AnnotatedElementUtils.hasAnnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.function.Consumer;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.util.AnnotationUtils;
import wolox.training.models.User;
import wolox.training.repositories.UserRepository;
import wolox.training.services.authentication.JwtTokenService;
import wolox.training.utils.ValuesGenerator;

/**
 * An {@link Extension} that allows testing the web layer with JWT support.
 */
class JwtExtension implements ParameterResolver, BeforeTestExecutionCallback {

    /**
     * The {@link JwtTokenService} used to generate JWT tokens to be injected in methods annotated
     * with the {@link AuthenticatedWithJwt} annotation, and that have parameters annotated with the
     * {@link ValidJwt} annotation.
     */
    private final JwtTokenService jwtTokenService;
    /**
     * A {@link Consumer} that takes a {@link User} and registers it (i.e can be anything that makes
     * the {@link UserRepository} used by the {@link #jwtTokenService} return an {@link Optional}
     * with the {@link User} with which this {@link Consumer} will be called when {@link
     * UserRepository#getFirstByUsername(String)} is called with the {@link User}'s username.
     */
    private final Consumer<User> registerConsumer;

    /**
     * Constructor.
     *
     * @param jwtTokenService The {@link JwtTokenService} used to generate JWT tokens to be injected
     * in methods annotated with the {@link AuthenticatedWithJwt} annotation, and that have
     * parameters annotated with the {@link ValidJwt} annotation.
     * @param registerConsumer A {@link Consumer} that takes a {@link User} and registers it (i.e
     * can be anything that makes the {@link UserRepository} used by the {@code jwtTokenService}
     * return an {@link Optional} with the {@link User} with which this {@link Consumer} will be
     * called when {@link UserRepository#getFirstByUsername(String)} is called with the {@link
     * User}'s username.
     */
    JwtExtension(final JwtTokenService jwtTokenService, final Consumer<User> registerConsumer) {
        this.jwtTokenService = jwtTokenService;
        this.registerConsumer = registerConsumer;
    }


    @Override
    public boolean supportsParameter(
        final ParameterContext parameterContext,
        final ExtensionContext extensionContext) throws ParameterResolutionException {

        final var param = parameterContext.getParameter();
        return param.getType().isAssignableFrom(String.class)
            && hasAnnotation(param, ValidJwt.class)
            && hasAuthenticatedWithJwt(extensionContext);
    }

    @Override
    public Object resolveParameter(
        final ParameterContext parameterContext,
        final ExtensionContext extensionContext) throws ParameterResolutionException {

        final var authenticated = parameterContext.getDeclaringExecutable()
            .getAnnotation(AuthenticatedWithJwt.class);
        return createValidToken(authenticated.username(), authenticated.password());
    }

    @Override
    public void beforeTestExecution(final ExtensionContext context) {
        getAuthenticatedWithJwtInAllHierarchy(context)
            .ifPresent(auth -> jwtConfig(auth.username(), auth.password()));
    }


    /**
     * Indicates whether the given {@code extensionContext} contains the {@link
     * AuthenticatedWithJwt} annotation.
     *
     * @param extensionContext The {@link ExtensionContext} to be analyzed.
     * @return {@code true} if the given {@code extensionContext} contains the {@link
     * AuthenticatedWithJwt} annotation, or {@code false} otherwise.
     */
    private static boolean hasAuthenticatedWithJwt(final ExtensionContext extensionContext) {
        return getAuthenticatedWithJwtInAllHierarchy(extensionContext).isPresent();
    }

    /**
     * Searches for the {@link AuthenticatedWithJwt} annotation in the given {@code
     * extensionContext}.
     *
     * @param extensionContext The {@link ExtensionContext} to be analyzed.
     * @return An {@link Optional} containing the nearest {@link AuthenticatedWithJwt} annotation in
     * the given {@code extensionContext} hierarchy, if it exists, or empty otherwise.
     */
    private static Optional<AuthenticatedWithJwt> getAuthenticatedWithJwtInAllHierarchy(
        final ExtensionContext extensionContext) {

        var actual = Optional.of(extensionContext);
        while (actual.isPresent()) {
            final var authenticatedWithJwtOptional = actual.get().getElement()
                .flatMap(elem -> AnnotationUtils.findAnnotation(elem, AuthenticatedWithJwt.class));
            if (authenticatedWithJwtOptional.isPresent()) {
                return authenticatedWithJwtOptional;
            }
            actual = actual.flatMap(ExtensionContext::getParent);
        }
        return Optional.empty();
    }

    /**
     * Configures the JWT system to accept a {@link User} with the given {@code username} and {@code
     * password}.
     */
    private void jwtConfig(final String username, final String password) {
        final var user = new User(
            username,
            ValuesGenerator.validUserName(),
            ValuesGenerator.validUserBirthDate()
        );
        user.changePassword(password);
        registerConsumer.accept(user);
    }


    /**
     * Creates a valid JWT for a {@link User} with the given {@code username} and {@code password}.
     *
     * @param username The username of the {@link User} that will own the token.
     * @param password The password of the {@link User} that will own the token.
     * @return The created token.
     */
    private String createValidToken(final String username, final String password) {
        return jwtTokenService.issueToken(username, password).getRawToken();
    }


    /**
     * When used in a method, test class, or an annotation placed in a test class (including all the
     * {@link ExtensionContext} hierarchy), the {@link JwtExtension} will configure the {@link
     * JwtTokenService} to allow authentication for a {@link User} with the given {@code username}
     * and {@code password}.
     */
    @Documented
    @Target({METHOD, TYPE, ANNOTATION_TYPE,})
    @Retention(RetentionPolicy.RUNTIME)
    @interface AuthenticatedWithJwt {

        String username() default "username";

        String password() default "P4s5w0rd!";
    }

    /**
     * When placed in a parameter of a test method with the {@link AuthenticatedWithJwt}, or if the
     * method belongs to a test class with the said annotation (including all the {@link
     * ExtensionContext} hierarchy), the {@link JwtExtension} will inject a valid JWT created by the
     * {@link JwtTokenService} into the test method.
     */
    @Documented
    @Target({PARAMETER,})
    @Retention(RetentionPolicy.RUNTIME)
    @interface ValidJwt {

    }

}
