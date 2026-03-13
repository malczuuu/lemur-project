package arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AnalyzeClasses(packages = {"io.github.malczuuu.lemur.app"})
class ConfigurationClassArchTests {

  @ArchTest
  void configurationClasses_mustBeWithinInfraConfigPackage(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(Configuration.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.infra..")
        .check(classes);
  }

  @ArchTest
  void configurationClasses_mustHaveConfigurationNameSuffix(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(Configuration.class)
        .should()
        .haveSimpleNameEndingWith("Configuration")
        .check(classes);
  }

  @ArchTest
  void configurationClasses_mustBePublic(JavaClasses classes) {
    classes().that().areAnnotatedWith(Configuration.class).should().bePublic().check(classes);
  }

  @ArchTest
  void beanMethods_mustBePackagePrivate(JavaClasses classes) {
    methods().that().areAnnotatedWith(Bean.class).should().bePackagePrivate().check(classes);
  }
}
