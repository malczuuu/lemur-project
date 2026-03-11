package io.github.malczuuu.lemur.app.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.github.malczuuu.lemur.app.arch.ArchUtils.getAppClasses;

import org.junit.jupiter.api.Test;

/**
 * Tests to verify compliance with clean/onion architecture rules, but only on relationship between
 * packages.
 */
class CleanComplianceArchTests {

  @Test
  void corePackage_mustNotDependOnAdapterPackage() {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.core..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.adapter..")
        .check(getAppClasses());
  }

  @Test
  void corePackage_mustNotDependOnContractPackage() {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.core..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.contract..")
        .check(getAppClasses());
  }

  @Test
  void corePackage_mustNotDependOnInfraPackage() {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.core..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.infra..")
        .check(getAppClasses());
  }

  @Test
  void domainPackage_mustNotDependOnSpring() {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("org.springframework..")
        .check(getAppClasses());
  }

  @Test
  void domainPackage_mustNotDependOnAdapterPackage() {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.adapter..")
        .check(getAppClasses());
  }

  @Test
  void domainPackage_mustNotDependOnCorePackage() {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.core..")
        .check(getAppClasses());
  }

  @Test
  void domainPackage_mustNotDependOnInfraPackage() {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.infra..")
        .check(getAppClasses());
  }
}
