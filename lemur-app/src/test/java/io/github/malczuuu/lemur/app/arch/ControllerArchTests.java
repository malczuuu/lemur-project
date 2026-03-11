package io.github.malczuuu.lemur.app.arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.github.malczuuu.lemur.app.arch.ArchUtils.getAppClasses;

import io.github.problem4j.spring.web.resolver.ProblemResolver;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

class ControllerArchTests {

  @Test
  void controllerClasses_mustHaveControllerSuffix() {
    classes()
        .that()
        .areAnnotatedWith(Controller.class)
        .or()
        .areAnnotatedWith(RestController.class)
        .should()
        .haveSimpleNameEndingWith("Controller")
        .check(getAppClasses());
  }

  @Test
  void controllerClasses_mustBeWithinAdapterRestPackage() {
    classes()
        .that()
        .areAnnotatedWith(Controller.class)
        .or()
        .areAnnotatedWith(RestController.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
        .check(getAppClasses());
  }

  @Test
  void controllerAdviceClasses_mustBeWithinAdapterRestPackage() {
    classes()
        .that()
        .areAnnotatedWith(ControllerAdvice.class)
        .or()
        .areAnnotatedWith(RestControllerAdvice.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
        .check(getAppClasses());
  }

  @Test
  void problemResolverClasses_mustBeWithinAdapterRestPackage() {
    classes()
        .that()
        .implement(ProblemResolver.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
        .check(getAppClasses());
  }

  @Test
  void controllerClasses_mustNotDependOnDomain() {
    classes()
        .that()
        .areAnnotatedWith(Controller.class)
        .or()
        .areAnnotatedWith(RestController.class)
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.domain..")
        .check(getAppClasses());
  }
}
