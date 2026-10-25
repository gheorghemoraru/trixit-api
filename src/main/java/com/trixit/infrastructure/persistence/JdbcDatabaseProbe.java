package com.trixit.infrastructure.persistence;

import com.trixit.application.DatabaseProbe;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcDatabaseProbe implements DatabaseProbe {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDatabaseProbe(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isReachable() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return result != null && result == 1;
    }
}
