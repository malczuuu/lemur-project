package io.github.malczuuu.lemur.testkit.archunit;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.lang.ArchRule;

public final class NamingRules {

  public static final ArchRule TEST_CLASSES_PLURAL =
      classes()
          .that()
          .containAnyMethodsThat(
              describe(
                  "are annotated with @Test or @ArchTest",
                  m ->
                      m.isAnnotatedWith(org.junit.jupiter.api.Test.class)
                          || m.isAnnotatedWith(com.tngtech.archunit.junit.ArchTest.class)))
          .should()
          .haveSimpleNameEndingWith("Tests");

  private NamingRules() {}
}
