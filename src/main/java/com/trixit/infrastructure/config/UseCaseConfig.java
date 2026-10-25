package com.trixit.infrastructure.config;

import com.trixit.application.CheckHealthUseCase;
import com.trixit.application.DatabaseProbe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires framework-free use cases, keeping the application layer independent of Spring. */
@Configuration
public class UseCaseConfig {

    @Bean
    CheckHealthUseCase checkHealthUseCase(DatabaseProbe databaseProbe) {
        return new CheckHealthUseCase(databaseProbe);
    }
}
