package wolox.training.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import wolox.training.services.authentication.JwtTokenService;

/**
 * Web security configuration class.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * The {@link JwtTokenService} to be passed to the {@link JwtAuthenticationProvider} bean
     * created by this configuration class.
     */
    private final JwtTokenService jwtTokenService;

    /**
     * The "authentication.jwt.allow_anonymous" flag to be passed to the {@link
     * JwtAuthenticationProvider} bean created by this configuration class.
     */
    private final boolean allowAnonymous;


    /**
     * Constructor.
     *
     * @param jwtTokenService The {@link JwtTokenService} to be passed to the {@link
     * JwtAuthenticationProvider} bean created by the configuration class.
     * @param allowAnonymous The "authentication.jwt.allow_anonymous" flag to be passed to the
     * {@link JwtAuthenticationProvider} bean created by this configuration class.
     */
    @Autowired
    public WebSecurityConfig(
        final JwtTokenService jwtTokenService,
        @Value("${authentication.jwt.allow_anonymous}") final boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
        this.jwtTokenService = jwtTokenService;
    }


    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .rememberMe().disable()
            .logout().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "/api/tokens").permitAll()
            .mvcMatchers(HttpMethod.POST, "/api/users").permitAll()
            .mvcMatchers(HttpMethod.PUT, "/api/users/{userId:\\d+}/password").permitAll()
            .mvcMatchers(HttpMethod.POST, "/api/books/").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint())
        ;
    }


    /**
     * Creates a bean of a {@link JwtAuthenticationFilter}, setting {@link #authenticationManager()}
     * as the {@link org.springframework.security.authentication.AuthenticationManager} used by the
     * {@link JwtAuthenticationFilter} to be returned.
     *
     * @return The created {@link JwtAuthenticationFilter} bean.
     * @throws Exception If {@link #authenticationManager()} throws it.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        final var filter = new JwtAuthenticationFilter();
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

    /**
     * Creates a bean of a {@link JwtAuthenticationProvider}.
     *
     * @return The created {@link JwtAuthenticationProvider} bean.
     */
    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtTokenService, allowAnonymous);
    }

    /**
     * Creates a bean of a {@link RestAuthenticationEntryPoint}.
     *
     * @return The created {@link RestAuthenticationEntryPoint} bean.
     */
    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }
}
