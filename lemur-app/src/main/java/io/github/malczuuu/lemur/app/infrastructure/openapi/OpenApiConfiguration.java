package io.github.malczuuu.lemur.app.infrastructure.openapi;

import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Contains minor fixtures for OpenAPI documentation generation. */
@Configuration
public class OpenApiConfiguration {

  /**
   * Most likely API should be versioned independent of the application version, but for now let's
   * use the application version as API version.
   *
   * @return {@link OpenApiCustomizer} that sets the API version and title in the generated OpenAPI
   *     documentation
   */
  @Bean
  public OpenApiCustomizer openApiInfoCustomizer() {
    return openApi -> {
      String version = getClass().getPackage().getImplementationVersion();
      openApi.setInfo(new Info().version(version).title("Lemur Project API"));
    };
  }

  /**
   * It was observed, that the order of paths in OpenAPI documentation is not deterministic. This
   * customizer sorts the paths alphabetically to ensure a consistent generated docs order.
   *
   * @return {@link OpenApiCustomizer} that sorts paths in the generated OpenAPI documentation
   */
  @Bean
  public OpenApiCustomizer openApiPathCustomizer() {
    return openApi -> {
      Paths paths = new Paths();

      openApi.getPaths().keySet().stream()
          .sorted()
          .forEach(path -> paths.addPathItem(path, openApi.getPaths().get(path)));

      openApi.setPaths(paths);
    };
  }
}
