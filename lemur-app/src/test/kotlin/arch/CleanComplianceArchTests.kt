package arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

/**
 * Tests to verify compliance with clean/onion architecture rules, but only on relationship between
 * packages.
 */
@AnalyzeClasses(packages = ["io.github.malczuuu.lemur.app"])
class CleanComplianceArchTests {

    @ArchTest
    fun corePackageMustNotDependOnAdapterPackage(classes: JavaClasses) {
        classes()
            .that().resideInAPackage("io.github.malczuuu.lemur.app.core..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.adapter..")
            .check(classes)
    }

    @ArchTest
    fun corePackageMustNotDependOnContractPackage(classes: JavaClasses) {
        classes()
            .that().resideInAPackage("io.github.malczuuu.lemur.app.core..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.contract..")
            .check(classes)
    }

    @ArchTest
    fun corePackageMustNotDependOnInfraPackage(classes: JavaClasses) {
        classes()
            .that().resideInAPackage("io.github.malczuuu.lemur.app.core..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.infra..")
            .check(classes)
    }

    @ArchTest
    fun domainPackageMustNotDependOnSpring(classes: JavaClasses) {
        classes()
            .that().resideInAPackage("io.github.malczuuu.lemur.app.domain..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("org.springframework..")
            .check(classes)
    }

    @ArchTest
    fun domainPackageMustNotDependOnAdapterPackage(classes: JavaClasses) {
        classes()
            .that().resideInAPackage("io.github.malczuuu.lemur.app.domain..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.adapter..")
            .check(classes)
    }

    @ArchTest
    fun domainPackageMustNotDependOnCorePackage(classes: JavaClasses) {
        classes()
            .that().resideInAPackage("io.github.malczuuu.lemur.app.domain..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.core..")
            .check(classes)
    }

    @ArchTest
    fun domainPackageMustNotDependOnInfraPackage(classes: JavaClasses) {
        classes()
            .that().resideInAPackage("io.github.malczuuu.lemur.app.domain..")
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.infra..")
            .check(classes)
    }
}
