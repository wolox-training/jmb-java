package wolox.training;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

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

    /**
     * Creates a {@link RestTemplate} bean.
     *
     * @param restTemplateBuilder The {@link RestTemplateBuilder} used to create the {@link
     * RestTemplate}.
     * @return A {@link RestTemplate} bean.
     */
    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
