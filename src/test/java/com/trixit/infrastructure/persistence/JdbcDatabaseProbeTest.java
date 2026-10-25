package com.trixit.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@Import(JdbcDatabaseProbe.class)
class JdbcDatabaseProbeTest {

    @Autowired
    JdbcDatabaseProbe probe;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void reachesTheInMemoryDatabase() {
        assertThat(probe.isReachable()).isTrue();
    }

    @Test
    void flywayAppliedTheInitMigration() {
        Integer applied = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"flyway_schema_history\" WHERE \"version\" = '1' AND \"success\" = TRUE",
                Integer.class);

        assertThat(applied).isEqualTo(1);
    }
}
