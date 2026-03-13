package arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import io.github.problem4j.spring.web.resolver.ProblemResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@AnalyzeClasses(packages = {"io.github.malczuuu.lemur.app"})
class ControllerArchTests {

  @ArchTest
  void controllerClasses_mustHaveControllerSuffix(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(Controller.class)
        .or()
        .areAnnotatedWith(RestController.class)
        .should()
        .haveSimpleNameEndingWith("Controller")
        .check(classes);
  }

  @ArchTest
  void controllerClasses_mustBeWithinAdapterRestPackage(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(Controller.class)
        .or()
        .areAnnotatedWith(RestController.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
        .check(classes);
  }

  @ArchTest
  void controllerAdviceClasses_mustBeWithinAdapterRestPackage(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(ControllerAdvice.class)
        .or()
        .areAnnotatedWith(RestControllerAdvice.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
        .check(classes);
  }

  @ArchTest
  void problemResolverClasses_mustBeWithinAdapterRestPackage(JavaClasses classes) {
    classes()
        .that()
        .implement(ProblemResolver.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.rest..")
        .check(classes);
  }

  @ArchTest
  void controllerClasses_mustNotDependOnDomain(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(Controller.class)
        .or()
        .areAnnotatedWith(RestController.class)
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.domain..")
        .check(classes);
  }
}
