package io.github.malczuuu.lemur.testkit.annotation;

import static io.github.malczuuu.lemur.testkit.TestkitTags.TESTCONTAINERS;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/**
 * Meta-annotation for test fields that require Testcontainers infrastructure. Used for filtering
 * out integration tests for a quick build.
 */
@Tag(TESTCONTAINERS)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ContainerTest {}
