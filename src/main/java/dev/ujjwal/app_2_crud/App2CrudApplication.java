package dev.ujjwal.app_2_crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class App2CrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(App2CrudApplication.class, args);
    }

}
