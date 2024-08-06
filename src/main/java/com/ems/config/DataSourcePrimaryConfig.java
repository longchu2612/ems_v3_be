package com.ems.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        DataSourcePrimaryConfig.REPOSITORY_PACKAGES })
public class DataSourcePrimaryConfig {
    public static final String ENTITY_PACKAGES = "com.ems.application.entity";
    public static final String REPOSITORY_PACKAGES = "com.ems.application.repository";

    public static final String ENTITY_MANAGER = "entityManagerFactory";
    public static final String TRANSACTION_MANAGER = "transactionManager";
    public static final String PERSISTENCE_UNIT_NAME = "primary";

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource(DataSourceProperties dataSource) {
        return dataSource.initializeDataSourceBuilder().build();
    }

    /**
     * @param builder
     *                   EntityManagerFactoryBuilder
     * @param dataSource
     *                   DataSource
     * @param env
     *                   Environment
     * @return
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource, Environment env) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(ENTITY_PACKAGES);
        em.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        em.setJpaProperties(additionalJpaProperties(env));
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.afterPropertiesSet();
        return em;
    }

    /**
     * @param primaryEntityManager
     *                             LocalContainerEntityManagerFactoryBean
     * @return
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(primaryEntityManager.getObject());
        return transactionManager;
    }

    private Properties additionalJpaProperties(Environment env) {
        Properties properties = new Properties();
        // Fixed value that will not require change
        properties.setProperty("hibernate.transaction.jta.platform", "");
        // Can be changed in the property
        properties.setProperty("hibernate.id.new_generator_mappings",
                getValue(env, "spring.jpa.hibernate.id.new_generator_mappings"));
        properties.setProperty("hibernate.physical_naming_strategy",
                getValue(env, "spring.jpa.hibernate.physical_naming_strategy"));
        properties.setProperty("hibernate.implicit_naming_strategy",
                getValue(env, "spring.jpa.hibernate.implicit_naming_strategy"));
        properties.setProperty("hibernate.connection.handling_mode",
                getValue(env, "spring.jpa.hibernate.connection.handling_mode"));
        properties.setProperty("hibernate.hbm2ddl.auto", getValue(env, "spring.jpa.hibernate.ddl-auto"));
        properties.setProperty("hibernate.jdbc.lob.non_contextual_creation",
                getValue(env, "spring.jpa.hibernate.jdbc.lob.non_contextual_creation"));
        properties.setProperty("hibernate.dialect", getValue(env, "spring.jpa.hibernate.dialect"));
        properties.setProperty("hibernate.show_sql", getValue(env, "spring.jpa.hibernate.show_sql"));
        properties.setProperty("spring.jpa.properties.hibernate.jdbc.time_zone", "UTC");
        return properties;
    }

    /** Gets the property value. Returns an empty string if null。 */
    private String getValue(Environment env, String key) {
        String value = env.getProperty(key);
        if (value == null) {
            return "";
        }
        return value;
    }
}
