package io.github.malczuuu.lemur.app.adapter.rest.support

import io.github.malczuuu.lemur.app.common.getLogger
import io.github.problem4j.core.Problem
import io.github.problem4j.core.ProblemContext
import io.github.problem4j.spring.webmvc.AdviceWebMvcInspector
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest

@Component
class DiagnosticInspector(private val meterRegistry: MeterRegistry) : AdviceWebMvcInspector {

    companion object {
        private const val REST_ERRORS_METRIC = "lemur.rest.errors"
        private val log = getLogger(DiagnosticInspector::class)
    }

    override fun inspect(
        context: ProblemContext,
        problem: Problem,
        ex: Exception,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ) {
        val info = readInfoFrom(request, problem, ex)

        val tags = listOf(
            Tag.of("error", info.error),
            Tag.of("status", status.value().toString()),
            Tag.of("path", info.path),
            Tag.of("method", info.method),
        )

        meterRegistry.counter(REST_ERRORS_METRIC, tags).increment()

        var builder = log.atError()
        for (tag in tags) {
            builder = builder.addKeyValue(tag.key, tag.value)
        }
        if (log.isDebugEnabled) {
            builder = builder.setCause(ex)
        }
        builder.log("Handled exception in HTTP controller")
    }

    private fun readInfoFrom(request: WebRequest, problem: Problem, ex: Exception): RequestInfo {
        val type = if (problem.isTypeNonBlank) problem.type.toString() else ex.javaClass.simpleName

        var path = "unknown"
        var method = "unknown"

        if (request is ServletWebRequest) {
            path = request.request.requestURI
            method = request.request.method
        }

        return RequestInfo(path, method, type)
    }

    private data class RequestInfo(val path: String, val method: String, val error: String)
}
