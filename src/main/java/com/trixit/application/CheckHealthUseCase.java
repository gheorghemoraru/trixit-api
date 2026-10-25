package com.trixit.application;

import com.trixit.domain.HealthStatus;

public class CheckHealthUseCase {

    private final DatabaseProbe databaseProbe;

    public CheckHealthUseCase(DatabaseProbe databaseProbe) {
        this.databaseProbe = databaseProbe;
    }

    public HealthStatus execute() {
        try {
            return databaseProbe.isReachable() ? HealthStatus.UP : HealthStatus.DOWN;
        } catch (RuntimeException e) {
            return HealthStatus.DOWN;
        }
    }
}
