package arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

/**
 * Tests to verify compliance with clean/onion architecture rules, but only on relationship between
 * packages.
 */
@AnalyzeClasses(packages = {"io.github.malczuuu.lemur.app"})
class CleanComplianceArchTests {

  @ArchTest
  void corePackage_mustNotDependOnAdapterPackage(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.core..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.adapter..")
        .check(classes);
  }

  @ArchTest
  void corePackage_mustNotDependOnContractPackage(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.core..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.contract..")
        .check(classes);
  }

  @ArchTest
  void corePackage_mustNotDependOnInfraPackage(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.core..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.infra..")
        .check(classes);
  }

  @ArchTest
  void domainPackage_mustNotDependOnSpring(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("org.springframework..")
        .check(classes);
  }

  @ArchTest
  void domainPackage_mustNotDependOnAdapterPackage(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.adapter..")
        .check(classes);
  }

  @ArchTest
  void domainPackage_mustNotDependOnCorePackage(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.core..")
        .check(classes);
  }

  @ArchTest
  void domainPackage_mustNotDependOnInfraPackage(JavaClasses classes) {
    classes()
        .that()
        .resideInAPackage("io.github.malczuuu.lemur.app.domain..")
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.infra..")
        .check(classes);
  }
}
