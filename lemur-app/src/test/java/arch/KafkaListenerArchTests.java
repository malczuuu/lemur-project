package arch;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.kafka.annotation.KafkaListener;

@AnalyzeClasses(packages = {"io.github.malczuuu.lemur.app"})
class KafkaListenerArchTests {

  @ArchTest
  void kafkaListener_mustBeWithinAdapterKafkaPackage(JavaClasses classes) {
    classes()
        .that()
        .containAnyMethodsThat(haveKafkaListenerAnnotation())
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.kafka..")
        .check(classes);
  }

  @ArchTest
  void kafkaListener_mustNotDependOnDomain(JavaClasses classes) {
    classes()
        .that()
        .containAnyMethodsThat(haveKafkaListenerAnnotation())
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.domain..")
        .check(classes);
  }

  private DescribedPredicate<JavaMethod> haveKafkaListenerAnnotation() {
    return describe(
        "are annotated with @KafkaListener", m -> m.isAnnotatedWith(KafkaListener.class));
  }
}
