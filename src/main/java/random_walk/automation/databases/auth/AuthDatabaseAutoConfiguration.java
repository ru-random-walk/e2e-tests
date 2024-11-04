package random_walk.automation.databases.auth;

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
@EnableJpaRepositories(basePackages = "random_walk.automation.databases.auth.repos",
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager")
@EnableConfigurationProperties
public class AuthDatabaseAutoConfiguration {
    @Bean
    @ConfigurationProperties("datasource.auth")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public JdbcTemplate authJdbcTemplate() { return new JdbcTemplate(authDataSource());}

    @Bean
    @ConfigurationProperties("datasource.auth.configuration")
    public HikariDataSource authDataSource() {
        return authDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(@Qualifier("authBuilder") EntityManagerFactoryBuilder builder, DataSourceProperties dsp) {
        return builder.dataSource(authDataSource()).packages("api.database.entities").build();
    }

    @Bean
    public PlatformTransactionManager authTransactionManager(@Qualifier("authEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean(name = "authBuilder")
    public EntityManagerFactoryBuilder authManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

}

