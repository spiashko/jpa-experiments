package com.spiashko.jpademo;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class AbstractApplicationTest {

    public static JdbcDatabaseContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("tests-db")
            .withUsername("sa")
            .withPassword("sa")
            .withInitScript("postgre-init.sql")
            .withEnv("TZ", "UTC")
            .withEnv("PGTZ", "UTC")
            .withReuse(true);

    static {
        dbContainer.start();
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", dbContainer::getJdbcUrl);
        registry.add("spring.datasource.username", dbContainer::getUsername);
        registry.add("spring.datasource.password", dbContainer::getPassword);
    }

}
