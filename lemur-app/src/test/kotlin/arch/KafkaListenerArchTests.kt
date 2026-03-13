package arch

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaMethod
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import org.springframework.kafka.annotation.KafkaListener

@AnalyzeClasses(packages = ["io.github.malczuuu.lemur.app"])
class KafkaListenerArchTests {

    @ArchTest
    fun kafkaListenerMustBeWithinAdapterKafkaPackage(classes: JavaClasses) {
        classes()
            .that().containAnyMethodsThat(haveKafkaListenerAnnotation())
            .should().resideInAPackage("io.github.malczuuu.lemur.app.adapter.kafka..")
            .check(classes)
    }

    @ArchTest
    fun kafkaListenerMustNotDependOnDomain(classes: JavaClasses) {
        classes()
            .that().containAnyMethodsThat(haveKafkaListenerAnnotation())
            .should().onlyDependOnClassesThat().resideOutsideOfPackage("io.github.malczuuu.lemur.app.domain..")
            .check(classes)
    }

    @ArchTest
    fun listenerMethodsMustHaveVoidReturnType(classes: JavaClasses) {
        methods()
            .that().areAnnotatedWith(KafkaListener::class.java)
            .should().haveRawReturnType(Void.TYPE)
            .check(classes)
    }

    private fun haveKafkaListenerAnnotation(): DescribedPredicate<JavaMethod> =
        describe("are annotated with @KafkaListener") { m -> m.isAnnotatedWith(KafkaListener::class.java) }
}
