package arch;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;

@AnalyzeClasses(packages = {"io.github.malczuuu.lemur.app"})
class JpaEntityArchTests {

  @ArchTest
  void entityClasses_mustBeWithinInfraDataJpaPackage(JavaClasses classes) {
    classes()
        .that()
        .areAnnotatedWith(Entity.class)
        .or()
        .areAnnotatedWith(Embeddable.class)
        .or()
        .areAnnotatedWith(MappedSuperclass.class)
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.infra.data.jpa..")
        .check(classes);
  }
}
