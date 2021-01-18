package bsoft.com.ambtsgrenzen.config;

import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryPg",
        transactionManagerRef = "transactionManagerPg"
)
public class DatabaseConfigPostgres extends DatabaseConfig {
    private PrometheusMeterRegistry prometheusMeterRegistry;

    public DatabaseConfigPostgres(final PrometheusMeterRegistry prometheusMeterRegistry) {
        this.prometheusMeterRegistry = prometheusMeterRegistry;
    }

    @Bean
    @Primary
    @ConfigurationProperties("postgres.datasource")
    public DataSourceProperties pgDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("postgres.datasource.configuration")
    public HikariDataSource dataSource() {
        log.info("datasourcepg config: {}", pgDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build().toString());
        return pgDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /*
    @LiquibaseDataSource
    @Primary
    public DataSource liqDataSourcePg(@Qualifier("dataSource") final HikariDataSource dataSource) {
        // Create data source, set pool size to 1 and return it
        return (DataSource) dataSource;
    }
     */

    @PostConstruct
    public void setUpHikariWithMetrics() {
        if (dataSource() instanceof HikariDataSource) {
            ((HikariDataSource) dataSource()).setMetricRegistry(prometheusMeterRegistry);
        }
    }

    @Bean
    public SessionFactory sfPostgres(@Qualifier("entityManagerFactoryPg") final LocalContainerEntityManagerFactoryBean entityManagerFactoryPg) {
        return Objects.requireNonNull(entityManagerFactoryPg.getObject()).unwrap(SessionFactory.class);
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.postgres.liquibase")
    public LiquibaseProperties liquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean(name="liquibase")
    @DependsOn("dataSource")
    @Primary
    public SpringLiquibase liquibase(@Qualifier("dataSource") final DataSource dataSource,
                                     @Qualifier("liquibaseProperties") LiquibaseProperties liquibaseProperties) {
        return springLiquibase(dataSource, liquibaseProperties);
    }
/*
    @Bean
    public Liquibase lqPg(@Qualifier("dataSource") final DataSource dataSource) throws SQLException, LiquibaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
        Liquibase liquibase = new Liquibase("db/changelog/changelog-postgres-master.yaml", new ClassLoaderResourceAccessor(), database);
        liquibase.update(new Contexts(), new LabelExpression());
        return liquibase;
    }
 */

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryPg(@Qualifier("dataSource") final DataSource dataSource) {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setJpaProperties(hibernateProperties());
        factoryBean.setPackagesToScan("nl.bsoft.mybatch.config.postgres.repo", "nl.bsoft.mybatch.database");
        factoryBean.setPersistenceUnitName("postgres-unit");

        //factoryBean.setSharedCacheMode(SharedCacheMode.ALL);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManagerPg(@Qualifier("entityManagerFactoryPg") LocalContainerEntityManagerFactoryBean entityManagerFactoryPg) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryPg.getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return hibernateProperties;
    }
}
