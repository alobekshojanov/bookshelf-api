package com.alobek.bookshelf.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.util.List;

@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {

        Info info = new Info()
                .title("BookShelf API")
                .version("1.0")
                .description("Belows are  BookShelf's API-s ")
                .contact(new Contact()
                        .name("Alobek")
                        .email("alobekshojanov@gmail.com")
                        .url("https://alobekshojanov.com")
                )
                .license(new License()
                        .name("bookshelf.uz")
                        .url("https:/bookshelf.uz/")
                )
                .termsOfService("https://alobekshojanov.com");

        // serves ....
        Server server1 = new Server()
                .description("Local")
                .url("http://localhost:8080");
        Server server2 = new Server()
                .description("DEV")
                .url("http://book-shelf.uz");
        Server server3 = new Server()
                .description("PROD")
                .url("http://localhost:8080");

        // security type (biznig holatda JWT)
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("bearerAuth");

        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.setName("bearerAuth");
        securityScheme.setType(SecurityScheme.Type.HTTP);
        securityScheme.bearerFormat("JWT");
        securityScheme.setIn(SecurityScheme.In.HEADER);
        securityScheme.setScheme("bearer");

        Components components = new Components();
        components.addSecuritySchemes("bearerAuth", securityScheme);

        // collect all together
        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);
        openAPI.setServers(List.of(server1, server2, server3));
        openAPI.setSecurity(List.of(securityRequirement));
        openAPI.components(components);

        // return-xe
        return openAPI;
    }

}
