package io.github.malczuuu.lemur.app.adapter.rest.support

import io.github.problem4j.core.Problem
import io.github.problem4j.core.ProblemBuilder
import io.github.problem4j.core.ProblemContext
import io.github.problem4j.spring.web.ProblemFormat
import io.github.problem4j.spring.web.ProblemSupport
import io.github.problem4j.spring.web.TypeNameMapper
import io.github.problem4j.spring.web.resolver.HttpMessageNotReadableProblemResolver
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import tools.jackson.module.kotlin.KotlinInvalidNullException
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

/**
 * Most likely won't be needed in the future.
 */
@Component
class KotlinHttpMessageNotReadableResolver(problemFormat: ProblemFormat, private val typeNameMapper: TypeNameMapper) :
    HttpMessageNotReadableProblemResolver(problemFormat, typeNameMapper) {

    override fun resolveBuilder(
        context: ProblemContext,
        ex: Exception,
        headers: HttpHeaders,
        status: HttpStatusCode,
    ): ProblemBuilder {
        if (ex.cause is KotlinInvalidNullException) {
            return resolveKotlinInvalidNullCause(ex.cause as KotlinInvalidNullException)
        }
        return super.resolveBuilder(context, ex, headers, status)
    }

    private fun resolveKotlinInvalidNullCause(ex: KotlinInvalidNullException): ProblemBuilder {
        val builder = Problem.builder().status(HttpStatus.BAD_REQUEST.value())
        resolvePropertyPath(ex)?.let {
            builder.detail(ProblemSupport.TYPE_MISMATCH_DETAIL)
            builder.extension(ProblemSupport.PROPERTY_EXTENSION, it)

            findPropertyType(ex)?.let { kindType ->
                typeNameMapper.map(kindType.javaType as Class<*>).ifPresent { kind ->
                    builder.extension(ProblemSupport.KIND_EXTENSION, kind)
                }
            }
        }

        return builder
    }

    private fun resolvePropertyPath(e: KotlinInvalidNullException): String? {
        val property = e.getPath()
            .map { it.propertyName }
            .filter { StringUtils.hasLength(it) }
            .joinToString(separator = ".")

        return if (StringUtils.hasLength(property)) property else null
    }

    private fun findPropertyType(ex: KotlinInvalidNullException): KType? {
        val type: KProperty<*>? = ex.targetType.kotlin.memberProperties.find { it.name == ex.kotlinPropertyName }
        return type?.returnType
    }
}
