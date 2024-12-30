package com.tokioschool.liquibase;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles("test")
public class LiquibaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SpringLiquibase liquibase;


    @Test
    void givenConfigLiquibase_whenDatabaseChangeLogTable_thenOk() {
        // Verifica que la tabla DATABASECHANGELOG existe
        Integer changelogCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM DATABASECHANGELOG",
                Integer.class
        );

        assertThat(changelogCount).isGreaterThan(0);  // Ensure DATABASECHANGELOG table exists
    }

    @Test
    void givenConfigLiquibase_whenLiquibaseAppliedChanges_thenOk() throws LiquibaseException {

        // Verifica que una tabla específica fue creada
        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'example_table'",
                Integer.class
        );
        assertThat(tableCount).isEqualTo(1);
    }

}
