package io.github.malczuuu.lemur.app.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static io.github.malczuuu.lemur.app.arch.ArchUtils.getAppClasses;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class ConfigurationClassArchTests {

  @Test
  void configurationClasses_mustBeWithinInfraConfigPackage() {
    classes()
        .that()
        .areAnnotatedWith(Configuration.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.infra..")
        .check(getAppClasses());
  }

  @Test
  void configurationClasses_mustHaveConfigurationNameSuffix() {
    classes()
        .that()
        .areAnnotatedWith(Configuration.class)
        .should()
        .haveSimpleNameEndingWith("Configuration")
        .check(getAppClasses());
  }

  @Test
  void configurationClasses_mustBePublic() {
    classes()
        .that()
        .areAnnotatedWith(Configuration.class)
        .should()
        .bePublic()
        .check(getAppClasses());
  }

  @Test
  void beanMethods_mustBePackagePrivate() {
    methods()
        .that()
        .areAnnotatedWith(Bean.class)
        .should()
        .bePackagePrivate()
        .check(getAppClasses());
  }
}
