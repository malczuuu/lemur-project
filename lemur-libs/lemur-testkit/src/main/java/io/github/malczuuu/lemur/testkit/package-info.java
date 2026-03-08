/**
 * Provides shared test infrastructure for integration testing in the Lemur project.
 *
 * <p>This package contains utility classes and tags for configuring and running tests with
 * Testcontainers, Kafka, and PostgreSQL. It is intended to be used by test modules and testkit
 * consumers to enable consistent, isolated, and repeatable integration tests.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Testcontainers integration for Kafka and PostgreSQL
 *   <li>Custom annotations for test configuration
 *   <li>Kafka test utilities and consumer injection
 *   <li>Standardized test tags for filtering and grouping
 * </ul>
 */
@NullMarked
package io.github.malczuuu.lemur.testkit;

import org.jspecify.annotations.NullMarked;
