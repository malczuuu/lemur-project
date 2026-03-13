package io.github.malczuuu.lemur.app.infra.rest

import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/** Contains minor fixtures for OpenAPI documentation generation.  */
@Configuration
class OpenApiConfiguration {

    /**
     * Most likely API should be versioned independent of the application version, but for now let's
     * use the application version as API version.
     *
     * @return [OpenApiCustomizer] that sets the API version and title in the generated OpenAPI
     * documentation
     */
    @Bean
    fun openApiInfoCustomizer(): OpenApiCustomizer = {
        val version = javaClass.getPackage().implementationVersion
        it.info = Info().version(version).title("Lemur Project API")
    }

    /**
     * It was observed, that the order of paths in OpenAPI documentation is not deterministic. This
     * customizer sorts the paths alphabetically to ensure a consistent generated docs order.
     *
     * @return [OpenApiCustomizer] that sorts paths in the generated OpenAPI documentation
     */
    @Bean
    fun openApiPathCustomizer(): OpenApiCustomizer = {
        val paths = Paths()
        it.paths.keys.stream()
            .sorted()
            .forEach { path -> paths.addPathItem(path, it.paths[path]) }
        it.paths = paths
    }
}
