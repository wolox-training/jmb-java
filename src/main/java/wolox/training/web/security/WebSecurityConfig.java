package wolox.training.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Web security configuration class.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * The {@link JwtAuthenticationProvider} used to process {@link PreAuthenticatedJwtToken}s.
     */
    private final JwtAuthenticationProvider jwtAuthenticationProvider;


    /**
     * Constructor.
     *
     * @param jwtAuthenticationProvider The {@link JwtAuthenticationProvider} used to process {@link
     * PreAuthenticatedJwtToken}s.
     */
    @Autowired
    public WebSecurityConfig(final JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }


    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider);
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
//            .and()
//            .authorizeRequests().anyRequest().anonymous()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
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
}
