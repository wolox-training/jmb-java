package wolox.training;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main configuration class.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = {
        "wolox.training.repositories"
    }
)
public class AppConfig {

}
