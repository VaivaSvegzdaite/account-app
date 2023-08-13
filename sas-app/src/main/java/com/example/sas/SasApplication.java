package com.example.sas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(info=@Info(title="Simple Banking System"))
@SpringBootApplication
@EnableScheduling
public class SasApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SasApplication.class, args);
    }

}
