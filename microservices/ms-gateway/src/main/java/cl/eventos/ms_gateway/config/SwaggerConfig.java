package cl.eventos.ms_gateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi checkinApi() {
        return GroupedOpenApi.builder()
                .group("checkin")
                .pathsToMatch("/api/checkin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("autenticacion")
                .pathsToMatch("/api/auth/**")
                .build();
    }
}
