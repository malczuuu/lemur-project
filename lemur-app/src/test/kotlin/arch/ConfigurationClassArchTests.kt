package arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@AnalyzeClasses(packages = ["io.github.malczuuu.lemur.app"])
class ConfigurationClassArchTests {

    @ArchTest
    fun configurationClassesMustBeWithinInfraConfigPackage(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Configuration::class.java)
            .should().resideInAPackage("io.github.malczuuu.lemur.app.infra..")
            .check(classes)
    }

    @ArchTest
    fun configurationClassesMustHaveConfigurationNameSuffix(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Configuration::class.java)
            .should().haveSimpleNameEndingWith("Configuration")
            .check(classes)
    }

    @ArchTest
    fun configurationClassesMustBePublic(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Configuration::class.java)
            .should().bePublic()
            .check(classes)
    }

    @ArchTest
    fun beanMethodsMustNotBePrivate(classes: JavaClasses) {
        methods()
            .that().areAnnotatedWith(Bean::class.java)
            .should().notBePrivate()
            .check(classes)
    }
}
