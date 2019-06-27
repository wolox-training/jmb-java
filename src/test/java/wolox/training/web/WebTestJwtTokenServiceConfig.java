package wolox.training.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to be loaded by test classes, allowing component scanning of the JWT
 * components.
 */
@Configuration
@ComponentScan(
    basePackages = {
        "wolox.training.services.authentication"
    }
)
public class WebTestJwtTokenServiceConfig {

}
