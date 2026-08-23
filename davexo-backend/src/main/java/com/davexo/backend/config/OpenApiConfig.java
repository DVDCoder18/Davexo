package com.davexo.backend.config;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI davexoOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Davexo API")
                        .version("1.0.0")
                        .description("REST API documentation for Davexo"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
    
    @Bean
public OpenApiCustomizer nullableSchemaCustomizer() {

    return openApi -> {

        Schema<?> dashboardSchema = openApi
                .getComponents()
                .getSchemas()
                .get("DashboardResponseDto");

        if (dashboardSchema == null
                || dashboardSchema.getProperties() == null) {
            return;
        }

        Schema<?> budgetReferenceSchema = new Schema<>()
                .$ref("#/components/schemas/BudgetConsumptionResponseDto");

        Schema<?> nullSchema = new Schema<>();
        nullSchema.addType("null");

        ComposedSchema nullableBudgetSchema = new ComposedSchema();
        nullableBudgetSchema.setOneOf(
                List.of(
                        budgetReferenceSchema,
                        nullSchema));

        nullableBudgetSchema.setDescription(
                "Current consumption of the user's global budget, or null if no global budget exists");

        dashboardSchema
                .getProperties()
                .put(
                        "globalBudgetConsumption",
                        nullableBudgetSchema);
                };
        }
}