package io.github.malczuuu.lemur.app.adapter.rest.support

import io.github.malczuuu.lemur.app.domain.error.DomainException
import io.github.malczuuu.lemur.app.domain.error.ErrorCategory
import io.github.problem4j.core.Problem
import io.github.problem4j.core.ProblemBuilder
import io.github.problem4j.core.ProblemContext
import io.github.problem4j.spring.web.resolver.AbstractProblemResolver
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component

@Component
class DomainExceptionResolver : AbstractProblemResolver(DomainException::class.java) {

    companion object {
        private val CATEGORY_TO_STATUS = mapOf(
            ErrorCategory.MALFORMED_PAYLOAD to HttpStatus.BAD_REQUEST,
            ErrorCategory.NOT_FOUND to HttpStatus.NOT_FOUND,
            ErrorCategory.CONFLICT to HttpStatus.CONFLICT,
            ErrorCategory.INTERNAL_ERROR to HttpStatus.INTERNAL_SERVER_ERROR,
        )
    }

    override fun resolveBuilder(
        context: ProblemContext,
        ex: Exception,
        headers: HttpHeaders,
        status: HttpStatusCode,
    ): ProblemBuilder {
        val e = ex as DomainException

        val httpStatus = CATEGORY_TO_STATUS.getOrDefault(
            e.error.category,
            HttpStatus.INTERNAL_SERVER_ERROR,
        )

        return Problem.builder()
            .type(e.error.type)
            .title(httpStatus.reasonPhrase)
            .status(httpStatus.value())
            .detail(e.message)
    }
}
