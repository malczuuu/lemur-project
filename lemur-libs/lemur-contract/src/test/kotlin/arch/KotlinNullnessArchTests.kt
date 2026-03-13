package arch

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaField
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.jetbrains.annotations.NotNull

@AnalyzeClasses(
    packages = ["io.github.malczuuu.lemur.contract"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class KotlinNullnessArchTests {

    /**
     * DTO must allow nullability in Kotlin, as they are used for serialization and deserialization of messages, for
     * validation with Hibernate Validator and annotations coming from [jakarta.validation.constraints].
     *
     * If a field is non-nullable in Kotlin, then deserialization will fail first, before failure in validation.
     *
     * Checks for [org.jetbrains.annotations.NotNull], because this is the annotation that Kotlin compiler applies in
     * bytecode for non-nullable fields.
     */
    @ArchTest
    fun dataTransferObjectsMustAllowKotlinNullability(classes: JavaClasses) {
        ArchRuleDefinition.fields()
            .that().areNotStatic()
            .should(beNullableInKotlin())
            .check(classes)
    }

    private fun beNullableInKotlin(): ArchCondition<JavaField> = ArchCondition.from(
        DescribedPredicate.describe("be nullable in Kotlin") {
            val isJetbrainsNotNullPresent = it.isAnnotatedWith(NotNull::class.java)
            val isPrimitive = it.rawType.isPrimitive

            !isJetbrainsNotNullPresent || isPrimitive
        },
    )
}
