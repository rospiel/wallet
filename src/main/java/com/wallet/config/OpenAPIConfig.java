package com.wallet.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenAPIConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("API WALLET")
                .description("API MANAGER WALLET")
                .version("v1");

        return new OpenAPI().info(info)
                .tags(createTags());
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getPaths()
                    .values()
                    .stream()
                    .flatMap(pathItem -> pathItem.readOperations().stream())
                    .forEach(operation -> {
                        ApiResponses responses = operation.getResponses();
                        responses.addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                                createApiResponse("Internal server error"));
                    });
        };
    }

    private List<Tag> createTags() {
        return Arrays.asList(
                new Tag().name("Wallets").description("Manager wallets")
        );

    }

    private ApiResponse createApiResponse(String description) {
        return new ApiResponse().description(description);
    }
}
