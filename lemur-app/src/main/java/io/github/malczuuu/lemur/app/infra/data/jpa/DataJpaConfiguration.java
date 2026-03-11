package io.github.malczuuu.lemur.app.infra.data.jpa;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = {DataJpaConfiguration.class})
@EntityScan(basePackageClasses = {DataJpaConfiguration.class})
public class DataJpaConfiguration {}
