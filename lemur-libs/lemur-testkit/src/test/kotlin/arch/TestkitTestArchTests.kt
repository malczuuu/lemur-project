package arch

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import io.github.malczuuu.lemur.testkit.archunit.NamingRules

@AnalyzeClasses(
    packages = ["arch", "io.github.malczuuu.lemur.testkit"],
    importOptions = [ImportOption.OnlyIncludeTests::class],
)
class TestkitTestArchTests {

    /**
     * This verifies that all test classes (containing `@Test` or `@ArchTest`) must end with `"...Tests"` suffix, and
     * not `"...Test"`.
     */
    @ArchTest
    fun testClassesMustEndWithPluralTests(classes: JavaClasses) {
        NamingRules.TEST_CLASSES_PLURAL.check(classes)
    }
}
