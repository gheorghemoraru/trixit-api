package com.trixit.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.trixit.domain.HealthStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckHealthUseCaseTest {

    @Mock
    DatabaseProbe databaseProbe;

    @InjectMocks
    CheckHealthUseCase useCase;

    @Test
    void isUpWhenDatabaseIsReachable() {
        when(databaseProbe.isReachable()).thenReturn(true);

        assertThat(useCase.execute()).isEqualTo(HealthStatus.UP);
    }

    @Test
    void isDownWhenDatabaseIsNotReachable() {
        when(databaseProbe.isReachable()).thenReturn(false);

        assertThat(useCase.execute()).isEqualTo(HealthStatus.DOWN);
    }

    @Test
    void isDownWhenProbeFails() {
        when(databaseProbe.isReachable()).thenThrow(new IllegalStateException("boom"));

        assertThat(useCase.execute()).isEqualTo(HealthStatus.DOWN);
    }
}
