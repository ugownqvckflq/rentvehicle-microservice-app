package com.proj.api_gateway.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0")
                        .description("API Documentation for the Auth Service"))
                .components(new Components()
                        .addParameters("X-User-Role", new Parameter()
                                .in(ParameterIn.HEADER.toString())
                                .name("X-User-Role")
                                .description("Role of the user")
                                .required(false)
                                .schema(new io.swagger.v3.oas.models.media.StringSchema())));
    }
}
