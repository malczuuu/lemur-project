package io.github.malczuuu.lemur.app.infra;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class TransactionConfiguration {

  /**
   * There are two transaction managers present - one for JPA and another for Kafka. This bean is
   * marked as primary to avoid ambiguity when {@code @Transactional} is used without specifying the
   * transaction manager. This way, by default, the JPA transaction manager will be used for
   * {@code @Transactional} annotations, and the Kafka transaction manager can be explicitly
   * specified when needed.
   *
   * @param entityManagerFactory Spring-managed instance of {@link EntityManagerFactory}
   * @return a new instance of {@link JpaTransactionManager}
   */
  @Bean
  @Primary
  public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}
