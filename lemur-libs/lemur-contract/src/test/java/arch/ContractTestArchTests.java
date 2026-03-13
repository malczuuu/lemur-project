package arch;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import io.github.malczuuu.lemur.testkit.archunit.NamingRules;

@AnalyzeClasses(
    packages = {"arch", "io.github.malczuuu.lemur.contract"},
    importOptions = ImportOption.OnlyIncludeTests.class)
class ContractTestArchTests {

  /**
   * This verifies that all test classes (containing {@code @Test} or {@code @ArchTest}) must end
   * with {@code "...Tests"} suffix, and not {@code "...Test"}.
   */
  @ArchTest
  void testClasses_mustEndWithPluralTests(JavaClasses classes) {
    NamingRules.TEST_CLASSES_PLURAL.check(classes);
  }
}
