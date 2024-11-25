package dev.ujjwal.app_2_crud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usersApi() {
        return GroupedOpenApi.builder()
                .group("employee")
                .pathsToMatch("/api/v1/employee/**")
                .build();
    }

    @Bean
    public OpenAPI defineOpenApi() {
        Contact myContact = new Contact();
        myContact.setName("Ujjwal Maity");
        myContact.setEmail("ujjwalmaity98@gmail.com");
        myContact.setUrl("https://www.linkedin.com/in/ujjwalmaity");
        Info information = new Info()
                .title("CRUD")
                .version("v1")
                .description("API Documentation")
                .contact(myContact);

        return new OpenAPI()
                .info(information);
    }

}
