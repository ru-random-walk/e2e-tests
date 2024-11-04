package random_walk.automation.databases;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

@Configuration
@ComponentScan("random_walk.automation.databases")
@Profile("test")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "random_walk.automation.databases.repos",
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager")
@EnableConfigurationProperties
public class PostgresDatabaseAutoConfiguration {
    @Bean
    @ConfigurationProperties("datasource.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public JdbcTemplate postgresJdbcTemplate() { return new JdbcTemplate(postgresDataSource());}

    @Bean
    @ConfigurationProperties("datasource.postgres.configuration")
    public HikariDataSource postgresDataSource() {
        return postgresDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(@Qualifier("postgresBuilder") EntityManagerFactoryBuilder builder) {
        return builder.dataSource(postgresDataSource()).packages("random_walk.automation.databases.entities").build();
    }

    @Bean
    public PlatformTransactionManager postgresTransactionManager(@Qualifier("postgresEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "postgresBuilder")
    public EntityManagerFactoryBuilder postgresManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

}

