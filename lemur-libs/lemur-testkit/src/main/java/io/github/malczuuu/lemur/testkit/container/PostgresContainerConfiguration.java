package io.github.malczuuu.lemur.testkit.container;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Spring {@link TestConfiguration} for managing a PostgreSQL Testcontainer for integration testing.
 *
 * <p>Provides a bean for the PostgreSQL container lifecycle.
 */
@TestConfiguration(proxyBeanMethods = false)
public class PostgresContainerConfiguration {

  @Bean
  @ServiceConnection
  PostgreSQLContainer postgresContainer() {
    return new PostgreSQLContainer(DockerImageName.parse("postgres:18.3-alpine"));
  }
}
