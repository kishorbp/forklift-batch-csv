package com.informazine.forklift;

// java imports
import java.util.Properties;

// java sql imports
import javax.sql.DataSource;

// spring framework imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
 * The following core resources were used as a research reference for jpa configuration:
 * http://www.baeldung.com/the-persistence-layer-with-spring-data-jpa
 * http://www.baeldung.com/2011/12/13/the-persistence-layer-with-spring-3-1-and-jpa/
 * https://vladmihalcea.com/tutorials/hibernate/
 * https://vladmihalcea.com/books/high-performance-java-persistence/
 */

@Configuration
@EnableTransactionManagement

// load the database persistence properties using spring boot profiles
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@ComponentScan({ "com.informazine.forklift" })

@EnableJpaRepositories(basePackages = "com.informazine.forklift")
public class DatabaseConfig 
{

	// autowire the environment variables so we can load the datasource connector properties
    @Autowired
    private Environment env;

    public DatabaseConfig() 
    {
        super();
    }

    // http://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/orm/jpa/LocalContainerEntityManagerFactoryBean.html
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() 
    {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "com.informazine.forklift" });
        
        // http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/orm/jpa/vendor/HibernateJpaVendorAdapter.html
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    // http://docs.spring.io/spring-boot/docs/current/reference/html/howto-data-access.html
    @Bean
    public DataSource dataSource() 
    {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        // datasource connection properties are obtained via profiles and application*.properties
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));
        return dataSource;
    }
    
    // http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html
    @Bean
    public JpaTransactionManager transactionManager() 
    {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    // http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/dao/annotation/PersistenceExceptionTranslationPostProcessor.html
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() 
    {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    // https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html
    final Properties additionalProperties() 
    {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        return hibernateProperties;
    }

}