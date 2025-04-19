package com.easygame.api.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Easygame API")
                .version("1.0")
                .description("""
                    Welcome to the Easygame API documentation.  
                    You can explore and test available endpoints here.
                
                    * To access secured APIs:
                    1. Get an authentication token from Step 1 - User API.
                    2. Click the 'Authorize' button at the top-right corner and paste the token.
                    3. Public query APIs do not require authentication.
                    """
                ))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components().addSecuritySchemes("JWT",
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                ));
    }

    @Bean
    public GroupedOpenApi api() {
        String[] paths = {"/api/v1/**"};
        String[] packagesToScan = {"com.easygame"};
        return GroupedOpenApi.builder().group("easygame-openapi")
                .pathsToMatch(paths)
                .packagesToScan(packagesToScan)
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.addSecurityItem(new SecurityRequirement().addList("JWT"));
                    return operation;
                })
                .build();
    }


}
