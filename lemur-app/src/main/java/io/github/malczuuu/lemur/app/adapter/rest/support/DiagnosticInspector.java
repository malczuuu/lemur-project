package io.github.malczuuu.lemur.app.adapter.rest.support;

import io.github.problem4j.core.Problem;
import io.github.problem4j.core.ProblemContext;
import io.github.problem4j.spring.webmvc.AdviceWebMvcInspector;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Component
public class DiagnosticInspector implements AdviceWebMvcInspector {

  private static final Logger log = LoggerFactory.getLogger(DiagnosticInspector.class);

  private static final String REST_ERRORS_METRIC = "lemur.rest.errors";

  private final MeterRegistry meterRegistry;

  public DiagnosticInspector(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @Override
  public void inspect(
      ProblemContext context,
      Problem problem,
      Exception ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    List<Tag> tags = new ArrayList<>();
    tags.add(Tag.of("error", ex.getClass().getSimpleName()));
    tags.add(Tag.of("status", String.valueOf(status.value())));
    if (request instanceof ServletWebRequest r) {
      tags.add(Tag.of("path", r.getRequest().getRequestURI()));
      tags.add(Tag.of("method", r.getRequest().getMethod()));
    }
    if (problem.isTypeNonBlank()) {
      tags.add(Tag.of("type", problem.getType().toString()));
    }

    meterRegistry.counter(REST_ERRORS_METRIC, tags).increment();

    LoggingEventBuilder builder = log.atError();
    for (Tag tag : tags) {
      builder = builder.addKeyValue(tag.getKey(), tag.getValue());
    }
    if (log.isDebugEnabled()) {
      builder = builder.setCause(ex);
    }

    builder.log("Handled exception in HTTP controller");
  }
}
