package com.example.bankcards.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Swagger config.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Bank cards api open api.
     *
     * @return the open api
     */
    @Bean
    public OpenAPI bankCardsApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Cards REST API")
                        .description("API for bank card management")
                        .version("1.0.0"));
    }
}