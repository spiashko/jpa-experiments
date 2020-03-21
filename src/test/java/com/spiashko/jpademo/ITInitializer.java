package com.spiashko.jpademo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@Slf4j
public class ITInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:9.6")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

        postgreSQLContainer.isShouldBeReused();

        postgreSQLContainer.start();

        log.info("jdbcUrl=" + postgreSQLContainer.getJdbcUrl());
        log.info("username=" + postgreSQLContainer.getUsername());
        log.info("password=" + postgreSQLContainer.getPassword());

        TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
