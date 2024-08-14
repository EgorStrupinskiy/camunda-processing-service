package com.azati.warshipprocessing.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Sea War API",
                description = "Processing service for sea wars", version = "1.0.0",
                contact = @Contact(
                        name = "Strupinski Yahor"
                )
        )
)
public class OpenApiConfig {
    
}