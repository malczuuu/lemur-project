package io.github.malczuuu.lemur.testkit.archunit

import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

object NamingRules {

    val TEST_CLASSES_PLURAL: ArchRule =
        classes()
            .that().containAnyMethodsThat(
                describe("are annotated with @Test or @ArchTest") {
                    it.isAnnotatedWith(Test::class.java) || it.isAnnotatedWith(ArchTest::class.java)
                },
            )
            .should().haveSimpleNameEndingWith("Tests")
}
