package arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.MappedSuperclass

@AnalyzeClasses(packages = ["io.github.malczuuu.lemur.app"])
class JpaEntityArchTests {

    @ArchTest
    fun entityClassesMustBeWithinInfraDataJpaPackage(classes: JavaClasses) {
        classes()
            .that().areAnnotatedWith(Entity::class.java)
            .or().areAnnotatedWith(Embeddable::class.java)
            .or().areAnnotatedWith(MappedSuperclass::class.java)
            .should().resideInAPackage("io.github.malczuuu.lemur.app.infra.data.jpa..")
            .check(classes)
    }
}
