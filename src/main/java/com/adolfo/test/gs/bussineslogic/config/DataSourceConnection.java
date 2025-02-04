package com.adolfo.test.gs.bussineslogic.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.adolfo.test.gs.bussineslogic.repositories",
    entityManagerFactoryRef = "datasourceEntityManagerFactory",
    transactionManagerRef = "datasourceTransactionManager"
)
public class DataSourceConnection {
    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties datasourceDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Primary
    @Bean
    public DataSource datasourceDataSource(@Qualifier("datasourceDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean datasourceEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("datasourceDataSource") DataSource dataSource,
        @Qualifier("entityManagerFactoryProperties") Map<String, Object> properties) {
        return builder
            .dataSource(dataSource)
            .properties(properties)
            .packages("com.adolfo.test.gs.bussineslogic.entities")
            .persistenceUnit("datasource")
            .build();
    }
    @Primary
    @Bean
    public PlatformTransactionManager datasourceTransactionManager(
        @Qualifier("datasourceEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}