package io.github.malczuuu.lemur.testkit.container;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Interface to be implemented by tests that require a PostgreSQL container. It provides a
 * pre-configured PostgreSQL container for testing purposes.
 */
public interface PostgresAwareTest {

  /**
   * <strong>Must not be accessed directly by test code.</strong> To be used by test framework only.
   */
  @Container @ServiceConnection
  PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:18.3-alpine");
}
