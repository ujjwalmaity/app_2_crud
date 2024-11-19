package dev.ujjwal.app_2_crud.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    ModelMapper getModelMapper() {
        return new ModelMapper();
    }

}
