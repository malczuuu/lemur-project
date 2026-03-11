package io.github.malczuuu.lemur.app.arch;

import static com.tngtech.archunit.base.DescribedPredicate.describe;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static io.github.malczuuu.lemur.app.arch.ArchUtils.getAppClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.annotation.KafkaListener;

class KafkaListenerArchTests {

  @Test
  void kafkaListener_mustBeWithinAdapterKafkaPackage() {
    classes()
        .that()
        .containAnyMethodsThat(haveKafkaListenerAnnotation())
        .should()
        .resideInAPackage("io.github.malczuuu.lemur.app.adapter.kafka..")
        .check(getAppClasses());
  }

  @Test
  void kafkaListener_mustNotDependOnDomain() {
    classes()
        .that()
        .containAnyMethodsThat(haveKafkaListenerAnnotation())
        .should()
        .onlyDependOnClassesThat()
        .resideOutsideOfPackage("io.github.malczuuu.lemur.app.domain..")
        .check(getAppClasses());
  }

  private DescribedPredicate<JavaMethod> haveKafkaListenerAnnotation() {
    return describe(
        "are annotated with @KafkaListener", m -> m.isAnnotatedWith(KafkaListener.class));
  }
}
